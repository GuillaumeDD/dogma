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
import fr.dubuissonduplessis.simpleSemantics.proposition.Proposition
import fr.dubuissonduplessis.simpleSemantics.question._
import fr.dubuissonduplessis.simpleSemantics.shortAnswer.ShortAns
import fr.dubuissonduplessis.simpleSemantics.proposition.Proposition1
import fr.dubuissonduplessis.simpleSemantics.proposition.Proposition2
import fr.dubuissonduplessis.simpleSemantics.proposition.Proposition3
import fr.dubuissonduplessis.simpleSemantics.shortAnswer.Yes
import fr.dubuissonduplessis.simpleSemantics.shortAnswer.No
import fr.dubuissonduplessis.predicateCalculus.Individual
import fr.dubuissonduplessis.simpleSemantics.proposition.NegProposition
import fr.dubuissonduplessis.simpleSemantics.Sentence

object Relevant extends QuestionSentenceRelation {
  def apply(s: Sentence, q: Question)(implicit semCompatible: (Individual, Question) => Boolean): Boolean =
    s match {
      case p: Proposition => Relevant(p, q)
      case ans: ShortAns => Relevant(ans, q)
      case _ => false
    }

  def apply(p: Proposition, q: Question)(implicit semCompatible: (Individual, Question) => Boolean): Boolean =
    q match {
      case WhQuestion(name, variable) =>
        if (p.negation) {
          Resolves(p.neg, q)
        } else {
          Resolves(p, q)
        }
      case ParametrizedWhQuestion2(name,
        parameter,
        variable) =>
        if (p.negation) {
          Resolves(p.neg, q)
        } else {
          Resolves(p, q)
        }
      case ParametrizedWhQuestion3(name,
        parameter1,
        parameter2,
        variable) =>
        if (p.negation) {
          Resolves(p.neg, q)
        } else {
          Resolves(p, q)
        }
      case AltQuestion(alternatives) =>
        if (p.negation) {
          Resolves(p.neg, q)
        } else {
          Resolves(p, q)
        }

      case YNQuestion(prop) =>
        Resolves(p, q)
    }

  def apply(ans: ShortAns, q: Question)(implicit semCompatible: (Individual, Question) => Boolean): Boolean =
    q match {
      case WhQuestion(name, variable) =>
        if (ans.negation) {
          Resolves(ans.neg, q)
        } else {
          Resolves(ans, q)
        }
      case ParametrizedWhQuestion2(name,
        parameter,
        variable) =>
        if (ans.negation) {
          Resolves(ans.neg, q)
        } else {
          Resolves(ans, q)
        }
      case ParametrizedWhQuestion3(name,
        parameter1,
        parameter2,
        variable) =>
        if (ans.negation) {
          Resolves(ans.neg, q)
        } else {
          Resolves(ans, q)
        }
      case YNQuestion(prop) =>
        Resolves(ans, q)
      case AltQuestion(alternatives) =>
        false
    }
}
