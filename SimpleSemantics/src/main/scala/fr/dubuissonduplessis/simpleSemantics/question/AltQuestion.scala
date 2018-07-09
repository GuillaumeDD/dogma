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
import fr.dubuissonduplessis.predicateCalculus.substitution.SubstitutionSet
import fr.dubuissonduplessis.simpleSemantics.proposition.Proposition

/**
 * Represents alternative questions such as "Do you want a beer or do you want an orange juice?",
 * as a set of [[fr.dubuissonduplessis.simpleSemantics.question.YNQuestion]].
 *
 * The name of the AltQuestion is the concatenation of the name of the YNQuestion ("?prop") with an
 * underscore between them.
 *
 * @param alternatives Set of [[fr.dubuissonduplessis.simpleSemantics.question.YNQuestion]].
 */
case class AltQuestion(
  alternatives: List[YNQuestion])
  extends Question {

  def name: String =
    alternatives.mkString("_")
  def terms: List[YNQuestion] = alternatives
  def substitute(subSet: SubstitutionSet): AltQuestion =
    AltQuestion(alternatives.map(_.substitute(subSet)))

  override def toString: String =
    alternatives.mkString("{", ", ", "}")
}

/**
 * Factory for [[fr.dubuissonduplessis.simpleSemantics.question.AltQuestion]].
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
object AltQuestion {
  /**
   * Builds a choice question from several proposition(s).
   * @note require strictly more than 1 proposition
   */
  def apply(props: Proposition*): AltQuestion =
    {
      require(props.size > 1)
      val alternatives =
        for (p <- props)
          yield (YNQuestion(p))

      AltQuestion(alternatives.toList)
    }
}
