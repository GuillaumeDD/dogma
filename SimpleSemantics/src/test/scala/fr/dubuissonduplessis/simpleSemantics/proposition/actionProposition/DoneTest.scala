/**
 * *****************************************************************************
 * Copyright (c) 2014 Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr> - initial API and implementation
 * ****************************************************************************
 */
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
package fr.dubuissonduplessis.simpleSemantics.proposition.actionProposition

import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import fr.dubuissonduplessis.predicateCalculus.Variable
import fr.dubuissonduplessis.predicateCalculus.Individual
import fr.dubuissonduplessis.predicateCalculus.substitution.SubstitutionSet
import fr.dubuissonduplessis.predicateCalculus.substitution.Substitution
import fr.dubuissonduplessis.predicateCalculus.PredN
import fr.dubuissonduplessis.simpleSemantics.proposition.NegProposition
import fr.dubuissonduplessis.simpleSemantics.action.Action0

@RunWith(classOf[JUnitRunner])
class DoneSuite extends FunSuite {
  trait TestDone {
    // Actions
    val action01 = Action0("a1")
    val action02 = Action0("a2")

    // ActionPred
    val done01 = Done(action01) // done(a1)
    val done02 = Done(action02) // done(a2)

    // Variables
    val X = Variable("X")
    val Y = Variable("Y")

    val subSet01 = SubstitutionSet() + Substitution(X, PredN("feur", List()))
    val subSet02 = SubstitutionSet() +
      Substitution(X, PredN("feur", List())) +
      Substitution(Y, PredN("quoi", List()))
  }

  test("Done: name") {
    new TestDone {
      assert(done01.name === "done")
      assert(done02.name === "done")
    }
  }

  test("Done: equality") {
    new TestDone {
      assert(done01 predEquals done01)
      assert(!(done01 predEquals done02))

      assert(!(done02 predEquals done01))
      assert(done02 predEquals done02)
    }
  }

  test("Done: arity, action and terms") {
    new TestDone {
      assert(done01.arity === 1)
      assert(done01.action === action01)
      assert(done01.terms === List(action01))

      assert(done02.arity === 1)
      assert(done02.action === action02)
      assert(done02.terms === List(action02))
    }
  }

  test("Done: substitution") {
    new TestDone {
      assert(done01.substitute(subSet01) predEquals done01)
      assert(done02.substitute(subSet01) predEquals done02)

      assert(done01.substitute(subSet02) predEquals done01)
      assert(done02.substitute(subSet02) predEquals done02)
    }
  }

  test("Done: neg") {
    new TestDone {
      assert(!(done01.neg predEquals done01))
      assert(!(done02.neg predEquals done02))

      assert(done01.neg predEquals NegProposition(done01))
      assert(done02.neg predEquals NegProposition(done02))

      assert(done01.neg.neg predEquals done01)
      assert(done02.neg.neg predEquals done02)
    }
  }
}
