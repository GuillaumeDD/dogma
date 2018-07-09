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
class ParametrizedWhQuestion2Suite extends FunSuite {
  trait TestParametrizedWhQuestion2 {
    // Variables
    val X = Variable("X")
    val Y = Variable("Y")

    // Individual
    val paludism = Individual("paludism") // paludism
    val aids = Individual("aids") // aids
    val therapeutic = Individual("therapeutic") // therapeutic

    // Questions
    val whquestion01 = ParametrizedWhQuestion2(
      "synonym",
      paludism,
      X) // ?X.synonym(paludism, X)
    val whquestion02 = ParametrizedWhQuestion2(
      "synonym",
      aids,
      X) // ?X.synonym(aids, X)
    val whquestion03 = ParametrizedWhQuestion2(
      "hypernym",
      therapeutic,
      X) // ?X.therapeutic(hypernym, X)

    val substitutionset01 = SubstitutionSet()
    val substitutionset02 = SubstitutionSet() +
      Substitution(X, Individual("test"))
    val substitutionset03 = SubstitutionSet() +
      Substitution(Y, Individual("test"))
  }

  test("ParametrizedWhQuestion2: equality") {
    new TestParametrizedWhQuestion2 {
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

  test("ParametrizedWhQuestion2: terms and arity") {
    new TestParametrizedWhQuestion2 {
      assert(whquestion01.arity === 2)
      assert(whquestion01.terms === List(paludism, X))

      assert(whquestion02.arity === 2)
      assert(whquestion02.terms === List(aids, X))

      assert(whquestion03.arity === 2)
      assert(whquestion03.terms === List(therapeutic, X))
    }
  }

  test("ParametrizedWhQuestion2: substitution") {
    new TestParametrizedWhQuestion2 {
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
