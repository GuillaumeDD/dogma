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
package fr.dubuissonduplessis.dogma.examples.gdd.dialogueGame.action

import fr.dubuissonduplessis.dogma.commitment.Commitments
import fr.dubuissonduplessis.dogma.commitment.SemanticTypes

/**
 * Module that defines the goal of an action-based dialogue game as well as
 * some utility methods.
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait ActionGoal {
  this: Commitments with SemanticTypes =>

  /**
   * Represents the type of the goal (or theme) of an action-based dialogue game.
   */
  type ActionGoal <: ActionContent

  /**
   * Helper method that should try to cast a given object into
   * the type of the goal of an action-based dialogue game.
   * It should throw an exception if the cast is impossible.
   *
   */
  protected def castActionGoal(o: Any): ActionGoal

  /**
   * Computes the negation of a given action.
   */
  def actionNeg(action: ActionGoal): ActionGoal
}
