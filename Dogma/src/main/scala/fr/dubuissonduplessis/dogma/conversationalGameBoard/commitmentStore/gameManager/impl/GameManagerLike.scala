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
package fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.impl

import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.GameManager
import fr.dubuissonduplessis.dogma.game.CommunicationGames
import fr.dubuissonduplessis.dogma.gameInstance.GameInstances
import fr.dubuissonduplessis.dogma.game.DialogueGames
import fr.dubuissonduplessis.dogma.game.language.AbstractGameProposition
import scala.collection.mutable
import com.typesafe.scalalogging.slf4j.LazyLogging
import fr.dubuissonduplessis.dogma.dialogue.Dialogue
import fr.dubuissonduplessis.dogma.game.factory.GameRepositories
import fr.dubuissonduplessis.dogma.dialogue.impl.TwoInterlocutors
import fr.dubuissonduplessis.dogma.event.Event
import fr.dubuissonduplessis.dogma.gameInstance.DialogueGameInstances
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor

/**
 * Default implementation of module [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.GameManager GameManager]].
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait GameManagerLike extends GameManager with LazyLogging {
  this: Dialogue with GameInstances with DialogueGameInstances with DialogueGames with CommunicationGames =>

  private def gameStore: GameStore =
    gameStore_

  private var gameStore_ : GameStore = GameStore()
  // DialogueGameInstance states
  private val dialogueGames = mutable.Map[GameID, DialogueGame]()
  private val dialogueGameInstancesPast = mutable.Map[GameID, List[Event]]().withDefaultValue(List())
  private val dialogueGameInstancesState = mutable.Map[GameID, ContextualisationState]()
  private val dialogueGameInstancesDialogueGameState = mutable.Map[GameID, Set[DialogueGameState]]()
  private val gameInstancesTime = mutable.Map[GameID, Time]()

  private[conversationalGameBoard] class GameStore private (
    private val embeddings: Set[GamePrio],
    val suggestedGameSpace: List[AbstractGameProposition],
    val communicationGamesSet: Set[CommunicationGame],
    val suggestedGames: Set[DialogueGameInstance],
    val openedGames: List[DialogueGameInstance],
    val closedGames: Set[DialogueGameInstance])
    extends LazyLogging {

    private def update(embeddings: Set[GamePrio] = this.embeddings,
      suggestedGameSpace: List[AbstractGameProposition] = this.suggestedGameSpace,
      communicationGamesSet: Set[CommunicationGame] = this.communicationGamesSet,
      suggestedGames: Set[DialogueGameInstance] = this.suggestedGames,
      openedGames: List[DialogueGameInstance] = this.openedGames,
      closedGames: Set[DialogueGameInstance] = this.closedGames): GameStore =
      new GameStore(
        embeddings,
        suggestedGameSpace,
        communicationGamesSet,
        suggestedGames,
        openedGames,
        closedGames)

    // Game embedding
    def embedded(gEmb: GameInstance, gParent: GameInstance): GameStore = {
      logger.info(s"Game instance embedding: $gEmb is embedded in $gParent")
      require(!embeddings.exists(_.<(gParent, gEmb)))
      update(embeddings = this.embeddings + GamePrio(gEmb, gParent))
    }

    def embeddedIn(gEmb: GameInstance, gParent: GameInstance): Boolean =
      embeddings.exists(_.<(gEmb, gParent)) ||
        embeddedIn(gEmb, gParent, gameInstances) // Transitivity

    private def embeddedIn(
      gEmb: GameInstance,
      gParent: GameInstance,
      otherGames: Set[GameInstance]): Boolean =
      embeddings.exists(_.<(gEmb, gParent)) ||
        (for {
          g <- otherGames
          if ((g != gEmb) && (g != gParent))
          newGames = otherGames.filter(_ != gEmb).filter(_ != gParent)
        } yield {
          (embeddedIn(gEmb, g, newGames) && embeddedIn(g, gParent, newGames))
        }).exists(b => b == true)

    // Suggested Game Space
    def pushSuggestedGame(suggestedGame: AbstractGameProposition): GameStore = {
      logger.debug(s"Addition to suggestion game space: $suggestedGame")
      update(suggestedGameSpace = suggestedGame +: this.suggestedGameSpace)
    }

    def firstExtendedSuggestedGameProposition(
      prop: AbstractGameProposition): Option[AbstractGameProposition] =
      suggestedGameSpace.find(fr.dubuissonduplessis.dogma.game.language.isAnExtensionOf(prop, _))

    def belongToSuggestedGameSpace(
      prop: AbstractGameProposition): Boolean =
      suggestedGameSpace.contains(prop)

    def clearSuggestedGame(suggestedGame: AbstractGameProposition): GameStore = {
      logger.debug(s"Removal from suggestion game space: $suggestedGame")
      logger.debug(s"Suggestion game space: ${suggestedGameSpace.mkString("[", " < ", "]")}")
      update(suggestedGameSpace =
        suggestedGameSpace.filterNot(_ == suggestedGame))
    }

    // Game Instance Management
    def addCommunicationGame(cg: CommunicationGame): GameStore =
      update(communicationGamesSet = this.communicationGamesSet + cg)

    def removeCommunicationGame(cg: CommunicationGame): GameStore =
      update(communicationGamesSet = this.communicationGamesSet.filterNot(_ == cg))

    def removeGame(game: DialogueGameInstance): GameStore = {
      update(
        suggestedGames = this.suggestedGames.filter(g => g.id != game.id),
        openedGames = this.openedGames.filter(g => g.id != game.id))
    }

    def removeSuggestedGame(game: DialogueGameInstance): GameStore = {
      require(state(game) == ContextualisationState.SUGGESTED)
      update(suggestedGames = this.suggestedGames - game)
    }

    def addSuggestedGame(game: DialogueGameInstance): GameStore = {
      require(state(game) == ContextualisationState.SUGGESTED)
      update(suggestedGames = this.suggestedGames + game)
    }

    def moveToOpen(game: DialogueGameInstance): GameStore = {
      require(state(game) == ContextualisationState.OPEN)
      update(
        suggestedGames = this.suggestedGames.filter(g => g.id != game.id),
        openedGames = game :: this.openedGames)
    }

    def moveToClosed(game: DialogueGameInstance): GameStore = {
      require(state(game) == ContextualisationState.CLOSED)
      update(
        embeddings = this.embeddings.filter(emb => emb.gEmb != game && emb.gParent != game),
        suggestedGames = this.suggestedGames.filter(g => g.id != game.id),
        openedGames = this.openedGames.filter(g => g.id != game.id),
        closedGames = this.closedGames + game)
    }

    def communicationGames: Set[CommunicationGame] =
      communicationGamesSet

    override def toString: String = "Games:\n" +
      "\tSuggested=" + suggestedGames.mkString("{", ", ", "}") + "\n" +
      "\tOpened=" + openedGames.mkString("[", " < ", "]") + "\n" +
      "\tClosed=" + closedGames.mkString("{", ", ", "}")
  }

  private object GameStore {
    def apply(): GameStore = new GameStore(Set(), List(), Set(), Set(), List(), Set())
  }

  private case class GamePrio(gEmb: GameInstance, gParent: GameInstance) {
    def <(gE: GameInstance, gP: GameInstance): Boolean =
      (gEmb == gE) && (gParent == gP)
  }

  private[conversationalGameBoard] def openedDialogueGames: Set[DialogueGameInstance] =
    gameStore.openedGames.toSet

  private[conversationalGameBoard] def closedDialogueGames: Set[DialogueGameInstance] =
    gameStore.closedGames

  private[conversationalGameBoard] def suggestedDialogueGames: Set[DialogueGameInstance] =
    gameStore.suggestedGames

  private[conversationalGameBoard] def communicationGames: Set[CommunicationGame] =
    gameStore.communicationGames

  private[conversationalGameBoard] def embeddedIn(gEmb: GameInstance, gParent: GameInstance): Boolean =
    gameStore.embeddedIn(gEmb, gParent)

  // Transformers
  private[commitmentStore] def addCommunicationGame(game: CommunicationGame): Unit =
    {
      gameInstancesTime += (game.id -> currentTime)
      gameStore_ = gameStore.addCommunicationGame(game)
    }

  private[commitmentStore] def addSuggestedGame(dialogueGame: DialogueGameInstance): Unit =
    {
      // Change the contextualisation state
      dialogueGameInstancesState += (dialogueGame.id -> ContextualisationState.SUGGESTED)
      gameInstancesTime += (dialogueGame.id -> currentTime)
      dialogueGameInstancesDialogueGameState += (dialogueGame.id -> defaultGameState(interlocutors))
      gameStore_ = gameStore.addSuggestedGame(dialogueGame)
    }

  private[commitmentStore] def addSuggestedGame(
    instance: DialogueGameInstance,
    dg: DialogueGame): Unit = {
    addSuggestedGame(instance)
    // Register the link between the instance and the game
    logger.debug(s"Register the link between the dialogue game $instance and the instance $dg")
    dialogueGames += (instance.id -> dg)
  }

  private[gameManager] def getDialogueGame(instance: DialogueGameInstance): DialogueGame =
    dialogueGames(instance.id)

  private[gameManager] def savePast(e: Event, instance: DialogueGameInstance): Unit =
    {
      val pastEvents = dialogueGameInstancesPast(instance.id)
      dialogueGameInstancesPast += (instance.id -> (e :: pastEvents))
    }

  private[gameManager] def clearPast(instance: DialogueGameInstance): Unit =
    {
      dialogueGameInstancesPast += (instance.id -> List())
    }

  private[gameManager] def getPast(instance: DialogueGameInstance): List[Event] =
    dialogueGameInstancesPast(instance.id)

  private[gameManager] def state(instance: DialogueGameInstance): ContextualisationState =
    dialogueGameInstancesState(instance.id)

  // Implements: fr.dubuissonduplessis.dogma.gameInstance.GameInstances#time
  private[gameManager] def time(instance: GameInstance): Time =
    gameInstancesTime(instance.id)

  private[gameManager] def gameStateFor(
    instance: DialogueGameInstance,
    loc: Interlocutor): GameState =
    {
      val m = dialogueGameInstancesDialogueGameState(instance.id).toMap
      m(loc)
    }

  private[conversationalGameBoard] def getInstance(id: GameID): Option[DialogueGameInstance] = {
    val allInstances = openedDialogueGames ++ closedDialogueGames ++ suggestedDialogueGames
    allInstances.find(_.id == id)
  }

  // Helper method
  private[GameManagerLike] def unregisterDialogueGameInstance(instance: DialogueGameInstance): Unit =
    {
      // Unregister the instance
      dialogueGames -= instance.id
      dialogueGameInstancesPast -= instance.id
      dialogueGameInstancesState -= instance.id
      dialogueGameInstancesDialogueGameState -= instance.id
      gameInstancesTime -= instance.id
    }

  private[commitmentStore] def removeGame(dialogueGame: DialogueGameInstance): Unit =
    {

      gameStore_ = gameStore.removeGame(dialogueGame)
      // Unregister the instance
      unregisterDialogueGameInstance(dialogueGame)
    }

  private[commitmentStore] def removeSuggestedGame(dialogueGame: DialogueGameInstance): Unit =
    {
      gameStore_ = gameStore.removeSuggestedGame(dialogueGame)
      // Unregister the instance
      unregisterDialogueGameInstance(dialogueGame)
    }

  private[commitmentStore] def openGame(dialogueGame: DialogueGameInstance): Unit =
    {
      // Change the contextualisation state
      dialogueGameInstancesState += (dialogueGame.id -> ContextualisationState.OPEN)
      gameInstancesTime += (dialogueGame.id -> currentTime)
      gameStore_ = gameStore.moveToOpen(dialogueGame)
    }

  private[commitmentStore] def closeGame(instance: DialogueGameInstance): Unit =
    {
      // Change the contextualisation state
      dialogueGameInstancesState += (instance.id -> ContextualisationState.CLOSED)
      // Time update
      gameInstancesTime += (instance.id -> currentTime)

      // Game State update
      // First: determine the dialogue game state
      val dialogueGame = getDialogueGame(instance)
      def determineGameState(locutor: Interlocutor): GameState.Value =
        if (dialogueGame.getSuccessExitConditionsFor(locutor)()) {
          GameState.SUCCESS
        } else if (dialogueGame.getFailureExitConditionsFor(locutor)()) {
          GameState.FAILURE
        } else {
          logger.warn(s"Closing an instance which is in progress: $instance")
          GameState.INPROGRESS
        }

      // Then: create the new game state
      val newGameState = for (i <- dialogueGame.dialogueParticipants)
        yield ((i, determineGameState(i)))
      // Finally: register the game state
      dialogueGameInstancesDialogueGameState += (instance.id -> newGameState)

      // Move to close
      gameStore_ = gameStore.moveToClosed(instance)
    }

  private[commitmentStore] def embedded(gEmb: GameInstance, gParent: GameInstance): Unit =
    {
      gameStore_ = gameStore.embedded(gEmb, gParent)
    }

  private[commitmentStore] def pushSuggestedGame(suggestedGame: AbstractGameProposition): Unit =
    {
      gameStore_ = gameStore.pushSuggestedGame(suggestedGame)
    }

  private[commitmentStore] def clearSuggestedGame(suggestedGame: AbstractGameProposition): Unit =
    {
      gameStore_ = gameStore.clearSuggestedGame(suggestedGame)
    }

  private[commitmentStore] def prio(g1: GameInstance, g2: GameInstance): Unit = {
    // Priority between game instance is dealt by the game store and embedding
    logger.warn(s"Calling prio($g1, $g2) has no effect")
  }

}
