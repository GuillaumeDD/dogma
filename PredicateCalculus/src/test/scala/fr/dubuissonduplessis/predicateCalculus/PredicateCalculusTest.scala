/*******************************************************************************
 * Copyright (c) 2014 Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr> - initial API and implementation
 ******************************************************************************/
/**
 * *****************************************************************************
 * Copyright (c) 2013 Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr> - initial API and implementation
 * ****************************************************************************
 */
package fr.dubuissonduplessis.predicateCalculus
import substitution._
import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class PredicateCalculusSuite extends FunSuite {
  trait TestPredicateCalculus {
    val ind01 = Individual("ind01")
    val ind01bis = Individual("ind01")
    val ind02 = Individual("ind02")

    val var01 = Variable("V1")
    val var01bis = Variable("V1")
    val var02 = Variable("V2")

    val pred01a = PredN("city",
      List(ind01))
    val pred01aBis = PredN("city",
      List(ind01))

    val pred01b = PredN("city",
      List(var01))
    val pred01c = PredN("city",
      List(pred01a, var01))

    def determineSubSet(s: SubstitutionSet): SubstitutionSet =
      SubstitutionSet.simplify(s)
  }

  test("PredicateCalculus: mixed") {
    new TestPredicateCalculus {
      val john = Individual("john")
      val mary = Individual("mary")

      // love(X, Y)
      // love(john, mary)
      val loveXY = PredN("love",
        List(var01, var02))
      val loveXX = PredN("love",
        List(var01, var01))
      val loveJohnMary = PredN("love",
        List(john, mary))

      assert(determineSubSet(loveXY.unifyWith(loveJohnMary).get) ===
        SubstitutionSet() +
        Substitution(var01, john) +
        Substitution(var02, mary))
      assert(determineSubSet(loveJohnMary.unifyWith(loveXY).get) ===
        SubstitutionSet() +
        Substitution(var01, john) +
        Substitution(var02, mary))

      val solution01 = PredN("love",
        List(john, mary))
      val substitution01a = loveJohnMary.unifyWith(loveXY).get
      val substitution01b = loveJohnMary.unifyWith(loveXY).get
      assert(loveJohnMary.substitute(substitution01a) predEquals solution01)
      assert(loveJohnMary.substitute(substitution01b) predEquals solution01)
      assert(loveXY.substitute(substitution01a) predEquals solution01)
      assert(loveXY.substitute(substitution01b) predEquals solution01)

      // love(X, X)
      // love(john, mary)          
      assert(loveXX.unifyWith(loveJohnMary) === None)
      assert(loveJohnMary.unifyWith(loveXX) === None)

      // love(john, X)
      // love(X, john)
      val loveJohnX = PredN("love",
        List(john, var01))
      val loveXJohn = PredN("love",
        List(var01, john))
      assert(determineSubSet(loveJohnX.unifyWith(loveXJohn).get) ===
        SubstitutionSet() + Substitution(var01, john))
      assert(determineSubSet(loveXJohn.unifyWith(loveJohnX).get) ===
        SubstitutionSet() + Substitution(var01, john))

      val solution02 = PredN("love",
        List(john, john))
      val substitution02a = loveJohnX.unifyWith(loveXJohn).get
      val substitution02b = loveXJohn.unifyWith(loveJohnX).get
      assert(loveJohnX.substitute(substitution02a) predEquals solution02)
      assert(loveJohnX.substitute(substitution02b) predEquals solution02)
      assert(loveXJohn.substitute(substitution02a) predEquals solution02)
      assert(loveXJohn.substitute(substitution02b) predEquals solution02)

      // love(john, X)
      // love(X, Y)
      assert(determineSubSet(loveJohnX.unifyWith(loveXY).get) ===
        SubstitutionSet() + Substitution(var01, john) +
        Substitution(var02, john))
      assert(determineSubSet(loveXY.unifyWith(loveJohnX).get) ===
        SubstitutionSet() + Substitution(var01, john) +
        Substitution(var02, john))

      val solution03 = PredN("love",
        List(john, john))
      val substitution03a = loveJohnX.unifyWith(loveXY).get
      val substitution03b = loveXY.unifyWith(loveJohnX).get
      assert(loveJohnX.substitute(substitution03a) predEquals solution03)
      assert(loveJohnX.substitute(substitution03b) predEquals solution03)
      assert(loveXY.substitute(substitution03a) predEquals solution03)
      assert(loveXY.substitute(substitution03b) predEquals solution03,
        loveXY.substitute(substitution03b) + " did not equal " + solution03)

      // sneaky(john, X, Y, A, B, C)
      // sneaky(X, Y, Z, B, C, mary)
      val V = Variable("V")
      val W = Variable("W")
      val X = Variable("X")
      val Y = Variable("Y")
      val Z = Variable("Z")
      val A = Variable("A")
      val B = Variable("B")
      val C = Variable("C")
      val D = Variable("D")
      val E = Variable("E")

      val sneaky01 = PredN("sneaky",
        List(V, john, X, Y, A, B, C, D))
      val sneaky02 = PredN("sneaky",
        List(W, X, Y, Z, B, C, mary, E))

      assert(determineSubSet(sneaky01.unifyWith(sneaky02).get) ===
        SubstitutionSet() +
        Substitution(X, john) +
        Substitution(Y, john) +
        Substitution(Z, john) +
        Substitution(A, mary) +
        Substitution(B, mary) +
        Substitution(C, mary) +
        Substitution(V, W) +
        Substitution(D, E))

      assert(determineSubSet(sneaky02.unifyWith(sneaky01).get) ===
        SubstitutionSet() +
        Substitution(X, john) +
        Substitution(Y, john) +
        Substitution(Z, john) +
        Substitution(A, mary) +
        Substitution(B, mary) +
        Substitution(C, mary) +
        Substitution(W, V) +
        Substitution(E, D))

      // sneaky(W, X, Y, A, B, C)
      // sneaky(X, Y, Z, B, C, D)
      val sneaky03 = PredN("sneaky",
        List(W, X, Y, A, B, C))
      val sneaky04 = PredN("sneaky",
        List(X, Y, Z, B, C, D))

      assert(sneaky03.unifyWith(sneaky04).get.substitutions.size === 6)

      assert(sneaky04.unifyWith(sneaky03).get.substitutions.size === 6)
    }
  }

  test("PredicateCalculus: Individual") {
    new TestPredicateCalculus {
      // ind01
      assert(ind01.unifyWith(ind01) ===
        Some(SubstitutionSet()))
      assert(ind01.unifyWith(ind01bis) ===
        Some(SubstitutionSet()))
      assert(ind01.unifyWith(ind02) === None)
      assert(ind01.unifyWith(var01) ===
        Some(SubstitutionSet() + Substitution(var01, ind01)))
      assert(ind01.unifyWith(var01bis) ===
        Some(SubstitutionSet() + Substitution(var01bis, ind01)))
      assert(ind01.unifyWith(var02) ===
        Some(SubstitutionSet() + Substitution(var02, ind01)))
      assert(ind01.unifyWith(pred01a) === None)
      assert(ind01.unifyWith(pred01aBis) === None)
      assert(ind01.unifyWith(pred01b) === None)
      assert(ind01.unifyWith(pred01c) === None)

      // ind01bis
      assert(ind01bis.unifyWith(ind01) ===
        Some(SubstitutionSet()))
      assert(ind01bis.unifyWith(ind01bis) ===
        Some(SubstitutionSet()))
      assert(ind01bis.unifyWith(ind02) === None)
      assert(ind01bis.unifyWith(var01) ===
        Some(SubstitutionSet() + Substitution(var01, ind01bis)))
      assert(ind01bis.unifyWith(var01bis) ===
        Some(SubstitutionSet() + Substitution(var01bis, ind01bis)))
      assert(ind01bis.unifyWith(var02) ===
        Some(SubstitutionSet() + Substitution(var02, ind01bis)))
      assert(ind01bis.unifyWith(pred01a) === None)
      assert(ind01bis.unifyWith(pred01aBis) === None)
      assert(ind01bis.unifyWith(pred01b) === None)
      assert(ind01bis.unifyWith(pred01c) === None)

      // ind02
      assert(ind02.unifyWith(ind01) === None)
      assert(ind02.unifyWith(ind01bis) === None)
      assert(ind02.unifyWith(ind02) === Some(SubstitutionSet()))
      assert(ind02.unifyWith(var01) ===
        Some(SubstitutionSet() + Substitution(var01, ind02)))
      assert(ind02.unifyWith(var01bis) ===
        Some(SubstitutionSet() + Substitution(var01bis, ind02)))
      assert(ind02.unifyWith(var02) ===
        Some(SubstitutionSet() + Substitution(var02, ind02)))
      assert(ind02.unifyWith(pred01a) === None)
      assert(ind02.unifyWith(pred01aBis) === None)
      assert(ind02.unifyWith(pred01b) === None)
      assert(ind02.unifyWith(pred01c) === None)
    }
  }

  test("PredicateCalculus: Variable") {
    new TestPredicateCalculus {
      // var01
      assert(var01.unifyWith(ind01) ===
        Some(SubstitutionSet() + Substitution(var01, ind01)))
      assert(var01.unifyWith(ind01bis) ===
        Some(SubstitutionSet() + Substitution(var01, ind01bis)))
      assert(var01.unifyWith(ind02) ===
        Some(SubstitutionSet() + Substitution(var01, ind02)))
      assert(var01.unifyWith(var01) ===
        Some(SubstitutionSet()))
      assert(var01.unifyWith(var01bis) ===
        Some(SubstitutionSet()))
      assert(var01.unifyWith(var02) ===
        Some(SubstitutionSet() + VariableSubstitution(var01, var02)))
      assert(var01.unifyWith(pred01a) ===
        Some(SubstitutionSet() + Substitution(var01, pred01a)))
      assert(var01.unifyWith(pred01aBis) ===
        Some(SubstitutionSet() + Substitution(var01, pred01aBis)))
      assert(var01.unifyWith(pred01b) === None)
      assert(var01.unifyWith(pred01c) === None)

      // var01bis
      assert(var01bis.unifyWith(ind01) ===
        Some(SubstitutionSet() + Substitution(var01bis, ind01)))
      assert(var01bis.unifyWith(ind01bis) ===
        Some(SubstitutionSet() + Substitution(var01bis, ind01bis)))
      assert(var01bis.unifyWith(ind02) ===
        Some(SubstitutionSet() + Substitution(var01bis, ind02)))
      assert(var01bis.unifyWith(var01) ===
        Some(SubstitutionSet()))
      assert(ind01bis.unifyWith(var01bis) ===
        Some(SubstitutionSet() + Substitution(var01bis, ind01bis)))
      assert(var01bis.unifyWith(var02) ===
        Some(SubstitutionSet() + VariableSubstitution(var01bis, var02)))
      assert(var01bis.unifyWith(pred01a) ===
        Some(SubstitutionSet() + Substitution(var01bis, pred01a)))
      assert(var01bis.unifyWith(pred01aBis) ===
        Some(SubstitutionSet() + Substitution(var01bis, pred01aBis)))
      assert(var01bis.unifyWith(pred01b) === None)
      assert(var01bis.unifyWith(pred01c) === None)

      // var02
      assert(var02.unifyWith(ind01) ===
        Some(SubstitutionSet() + Substitution(var02, ind01)))
      assert(var02.unifyWith(ind01bis) ===
        Some(SubstitutionSet() + Substitution(var02, ind01bis)))
      assert(var02.unifyWith(ind02) ===
        Some(SubstitutionSet() + Substitution(var02, ind02)))
      assert(var02.unifyWith(var01) ===
        Some(SubstitutionSet() + VariableSubstitution(var02, var01)))
      assert(var02.unifyWith(var01bis) ===
        Some(SubstitutionSet() + VariableSubstitution(var02, var01bis)))
      assert(var02.unifyWith(var02) ===
        Some(SubstitutionSet()))
      assert(var02.unifyWith(pred01a) ===
        Some(SubstitutionSet() + Substitution(var02, pred01a)))
      assert(var02.unifyWith(pred01aBis) ===
        Some(SubstitutionSet() + Substitution(var02, pred01aBis)))
      assert(var02.unifyWith(pred01b) ===
        Some(SubstitutionSet() + Substitution(var02, pred01b)))
      assert(var02.unifyWith(pred01c) ===
        Some(SubstitutionSet() + Substitution(var02, pred01c)))
    }
  }

  test("PredicateCalculus: PredN") {
    new TestPredicateCalculus {
      // pred01
      assert(pred01a.unifyWith(ind01) === None)
      assert(pred01a.unifyWith(ind01bis) ===
        None)
      assert(pred01a.unifyWith(ind02) ===
        None)
      assert(pred01a.unifyWith(var01) ===
        Some(SubstitutionSet() + Substitution(var01, pred01a)))
      assert(pred01a.unifyWith(var01bis) ===
        Some(SubstitutionSet() + Substitution(var01bis, pred01a)))
      assert(pred01a.unifyWith(var02) ===
        Some(SubstitutionSet() + Substitution(var02, pred01a)))
      assert(pred01a.unifyWith(pred01a) ===
        Some(SubstitutionSet()))
      assert(pred01a.unifyWith(pred01aBis) ===
        Some(SubstitutionSet()))
      assert(pred01a.unifyWith(pred01b) ===
        Some(SubstitutionSet() + Substitution(var01, ind01)))
      assert(pred01a.unifyWith(pred01c) === None)

      // pred01aBis
      assert(pred01aBis.unifyWith(ind01) === None)
      assert(pred01aBis.unifyWith(ind01bis) ===
        None)
      assert(pred01aBis.unifyWith(ind02) ===
        None)
      assert(pred01aBis.unifyWith(var01) ===
        Some(SubstitutionSet() + Substitution(var01, pred01aBis)))
      assert(pred01aBis.unifyWith(var01bis) ===
        Some(SubstitutionSet() + Substitution(var01bis, pred01aBis)))
      assert(pred01aBis.unifyWith(var02) ===
        Some(SubstitutionSet() + Substitution(var02, pred01aBis)))
      assert(pred01aBis.unifyWith(pred01a) ===
        Some(SubstitutionSet()))
      assert(pred01aBis.unifyWith(pred01aBis) ===
        Some(SubstitutionSet()))
      assert(pred01aBis.unifyWith(pred01b) ===
        Some(SubstitutionSet() + Substitution(var01, ind01)))
      assert(pred01aBis.unifyWith(pred01c) === None)

      // pred01b
      assert(pred01b.unifyWith(ind01) === None)
      assert(pred01b.unifyWith(ind01bis) ===
        None)
      assert(pred01b.unifyWith(ind02) ===
        None)
      assert(pred01b.unifyWith(var01) === None)
      assert(pred01b.unifyWith(var01bis) === None)
      assert(pred01b.unifyWith(var02) ===
        Some(SubstitutionSet() + Substitution(var02, pred01b)))
      assert(pred01b.unifyWith(pred01a) ===
        Some(SubstitutionSet() + Substitution(var01, ind01)))
      assert(pred01b.unifyWith(pred01aBis) ===
        Some(SubstitutionSet() + Substitution(var01, ind01)))
      assert(pred01b.unifyWith(pred01b) ===
        Some(SubstitutionSet()))
      assert(pred01b.unifyWith(pred01c) === None)

      // pred01c
      assert(pred01c.unifyWith(ind01) === None)
      assert(pred01c.unifyWith(ind01bis) ===
        None)
      assert(pred01c.unifyWith(ind02) ===
        None)
      assert(pred01c.unifyWith(var01) === None)
      assert(pred01c.unifyWith(var01bis) === None)
      assert(pred01c.unifyWith(var02) ===
        Some(SubstitutionSet() + Substitution(var02, pred01c)))
      assert(pred01c.unifyWith(pred01a) === None)
      assert(pred01c.unifyWith(pred01aBis) === None)
      assert(pred01c.unifyWith(pred01b) === None)
      assert(pred01c.unifyWith(pred01c) === Some(SubstitutionSet()))
    }
  }

  test("PredicateCalculus: isUnifiableWith") {
    new TestPredicateCalculus {
      assert(ind01.isUnifiableWith(ind01))
      assert(ind01.isUnifiableWith(ind01bis))
      assert(!ind01.isUnifiableWith(ind02))
      assert(ind01.isUnifiableWith(var01))
      assert(ind01.isUnifiableWith(var01bis))
      assert(ind01.isUnifiableWith(var02))
      assert(!ind01.isUnifiableWith(pred01a))
      assert(!ind01.isUnifiableWith(pred01aBis))
      assert(!ind01.isUnifiableWith(pred01b))
      assert(!ind01.isUnifiableWith(pred01c))

      assert(ind01bis.isUnifiableWith(ind01))
      assert(ind01bis.isUnifiableWith(ind01bis))
      assert(!ind01bis.isUnifiableWith(ind02))
      assert(ind01bis.isUnifiableWith(var01))
      assert(ind01bis.isUnifiableWith(var01bis))
      assert(ind01bis.isUnifiableWith(var02))
      assert(!ind01bis.isUnifiableWith(pred01a))
      assert(!ind01bis.isUnifiableWith(pred01aBis))
      assert(!ind01bis.isUnifiableWith(pred01b))
      assert(!ind01bis.isUnifiableWith(pred01c))

      assert(!ind02.isUnifiableWith(ind01))
      assert(!ind02.isUnifiableWith(ind01bis))
      assert(ind02.isUnifiableWith(ind02))
      assert(ind02.isUnifiableWith(var01))
      assert(ind02.isUnifiableWith(var01bis))
      assert(ind02.isUnifiableWith(var02))
      assert(!ind02.isUnifiableWith(pred01a))
      assert(!ind02.isUnifiableWith(pred01aBis))
      assert(!ind02.isUnifiableWith(pred01b))
      assert(!ind02.isUnifiableWith(pred01c))

      assert(var01.isUnifiableWith(ind01))
      assert(var01.isUnifiableWith(ind01bis))
      assert(var01.isUnifiableWith(ind02))
      assert(var01.isUnifiableWith(var01))
      assert(var01.isUnifiableWith(var01bis))
      assert(var01.isUnifiableWith(var02))
      assert(var01.isUnifiableWith(pred01a))
      assert(var01.isUnifiableWith(pred01aBis))
      assert(!var01.isUnifiableWith(pred01b)) // pred01b contains var01
      assert(!var01.isUnifiableWith(pred01c)) // pred01c contains var01

      assert(var01bis.isUnifiableWith(ind01))
      assert(var01bis.isUnifiableWith(ind01bis))
      assert(var01bis.isUnifiableWith(ind02))
      assert(var01bis.isUnifiableWith(var01))
      assert(var01bis.isUnifiableWith(var01bis))
      assert(var01bis.isUnifiableWith(var02))
      assert(var01bis.isUnifiableWith(pred01a))
      assert(var01bis.isUnifiableWith(pred01aBis))
      assert(!var01bis.isUnifiableWith(pred01b)) // pred01b contains var01
      assert(!var01bis.isUnifiableWith(pred01c)) // pred01c contains var01

      assert(var02.isUnifiableWith(ind01))
      assert(var02.isUnifiableWith(ind01bis))
      assert(var02.isUnifiableWith(ind02))
      assert(var02.isUnifiableWith(var01))
      assert(var02.isUnifiableWith(var01bis))
      assert(var02.isUnifiableWith(var02))
      assert(var02.isUnifiableWith(pred01a))
      assert(var02.isUnifiableWith(pred01aBis))
      assert(var02.isUnifiableWith(pred01b))
      assert(var02.isUnifiableWith(pred01c))

      assert(!pred01a.isUnifiableWith(ind01))
      assert(!pred01a.isUnifiableWith(ind01bis))
      assert(!pred01a.isUnifiableWith(ind02))
      assert(pred01a.isUnifiableWith(var01))
      assert(pred01a.isUnifiableWith(var01bis))
      assert(pred01a.isUnifiableWith(var02))
      assert(pred01a.isUnifiableWith(pred01a))
      assert(pred01a.isUnifiableWith(pred01aBis))
      assert(pred01a.isUnifiableWith(pred01b))
      assert(!pred01a.isUnifiableWith(pred01c))

      assert(!pred01aBis.isUnifiableWith(ind01))
      assert(!pred01aBis.isUnifiableWith(ind01bis))
      assert(!pred01aBis.isUnifiableWith(ind02))
      assert(pred01aBis.isUnifiableWith(var01))
      assert(pred01aBis.isUnifiableWith(var01bis))
      assert(pred01aBis.isUnifiableWith(var02))
      assert(pred01aBis.isUnifiableWith(pred01a))
      assert(pred01aBis.isUnifiableWith(pred01aBis))
      assert(pred01aBis.isUnifiableWith(pred01b))
      assert(!pred01aBis.isUnifiableWith(pred01c))

      assert(!pred01b.isUnifiableWith(ind01))
      assert(!pred01b.isUnifiableWith(ind01bis))
      assert(!pred01b.isUnifiableWith(ind02))
      assert(!pred01b.isUnifiableWith(var01)) // pred01b contains var01
      assert(!pred01b.isUnifiableWith(var01bis)) // pred01b contains var01bis
      assert(pred01b.isUnifiableWith(var02))
      assert(pred01b.isUnifiableWith(pred01a))
      assert(pred01b.isUnifiableWith(pred01aBis))
      assert(pred01b.isUnifiableWith(pred01b))
      assert(!pred01b.isUnifiableWith(pred01c))

      assert(!pred01c.isUnifiableWith(ind01))
      assert(!pred01c.isUnifiableWith(ind01bis))
      assert(!pred01c.isUnifiableWith(ind02))
      assert(!pred01c.isUnifiableWith(var01)) // pred01c contains var01
      assert(!pred01c.isUnifiableWith(var01bis)) // pred01c contains var01bis
      assert(pred01c.isUnifiableWith(var02))
      assert(!pred01c.isUnifiableWith(pred01a))
      assert(!pred01c.isUnifiableWith(pred01aBis))
      assert(!pred01c.isUnifiableWith(pred01b))
      assert(pred01c.isUnifiableWith(pred01c))
    }
  }

  test("PredicateCalculus: predEquals") {
    new TestPredicateCalculus {
      // Predicate Equality: reflexivity
      assert(ind01.predEquals(ind01))
      assert(ind01bis.predEquals(ind01bis))
      assert(ind02.predEquals(ind02))

      assert(var01.predEquals(var01))
      assert(var01bis.predEquals(var01bis))
      assert(var02.predEquals(var02))

      assert(pred01a.predEquals(pred01a))
      assert(pred01aBis.predEquals(pred01aBis))
      assert(pred01c.predEquals(pred01c))

      // Predicate Equality
      assert(ind01.predEquals(ind01))
      assert(ind01.predEquals(ind01bis))
      assert(!ind01.predEquals(ind02))
      assert(!ind01.predEquals(var01))
      assert(!ind01.predEquals(var01bis))
      assert(!ind01.predEquals(var02))
      assert(!ind01.predEquals(pred01a))
      assert(!ind01.predEquals(pred01aBis))
      assert(!ind01.predEquals(pred01b))
      assert(!ind01.predEquals(pred01c))

      assert(ind01bis.predEquals(ind01))
      assert(ind01bis.predEquals(ind01bis))
      assert(!ind01bis.predEquals(ind02))
      assert(!ind01bis.predEquals(var01))
      assert(!ind01bis.predEquals(var01bis))
      assert(!ind01bis.predEquals(var02))
      assert(!ind01bis.predEquals(pred01a))
      assert(!ind01bis.predEquals(pred01aBis))
      assert(!ind01bis.predEquals(pred01b))
      assert(!ind01bis.predEquals(pred01c))

      assert(!ind02.predEquals(ind01))
      assert(!ind02.predEquals(ind01bis))
      assert(ind02.predEquals(ind02))
      assert(!ind02.predEquals(var01))
      assert(!ind02.predEquals(var01bis))
      assert(!ind02.predEquals(var02))
      assert(!ind02.predEquals(pred01a))
      assert(!ind02.predEquals(pred01aBis))
      assert(!ind02.predEquals(pred01b))
      assert(!ind02.predEquals(pred01c))

      assert(!var01.predEquals(ind01))
      assert(!var01.predEquals(ind01bis))
      assert(!var01.predEquals(ind02))
      assert(var01.predEquals(var01))
      assert(var01.predEquals(var01bis))
      assert(!var01.predEquals(var02))
      assert(!var01.predEquals(pred01a))
      assert(!var01.predEquals(pred01aBis))
      assert(!var01.predEquals(pred01b))
      assert(!var01.predEquals(pred01c))

      assert(!var01bis.predEquals(ind01))
      assert(!var01bis.predEquals(ind01bis))
      assert(!var01bis.predEquals(ind02))
      assert(var01bis.predEquals(var01))
      assert(var01bis.predEquals(var01bis))
      assert(!var01bis.predEquals(var02))
      assert(!var01bis.predEquals(pred01a))
      assert(!var01bis.predEquals(pred01aBis))
      assert(!var01bis.predEquals(pred01b))
      assert(!var01bis.predEquals(pred01c))

      assert(!var02.predEquals(ind01))
      assert(!var02.predEquals(ind01bis))
      assert(!var02.predEquals(ind02))
      assert(!var02.predEquals(var01))
      assert(!var02.predEquals(var01bis))
      assert(var02.predEquals(var02))
      assert(!var02.predEquals(pred01a))
      assert(!var02.predEquals(pred01aBis))
      assert(!var02.predEquals(pred01b))
      assert(!var02.predEquals(pred01c))

      assert(!pred01a.predEquals(ind01))
      assert(!pred01a.predEquals(ind01bis))
      assert(!pred01a.predEquals(ind02))
      assert(!pred01a.predEquals(var01))
      assert(!pred01a.predEquals(var01bis))
      assert(!pred01a.predEquals(var02))
      assert(pred01a.predEquals(pred01a))
      assert(pred01a.predEquals(pred01aBis))
      assert(!pred01a.predEquals(pred01b))
      assert(!pred01a.predEquals(pred01c))

      assert(!pred01aBis.predEquals(ind01))
      assert(!pred01aBis.predEquals(ind01bis))
      assert(!pred01aBis.predEquals(ind02))
      assert(!pred01aBis.predEquals(var01))
      assert(!pred01aBis.predEquals(var01bis))
      assert(!pred01aBis.predEquals(var02))
      assert(pred01aBis.predEquals(pred01a))
      assert(pred01aBis.predEquals(pred01aBis))
      assert(!pred01aBis.predEquals(pred01b))
      assert(!pred01aBis.predEquals(pred01c))

      assert(!pred01b.predEquals(ind01))
      assert(!pred01b.predEquals(ind01bis))
      assert(!pred01b.predEquals(ind02))
      assert(!pred01b.predEquals(var01))
      assert(!pred01b.predEquals(var01bis))
      assert(!pred01b.predEquals(var02))
      assert(!pred01b.predEquals(pred01a))
      assert(!pred01b.predEquals(pred01aBis))
      assert(pred01b.predEquals(pred01b))
      assert(!pred01b.predEquals(pred01c))

      assert(!pred01c.predEquals(ind01))
      assert(!pred01c.predEquals(ind01bis))
      assert(!pred01c.predEquals(ind02))
      assert(!pred01c.predEquals(var01))
      assert(!pred01c.predEquals(var01bis))
      assert(!pred01c.predEquals(var02))
      assert(!pred01c.predEquals(pred01a))
      assert(!pred01c.predEquals(pred01aBis))
      assert(!pred01c.predEquals(pred01b))
      assert(pred01c.predEquals(pred01c))
    }
  }

}
