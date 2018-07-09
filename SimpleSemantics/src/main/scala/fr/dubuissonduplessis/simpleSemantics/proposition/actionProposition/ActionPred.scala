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
import fr.dubuissonduplessis.simpleSemantics.proposition.NegProposition
import fr.dubuissonduplessis.predicateCalculus.substitution.SubstitutionSet
import fr.dubuissonduplessis.simpleSemantics.proposition.Negation
import fr.dubuissonduplessis.simpleSemantics.action.Action

/**
 * Represents the "action" predicate. This proposition represents an utterance such as
 * "Action a should be performed".
 *
 * @param <T> Type of the Action.
 */
case class ActionPred[T <: Action](
  action: T) extends ActionProposition1
  with Negation[ActionPred[T]] {
  def name: String = "action"

  def substitute(subSet: SubstitutionSet): ActionPred[_ <: Action] = {
    ActionPred(action.substitute(subSet))
  }
}
