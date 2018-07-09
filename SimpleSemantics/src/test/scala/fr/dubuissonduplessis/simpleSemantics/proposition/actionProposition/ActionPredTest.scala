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
class ActionPredSuite extends FunSuite {
  trait TestActionPred {
    // Actions
    val action01 = Action0("a1")
    val action02 = Action0("a2")

    // ActionPred
    val predAction01 = ActionPred(action01) // action(a1)
    val predAction02 = ActionPred(action02) // action(a2)

    // Variables
    val X = Variable("X")
    val Y = Variable("Y")

    val subSet01 = SubstitutionSet() + Substitution(X, PredN("feur", List()))
    val subSet02 = SubstitutionSet() +
      Substitution(X, PredN("feur", List())) +
      Substitution(Y, PredN("quoi", List()))
  }

  test("ActionPred: name") {
    new TestActionPred {
      assert(predAction01.name === "action")
      assert(predAction02.name === "action")
    }
  }

  test("ActionPred: equality") {
    new TestActionPred {
      assert(predAction01 predEquals predAction01)
      assert(!(predAction01 predEquals predAction02))

      assert(!(predAction02 predEquals predAction01))
      assert(predAction02 predEquals predAction02)
    }
  }

  test("ActionPred: arity, action and terms") {
    new TestActionPred {
      assert(predAction01.arity === 1)
      assert(predAction01.action === action01)
      assert(predAction01.terms === List(action01))

      assert(predAction02.arity === 1)
      assert(predAction02.action === action02)
      assert(predAction02.terms === List(action02))
    }
  }

  test("ActionPred: substitution") {
    new TestActionPred {
      assert(predAction01.substitute(subSet01) predEquals predAction01)
      assert(predAction02.substitute(subSet01) predEquals predAction02)

      assert(predAction01.substitute(subSet02) predEquals predAction01)
      assert(predAction02.substitute(subSet02) predEquals predAction02)
    }
  }

  test("ActionPred: neg") {
    new TestActionPred {
      assert(!(predAction01.neg predEquals predAction01))
      assert(!(predAction02.neg predEquals predAction02))

      assert(predAction01.neg predEquals NegProposition(predAction01))
      assert(predAction02.neg predEquals NegProposition(predAction02))

      assert(predAction01.neg.neg predEquals predAction01)
      assert(predAction02.neg.neg predEquals predAction02)
    }
  }
}
