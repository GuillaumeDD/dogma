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
class CombineSuite extends FunSuite {
  trait TestCombine {
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

  test("Combine: AltQuestion") {
    new TestCombine {
      //altquestion01
      assert(Combine(Yes, altquestion01) === None)
      assert(Combine(No, altquestion01) === None)
      assert(Combine(ShortAns(paludism), altquestion01) === None)
      assert(Combine(ShortAns(aids), altquestion01) === None)
      assert(Combine(ShortAns(therapeutic), altquestion01) === None)
      assert(Combine(ShortAns(french), altquestion01) === None)
      assert(Combine(ShortAns(english), altquestion01) === None)
      assert(Combine(ShortAns(paludism).neg, altquestion01) === None)
      assert(Combine(ShortAns(aids).neg, altquestion01) === None)
      assert(Combine(ShortAns(therapeutic).neg, altquestion01) === None)
      assert(Combine(ShortAns(french).neg, altquestion01) === None)
      assert(Combine(ShortAns(english).neg, altquestion01) === None)
      assert(Combine(prop0a, altquestion01) === None)
      assert(Combine(prop0b, altquestion01) === None)
      assert(Combine(prop0c, altquestion01) === None)
      assert(Combine(prop1a, altquestion01).get predEquals Some(prop1a).get)
      assert(Combine(prop1b, altquestion01).get predEquals Some(prop1b).get)
      assert(Combine(prop1c, altquestion01).get predEquals Some(prop1c).get)
      assert(Combine(prop0a.neg, altquestion01) === None)
      assert(Combine(prop0b.neg, altquestion01) === None)
      assert(Combine(prop0c.neg, altquestion01) === None)
      assert(Combine(prop1a.neg, altquestion01).get predEquals Some(prop1a.neg).get)
      assert(Combine(prop1b.neg, altquestion01).get predEquals Some(prop1b.neg).get)
      assert(Combine(prop1c.neg, altquestion01).get predEquals Some(prop1c.neg).get)

      //altquestion02
      assert(Combine(Yes, altquestion02) === None)
      assert(Combine(No, altquestion02) === None)
      assert(Combine(ShortAns(paludism), altquestion02) === None)
      assert(Combine(ShortAns(aids), altquestion02) === None)
      assert(Combine(ShortAns(therapeutic), altquestion02) === None)
      assert(Combine(ShortAns(french), altquestion02) === None)
      assert(Combine(ShortAns(english), altquestion02) === None)
      assert(Combine(ShortAns(paludism).neg, altquestion02) === None)
      assert(Combine(ShortAns(aids).neg, altquestion02) === None)
      assert(Combine(ShortAns(therapeutic).neg, altquestion02) === None)
      assert(Combine(ShortAns(french).neg, altquestion02) === None)
      assert(Combine(ShortAns(english).neg, altquestion02) === None)
      assert(Combine(prop0a, altquestion02).get predEquals Some(prop0a).get)
      assert(Combine(prop0b, altquestion02).get predEquals Some(prop0b).get)
      assert(Combine(prop0c, altquestion02).get predEquals Some(prop0c).get)
      assert(Combine(prop1a, altquestion02) === None)
      assert(Combine(prop1b, altquestion02) === None)
      assert(Combine(prop1c, altquestion02) === None)
      assert(Combine(prop0a.neg, altquestion02).get predEquals Some(prop0a.neg).get)
      assert(Combine(prop0b.neg, altquestion02).get predEquals Some(prop0b.neg).get)
      assert(Combine(prop0c.neg, altquestion02).get predEquals Some(prop0c.neg).get)
      assert(Combine(prop1a.neg, altquestion02) === None)
      assert(Combine(prop1b.neg, altquestion02) === None)
      assert(Combine(prop1c.neg, altquestion02) === None)

      //altquestion03
      assert(Combine(Yes, altquestion03) === None)
      assert(Combine(No, altquestion03) === None)
      assert(Combine(ShortAns(paludism), altquestion03) === None)
      assert(Combine(ShortAns(aids), altquestion03) === None)
      assert(Combine(ShortAns(therapeutic), altquestion03) === None)
      assert(Combine(ShortAns(french), altquestion03) === None)
      assert(Combine(ShortAns(english), altquestion03) === None)
      assert(Combine(ShortAns(paludism).neg, altquestion03) === None)
      assert(Combine(ShortAns(aids).neg, altquestion03) === None)
      assert(Combine(ShortAns(therapeutic).neg, altquestion03) === None)
      assert(Combine(ShortAns(french).neg, altquestion03) === None)
      assert(Combine(ShortAns(english).neg, altquestion03) === None)
      assert(Combine(prop0a, altquestion03) === None)
      assert(Combine(prop0b, altquestion03).get predEquals Some(prop0b).get)
      assert(Combine(prop0c, altquestion03) === None)
      assert(Combine(prop1a, altquestion03).get predEquals Some(prop1a).get)
      assert(Combine(prop1b, altquestion03) === None)
      assert(Combine(prop1c, altquestion03).get predEquals Some(prop1c).get)
      assert(Combine(prop0a.neg, altquestion03) === None)
      assert(Combine(prop0b.neg, altquestion03).get predEquals Some(prop0b.neg).get)
      assert(Combine(prop0c.neg, altquestion03) === None)
      assert(Combine(prop1a.neg, altquestion03).get predEquals Some(prop1a.neg).get)
      assert(Combine(prop1b.neg, altquestion03) === None)
      assert(Combine(prop1c.neg, altquestion03).get predEquals Some(prop1c.neg).get)
    }
  }

  test("Combine: YNQuestion") {
    new TestCombine {
      //ynquestion01
      assert(Combine(Yes, ynquestion01).get predEquals Some(Yes).get)
      assert(Combine(No, ynquestion01).get predEquals Some(No).get)
      assert(Combine(ShortAns(paludism), ynquestion01) === None)
      assert(Combine(ShortAns(aids), ynquestion01) === None)
      assert(Combine(ShortAns(therapeutic), ynquestion01) === None)
      assert(Combine(ShortAns(french), ynquestion01) === None)
      assert(Combine(ShortAns(english), ynquestion01) === None)
      assert(Combine(ShortAns(paludism).neg, ynquestion01) === None)
      assert(Combine(ShortAns(aids).neg, ynquestion01) === None)
      assert(Combine(ShortAns(therapeutic).neg, ynquestion01) === None)
      assert(Combine(ShortAns(french).neg, ynquestion01) === None)
      assert(Combine(ShortAns(english).neg, ynquestion01) === None)
      assert(Combine(prop0a, ynquestion01).get predEquals Some(prop0a).get)
      assert(Combine(prop0b, ynquestion01) === None)
      assert(Combine(prop0c, ynquestion01) === None)
      assert(Combine(prop1a, ynquestion01) === None)
      assert(Combine(prop1b, ynquestion01) === None)
      assert(Combine(prop1c, ynquestion01) === None)
      assert(Combine(prop0a.neg, ynquestion01).get predEquals Some(prop0a.neg).get)
      assert(Combine(prop0b.neg, ynquestion01) === None)
      assert(Combine(prop0c.neg, ynquestion01) === None)
      assert(Combine(prop1a.neg, ynquestion01) === None)
      assert(Combine(prop1b.neg, ynquestion01) === None)
      assert(Combine(prop1c.neg, ynquestion01) === None)

      //ynquestion02
      assert(Combine(Yes, ynquestion02).get predEquals Some(Yes).get)
      assert(Combine(No, ynquestion02).get predEquals Some(No).get)
      assert(Combine(ShortAns(paludism), ynquestion02) === None)
      assert(Combine(ShortAns(aids), ynquestion02) === None)
      assert(Combine(ShortAns(therapeutic), ynquestion02) === None)
      assert(Combine(ShortAns(french), ynquestion02) === None)
      assert(Combine(ShortAns(english), ynquestion02) === None)
      assert(Combine(ShortAns(paludism).neg, ynquestion02) === None)
      assert(Combine(ShortAns(aids).neg, ynquestion02) === None)
      assert(Combine(ShortAns(therapeutic).neg, ynquestion02) === None)
      assert(Combine(ShortAns(french).neg, ynquestion02) === None)
      assert(Combine(ShortAns(english).neg, ynquestion02) === None)
      assert(Combine(prop0a, ynquestion02) === None)
      assert(Combine(prop0b, ynquestion02).get predEquals Some(prop0b).get)
      assert(Combine(prop0c, ynquestion02) === None)
      assert(Combine(prop1a, ynquestion02) === None)
      assert(Combine(prop1b, ynquestion02) === None)
      assert(Combine(prop1c, ynquestion02) === None)
      assert(Combine(prop0a.neg, ynquestion02) === None)
      assert(Combine(prop0b.neg, ynquestion02).get predEquals Some(prop0b.neg).get)
      assert(Combine(prop0c.neg, ynquestion02) === None)
      assert(Combine(prop1a.neg, ynquestion02) === None)
      assert(Combine(prop1b.neg, ynquestion02) === None)
      assert(Combine(prop1c.neg, ynquestion02) === None)

      //ynquestion03
      assert(Combine(Yes, ynquestion03).get predEquals Some(Yes).get)
      assert(Combine(No, ynquestion03).get predEquals Some(No).get)
      assert(Combine(ShortAns(paludism), ynquestion03) === None)
      assert(Combine(ShortAns(aids), ynquestion03) === None)
      assert(Combine(ShortAns(therapeutic), ynquestion03) === None)
      assert(Combine(ShortAns(french), ynquestion03) === None)
      assert(Combine(ShortAns(english), ynquestion03) === None)
      assert(Combine(ShortAns(paludism).neg, ynquestion03) === None)
      assert(Combine(ShortAns(aids).neg, ynquestion03) === None)
      assert(Combine(ShortAns(therapeutic).neg, ynquestion03) === None)
      assert(Combine(ShortAns(french).neg, ynquestion03) === None)
      assert(Combine(ShortAns(english).neg, ynquestion03) === None)
      assert(Combine(prop0a, ynquestion03) === None)
      assert(Combine(prop0b, ynquestion03) === None)
      assert(Combine(prop0c, ynquestion03).get predEquals Some(prop0c).get)
      assert(Combine(prop1a, ynquestion03) === None)
      assert(Combine(prop1b, ynquestion03) === None)
      assert(Combine(prop1c, ynquestion03) === None)
      assert(Combine(prop0a.neg, ynquestion03) === None)
      assert(Combine(prop0b.neg, ynquestion03) === None)
      assert(Combine(prop0c.neg, ynquestion03).get predEquals Some(prop0c.neg).get)
      assert(Combine(prop1a.neg, ynquestion03) === None)
      assert(Combine(prop1b.neg, ynquestion03) === None)
      assert(Combine(prop1c.neg, ynquestion03) === None)

      //ynquestion04
      assert(Combine(Yes, ynquestion04).get predEquals Some(Yes).get)
      assert(Combine(No, ynquestion04).get predEquals Some(No).get)
      assert(Combine(ShortAns(paludism), ynquestion04) === None)
      assert(Combine(ShortAns(aids), ynquestion04) === None)
      assert(Combine(ShortAns(therapeutic), ynquestion04) === None)
      assert(Combine(ShortAns(french), ynquestion04) === None)
      assert(Combine(ShortAns(english), ynquestion04) === None)
      assert(Combine(ShortAns(paludism).neg, ynquestion04) === None)
      assert(Combine(ShortAns(aids).neg, ynquestion04) === None)
      assert(Combine(ShortAns(therapeutic).neg, ynquestion04) === None)
      assert(Combine(ShortAns(french).neg, ynquestion04) === None)
      assert(Combine(ShortAns(english).neg, ynquestion04) === None)
      assert(Combine(prop0a, ynquestion04) === None)
      assert(Combine(prop0b, ynquestion04) === None)
      assert(Combine(prop0c, ynquestion04) === None)
      assert(Combine(prop1a, ynquestion04).get predEquals Some(prop1a).get)
      assert(Combine(prop1b, ynquestion04) === None)
      assert(Combine(prop1c, ynquestion04) === None)
      assert(Combine(prop0a.neg, ynquestion04) === None)
      assert(Combine(prop0b.neg, ynquestion04) === None)
      assert(Combine(prop0c.neg, ynquestion04) === None)
      assert(Combine(prop1a.neg, ynquestion04).get predEquals Some(prop1a.neg).get)
      assert(Combine(prop1b.neg, ynquestion04) === None)
      assert(Combine(prop1c.neg, ynquestion04) === None)

      //ynquestion05
      assert(Combine(Yes, ynquestion05).get predEquals Some(Yes).get)
      assert(Combine(No, ynquestion05).get predEquals Some(No).get)
      assert(Combine(ShortAns(paludism), ynquestion05) === None)
      assert(Combine(ShortAns(aids), ynquestion05) === None)
      assert(Combine(ShortAns(therapeutic), ynquestion05) === None)
      assert(Combine(ShortAns(french), ynquestion05) === None)
      assert(Combine(ShortAns(english), ynquestion05) === None)
      assert(Combine(ShortAns(paludism).neg, ynquestion05) === None)
      assert(Combine(ShortAns(aids).neg, ynquestion05) === None)
      assert(Combine(ShortAns(therapeutic).neg, ynquestion05) === None)
      assert(Combine(ShortAns(french).neg, ynquestion05) === None)
      assert(Combine(ShortAns(english).neg, ynquestion05) === None)
      assert(Combine(prop0a, ynquestion05) === None)
      assert(Combine(prop0b, ynquestion05) === None)
      assert(Combine(prop0c, ynquestion05) === None)
      assert(Combine(prop1a, ynquestion05) === None)
      assert(Combine(prop1b, ynquestion05).get predEquals Some(prop1b).get)
      assert(Combine(prop1c, ynquestion05) === None)
      assert(Combine(prop0a.neg, ynquestion05) === None)
      assert(Combine(prop0b.neg, ynquestion05) === None)
      assert(Combine(prop0c.neg, ynquestion05) === None)
      assert(Combine(prop1a.neg, ynquestion05) === None)
      assert(Combine(prop1b.neg, ynquestion05).get predEquals Some(prop1b.neg).get)
      assert(Combine(prop1c.neg, ynquestion05) === None)

      //ynquestion06
      assert(Combine(Yes, ynquestion06).get predEquals Some(Yes).get)
      assert(Combine(No, ynquestion06).get predEquals Some(No).get)
      assert(Combine(ShortAns(paludism), ynquestion06) === None)
      assert(Combine(ShortAns(aids), ynquestion06) === None)
      assert(Combine(ShortAns(therapeutic), ynquestion06) === None)
      assert(Combine(ShortAns(french), ynquestion06) === None)
      assert(Combine(ShortAns(english), ynquestion06) === None)
      assert(Combine(ShortAns(paludism).neg, ynquestion06) === None)
      assert(Combine(ShortAns(aids).neg, ynquestion06) === None)
      assert(Combine(ShortAns(therapeutic).neg, ynquestion06) === None)
      assert(Combine(ShortAns(french).neg, ynquestion06) === None)
      assert(Combine(ShortAns(english).neg, ynquestion06) === None)
      assert(Combine(prop0a, ynquestion06) === None)
      assert(Combine(prop0b, ynquestion06) === None)
      assert(Combine(prop0c, ynquestion06) === None)
      assert(Combine(prop1a, ynquestion06) === None)
      assert(Combine(prop1b, ynquestion06) === None)
      assert(Combine(prop1c, ynquestion06).get predEquals Some(prop1c).get)
      assert(Combine(prop0a.neg, ynquestion06) === None)
      assert(Combine(prop0b.neg, ynquestion06) === None)
      assert(Combine(prop0c.neg, ynquestion06) === None)
      assert(Combine(prop1a.neg, ynquestion06) === None)
      assert(Combine(prop1b.neg, ynquestion06) === None)
      assert(Combine(prop1c.neg, ynquestion06).get predEquals Some(prop1c.neg).get)
    }
  }

  test("Combine: WhQuestion") {
    new TestCombine {
      //whquestion01
      assert(Combine(Yes, whquestion01) === None)
      assert(Combine(No, whquestion01) === None)
      assert(Combine(ShortAns(paludism), whquestion01).get predEquals Some(ShortAns(paludism)).get)
      assert(Combine(ShortAns(aids), whquestion01).get predEquals Some(ShortAns(aids)).get)
      assert(Combine(ShortAns(therapeutic), whquestion01).get predEquals Some(ShortAns(therapeutic)).get)
      assert(Combine(ShortAns(french), whquestion01).get predEquals Some(ShortAns(french)).get)
      assert(Combine(ShortAns(english), whquestion01).get predEquals Some(ShortAns(english)).get)
      assert(Combine(ShortAns(paludism).neg, whquestion01).get predEquals Some(ShortAns(paludism).neg).get)
      assert(Combine(ShortAns(aids).neg, whquestion01).get predEquals Some(ShortAns(aids).neg).get)
      assert(Combine(ShortAns(therapeutic).neg, whquestion01).get predEquals Some(ShortAns(therapeutic).neg).get)
      assert(Combine(ShortAns(french).neg, whquestion01).get predEquals Some(ShortAns(french).neg).get)
      assert(Combine(ShortAns(english).neg, whquestion01).get predEquals Some(ShortAns(english).neg).get)
      assert(Combine(prop0a, whquestion01) === None)
      assert(Combine(prop0b, whquestion01) === None)
      assert(Combine(prop0c, whquestion01) === None)
      assert(Combine(prop1a, whquestion01) === None)
      assert(Combine(prop1b, whquestion01) === None)
      assert(Combine(prop1c, whquestion01) === None)
      assert(Combine(prop0a.neg, whquestion01) === None)
      assert(Combine(prop0b.neg, whquestion01) === None)
      assert(Combine(prop0c.neg, whquestion01) === None)
      assert(Combine(prop1a.neg, whquestion01) === None)
      assert(Combine(prop1b.neg, whquestion01) === None)
      assert(Combine(prop1c.neg, whquestion01) === None)

      //whquestion02
      assert(Combine(Yes, whquestion02) === None)
      assert(Combine(No, whquestion02) === None)
      assert(Combine(ShortAns(paludism), whquestion02).get predEquals Some(ShortAns(paludism)).get)
      assert(Combine(ShortAns(aids), whquestion02).get predEquals Some(ShortAns(aids)).get)
      assert(Combine(ShortAns(therapeutic), whquestion02).get predEquals Some(ShortAns(therapeutic)).get)
      assert(Combine(ShortAns(french), whquestion02).get predEquals Some(ShortAns(french)).get)
      assert(Combine(ShortAns(english), whquestion02).get predEquals Some(ShortAns(english)).get)
      assert(Combine(ShortAns(paludism).neg, whquestion02).get predEquals Some(ShortAns(paludism).neg).get)
      assert(Combine(ShortAns(aids).neg, whquestion02).get predEquals Some(ShortAns(aids).neg).get)
      assert(Combine(ShortAns(therapeutic).neg, whquestion02).get predEquals Some(ShortAns(therapeutic).neg).get)
      assert(Combine(ShortAns(french).neg, whquestion02).get predEquals Some(ShortAns(french).neg).get)
      assert(Combine(ShortAns(english).neg, whquestion02).get predEquals Some(ShortAns(english).neg).get)
      assert(Combine(prop0a, whquestion02) === None)
      assert(Combine(prop0b, whquestion02) === None)
      assert(Combine(prop0c, whquestion02) === None)
      assert(Combine(prop1a, whquestion02).get predEquals Some(prop1a).get)
      assert(Combine(prop1b, whquestion02) === None)
      assert(Combine(prop1c, whquestion02).get predEquals Some(prop1c).get)
      assert(Combine(prop0a.neg, whquestion02) === None)
      assert(Combine(prop0b.neg, whquestion02) === None)
      assert(Combine(prop0c.neg, whquestion02) === None)
      assert(Combine(prop1a.neg, whquestion02).get predEquals Some(prop1a.neg).get)
      assert(Combine(prop1b.neg, whquestion02) === None)
      assert(Combine(prop1c.neg, whquestion02).get predEquals Some(prop1c.neg).get)

      //whquestion03
      assert(Combine(Yes, whquestion03) === None)
      assert(Combine(No, whquestion03) === None)
      assert(Combine(ShortAns(paludism), whquestion03).get predEquals Some(ShortAns(paludism)).get)
      assert(Combine(ShortAns(aids), whquestion03).get predEquals Some(ShortAns(aids)).get)
      assert(Combine(ShortAns(therapeutic), whquestion03).get predEquals Some(ShortAns(therapeutic)).get)
      assert(Combine(ShortAns(french), whquestion03).get predEquals Some(ShortAns(french)).get)
      assert(Combine(ShortAns(english), whquestion03).get predEquals Some(ShortAns(english)).get)
      assert(Combine(ShortAns(paludism).neg, whquestion03).get predEquals Some(ShortAns(paludism).neg).get)
      assert(Combine(ShortAns(aids).neg, whquestion03).get predEquals Some(ShortAns(aids).neg).get)
      assert(Combine(ShortAns(therapeutic).neg, whquestion03).get predEquals Some(ShortAns(therapeutic).neg).get)
      assert(Combine(ShortAns(french).neg, whquestion03).get predEquals Some(ShortAns(french).neg).get)
      assert(Combine(ShortAns(english).neg, whquestion03).get predEquals Some(ShortAns(english).neg).get)
      assert(Combine(prop0a, whquestion03) === None)
      assert(Combine(prop0b, whquestion03) === None)
      assert(Combine(prop0c, whquestion03) === None)
      assert(Combine(prop1a, whquestion03) === None)
      assert(Combine(prop1b, whquestion03).get predEquals Some(prop1b).get)
      assert(Combine(prop1c, whquestion03) === None)
      assert(Combine(prop0a.neg, whquestion03) === None)
      assert(Combine(prop0b.neg, whquestion03) === None)
      assert(Combine(prop0c.neg, whquestion03) === None)
      assert(Combine(prop1a.neg, whquestion03) === None)
      assert(Combine(prop1b.neg, whquestion03).get predEquals Some(prop1b.neg).get)
      assert(Combine(prop1c.neg, whquestion03) === None)
    }
  }

}
