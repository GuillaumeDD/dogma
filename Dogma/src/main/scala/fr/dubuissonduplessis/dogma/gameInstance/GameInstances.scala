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
package fr.dubuissonduplessis.dogma.gameInstance

import fr.dubuissonduplessis.dogma.dialogue.Dialogue

/**
 * Module providing the definitions and implementations of abstract game instances.
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait GameInstances {
  this: GameInstancesRequirements =>

  // GameID
  /**
   * Unique game ID.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected case class GameID(id: String) {
    override def toString: String = id
  }

  /**
   * Factory for [[fr.dubuissonduplessis.dogma.gameInstance.GameInstances#GameID]].
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected object GameID {
    private var count: Int = -1
    /**
     * Builds a unique game ID
     */
    def generateId: GameID = {
      count = count + 1
      GameID("g" + this.count)
    }
  }

  // GameInstance
  /**
   * Base trait for (dialogue or communication) game instance.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected trait GameInstance {
    /**
     * ID of this instance
     */
    def id: GameID

    /**
     * Determines if this instance is a dialogue game.
     * @return true if this instance refers to a dialogue game, else false
     */
    def isDialogueGame: Boolean
    /**
     * Determines if this instance is a communication game.
     * @return true if this instance refers to a communication game, else false
     */
    def isCommunicationGame: Boolean =
      !isDialogueGame

    override def equals(other: Any): Boolean = other match {
      case that: GameInstance =>
        this.id == that.id
      case _ => false
    }

    override def hashCode: Int =
      41 + id.hashCode
  }

  /**
   * Sorts game instances by ascending time.
   *
   */
  protected def gameInstanceOrdering: Ordering[GameInstance]
}
