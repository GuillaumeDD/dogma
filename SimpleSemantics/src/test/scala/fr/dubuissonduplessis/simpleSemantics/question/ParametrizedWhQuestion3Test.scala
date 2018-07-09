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
package fr.dubuissonduplessis.simpleSemantics.question

import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import fr.dubuissonduplessis.predicateCalculus.Variable
import fr.dubuissonduplessis.simpleSemantics.proposition.Proposition1
import fr.dubuissonduplessis.predicateCalculus.Individual
import fr.dubuissonduplessis.simpleSemantics.proposition.Proposition0
import fr.dubuissonduplessis.predicateCalculus.substitution.SubstitutionSet
import fr.dubuissonduplessis.predicateCalculus.substitution.Substitution

@RunWith(classOf[JUnitRunner])
class ParametrizedWhQuestion3Suite extends FunSuite {
  trait TestParametrizedWhQuestion3 {
    // Variables
    val X = Variable("X")
    val Y = Variable("Y")

    // Individual
    val paludism = Individual("paludism") // paludism
    val aids = Individual("aids") // aids
    val therapeutic = Individual("therapeutic") // therapeutic

    val french = Individual("french")
    val english = Individual("english")

    // Questions
    val whquestion01 = ParametrizedWhQuestion3(
      "definition",
      paludism,
      french,
      X) // ?X.definition(paludism, french, X)
    val whquestion02 = ParametrizedWhQuestion3(
      "definition",
      aids,
      english,
      X) // ?X.definition(aids, english, X)
    val whquestion03 = ParametrizedWhQuestion3(
      "definition",
      therapeutic,
      french,
      X) // ?X.definition(therapeutic, french, X)

    val substitutionset01 = SubstitutionSet()
    val substitutionset02 = SubstitutionSet() +
      Substitution(X, Individual("test"))
    val substitutionset03 = SubstitutionSet() +
      Substitution(Y, Individual("test"))
  }

  test("ParametrizedWhQuestion3: equality") {
    new TestParametrizedWhQuestion3 {
      assert(whquestion01 predEquals whquestion01)
      assert(!(whquestion01 predEquals whquestion02))
      assert(!(whquestion01 predEquals whquestion03))

      assert(!(whquestion02 predEquals whquestion01))
      assert(whquestion02 predEquals whquestion02)
      assert(!(whquestion02 predEquals whquestion03))

      assert(!(whquestion03 predEquals whquestion01))
      assert(!(whquestion03 predEquals whquestion02))
      assert(whquestion03 predEquals whquestion03)
    }
  }

  test("ParametrizedWhQuestion3: terms and arity") {
    new TestParametrizedWhQuestion3 {
      assert(whquestion01.arity === 3)
      assert(whquestion01.terms === List(paludism, french, X))

      assert(whquestion02.arity === 3)
      assert(whquestion02.terms === List(aids, english, X))

      assert(whquestion03.arity === 3)
      assert(whquestion03.terms === List(therapeutic, french, X))
    }
  }

  test("ParametrizedWhQuestion3: substitution") {
    new TestParametrizedWhQuestion3 {
      assert(whquestion01.substitute(substitutionset01) predEquals whquestion01)
      assert(whquestion02.substitute(substitutionset01) predEquals whquestion02)
      assert(whquestion03.substitute(substitutionset01) predEquals whquestion03)

      assert(whquestion01.substitute(substitutionset02) predEquals whquestion01)
      assert(whquestion02.substitute(substitutionset02) predEquals whquestion02)
      assert(whquestion03.substitute(substitutionset02) predEquals whquestion03)

      assert(whquestion01.substitute(substitutionset03) predEquals whquestion01)
      assert(whquestion02.substitute(substitutionset03) predEquals whquestion02)
      assert(whquestion03.substitute(substitutionset03) predEquals whquestion03)
    }
  }
}
