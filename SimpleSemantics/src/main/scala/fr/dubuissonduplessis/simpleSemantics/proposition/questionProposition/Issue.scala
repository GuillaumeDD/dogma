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
import fr.dubuissonduplessis.simpleSemantics.question.Question
import fr.dubuissonduplessis.predicateCalculus.Unifiable
import fr.dubuissonduplessis.predicateCalculus.Individual
import fr.dubuissonduplessis.simpleSemantics.proposition.NegProposition
import fr.dubuissonduplessis.predicateCalculus.substitution.SubstitutionSet
import fr.dubuissonduplessis.simpleSemantics.proposition.Negation

/**
 * Represents a proposition to speak about question, such as a question "?issue(Q)" meaning
 * "Should Q become an issue?".
 *
 * See also [[fr.dubuissonduplessis.simpleSemantics.proposition.Und]].
 * @param question Question that is the subject of this proposition.
 * @param <T> Type of the question.
 */
case class Issue[T <: Question](
  question: T)
  extends QuestionProposition1
  with Negation[Issue[T]] {

  def name: String = "issue"

  def substitute(subSet: SubstitutionSet): Issue[_ <: Question] =
    Issue(question.substitute(subSet))
}
