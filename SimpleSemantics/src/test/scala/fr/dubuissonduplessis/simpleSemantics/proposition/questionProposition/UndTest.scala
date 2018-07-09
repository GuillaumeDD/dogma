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
class UnfSuite extends FunSuite {
  trait TestUnd {
    // Variables
    val X = Variable("X")
    val Y = Variable("Y")

    val q01 = WhQuestion("movie", X) // ?X.movie(X)
    val q02 = YNQuestion(Proposition1("have_a", Individual("beer"))) // ?have_a(beer)

    // ActionPred
    val und01 = Und(q01) // und(?X.movie(X))
    val und02 = Und(q02) // und(?have_a(beer))

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

  test("Und: arity, question and terms") {
    new TestUnd {
      assert(und01.arity === 1)
      assert(und01.question === q01)
      assert(und01.terms === List(q01))

      assert(und02.arity === 1)
      assert(und02.question === q02)
      assert(und02.terms === List(q02))
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
