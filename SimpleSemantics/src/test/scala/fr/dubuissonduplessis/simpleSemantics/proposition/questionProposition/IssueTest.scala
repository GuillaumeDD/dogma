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
class IssueSuite extends FunSuite {
  trait TestIssue {
    // Variables
    val X = Variable("X")
    val Y = Variable("Y")

    val q01 = WhQuestion("movie", X) // ?X.movie(X)
    val q02 = YNQuestion(Proposition1("have_a", Individual("beer"))) // ?have_a(beer)

    // ActionPred
    val issue01 = Issue(q01) // und(?X.movie(X))
    val issue02 = Issue(q02) // und(?have_a(beer))

    val subSet01 = SubstitutionSet() + Substitution(X, PredN("feur", List()))
    val subSet02 = SubstitutionSet() +
      Substitution(X, PredN("feur", List())) +
      Substitution(Y, PredN("quoi", List()))

  }

  test("Issue: name") {
    new TestIssue {
      assert(issue01.name === "issue")
      assert(issue02.name === "issue")
    }
  }

  test("Issue: equality") {
    new TestIssue {
      assert(issue01 predEquals issue01)
      assert(!(issue01 predEquals issue02))

      assert(!(issue02 predEquals issue01))
      assert(issue02 predEquals issue02)
    }
  }

  test("Issue: arity, question and terms") {
    new TestIssue {
      assert(issue01.arity === 1)
      assert(issue01.question === q01)
      assert(issue01.terms === List(q01))

      assert(issue02.arity === 1)
      assert(issue02.question === q02)
      assert(issue02.terms === List(q02))
    }
  }

  test("Issue: substitution") {
    new TestIssue {
      assert(issue01.substitute(subSet01) predEquals issue01)
      assert(issue02.substitute(subSet01) predEquals issue02)

      assert(issue01.substitute(subSet02) predEquals issue01)
      assert(issue02.substitute(subSet02) predEquals issue02)
    }
  }

  test("Issue: neg") {
    new TestIssue {
      assert(!(issue01.neg predEquals issue01))
      assert(!(issue02.neg predEquals issue02))

      assert(issue01.neg predEquals NegProposition(issue01))
      assert(issue02.neg predEquals NegProposition(issue02))

      assert(issue01.neg.neg predEquals issue01)
      assert(issue02.neg.neg predEquals issue02)
    }
  }
}
