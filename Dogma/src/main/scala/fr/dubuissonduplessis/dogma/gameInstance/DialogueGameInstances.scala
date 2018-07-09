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

import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
import fr.dubuissonduplessis.dogma.dialogue.Dialogue
import fr.dubuissonduplessis.dogma.event.Event

/**
 * Module refining [[fr.dubuissonduplessis.dogma.gameInstance.GameInstances]] by adding dialogue
 * game instances.
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait DialogueGameInstances extends GameInstances {
  this: GameInstancesRequirements =>

  // Game State
  /**
   * Possible states of a dialogue game instance: success, failure or inprogress.
   */
  protected type GameState = GameState.Value
  protected object GameState extends Enumeration {
    type GameState = Value
    val SUCCESS, FAILURE, INPROGRESS = Value
  }

  // DialogueGame State
  /**
   * Dialogue game state relative to a player point of view.
   * Defined as a pair which represents the player and the state of the game for this player.
   */
  protected type DialogueGameState = (Interlocutor, GameState)

  /**
   * Computes a default dialogue game state for a given set of participants.
   * The game state is set to 'inprogress' for each player.
   * @param participants participants of the dialogue game
   * @return a default dialogue game state where the states are 'inprogress' for every players
   */
  protected def defaultGameState(participants: Set[Interlocutor]): Set[DialogueGameState] =
    for (i <- participants)
      yield ((i, GameState.INPROGRESS))

  // Contextualisation State
  /**
   * Contextualisation state of a dialogue game instance. It could be: suggested, open or closed.
   */
  protected type ContextualisationState = ContextualisationState.Value
  protected object ContextualisationState extends Enumeration {
    type ContextualisationState = Value
    val SUGGESTED, OPEN, CLOSED = Value

    /**
     * @return default contextualisation state (suggested)
     */
    def default: ContextualisationState =
      SUGGESTED
  }

  // Dialogue Game Instance
  /**
   * Dialogue game instance.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected abstract class DialogueGameInstance extends GameInstance {
    def isDialogueGame: Boolean =
      true
  }

  /**
   * Factory method for dialogue game instances.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected object DialogueGameInstance {
    /**
     * Creates a dialogue game instance for given initiator and partner of the dialogue game.
     * @param initiator initiator of the dialogue game
     * @param partner partner of the dialogue game
     * @return a dialogue game instance for given initiator and partner of the dialogue game
     */
    def apply(initiator: Interlocutor, partner: Interlocutor): DialogueGameInstance =
      apply()

    protected def apply(): DialogueGameInstance =
      new DialogueGameInstanceImpl(
        GameID.generateId)

    private class DialogueGameInstanceImpl(
      val id: GameID)
      extends DialogueGameInstance {

      override def equals(other: Any): Boolean = other match {
        case that: DialogueGameInstance =>
          this.id == that.id
        case _ => false
      }

      override def hashCode: Int = id.hashCode

      override def toString: String =
        id.toString
    }
  }
}
