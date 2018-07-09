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
package fr.dubuissonduplessis.simpleSemantics.proposition

import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import fr.dubuissonduplessis.predicateCalculus.Variable
import fr.dubuissonduplessis.predicateCalculus.Individual
import fr.dubuissonduplessis.predicateCalculus.substitution.SubstitutionSet
import fr.dubuissonduplessis.predicateCalculus.substitution.Substitution

@RunWith(classOf[JUnitRunner])
class Proposition3Suite extends FunSuite {
  trait TestProposition3 {
    val ind01 = Individual("france")
    val ind02 = Individual("spain")

    val ind03 = Individual("west")
    val ind04 = Individual("north")

    val ind05 = Individual("December")
    val ind06 = Individual("January")

    val proposition3a = Proposition3("go", ind01, ind04, ind05) // go(france, north, december)
    val proposition3b = Proposition3("bo", ind02, ind03, ind06) // bo(spain, west, january)

    val X = Variable("X")
    val Y = Variable("Y")

    val subSet01 = SubstitutionSet() + Substitution(X, Proposition0("feur"))
    val subSet02 = SubstitutionSet() +
      Substitution(X, Proposition0("feur")) +
      Substitution(Y, Proposition0("quoi"))

  }

  test("Proposition3: name") {
    new TestProposition3 {
      assert(proposition3a.name === "go")
      assert(proposition3b.name === "bo")
    }
  }

  test("Proposition3: equality") {
    new TestProposition3 {
      assert(proposition3a predEquals proposition3a)
      assert(!(proposition3a predEquals proposition3b))

      assert(!(proposition3b predEquals proposition3a))
      assert(proposition3b predEquals proposition3b)
    }
  }

  test("Proposition3: arity, first, second, third and terms") {
    new TestProposition3 {
      assert(proposition3a.arity === 3)
      assert(proposition3a.first === ind01)
      assert(proposition3a.second === ind04)
      assert(proposition3a.third === ind05)
      assert(proposition3a.terms === List(ind01, ind04, ind05))

      assert(proposition3b.arity === 3)
      assert(proposition3b.first === ind02)
      assert(proposition3b.second === ind03)
      assert(proposition3b.third === ind06)
      assert(proposition3b.terms === List(ind02, ind03, ind06))
    }
  }

  test("Proposition3: substitution") {
    new TestProposition3 {
      assert(proposition3a.substitute(subSet01) predEquals proposition3a)
      assert(proposition3b.substitute(subSet01) predEquals proposition3b)

      assert(proposition3a.substitute(subSet02) predEquals proposition3a)
      assert(proposition3b.substitute(subSet02) predEquals proposition3b)
    }
  }

  test("Proposition3: neg") {
    new TestProposition3 {
      assert(!(proposition3a.neg predEquals proposition3a))
      assert(!(proposition3b.neg predEquals proposition3b))

      assert(proposition3a.neg predEquals NegProposition(proposition3a))
      assert(proposition3b.neg predEquals NegProposition(proposition3b))

      assert(proposition3a.neg.neg predEquals proposition3a)
      assert(proposition3b.neg.neg predEquals proposition3b)
    }
  }
}
