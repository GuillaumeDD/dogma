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
package fr.dubuissonduplessis.simpleSemantics.proposition.questionProposition

import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import fr.dubuissonduplessis.predicateCalculus.Variable
import fr.dubuissonduplessis.predicateCalculus.Individual
import fr.dubuissonduplessis.predicateCalculus.substitution.SubstitutionSet
import fr.dubuissonduplessis.predicateCalculus.substitution.Substitution
import fr.dubuissonduplessis.simpleSemantics.question.WhQuestion
import fr.dubuissonduplessis.simpleSemantics.question.YNQuestion
import fr.dubuissonduplessis.simpleSemantics.proposition.Proposition1
import fr.dubuissonduplessis.predicateCalculus.PredN
import fr.dubuissonduplessis.simpleSemantics.proposition.NegProposition

@RunWith(classOf[JUnitRunner])
class FailSuite extends FunSuite {
  trait TestFail {
    // Variables
    val X = Variable("X")
    val Y = Variable("Y")

    val q01 = WhQuestion("movie", X) // ?X.movie(X)
    val q02 = YNQuestion(Proposition1("have_a", Individual("beer"))) // ?have_a(beer)

    // ActionPred
    val fail01 = Fail(q01) // fail(?X.movie(X))
    val fail02 = Fail(q02) // fail(?have_a(beer))

    val subSet01 = SubstitutionSet() + Substitution(X, PredN("feur", List()))
    val subSet02 = SubstitutionSet() +
      Substitution(X, PredN("feur", List())) +
      Substitution(Y, PredN("quoi", List()))

  }

  test("Fail: name") {
    new TestFail {
      assert(fail01.name === "fail")
      assert(fail02.name === "fail")
    }
  }

  test("Fail: equality") {
    new TestFail {
      assert(fail01 predEquals fail01)
      assert(!(fail01 predEquals fail02))

      assert(!(fail02 predEquals fail01))
      assert(fail02 predEquals fail02)
    }
  }

  test("Fail: arity, question and terms") {
    new TestFail {
      assert(fail01.arity === 1)
      assert(fail01.question === q01)
      assert(fail01.terms === List(q01))

      assert(fail02.arity === 1)
      assert(fail02.question === q02)
      assert(fail02.terms === List(q02))
    }
  }

  test("Fail: substitution") {
    new TestFail {
      assert(fail01.substitute(subSet01) predEquals fail01)
      assert(fail02.substitute(subSet01) predEquals fail02)

      assert(fail01.substitute(subSet02) predEquals fail01)
      assert(fail02.substitute(subSet02) predEquals fail02)
    }
  }

  test("Fail: neg") {
    new TestFail {
      assert(!(fail01.neg predEquals fail01))
      assert(!(fail02.neg predEquals fail02))

      assert(fail01.neg predEquals NegProposition(fail01))
      assert(fail02.neg predEquals NegProposition(fail02))

      assert(fail01.neg.neg predEquals fail01)
      assert(fail02.neg.neg predEquals fail02)
    }
  }
}
