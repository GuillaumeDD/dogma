/**
 * *****************************************************************************
 * Copyright (c) 2014 Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr> - initial API and implementation
 * ****************************************************************************
 */
package fr.dubuissonduplessis.simpleSemantics

import fr.dubuissonduplessis.simpleSemantics.question.YNQuestion
import fr.dubuissonduplessis.simpleSemantics.question.Question
import fr.dubuissonduplessis.simpleSemantics.proposition.actionProposition.ActionPred
import fr.dubuissonduplessis.simpleSemantics.question.WhQuestion
import fr.dubuissonduplessis.simpleSemantics.proposition.Proposition
import fr.dubuissonduplessis.simpleSemantics.question.AltQuestion
import fr.dubuissonduplessis.simpleSemantics.proposition.questionProposition.Fail
import fr.dubuissonduplessis.simpleSemantics.shortAnswer.ShortAns
import fr.dubuissonduplessis.predicateCalculus._
import fr.dubuissonduplessis.simpleSemantics.action.Action
import fr.dubuissonduplessis.simpleSemantics.action.AtomicAction

package object util {
  /**
   * Computes the LaTeX representation of a sentence.
   *
   */
  def toLatex(s: Sentence): String =
    s match {
      case q: Question =>
        // Question case
        q match {
          case YNQuestion(prop) =>
            """\ynquestion{""" + toLatex(prop) + """}"""
          case AltQuestion(props) =>
            """\chquestion{""" + props.map(toLatex(_)).mkString(", ") + """}"""
          case WhQuestion(name, variable) =>
            """\whquestion{""" + name + """}"""
          case other =>
            other.toString()
        }

      case Fail(question) =>
        """\fail{""" + toLatex(question) + """}"""

      case action: AtomicAction =>
        // Predicate case
        if (action.negation) {
          """$\neg$""" + unifiableToLatex(action.neg)
        } else {
          unifiableToLatex(action)
        }

      case prop: Proposition =>
        // Predicate case
        if (prop.negation) {
          """$\neg$""" + unifiableToLatex(prop.neg)
        } else {
          unifiableToLatex(prop)
        }

      case ans: ShortAns =>
        // Constant case
        unifiableToLatex(ans)

      case other =>
        other.toString()
    }
}
