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
package fr.dubuissonduplessis.simpleSemantics.proposition.actionProposition
import fr.dubuissonduplessis.predicateCalculus.substitution.SubstitutionSet
import fr.dubuissonduplessis.simpleSemantics.proposition.NegProposition
import fr.dubuissonduplessis.simpleSemantics.proposition.Negation
import fr.dubuissonduplessis.simpleSemantics.action.Action

/**
 * Represents the "done" predicate. This proposition represents an utterance such as
 * "Action a has been successfully performed".
 *
 * @param <T> Type of the Action.
 */
case class Done[T <: Action](action: T) extends ActionProposition1
  with Negation[Done[T]] {
  def name: String = "done"

  def substitute(subSet: SubstitutionSet): Done[_ <: Action] =
    Done(action.substitute(subSet))
}
