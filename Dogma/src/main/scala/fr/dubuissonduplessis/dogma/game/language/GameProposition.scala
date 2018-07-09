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
package fr.dubuissonduplessis.dogma.game.language

import fr.dubuissonduplessis.dogma.instantiable.impl.Instantiated

/**
 * A simple dialogue game proposition that refers to a single dialogue game.
 * For instance:
 * {{{
 * // Builds a game proposition for dialogue game name ("interrogation") and a goal (which is a question)
 * val gameProp = GameProposition("interrogation", "To be or not to be?")
 * }}}
 * 
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 * @tparam GoalType type of the goal of the game
 * @constructor Builds a new dialogue game proposition from the name of the dialogue game and a goal
 * @param name name of the dialogue game
 * @param goal goal/theme of the dialogue game 
 */
case class GameProposition[GoalType](
  val name: String,
  val goal: GoalType)
  extends AbstractGameProposition
  with Instantiated[GameProposition[GoalType]] {

  override def toString: String =
    name + "(" + goal + ")"

  def dropLeft(): Option[AbstractGameProposition] =
    None

  def getLeft(): Option[GameProposition[GoalType]] =
    None

  def rightAssociative(): GameProposition[GoalType] =
    this

  def leftAssociative(): GameProposition[GoalType] =
    this

  def contains(prop: AbstractGameProposition): Boolean =
    this == prop

  override def hashCode: Int =
    41 * (41 + name.hashCode()) + goal.hashCode()

  override def equals(other: Any): Boolean =
    other match {
      case that: GameProposition[GoalType] =>
        (this.name == that.name) &&
          (this.goal == that.goal)
      case _ => false
    }
}
