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
package fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager

import fr.dubuissonduplessis.dogma.game.CommunicationGames
import fr.dubuissonduplessis.dogma.gameInstance.GameInstances
import fr.dubuissonduplessis.dogma.game.DialogueGames
import fr.dubuissonduplessis.dogma.game.language.AbstractGameProposition
import fr.dubuissonduplessis.dogma.game.language.GameProposition
import fr.dubuissonduplessis.dogma.dialogue.Dialogue
import fr.dubuissonduplessis.dogma.game.factory.GameRepositories
import fr.dubuissonduplessis.dogma.event.Event
import fr.dubuissonduplessis.dogma.gameInstance.DialogueGameInstances
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor

/**
 * Module defining a mutable game manager.
 *
 * This module deals with low-level operations related to dialogue game and communication game instances.
 * It provides the basic operations of a game manager: access to game instance, addition, removal,
 * embedding and prioritarization. Higher level operations are defined by modules
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.GameManagerOperations GameManagerOperations]] and
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.GameManagerQueries GameManagerQueries]].
 *
 * @note Default implementation of this module is provided by [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.impl.GameManagerLike GameManagerLike]]
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait GameManager {
  this: GameManagerRequirements =>

  /**
   * Determines the set of opened dialogue game instances AND communication games.
   *
   */
  private[conversationalGameBoard] def openedGames: Set[GameInstance] =
    openedDialogueGames ++ communicationGames

  /**
   * Determines the set of games that are under discussion
   * (i.e. dialogue game instances under discussion and communication games).
   *
   */
  private[conversationalGameBoard] def activeGames: Set[GameInstance] =
    activeDialogueGames ++ communicationGames

  /**
   * Determines the set of opened dialogue game instances.
   *
   */
  private[conversationalGameBoard] def openedDialogueGames: Set[DialogueGameInstance]
  /**
   * Determines the set of closed dialogue game instances.
   *
   */
  private[conversationalGameBoard] def closedDialogueGames: Set[DialogueGameInstance]
  /**
   * Determines the set of suggested dialogue game instances.
   *
   */
  private[conversationalGameBoard] def suggestedDialogueGames: Set[DialogueGameInstance]
  /**
   * Determines the set of dialogue game instances under discussion
   * (i.e. suggested and opened dialogue game instances).
   *
   */
  private[conversationalGameBoard] def activeDialogueGames: Set[DialogueGameInstance] =
    openedDialogueGames.toSet ++ suggestedDialogueGames

  /**
   * Determines the set of all dialogue game instances (suggested, opened and closed).
   *
   */
  private[conversationalGameBoard] def dialogueGameInstances: Set[DialogueGameInstance] =
    closedDialogueGames ++ openedDialogueGames.toSet ++ suggestedDialogueGames

  /**
   * Determines the set of communication games.
   *
   */
  private[conversationalGameBoard] def communicationGames: Set[CommunicationGame]

  /**
   * Determines the set of all game instances (communication games,
   * dialogue game instances (suggested, opened and closed))
   *
   */
  private[conversationalGameBoard] def gameInstances: Set[GameInstance] =
    // Communication Games
    communicationGames ++
      // Dialogue Games
      suggestedDialogueGames ++
      openedDialogueGames ++
      closedDialogueGames

  /**
   * Determines if the first given game instance is embedded in the second given game instance.
   * @return true if the first given game instance embedded in the second one, else false
   */
  private[conversationalGameBoard] def embeddedIn(gEmb: GameInstance, gParent: GameInstance): Boolean

  // DialogueGameInstance operations

  /**
   * Determines the dialogue game instance associated to a given ID, if it exists.
   *
   * @return an option consisting in the dialogue game instance associated to a given ID if it exists,
   * else false.
   */
  private[conversationalGameBoard] def getInstance(id: GameID): Option[DialogueGameInstance]

  /**
   * Determines the contextualisation state of a given dialogue game instance.
   */
  private[gameManager] def state(instance: DialogueGameInstance): ContextualisationState
  /**
   * Computes the dialogue game state of a given dialogue game instance for a given dialogue participant.
   *
   * @note the given dialogue participant should either be the initiator or the partner of the game
   *
   */
  private[gameManager] def gameStateFor(
    instance: DialogueGameInstance,
    loc: Interlocutor): GameState
  /**
   * Determines the dialogue game associated to the given dialogue game instance.
   */
  private[gameManager] def getDialogueGame(instance: DialogueGameInstance): DialogueGame
  /**
   * Adds a given event to the past history of a given dialogue game instance (side-effect).
   */
  private[gameManager] def savePast(e: Event, instance: DialogueGameInstance): Unit
  /**
   * Clears the history of events of a given dialogue game instance (side-effect).
   */
  private[gameManager] def clearPast(instance: DialogueGameInstance): Unit
  /**
   * Computes the history of events of a given dialogue game instance.
   */
  private[gameManager] def getPast(instance: DialogueGameInstance): List[Event]

  /**
   * Determines if the given dialogue game instance is in contextualisation state 'suggested'
   * @return true if the given dialogue game instance is in contextualisation state 'suggested', else false
   */
  private[gameManager] def isSuggested(g: DialogueGameInstance): Boolean =
    suggestedDialogueGames.contains(g)
  /**
   * Determines if the given dialogue game instance is in contextualisation state 'opened'
   * @return true if the given dialogue game instance is in contextualisation state 'opened', else false
   */
  private[gameManager] def isOpened(g: DialogueGameInstance): Boolean =
    openedDialogueGames.contains(g)
  /**
   * Determines if the given dialogue game instance is in contextualisation state 'closed'
   * @return true if the given dialogue game instance is in contextualisation state 'closed', else false
   */
  private[gameManager] def isClosed(g: DialogueGameInstance): Boolean =
    closedDialogueGames.contains(g)

  // Transformers
  /**
   * Adds a communication game to the set of active communication game.
   * @note it does not load the rules of the communication game
   */
  private[commitmentStore] def addCommunicationGame(game: CommunicationGame): Unit

  /**
   * Adds a suggested dialogue game and links it to a given dialogue game instance.
   * @param instance dialogue game instance referring to the dialogue game
   * @param dg dialogue game that is being suggested
   */
  private[commitmentStore] def addSuggestedGame(
    isntance: DialogueGameInstance,
    dg: DialogueGame): Unit

  /**
   * Removes a dialogue game instance from the game manager.
   */
  private[commitmentStore] def removeGame(instance: DialogueGameInstance): Unit
  /**
   * Removes the dialogue game referred by the given dialogue game instance.
   * @note precondition: the dialogue game must be in state 'suggested'
   */
  private[commitmentStore] def removeSuggestedGame(instance: DialogueGameInstance): Unit
  /**
   * Opens the dialogue game referred by the dialogue game instance.
   * @note precondition: the given dialogue game instance must be in state 'suggested'
   */
  private[commitmentStore] def openGame(instance: DialogueGameInstance): Unit
  /**
   * Closes the dialogue game referred by the dialogue game instance.
   * @note precondition: the given dialogue game instance must be in state 'opened'
   */
  private[commitmentStore] def closeGame(instance: DialogueGameInstance): Unit

  /**
   * Adds a given dialogue game proposition to the suggested dialogue game space.
   */
  private[commitmentStore] def pushSuggestedGame(suggestedGame: AbstractGameProposition): Unit
  /**
   * removes a given dialogue game proposition from the suggested dialogue game space.
   */
  private[commitmentStore] def clearSuggestedGame(suggestedGame: AbstractGameProposition): Unit

  /**
   * Embeds the first dialogue game instance into the second one.
   */
  private[commitmentStore] def embedded(gEmb: GameInstance, gParent: GameInstance): Unit
  /**
   * Prioritizes the first dialogue game instance over the second one.
   */
  private[commitmentStore] def prio(g1: GameInstance, g2: GameInstance): Unit

  /**
   * Determines the last time which has seen a modification of the given game instance.
   * @return the last time which has seen a modification of the given game instance
   */
  private[gameManager] def time(instance: GameInstance): Time
}
