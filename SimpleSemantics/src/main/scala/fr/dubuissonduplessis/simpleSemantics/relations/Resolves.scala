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
import fr.dubuissonduplessis.simpleSemantics.Sentence

object Resolves extends QuestionSentenceRelation {
  def apply(s: Sentence, q: Question)(implicit semCompatible: (Individual, Question) => Boolean): Boolean =
    s match {
      case p: Proposition => Resolves(p, q)
      case ans: ShortAns => Resolves(ans, q)
      case _ => false
    }
    
  def apply(p: Proposition, q: Question)(implicit semCompatible: (Individual, Question) => Boolean): Boolean =
    q match {
      case WhQuestion(name, variable) =>
        p match {
          case Proposition1(propName, arg) =>
            (name == propName) && // Same predicate (thus eliminating negation)
              semCompatible(arg, q) // Semantic, sortal and relational compatibility
          case _ =>
            false
        }
      case ParametrizedWhQuestion2(name,
        parameter,
        variable) =>
        p match {
          case Proposition2(propName, arg1, arg2) =>
            (name == propName) && // Same predicate (thus eliminating negation)
              (parameter predEquals arg1) && // Same parameter
              semCompatible(arg2, q) // Semantic, sortal and relational compatibility
          case _ =>
            false
        }
      case ParametrizedWhQuestion3(name,
        parameter1,
        parameter2,
        variable) =>
        p match {
          case Proposition3(propName, arg1, arg2, arg3) =>
            (name == propName) && // Same predicate (thus eliminating negation)
              (parameter1 predEquals arg1) && // Same parameters
              (parameter2 predEquals arg2) &&
              semCompatible(arg3, q) // Semantic, sortal and relational compatibility
          case _ =>
            false
        }

      case AltQuestion(alternatives) =>
        alternatives.exists(pi => pi.prop predEquals p)
      case YNQuestion(prop) =>
        (p predEquals prop) ||
          (p predEquals prop.neg)
    }

  def apply(ans: ShortAns, q: Question)(implicit semCompatible: (Individual, Question) => Boolean): Boolean =
    q match {
      case WhQuestion(name, variable) =>
        !ans.negation &&
          !(ans predEquals Yes) &&
          !(ans predEquals No) &&
          semCompatible(ans, q)
      case ParametrizedWhQuestion2(name,
        parameter,
        variable) =>
        !ans.negation &&
          !(ans predEquals Yes) &&
          !(ans predEquals No) &&
          semCompatible(ans, q)
      case ParametrizedWhQuestion3(name,
        parameter1,
        parameter2,
        variable) =>
        !ans.negation &&
          !(ans predEquals Yes) &&
          !(ans predEquals No) &&
          semCompatible(ans, q)
      case YNQuestion(prop) =>
        (ans predEquals Yes) ||
          (ans predEquals No)
      case AltQuestion(alternatives) =>
        false
    }
}
