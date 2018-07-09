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
package fr.dubuissonduplessis.simpleSemantics.relations

import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import fr.dubuissonduplessis.predicateCalculus.Variable
import fr.dubuissonduplessis.simpleSemantics.proposition.Proposition1
import fr.dubuissonduplessis.predicateCalculus.Individual
import fr.dubuissonduplessis.simpleSemantics.proposition.Proposition0
import fr.dubuissonduplessis.predicateCalculus.substitution.SubstitutionSet
import fr.dubuissonduplessis.predicateCalculus.substitution.Substitution
import fr.dubuissonduplessis.simpleSemantics.question.Question
import fr.dubuissonduplessis.simpleSemantics.question.WhQuestion
import fr.dubuissonduplessis.simpleSemantics.question.ParametrizedWhQuestion2
import fr.dubuissonduplessis.simpleSemantics.question.ParametrizedWhQuestion3
import fr.dubuissonduplessis.simpleSemantics.question.YNQuestion
import fr.dubuissonduplessis.simpleSemantics.question.AltQuestion
import fr.dubuissonduplessis.simpleSemantics.shortAnswer.ShortAns
import fr.dubuissonduplessis.simpleSemantics.shortAnswer.Yes
import fr.dubuissonduplessis.simpleSemantics.shortAnswer.No

@RunWith(classOf[JUnitRunner])
class ResolvesSuite extends FunSuite {
  trait TestResolves {
    implicit val semCompatible = (ind: Individual, q: Question) => true

    // Variables
    val X = Variable("X")
    val Y = Variable("Y")

    // Individual
    val paludism = Individual("paludism") // paludism
    val aids = Individual("aids") // aids
    val therapeutic = Individual("therapeutic") // therapeutic

    val french = Individual("french")
    val english = Individual("english")

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

    val whquestion2_01 = ParametrizedWhQuestion2(
      "synonym",
      paludism,
      X) // ?X.synonym(paludism, X)
    val whquestion2_02 = ParametrizedWhQuestion2(
      "synonym",
      aids,
      X) // ?X.synonym(aids, X)
    val whquestion2_03 = ParametrizedWhQuestion2(
      "hypernym",
      therapeutic,
      X) // ?X.therapeutic(hypernym, X)

    val whquestion3_01 = ParametrizedWhQuestion3(
      "definition",
      paludism,
      french,
      X) // ?X.definition(paludism, french, X)
    val whquestion3_02 = ParametrizedWhQuestion3(
      "definition",
      aids,
      english,
      X) // ?X.definition(aids, english, X)
    val whquestion3_03 = ParametrizedWhQuestion3(
      "definition",
      therapeutic,
      french,
      X) // ?X.definition(therapeutic, french, X)      

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

  test("Resolves: AltQuestion") {
    new TestResolves {
      //altquestion01
      assert(!Resolves(Yes, altquestion01))
      assert(!Resolves(No, altquestion01))
      assert(!Resolves(ShortAns(paludism), altquestion01))
      assert(!Resolves(ShortAns(aids), altquestion01))
      assert(!Resolves(ShortAns(therapeutic), altquestion01))
      assert(!Resolves(ShortAns(french), altquestion01))
      assert(!Resolves(ShortAns(english), altquestion01))
      assert(!Resolves(ShortAns(paludism).neg, altquestion01))
      assert(!Resolves(ShortAns(aids).neg, altquestion01))
      assert(!Resolves(ShortAns(therapeutic).neg, altquestion01))
      assert(!Resolves(ShortAns(french).neg, altquestion01))
      assert(!Resolves(ShortAns(english).neg, altquestion01))
      assert(!Resolves(prop0a, altquestion01))
      assert(!Resolves(prop0b, altquestion01))
      assert(!Resolves(prop0c, altquestion01))
      assert(Resolves(prop1a, altquestion01))
      assert(Resolves(prop1b, altquestion01))
      assert(Resolves(prop1c, altquestion01))
      assert(!Resolves(prop0a.neg, altquestion01))
      assert(!Resolves(prop0b.neg, altquestion01))
      assert(!Resolves(prop0c.neg, altquestion01))
      assert(!Resolves(prop1a.neg, altquestion01))
      assert(!Resolves(prop1b.neg, altquestion01))
      assert(!Resolves(prop1c.neg, altquestion01))

      //altquestion02
      assert(!Resolves(Yes, altquestion02))
      assert(!Resolves(No, altquestion02))
      assert(!Resolves(ShortAns(paludism), altquestion02))
      assert(!Resolves(ShortAns(aids), altquestion02))
      assert(!Resolves(ShortAns(therapeutic), altquestion02))
      assert(!Resolves(ShortAns(french), altquestion02))
      assert(!Resolves(ShortAns(english), altquestion02))
      assert(!Resolves(ShortAns(paludism).neg, altquestion02))
      assert(!Resolves(ShortAns(aids).neg, altquestion02))
      assert(!Resolves(ShortAns(therapeutic).neg, altquestion02))
      assert(!Resolves(ShortAns(french).neg, altquestion02))
      assert(!Resolves(ShortAns(english).neg, altquestion02))
      assert(Resolves(prop0a, altquestion02))
      assert(Resolves(prop0b, altquestion02))
      assert(Resolves(prop0c, altquestion02))
      assert(!Resolves(prop1a, altquestion02))
      assert(!Resolves(prop1b, altquestion02))
      assert(!Resolves(prop1c, altquestion02))
      assert(!Resolves(prop0a.neg, altquestion02))
      assert(!Resolves(prop0b.neg, altquestion02))
      assert(!Resolves(prop0c.neg, altquestion02))
      assert(!Resolves(prop1a.neg, altquestion02))
      assert(!Resolves(prop1b.neg, altquestion02))
      assert(!Resolves(prop1c.neg, altquestion02))

      //altquestion03
      assert(!Resolves(Yes, altquestion03))
      assert(!Resolves(No, altquestion03))
      assert(!Resolves(ShortAns(paludism), altquestion03))
      assert(!Resolves(ShortAns(aids), altquestion03))
      assert(!Resolves(ShortAns(therapeutic), altquestion03))
      assert(!Resolves(ShortAns(french), altquestion03))
      assert(!Resolves(ShortAns(english), altquestion03))
      assert(!Resolves(ShortAns(paludism).neg, altquestion03))
      assert(!Resolves(ShortAns(aids).neg, altquestion03))
      assert(!Resolves(ShortAns(therapeutic).neg, altquestion03))
      assert(!Resolves(ShortAns(french).neg, altquestion03))
      assert(!Resolves(ShortAns(english).neg, altquestion03))
      assert(!Resolves(prop0a, altquestion03))
      assert(Resolves(prop0b, altquestion03))
      assert(!Resolves(prop0c, altquestion03))
      assert(Resolves(prop1a, altquestion03))
      assert(!Resolves(prop1b, altquestion03))
      assert(Resolves(prop1c, altquestion03))
      assert(!Resolves(prop0a.neg, altquestion03))
      assert(!Resolves(prop0b.neg, altquestion03))
      assert(!Resolves(prop0c.neg, altquestion03))
      assert(!Resolves(prop1a.neg, altquestion03))
      assert(!Resolves(prop1b.neg, altquestion03))
      assert(!Resolves(prop1c.neg, altquestion03))
    }
  }

  test("Resolves: YNQuestion") {
    new TestResolves {
      //ynquestion01
      assert(Resolves(Yes, ynquestion01))
      assert(Resolves(No, ynquestion01))
      assert(!Resolves(ShortAns(paludism), ynquestion01))
      assert(!Resolves(ShortAns(aids), ynquestion01))
      assert(!Resolves(ShortAns(therapeutic), ynquestion01))
      assert(!Resolves(ShortAns(french), ynquestion01))
      assert(!Resolves(ShortAns(english), ynquestion01))
      assert(!Resolves(ShortAns(paludism).neg, ynquestion01))
      assert(!Resolves(ShortAns(aids).neg, ynquestion01))
      assert(!Resolves(ShortAns(therapeutic).neg, ynquestion01))
      assert(!Resolves(ShortAns(french).neg, ynquestion01))
      assert(!Resolves(ShortAns(english).neg, ynquestion01))
      assert(Resolves(prop0a, ynquestion01))
      assert(!Resolves(prop0b, ynquestion01))
      assert(!Resolves(prop0c, ynquestion01))
      assert(!Resolves(prop1a, ynquestion01))
      assert(!Resolves(prop1b, ynquestion01))
      assert(!Resolves(prop1c, ynquestion01))
      assert(Resolves(prop0a.neg, ynquestion01))
      assert(!Resolves(prop0b.neg, ynquestion01))
      assert(!Resolves(prop0c.neg, ynquestion01))
      assert(!Resolves(prop1a.neg, ynquestion01))
      assert(!Resolves(prop1b.neg, ynquestion01))
      assert(!Resolves(prop1c.neg, ynquestion01))

      //ynquestion02
      assert(Resolves(Yes, ynquestion02))
      assert(Resolves(No, ynquestion02))
      assert(!Resolves(ShortAns(paludism), ynquestion02))
      assert(!Resolves(ShortAns(aids), ynquestion02))
      assert(!Resolves(ShortAns(therapeutic), ynquestion02))
      assert(!Resolves(ShortAns(french), ynquestion02))
      assert(!Resolves(ShortAns(english), ynquestion02))
      assert(!Resolves(ShortAns(paludism).neg, ynquestion02))
      assert(!Resolves(ShortAns(aids).neg, ynquestion02))
      assert(!Resolves(ShortAns(therapeutic).neg, ynquestion02))
      assert(!Resolves(ShortAns(french).neg, ynquestion02))
      assert(!Resolves(ShortAns(english).neg, ynquestion02))
      assert(!Resolves(prop0a, ynquestion02))
      assert(Resolves(prop0b, ynquestion02))
      assert(!Resolves(prop0c, ynquestion02))
      assert(!Resolves(prop1a, ynquestion02))
      assert(!Resolves(prop1b, ynquestion02))
      assert(!Resolves(prop1c, ynquestion02))
      assert(!Resolves(prop0a.neg, ynquestion02))
      assert(Resolves(prop0b.neg, ynquestion02))
      assert(!Resolves(prop0c.neg, ynquestion02))
      assert(!Resolves(prop1a.neg, ynquestion02))
      assert(!Resolves(prop1b.neg, ynquestion02))
      assert(!Resolves(prop1c.neg, ynquestion02))

      //ynquestion03
      assert(Resolves(Yes, ynquestion03))
      assert(Resolves(No, ynquestion03))
      assert(!Resolves(ShortAns(paludism), ynquestion03))
      assert(!Resolves(ShortAns(aids), ynquestion03))
      assert(!Resolves(ShortAns(therapeutic), ynquestion03))
      assert(!Resolves(ShortAns(french), ynquestion03))
      assert(!Resolves(ShortAns(english), ynquestion03))
      assert(!Resolves(ShortAns(paludism).neg, ynquestion03))
      assert(!Resolves(ShortAns(aids).neg, ynquestion03))
      assert(!Resolves(ShortAns(therapeutic).neg, ynquestion03))
      assert(!Resolves(ShortAns(french).neg, ynquestion03))
      assert(!Resolves(ShortAns(english).neg, ynquestion03))
      assert(!Resolves(prop0a, ynquestion03))
      assert(!Resolves(prop0b, ynquestion03))
      assert(Resolves(prop0c, ynquestion03))
      assert(!Resolves(prop1a, ynquestion03))
      assert(!Resolves(prop1b, ynquestion03))
      assert(!Resolves(prop1c, ynquestion03))
      assert(!Resolves(prop0a.neg, ynquestion03))
      assert(!Resolves(prop0b.neg, ynquestion03))
      assert(Resolves(prop0c.neg, ynquestion03))
      assert(!Resolves(prop1a.neg, ynquestion03))
      assert(!Resolves(prop1b.neg, ynquestion03))
      assert(!Resolves(prop1c.neg, ynquestion03))

      //ynquestion04
      assert(Resolves(Yes, ynquestion04))
      assert(Resolves(No, ynquestion04))
      assert(!Resolves(ShortAns(paludism), ynquestion04))
      assert(!Resolves(ShortAns(aids), ynquestion04))
      assert(!Resolves(ShortAns(therapeutic), ynquestion04))
      assert(!Resolves(ShortAns(french), ynquestion04))
      assert(!Resolves(ShortAns(english), ynquestion04))
      assert(!Resolves(ShortAns(paludism).neg, ynquestion04))
      assert(!Resolves(ShortAns(aids).neg, ynquestion04))
      assert(!Resolves(ShortAns(therapeutic).neg, ynquestion04))
      assert(!Resolves(ShortAns(french).neg, ynquestion04))
      assert(!Resolves(ShortAns(english).neg, ynquestion04))
      assert(!Resolves(prop0a, ynquestion04))
      assert(!Resolves(prop0b, ynquestion04))
      assert(!Resolves(prop0c, ynquestion04))
      assert(Resolves(prop1a, ynquestion04))
      assert(!Resolves(prop1b, ynquestion04))
      assert(!Resolves(prop1c, ynquestion04))
      assert(!Resolves(prop0a.neg, ynquestion04))
      assert(!Resolves(prop0b.neg, ynquestion04))
      assert(!Resolves(prop0c.neg, ynquestion04))
      assert(Resolves(prop1a.neg, ynquestion04))
      assert(!Resolves(prop1b.neg, ynquestion04))
      assert(!Resolves(prop1c.neg, ynquestion04))

      //ynquestion05
      assert(Resolves(Yes, ynquestion05))
      assert(Resolves(No, ynquestion05))
      assert(!Resolves(ShortAns(paludism), ynquestion05))
      assert(!Resolves(ShortAns(aids), ynquestion05))
      assert(!Resolves(ShortAns(therapeutic), ynquestion05))
      assert(!Resolves(ShortAns(french), ynquestion05))
      assert(!Resolves(ShortAns(english), ynquestion05))
      assert(!Resolves(ShortAns(paludism).neg, ynquestion05))
      assert(!Resolves(ShortAns(aids).neg, ynquestion05))
      assert(!Resolves(ShortAns(therapeutic).neg, ynquestion05))
      assert(!Resolves(ShortAns(french).neg, ynquestion05))
      assert(!Resolves(ShortAns(english).neg, ynquestion05))
      assert(!Resolves(prop0a, ynquestion05))
      assert(!Resolves(prop0b, ynquestion05))
      assert(!Resolves(prop0c, ynquestion05))
      assert(!Resolves(prop1a, ynquestion05))
      assert(Resolves(prop1b, ynquestion05))
      assert(!Resolves(prop1c, ynquestion05))
      assert(!Resolves(prop0a.neg, ynquestion05))
      assert(!Resolves(prop0b.neg, ynquestion05))
      assert(!Resolves(prop0c.neg, ynquestion05))
      assert(!Resolves(prop1a.neg, ynquestion05))
      assert(Resolves(prop1b.neg, ynquestion05))
      assert(!Resolves(prop1c.neg, ynquestion05))

      //ynquestion06
      assert(Resolves(Yes, ynquestion06))
      assert(Resolves(No, ynquestion06))
      assert(!Resolves(ShortAns(paludism), ynquestion06))
      assert(!Resolves(ShortAns(aids), ynquestion06))
      assert(!Resolves(ShortAns(therapeutic), ynquestion06))
      assert(!Resolves(ShortAns(french), ynquestion06))
      assert(!Resolves(ShortAns(english), ynquestion06))
      assert(!Resolves(ShortAns(paludism).neg, ynquestion06))
      assert(!Resolves(ShortAns(aids).neg, ynquestion06))
      assert(!Resolves(ShortAns(therapeutic).neg, ynquestion06))
      assert(!Resolves(ShortAns(french).neg, ynquestion06))
      assert(!Resolves(ShortAns(english).neg, ynquestion06))
      assert(!Resolves(prop0a, ynquestion06))
      assert(!Resolves(prop0b, ynquestion06))
      assert(!Resolves(prop0c, ynquestion06))
      assert(!Resolves(prop1a, ynquestion06))
      assert(!Resolves(prop1b, ynquestion06))
      assert(Resolves(prop1c, ynquestion06))
      assert(!Resolves(prop0a.neg, ynquestion06))
      assert(!Resolves(prop0b.neg, ynquestion06))
      assert(!Resolves(prop0c.neg, ynquestion06))
      assert(!Resolves(prop1a.neg, ynquestion06))
      assert(!Resolves(prop1b.neg, ynquestion06))
      assert(Resolves(prop1c.neg, ynquestion06))
    }
  }

  test("Resolves: WhQuestion") {
    new TestResolves {
      //whquestion01
      assert(!Resolves(Yes, whquestion01))
      assert(!Resolves(No, whquestion01))
      assert(Resolves(ShortAns(paludism), whquestion01))
      assert(Resolves(ShortAns(aids), whquestion01))
      assert(Resolves(ShortAns(therapeutic), whquestion01))
      assert(Resolves(ShortAns(french), whquestion01))
      assert(Resolves(ShortAns(english), whquestion01))
      assert(!Resolves(ShortAns(paludism).neg, whquestion01))
      assert(!Resolves(ShortAns(aids).neg, whquestion01))
      assert(!Resolves(ShortAns(therapeutic).neg, whquestion01))
      assert(!Resolves(ShortAns(french).neg, whquestion01))
      assert(!Resolves(ShortAns(english).neg, whquestion01))
      assert(!Resolves(prop0a, whquestion01))
      assert(!Resolves(prop0b, whquestion01))
      assert(!Resolves(prop0c, whquestion01))
      assert(!Resolves(prop1a, whquestion01))
      assert(!Resolves(prop1b, whquestion01))
      assert(!Resolves(prop1c, whquestion01))
      assert(!Resolves(prop0a.neg, whquestion01))
      assert(!Resolves(prop0b.neg, whquestion01))
      assert(!Resolves(prop0c.neg, whquestion01))
      assert(!Resolves(prop1a.neg, whquestion01))
      assert(!Resolves(prop1b.neg, whquestion01))
      assert(!Resolves(prop1c.neg, whquestion01))

      //whquestion02
      assert(!Resolves(Yes, whquestion02))
      assert(!Resolves(No, whquestion02))
      assert(Resolves(ShortAns(paludism), whquestion02))
      assert(Resolves(ShortAns(aids), whquestion02))
      assert(Resolves(ShortAns(therapeutic), whquestion02))
      assert(Resolves(ShortAns(french), whquestion02))
      assert(Resolves(ShortAns(english), whquestion02))
      assert(!Resolves(ShortAns(paludism).neg, whquestion02))
      assert(!Resolves(ShortAns(aids).neg, whquestion02))
      assert(!Resolves(ShortAns(therapeutic).neg, whquestion02))
      assert(!Resolves(ShortAns(french).neg, whquestion02))
      assert(!Resolves(ShortAns(english).neg, whquestion02))
      assert(!Resolves(prop0a, whquestion02))
      assert(!Resolves(prop0b, whquestion02))
      assert(!Resolves(prop0c, whquestion02))
      assert(Resolves(prop1a, whquestion02))
      assert(!Resolves(prop1b, whquestion02))
      assert(Resolves(prop1c, whquestion02))
      assert(!Resolves(prop0a.neg, whquestion02))
      assert(!Resolves(prop0b.neg, whquestion02))
      assert(!Resolves(prop0c.neg, whquestion02))
      assert(!Resolves(prop1a.neg, whquestion02))
      assert(!Resolves(prop1b.neg, whquestion02))
      assert(!Resolves(prop1c.neg, whquestion02))

      //whquestion03
      assert(!Resolves(Yes, whquestion03))
      assert(!Resolves(No, whquestion03))
      assert(Resolves(ShortAns(paludism), whquestion03))
      assert(Resolves(ShortAns(aids), whquestion03))
      assert(Resolves(ShortAns(therapeutic), whquestion03))
      assert(Resolves(ShortAns(french), whquestion03))
      assert(Resolves(ShortAns(english), whquestion03))
      assert(!Resolves(ShortAns(paludism).neg, whquestion03))
      assert(!Resolves(ShortAns(aids).neg, whquestion03))
      assert(!Resolves(ShortAns(therapeutic).neg, whquestion03))
      assert(!Resolves(ShortAns(french).neg, whquestion03))
      assert(!Resolves(ShortAns(english).neg, whquestion03))
      assert(!Resolves(prop0a, whquestion03))
      assert(!Resolves(prop0b, whquestion03))
      assert(!Resolves(prop0c, whquestion03))
      assert(!Resolves(prop1a, whquestion03))
      assert(Resolves(prop1b, whquestion03))
      assert(!Resolves(prop1c, whquestion03))
      assert(!Resolves(prop0a.neg, whquestion03))
      assert(!Resolves(prop0b.neg, whquestion03))
      assert(!Resolves(prop0c.neg, whquestion03))
      assert(!Resolves(prop1a.neg, whquestion03))
      assert(!Resolves(prop1b.neg, whquestion03))
      assert(!Resolves(prop1c.neg, whquestion03))
    }
  }

}
