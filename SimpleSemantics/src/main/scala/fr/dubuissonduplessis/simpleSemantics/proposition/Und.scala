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
package fr.dubuissonduplessis.simpleSemantics.proposition
import fr.dubuissonduplessis.simpleSemantics.question.Question
import fr.dubuissonduplessis.predicateCalculus.substitution.SubstitutionSet
import fr.dubuissonduplessis.predicateCalculus._
/**
 * Understanding proposition that may be used in a question "?und(DP, C)" meaning
 * "Did DP means C?" or "Is C a correct understanding of DP utterance?".
 *
 * The first predicate argument is the dialogue participant followed by the proposition.
 *
 * @param dp Dialogue Participant
 * @param prop Proposition that is being understood.
 * @param <T> Type of the proposition
 */
case class Und[Interlocutor, T <: Proposition](
  dp: Interlocutor,
  prop: T)
  extends Proposition
  with Negation[Und[Interlocutor, T]] {

  def name: String = "und"

  override def terms: List[AnyUnifiable] =
    List[AnyUnifiable](Individual(dp.toString), prop)

  def substitute(subSet: SubstitutionSet): Und[Interlocutor, _ <: Proposition] =
    Und(dp, prop.substitute(subSet))
}
