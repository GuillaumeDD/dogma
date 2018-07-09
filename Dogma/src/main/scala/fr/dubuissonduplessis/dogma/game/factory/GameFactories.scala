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
package fr.dubuissonduplessis.dogma.game.factory

import fr.dubuissonduplessis.dogma.dialogue.Interlocutor

/**
 * Module providing base traits to create dialogue game factories and communication game factories.
 *
 * It contains two main traits:
 *  - [[fr.dubuissonduplessis.dogma.game.factory.GameFactories#DialogueGameFactory]]
 *  - [[fr.dubuissonduplessis.dogma.game.factory.GameFactories#CommunicationGameFactory]]
 *
 * Game factories can be used by a game repository (see [[fr.dubuissonduplessis.dogma.game.factory.GameRepositories]]).
 * 
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait GameFactories {
  this: GameFactoriesRequirements =>

  /**
   * A game factory is an abstract factory to create communication game or dialogue game.
   * A game factory is identified by a unique name which may correspond to the game name
   * (e.g., "contextualisation", "interrogation").
   *
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected trait GameFactory {
    /**
     * Unique name of the game factory
     */
    def name: String
  }

  // Dialogue Game Instance Factory
  /**
   * A dialogue game instance factory.
   * It should not be confused with [[fr.dubuissonduplessis.dogma.gameInstance.DialogueGameInstances#DialogueGameInstance]].
   * Here, an instance of dialogue game refers to the object-oriented instance of the class [[fr.dubuissonduplessis.dogma.game.DialogueGames#DialogueGame]].
   *
   * A dialogue game instance factory can be easily created from the companion object, see
   * [[[fr.dubuissonduplessis.dogma.game.factory.GameFactories#DialogueGameFactory.fromDialogueGame]]].
   *
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected trait DialogueGameFactory extends GameFactory {
    /**
     * Creates an instance of dialogue game from the given participants and the given goal/theme.
     *
     * @note Precondition: compliantGoal(goal) must be true (see [[fr.dubuissonduplessis.dogma.game.factory.GameFactories#DialogueGameFactory.compliantGoal]])
     * @param initiator initiator of the dialogue game
     * @param partner partner of the dialogue game
     * @param goal goal/theme of the game
     * @return a dialogue game with the given initiator, partner and goal
     */
    def apply[T](
      initiator: Interlocutor,
      partner: Interlocutor,
      goal: T): DialogueGame

    /**
     * Checks that the goal is compliant with the dialogue game.
     * It is a precondition of [[fr.dubuissonduplessis.dogma.game.factory.GameFactories#DialogueGameFactory.apply]].
     * @return true if it is compliant, else false
     */
    def compliantGoal[T](goal: T): Boolean
  }

  /**
   * Factories for dialogue game instance factory.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected object DialogueGameFactory {
    /**
     * Generates a dialogue game instance factory from a dialogue game generator and a cast function
     * @param dgName dialogue game unique name
     * @param dg function that creates a dialogue game given the initiator, the partner, and the goal
     * @param cast function that cast any object to the type of the goal (if not possible, this function must crash)
     * @return a dialogue game instance factory
     */
    def fromDialogueGame[T](
      dgName: String, 
      dg: (Interlocutor, Interlocutor, T) => DialogueGame, 
      cast: Any => T): DialogueGameFactory =
      new DialogueGameFactory {
        def name: String = dgName
        def apply[U](
          initiator: Interlocutor,
          partner: Interlocutor,
          goal: U): DialogueGame =
          dg(initiator, partner, cast(goal))

        def compliantGoal[T](goal: T): Boolean = {
          try {
            // Try to cast the goal
            cast(goal)
            // Compliant goal
            true
          } catch {
            case _: Throwable =>
              // Incorrect goal (cannot be cast)
              false
          }
        }
      }
  }

  // Communication Game Instance Factory
  /**
   * A communication game factory.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected trait CommunicationGameFactory extends GameFactory {
    /**
     * Creates a communication game from the given dialogue participants.
     * @param interlocutors dialogue participants of the communication game
     */
    def apply(
      interlocutors: Set[Interlocutor]): CommunicationGame
  }
}
