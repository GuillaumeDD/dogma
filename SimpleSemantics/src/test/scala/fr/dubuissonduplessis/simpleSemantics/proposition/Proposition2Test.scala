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
class Proposition2Suite extends FunSuite {
  trait TestProposition2 {
    val ind01 = Individual("france")
    val ind02 = Individual("spain")
    val ind03 = Individual("west")
    val ind04 = Individual("north")

    val proposition2a = Proposition2("go", ind01, ind04) // go(france, north)
    val proposition2b = Proposition2("bo", ind02, ind03) // bo(spain, west)

    val X = Variable("X")
    val Y = Variable("Y")

    val subSet01 = SubstitutionSet() + Substitution(X, Proposition0("feur"))
    val subSet02 = SubstitutionSet() +
      Substitution(X, Proposition0("feur")) +
      Substitution(Y, Proposition0("quoi"))

  }

  test("Proposition2: name") {
    new TestProposition2 {
      assert(proposition2a.name === "go")
      assert(proposition2b.name === "bo")
    }
  }

  test("Proposition2: equality") {
    new TestProposition2 {
      assert(proposition2a predEquals proposition2a)
      assert(!(proposition2a predEquals proposition2b))

      assert(!(proposition2b predEquals proposition2a))
      assert(proposition2b predEquals proposition2b)
    }
  }

  test("Proposition2: arity, first, second and terms") {
    new TestProposition2 {
      assert(proposition2a.arity === 2)
      assert(proposition2a.first === ind01)
      assert(proposition2a.second === ind04)
      assert(proposition2a.terms === List(ind01, ind04))

      assert(proposition2b.arity === 2)
      assert(proposition2b.first === ind02)
      assert(proposition2b.second === ind03)
      assert(proposition2b.terms === List(ind02, ind03))
    }
  }

  test("Proposition2: substitution") {
    new TestProposition2 {
      assert(proposition2a.substitute(subSet01) predEquals proposition2a)
      assert(proposition2b.substitute(subSet01) predEquals proposition2b)

      assert(proposition2a.substitute(subSet02) predEquals proposition2a)
      assert(proposition2b.substitute(subSet02) predEquals proposition2b)
    }
  }

  test("Proposition2: neg") {
    new TestProposition2 {
      assert(!(proposition2a.neg predEquals proposition2a))
      assert(!(proposition2b.neg predEquals proposition2b))

      assert(proposition2a.neg predEquals NegProposition(proposition2a))
      assert(proposition2b.neg predEquals NegProposition(proposition2b))

      assert(proposition2a.neg.neg predEquals proposition2a)
      assert(proposition2b.neg.neg predEquals proposition2b)
    }
  }

  test("Proposition2: neg and ==") {
    new TestProposition2 {
      assert(proposition2a.neg != proposition2a)
      assert(proposition2b.neg != proposition2b)

      assert(proposition2a.neg === NegProposition(proposition2a))
      assert(proposition2b.neg === NegProposition(proposition2b))

      assert(proposition2a.neg.neg === proposition2a)
      assert(proposition2b.neg.neg === proposition2b)
    }
  }
}
