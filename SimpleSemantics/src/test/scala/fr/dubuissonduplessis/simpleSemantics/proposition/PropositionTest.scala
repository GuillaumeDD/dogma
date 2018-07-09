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
class PropositionSuite extends FunSuite {
  trait TestProposition {
    val proposition0a = Proposition0("going") // going
    val proposition0b = Proposition0("boing") // boing

    val ind01 = Individual("west")
    val ind02 = Individual("north")

    val proposition1a = Proposition1("go", ind01) // go(west)
    val proposition1b = Proposition1("bo", ind02) // bo(north)

    val X = Variable("X")
    val Y = Variable("Y")

    val subSet01 = SubstitutionSet() + Substitution(X, Proposition0("feur"))
    val subSet02 = SubstitutionSet() +
      Substitution(X, Proposition0("feur")) +
      Substitution(Y, Proposition0("quoi"))

  }

  test("Proposition0: name") {
    new TestProposition {
      assert(proposition0a.name === "going")
      assert(proposition0b.name === "boing")
    }
  }

  test("Proposition0: equality") {
    new TestProposition {
      assert(proposition0a predEquals proposition0a)
      assert(!(proposition0a predEquals proposition0b))

      assert(!(proposition0b predEquals proposition0a))
      assert(proposition0b predEquals proposition0b)
    }
  }

  test("Proposition0: arity and terms") {
    new TestProposition {
      assert(proposition0a.arity === 0)
      assert(proposition0a.terms === List())

      assert(proposition0b.arity === 0)
      assert(proposition0b.terms === List())
    }
  }

  test("Proposition0: substitution") {
    new TestProposition {
      assert(proposition0a.substitute(subSet01) predEquals proposition0a)
      assert(proposition0b.substitute(subSet01) predEquals proposition0b)

      assert(proposition0a.substitute(subSet02) predEquals proposition0a)
      assert(proposition0b.substitute(subSet02) predEquals proposition0b)
    }
  }

  test("Proposition0: neg") {
    new TestProposition {
      assert(!(proposition0a.neg predEquals proposition0a))
      assert(!(proposition0b.neg predEquals proposition0b))

      assert(proposition0a.neg predEquals NegProposition(proposition0a))
      assert(proposition0b.neg predEquals NegProposition(proposition0b))

      assert(proposition0a.neg.neg predEquals proposition0a)
      assert(proposition0b.neg.neg predEquals proposition0b)
    }
  }

  test("Proposition1: name") {
    new TestProposition {
      assert(proposition1a.name === "go")
      assert(proposition1b.name === "bo")
    }
  }

  test("Proposition1: equality") {
    new TestProposition {
      assert(proposition1a predEquals proposition1a)
      assert(!(proposition1a predEquals proposition1b))

      assert(!(proposition1b predEquals proposition1a))
      assert(proposition1b predEquals proposition1b)
    }
  }

  test("Proposition1: arity, arg and terms") {
    new TestProposition {
      assert(proposition1a.arity === 1)
      assert(proposition1a.arg === ind01)
      assert(proposition1a.terms === List(ind01))

      assert(proposition1b.arity === 1)
      assert(proposition1b.arg === ind02)
      assert(proposition1b.terms === List(ind02))
    }
  }

  test("Proposition1: substitution") {
    new TestProposition {
      assert(proposition1a.substitute(subSet01) predEquals proposition1a)
      assert(proposition1b.substitute(subSet01) predEquals proposition1b)

      assert(proposition1a.substitute(subSet02) predEquals proposition1a)
      assert(proposition1b.substitute(subSet02) predEquals proposition1b)
    }
  }

  test("Proposition1: neg") {
    new TestProposition {
      assert(!(proposition1a.neg predEquals proposition1a))
      assert(!(proposition1b.neg predEquals proposition1b))

      assert(proposition1a.neg predEquals NegProposition(proposition1a))
      assert(proposition1b.neg predEquals NegProposition(proposition1b))

      assert(proposition1a.neg.neg predEquals proposition1a)
      assert(proposition1b.neg.neg predEquals proposition1b)
    }
  }
}
