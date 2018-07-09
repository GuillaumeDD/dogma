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
package fr.dubuissonduplessis.simpleSemantics.proposition

import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import fr.dubuissonduplessis.predicateCalculus.Variable
import fr.dubuissonduplessis.predicateCalculus.Individual
import fr.dubuissonduplessis.predicateCalculus.substitution.SubstitutionSet
import fr.dubuissonduplessis.predicateCalculus.substitution.Substitution
import fr.dubuissonduplessis.simpleSemantics.question.WhQuestion
import fr.dubuissonduplessis.simpleSemantics.question.YNQuestion
import fr.dubuissonduplessis.predicateCalculus._
import fr.dubuissonduplessis.simpleSemantics.proposition.questionProposition.Issue

@RunWith(classOf[JUnitRunner])
class UndSuite extends FunSuite {
  trait TestUnd {
    // Variables
    val X = Variable("X")
    val Y = Variable("Y")

    val dp01 = "X"
    val dp02 = "Y"

    val q01 = Issue(WhQuestion("movie", X)) // ?X.movie(X)
    val q02 = Issue(YNQuestion(Proposition1("have_a", Individual("beer")))) // ?have_a(beer)

    // ActionPred
    val und01 = Und(dp01, q01) // und(interlocutor.X, ?X.movie(X))
    val und02 = Und(dp02, q02) // und(interlocutor.X, ?have_a(beer))

    val subSet01 = SubstitutionSet() + Substitution(X, PredN("feur", List()))
    val subSet02 = SubstitutionSet() +
      Substitution(X, PredN("feur", List())) +
      Substitution(Y, PredN("quoi", List()))
  }

  test("Und: name") {
    new TestUnd {
      assert(und01.name === "und")
      assert(und02.name === "und")
    }
  }

  test("Und: equality") {
    new TestUnd {
      assert(und01 predEquals und01)
      assert(!(und01 predEquals und02))

      assert(!(und02 predEquals und01))
      assert(und02 predEquals und02)
    }
  }

  test("Und: arity, interlocutor, question and terms") {
    new TestUnd {
      assert(und01.arity === 2)
      assert(und01.dp === dp01)
      assert(und01.prop === q01)
      assert(und01.terms === List[AnyUnifiable](Individual(dp01), q01))

      assert(und02.arity === 2)
      assert(und02.dp === dp02)
      assert(und02.prop === q02)
      assert(und02.terms === List[AnyUnifiable](Individual(dp02), q02))
    }
  }

  test("Und: substitution") {
    new TestUnd {
      assert(und01.substitute(subSet01) predEquals und01)
      assert(und02.substitute(subSet01) predEquals und02)

      assert(und01.substitute(subSet02) predEquals und01)
      assert(und02.substitute(subSet02) predEquals und02)
    }
  }

  test("Und: neg") {
    new TestUnd {
      assert(!(und01.neg predEquals und01))
      assert(!(und02.neg predEquals und02))

      assert(und01.neg predEquals NegProposition(und01))
      assert(und02.neg predEquals NegProposition(und02))

      assert(und01.neg.neg predEquals und01)
      assert(und02.neg.neg predEquals und02)
    }
  }
}
