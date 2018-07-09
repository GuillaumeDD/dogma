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
class QuestionSuite extends FunSuite {
  trait TestQuestion {
    // Variables
    val X = Variable("X")
    val Y = Variable("Y")

    // Propositions
    val prop0a = Proposition0("rocks") // rocks
    val prop0b = Proposition0("rains") // rains
    val prop0c = Proposition0("sunny") // sunny

    val prop1a = Proposition1("synonym", Individual("paludism")) // synonym(paludism)
    val prop1b = Proposition1("definition", Individual("paludism")) // definition(paludism)
    val prop1c = Proposition1("synonym", Individual("therapeutic")) // synonym(therapeutic)

    // Questions
    val whquestion01 = WhQuestion("dancing", X) // ?X.dancing(X)
    val whquestion02 = WhQuestion("synonym", X) // ?X.synonym(X)
    val whquestion03 = WhQuestion("definition", X) // ?X.definition(X)

    val ynquestion01 = YNQuestion(prop0a) // ?rocks
    val ynquestion02 = YNQuestion(prop0b) // ?rains
    val ynquestion03 = YNQuestion(prop0c) // ?sunny
    val ynquestion04 = YNQuestion(prop1a) // ?synonym(paludism)
    val ynquestion05 = YNQuestion(prop1b) // ?definition(paludism)
    val ynquestion06 = YNQuestion(prop1c) // ?synonym(therapeutic)

    val altquestion01 = AltQuestion(
      List(ynquestion04, ynquestion05, ynquestion06)) // {?synonym(paludism), ?definition(paludism), ?synonym(therapeutic)}
    val altquestion02 = AltQuestion(
      List(ynquestion01, ynquestion02, ynquestion03)) // {?rocks, ?rains, ?sunny}
    val altquestion03 = AltQuestion(
      List(ynquestion06, ynquestion02, ynquestion04)) // {?synonym(therapeutic), ?rains, ?synonym(paludism)}
  }

  test("Questions: inter-question (pred-)equality") {
    new TestQuestion {
      // YNQuestion
      assert(!(ynquestion01 predEquals whquestion01))
      assert(!(ynquestion01 predEquals whquestion02))
      assert(!(ynquestion01 predEquals whquestion03))
      assert(!(ynquestion01 predEquals altquestion01))
      assert(!(ynquestion01 predEquals altquestion02))
      assert(!(ynquestion01 predEquals altquestion03))

      assert(!(ynquestion02 predEquals whquestion01))
      assert(!(ynquestion02 predEquals whquestion02))
      assert(!(ynquestion02 predEquals whquestion03))
      assert(!(ynquestion02 predEquals altquestion01))
      assert(!(ynquestion02 predEquals altquestion02))
      assert(!(ynquestion02 predEquals altquestion03))

      assert(!(ynquestion03 predEquals whquestion01))
      assert(!(ynquestion03 predEquals whquestion02))
      assert(!(ynquestion03 predEquals whquestion03))
      assert(!(ynquestion03 predEquals altquestion01))
      assert(!(ynquestion03 predEquals altquestion02))
      assert(!(ynquestion03 predEquals altquestion03))

      assert(!(ynquestion04 predEquals whquestion01))
      assert(!(ynquestion04 predEquals whquestion02))
      assert(!(ynquestion04 predEquals whquestion03))
      assert(!(ynquestion04 predEquals altquestion01))
      assert(!(ynquestion04 predEquals altquestion02))
      assert(!(ynquestion04 predEquals altquestion03))

      assert(!(ynquestion05 predEquals whquestion01))
      assert(!(ynquestion05 predEquals whquestion02))
      assert(!(ynquestion05 predEquals whquestion03))
      assert(!(ynquestion05 predEquals altquestion01))
      assert(!(ynquestion05 predEquals altquestion02))
      assert(!(ynquestion05 predEquals altquestion03))

      assert(!(ynquestion06 predEquals whquestion01))
      assert(!(ynquestion06 predEquals whquestion02))
      assert(!(ynquestion06 predEquals whquestion03))
      assert(!(ynquestion06 predEquals altquestion01))
      assert(!(ynquestion06 predEquals altquestion02))
      assert(!(ynquestion06 predEquals altquestion03))

      // Wh-question
      assert(!(whquestion01 predEquals ynquestion01))
      assert(!(whquestion01 predEquals ynquestion02))
      assert(!(whquestion01 predEquals ynquestion03))
      assert(!(whquestion01 predEquals ynquestion04))
      assert(!(whquestion01 predEquals ynquestion05))
      assert(!(whquestion01 predEquals ynquestion06))
      assert(!(whquestion01 predEquals altquestion01))
      assert(!(whquestion01 predEquals altquestion02))
      assert(!(whquestion01 predEquals altquestion03))

      assert(!(whquestion02 predEquals ynquestion01))
      assert(!(whquestion02 predEquals ynquestion02))
      assert(!(whquestion02 predEquals ynquestion03))
      assert(!(whquestion02 predEquals ynquestion04))
      assert(!(whquestion02 predEquals ynquestion05))
      assert(!(whquestion02 predEquals ynquestion06))
      assert(!(whquestion02 predEquals altquestion01))
      assert(!(whquestion02 predEquals altquestion02))
      assert(!(whquestion02 predEquals altquestion03))

      assert(!(whquestion03 predEquals ynquestion01))
      assert(!(whquestion03 predEquals ynquestion02))
      assert(!(whquestion03 predEquals ynquestion03))
      assert(!(whquestion03 predEquals ynquestion04))
      assert(!(whquestion03 predEquals ynquestion05))
      assert(!(whquestion03 predEquals ynquestion06))
      assert(!(whquestion03 predEquals altquestion01))
      assert(!(whquestion03 predEquals altquestion02))
      assert(!(whquestion03 predEquals altquestion03))

      // AltQ
      assert(!(altquestion01 predEquals whquestion01))
      assert(!(altquestion01 predEquals whquestion02))
      assert(!(altquestion01 predEquals whquestion03))
      assert(!(altquestion01 predEquals ynquestion01))
      assert(!(altquestion01 predEquals ynquestion02))
      assert(!(altquestion01 predEquals ynquestion03))
      assert(!(altquestion01 predEquals ynquestion04))
      assert(!(altquestion01 predEquals ynquestion05))
      assert(!(altquestion01 predEquals ynquestion06))

      assert(!(altquestion02 predEquals whquestion01))
      assert(!(altquestion02 predEquals whquestion02))
      assert(!(altquestion02 predEquals whquestion03))
      assert(!(altquestion02 predEquals ynquestion01))
      assert(!(altquestion02 predEquals ynquestion02))
      assert(!(altquestion02 predEquals ynquestion03))
      assert(!(altquestion02 predEquals ynquestion04))
      assert(!(altquestion02 predEquals ynquestion05))
      assert(!(altquestion02 predEquals ynquestion06))

      assert(!(altquestion03 predEquals whquestion01))
      assert(!(altquestion03 predEquals whquestion02))
      assert(!(altquestion03 predEquals whquestion03))
      assert(!(altquestion03 predEquals ynquestion01))
      assert(!(altquestion03 predEquals ynquestion02))
      assert(!(altquestion03 predEquals ynquestion03))
      assert(!(altquestion03 predEquals ynquestion04))
      assert(!(altquestion03 predEquals ynquestion05))
      assert(!(altquestion03 predEquals ynquestion06))
    }
  }

  test("AltQuestion: equality") {
    new TestQuestion {
      assert(altquestion01 predEquals altquestion01)
      assert(!(altquestion01 predEquals altquestion02))
      assert(!(altquestion01 predEquals altquestion03))

      assert(!(altquestion02 predEquals altquestion01))
      assert(altquestion02 predEquals altquestion02)
      assert(!(altquestion02 predEquals altquestion03))

      assert(!(altquestion03 predEquals altquestion01))
      assert(!(altquestion03 predEquals altquestion02))
      assert(altquestion03 predEquals altquestion03)

    }
  }

  test("AltQuestion: terms and arity") {
    new TestQuestion {
      // Arity
      assert(altquestion01.arity === 3)
      assert(altquestion02.arity === 3)
      assert(altquestion03.arity === 3)

      assert(altquestion01.terms === List(ynquestion04, ynquestion05, ynquestion06))
      assert(altquestion01.alternatives === List(ynquestion04, ynquestion05, ynquestion06))

      assert(altquestion02.terms === List(ynquestion01, ynquestion02, ynquestion03))
      assert(altquestion02.alternatives === List(ynquestion01, ynquestion02, ynquestion03))

      assert(altquestion03.terms === List(ynquestion06, ynquestion02, ynquestion04))
      assert(altquestion03.alternatives === List(ynquestion06, ynquestion02, ynquestion04))
    }
  }

  test("AltQuestion: substitution") {
    new TestQuestion {
      val substitutionset01 = SubstitutionSet()
      val substitutionset02 = SubstitutionSet() +
        Substitution(X, Individual("test"))
      val substitutionset03 = SubstitutionSet() +
        Substitution(Y, Individual("test"))

      assert(altquestion01.substitute(substitutionset01) === altquestion01)
      assert(altquestion02.substitute(substitutionset01) === altquestion02)
      assert(altquestion03.substitute(substitutionset01) === altquestion03)

      assert(altquestion01.substitute(substitutionset02) === altquestion01)
      assert(altquestion02.substitute(substitutionset02) === altquestion02)
      assert(altquestion03.substitute(substitutionset02) === altquestion03)

      assert(altquestion01.substitute(substitutionset03) === altquestion01)
      assert(altquestion02.substitute(substitutionset03) === altquestion02)
      assert(altquestion03.substitute(substitutionset03) === altquestion03)
    }
  }

  test("YNQuestion: equality") {
    new TestQuestion {
      assert(ynquestion01 predEquals ynquestion01)
      assert(!(ynquestion01 predEquals ynquestion02))
      assert(!(ynquestion01 predEquals ynquestion03))
      assert(!(ynquestion01 predEquals ynquestion04))
      assert(!(ynquestion01 predEquals ynquestion05))
      assert(!(ynquestion01 predEquals ynquestion06))

      assert(!(ynquestion02 predEquals ynquestion01))
      assert(ynquestion02 predEquals ynquestion02)
      assert(!(ynquestion02 predEquals ynquestion03))
      assert(!(ynquestion02 predEquals ynquestion04))
      assert(!(ynquestion02 predEquals ynquestion05))
      assert(!(ynquestion02 predEquals ynquestion06))

      assert(!(ynquestion03 predEquals ynquestion01))
      assert(!(ynquestion03 predEquals ynquestion02))
      assert(ynquestion03 predEquals ynquestion03)
      assert(!(ynquestion03 predEquals ynquestion04))
      assert(!(ynquestion03 predEquals ynquestion05))
      assert(!(ynquestion03 predEquals ynquestion06))

      assert(!(ynquestion04 predEquals ynquestion01))
      assert(!(ynquestion04 predEquals ynquestion02))
      assert(!(ynquestion04 predEquals ynquestion03))
      assert(ynquestion04 predEquals ynquestion04)
      assert(!(ynquestion04 predEquals ynquestion05))
      assert(!(ynquestion04 predEquals ynquestion06))

      assert(!(ynquestion05 predEquals ynquestion01))
      assert(!(ynquestion05 predEquals ynquestion02))
      assert(!(ynquestion05 predEquals ynquestion03))
      assert(!(ynquestion05 predEquals ynquestion04))
      assert(ynquestion05 predEquals ynquestion05)
      assert(!(ynquestion05 predEquals ynquestion06))

      assert(!(ynquestion06 predEquals ynquestion01))
      assert(!(ynquestion06 predEquals ynquestion02))
      assert(!(ynquestion06 predEquals ynquestion03))
      assert(!(ynquestion06 predEquals ynquestion04))
      assert(!(ynquestion06 predEquals ynquestion05))
      assert(ynquestion06 predEquals ynquestion06)
    }
  }

  test("YNQuestion: terms and arity") {
    new TestQuestion {
      // Arity
      assert(ynquestion01.arity === 1)
      assert(ynquestion02.arity === 1)
      assert(ynquestion03.arity === 1)
      assert(ynquestion04.arity === 1)
      assert(ynquestion05.arity === 1)
      assert(ynquestion06.arity === 1)

      // Terms
      assert(ynquestion01.terms === List(ynquestion01.prop))
      assert(ynquestion02.terms === List(ynquestion02.prop))
      assert(ynquestion03.terms === List(ynquestion03.prop))
      assert(ynquestion04.terms === List(ynquestion04.prop))
      assert(ynquestion05.terms === List(ynquestion05.prop))
      assert(ynquestion06.terms === List(ynquestion06.prop))
    }
  }
  test("YNQuestion: substitution") {
    new TestQuestion {
      val substitutionset01 = SubstitutionSet()
      val substitutionset02 = SubstitutionSet() +
        Substitution(X, Individual("test"))
      val substitutionset03 = SubstitutionSet() +
        Substitution(Y, Individual("test"))

      assert(ynquestion01.substitute(substitutionset01) === ynquestion01)
      assert(ynquestion02.substitute(substitutionset01) === ynquestion02)
      assert(ynquestion03.substitute(substitutionset01) === ynquestion03)
      assert(ynquestion04.substitute(substitutionset01) === ynquestion04)
      assert(ynquestion05.substitute(substitutionset01) === ynquestion05)
      assert(ynquestion06.substitute(substitutionset01) === ynquestion06)

      assert(ynquestion01.substitute(substitutionset02) === ynquestion01)
      assert(ynquestion02.substitute(substitutionset02) === ynquestion02)
      assert(ynquestion03.substitute(substitutionset02) === ynquestion03)
      assert(ynquestion04.substitute(substitutionset02) === ynquestion04)
      assert(ynquestion05.substitute(substitutionset02) === ynquestion05)
      assert(ynquestion06.substitute(substitutionset02) === ynquestion06)

      assert(ynquestion01.substitute(substitutionset03) === ynquestion01)
      assert(ynquestion02.substitute(substitutionset03) === ynquestion02)
      assert(ynquestion03.substitute(substitutionset03) === ynquestion03)
      assert(ynquestion04.substitute(substitutionset03) === ynquestion04)
      assert(ynquestion05.substitute(substitutionset03) === ynquestion05)
      assert(ynquestion06.substitute(substitutionset03) === ynquestion06)
    }
  }

  test("WhQuestion: equality") {
    new TestQuestion {
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

  test("WhQuestion: terms and arity") {
    new TestQuestion {
      assert(whquestion01.arity === 1)
      assert(whquestion02.arity === 1)
      assert(whquestion03.arity === 1)

      assert(whquestion01.terms.size === 1)
      assert(whquestion02.terms.size === 1)
      assert(whquestion03.terms.size === 1)

      assert(whquestion01.terms.head === X)
      assert(whquestion02.terms.head === X)
      assert(whquestion03.terms.head === X)
    }
  }

  test("WhQuestion: substitution") {
    new TestQuestion {
      val substitutionset01 = SubstitutionSet()
      val substitutionset02 = SubstitutionSet() +
        Substitution(X, Individual("test"))
      val substitutionset03 = SubstitutionSet() +
        Substitution(Y, Individual("test"))

      assert(whquestion01.substitute(substitutionset01) ===
        whquestion01)
      assert(whquestion01.substitute(substitutionset02) ===
        whquestion01)
      assert(whquestion01.substitute(substitutionset03) ===
        whquestion01)

      assert(whquestion02.substitute(substitutionset01) ===
        whquestion02)
      assert(whquestion02.substitute(substitutionset02) ===
        whquestion02)
      assert(whquestion02.substitute(substitutionset03) ===
        whquestion02)

      assert(whquestion03.substitute(substitutionset01) ===
        whquestion03)
      assert(whquestion03.substitute(substitutionset02) ===
        whquestion03)
      assert(whquestion03.substitute(substitutionset03) ===
        whquestion03)
    }
  }
}
