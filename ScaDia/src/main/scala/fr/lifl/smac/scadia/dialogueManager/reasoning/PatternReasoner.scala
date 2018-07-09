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
package fr.lifl.smac.scadia.dialogueManager.reasoning

import fr.dubuissonduplessis.dogma.event.DialogicEvent
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
import scala.collection.mutable
import fr.dubuissonduplessis.dogma.ditpp.informationTransfer.Answer

trait PatternReasoner {
  this: PatternReasonerSelfType =>

  protected def me: Interlocutor

  protected def nextEvents(): List[DialogicEvent] = {
    // GENERAL ALGORITHM FOR THE DIALOGUE MOVE GENERATION
    // Game Selection: we intent to play our part in a dialogue game
    val (successfulDialogueGameStep, events) = playDialogueGame(dialogueGameSpace.salientDialogueGames)
    // If a step has been effectively made in a dialogue game...
    if (successfulDialogueGameStep) {
      //... the agent produces the resulting events
      events match {
        case None =>
          List()
        case Some(evt) =>
          List(evt)
      }
    } else {
      // Consider the contextualisation communication game
      val (_, events) = playCommunicationGames(communicationGameSpace)
      //... the agent produces the resulting events
      events match {
        case None =>
          List()
        case Some(evt) =>
          List(evt)
      }
    }
  }

  /**
   * Generates the system response by taking into account the dialogue game space.
   * This algorithm tries to successfully play one and only one dialogue game. Every dialogue
   * game is tried until one is successfully played.
   * @param dialogueGames Dialogue Game Space
   * @return A pair consisting of a boolean that determines if a dialogue game has been successfully played along with an option consisting in the next dialogic event to be produced
   */
  private[reasoning] def playDialogueGame(dialogueGames: List[DialogueGameInstance]): (Boolean, Option[DialogicEvent]) =
    dialogueGames match {
      case List() =>
        // Every dialogue games has been tried, nothing has been done
        (false, None)
      case dgInstance :: otherGames =>
        import Role._
        // Try to play the current game
        val (success, dialogicEvent) =
          dgInstance.dialogueGame.role(me) match {
            case INITIATOR =>
              playAsInitiator(dgInstance.dialogueGame, dgInstance, dgInstance.relatedCommitments)
            case PARTNER =>
              playAsPartner(dgInstance.dialogueGame, dgInstance, dgInstance.relatedCommitments)
          }

        if (!success) {
          // If the game has not been successfully played, the next game is tried
          playDialogueGame(otherGames)
        } else {
          // The game has been successfully played
          (success, dialogicEvent)
        }
    }

  private[reasoning] def playCommunicationGames(communicationGames: List[CommunicationGame]): (Boolean, Option[DialogicEvent]) =
    communicationGames match {
      case List() =>
        // No communication games are being played
        (false, None)
      case commGameInfo :: otherGames =>
        // Try to play the current game
        val (success, dialogicEvent) = playCommunicationGame(commGameInfo, commGameInfo.relatedCommitments)
        if (!success) {
          // If the game has not been successfully played, the next game is tried
          playCommunicationGames(otherGames)
        } else {
          // The game has been successfully played
          (success, dialogicEvent)
        }
    }

  /**
   * Represents a pattern identified by its name (e.g., "interrogation", "request") and its
   * theme (or goal).
   *
   * @tparam T type of the theme of this pattern
   */
  private[reasoning] case class Pattern[T](name: String, goal: T)

  /**
   * Represents a pattern reasoning algorithm for an instance of a pattern that is being played.
   *
   */
  private[reasoning] trait DialogueGameReasoning {
    /**
     * Instance of the pattern that is attached to this reasoning algorithm
     * @return the instance of the pattern that is managed by this reasoning algorithm
     */
    val instance: GameInstance

    /**
     * Update the state of the pattern reasoning algorithm with the current state of the pattern.
     * @param state State of the pattern in terms of a set of game action commitment
     */
    def update(
      state: Set[GameActionCommitment]): Unit

    /**
     * Run the updated reasoning algorithm. It returns a pair consisting of :
     * 	- a boolean which is set to true if the algorithm worked out something, else false
     *  - an option consisting of a potential dialogue move to play
     * Note that, if the boolean is set to false, then the second result is necessarily a None.
     * @return A pair consisting of a boolean that determines the success of the algorithm, and a potential dialogue move.
     */
    def play(): (Boolean, Option[DialogicEvent])
  }

  // PATTERN <-> REASONING ALGORITHM LINKS

  // Links between pattern which are played as being an initiator and the reasoning algorithm
  private[reasoning] val inProgressDialogueGameAsInitiator = mutable.Map[Pattern[_], DialogueGameReasoning]()
  // Links between pattern which are played as being a partner and the reasoning algorithm  
  private[reasoning] val inProgressDialogueGameAsPartner = mutable.Map[Pattern[_], DialogueGameReasoning]()
  // Links between communication games and the reasoning algorithm
  private[reasoning] val communicationGameReasoner = mutable.Map[CommunicationGame, DialogueGameReasoning]()

  protected def createInitiatorReasoningModule(dg: DialogueGame, instance: DialogueGameInstance): DialogueGameReasoning
  protected def createPartnerReasoningModule(dg: DialogueGame, instance: DialogueGameInstance): DialogueGameReasoning
  protected def createReasoningModule(dg: CommunicationGame): DialogueGameReasoning

  private[reasoning] def loadReasoningModuleForInitiator[T](p: Pattern[T], module: DialogueGameReasoning): Unit = {
    if (inProgressDialogueGameAsInitiator.contains(p)) {
      logger.error(s"A reasoning module for $p is already loaded")
    }
    inProgressDialogueGameAsInitiator += (p -> module)
  }

  private[reasoning] def loadReasoningModuleForPartner[T](p: Pattern[T], module: DialogueGameReasoning): Unit =
    {
      if (inProgressDialogueGameAsPartner.contains(p)) {
        logger.error(s"A reasoning module for $p is already loaded")
      }
      inProgressDialogueGameAsPartner += (p -> module)
    }

  private[reasoning] def loadReasoningModuleFor(
    comm: CommunicationGame,
    module: DialogueGameReasoning): Unit =
    {
      if (communicationGameReasoner.contains(comm)) {
        logger.error(s"A reasoning module for $comm is already loaded")
      }
      communicationGameReasoner += (comm -> module)
    }

  private[reasoning] def playAsInitiator(
    dg: DialogueGame,
    instance: DialogueGameInstance,
    state: Set[GameActionCommitment]): (Boolean, Option[DialogicEvent]) =
    {
      // Selection of the dialogue game reasoning module
      inProgressDialogueGameAsInitiator.get(Pattern(dg.name, dg.goal)) match {
        case None => // No algorithm is bound to this pattern
          // Load the right reasoning module
          loadReasoningModuleForInitiator(
            Pattern(dg.name, dg.goal),
            createInitiatorReasoningModule(dg, instance))
          // Re-intent to play
          playAsInitiator(dg, instance, state)

        case Some(reasoningModule) =>
          // Update perception
          reasoningModule.update(state)
          // Play the game
          reasoningModule.play()
      }
    }

  private[reasoning] def playAsPartner(
    dg: DialogueGame,
    instance: DialogueGameInstance,
    state: Set[GameActionCommitment]): (Boolean, Option[DialogicEvent]) =
    {
      // Selection of the dialogue game reasoning module
      inProgressDialogueGameAsPartner.get(Pattern(dg.name, dg.goal)) match {
        case None => // No algorithm is bound to this pattern
          // Load the right reasoning module
          loadReasoningModuleForPartner(
            Pattern(dg.name, dg.goal),
            createPartnerReasoningModule(dg, instance))
          // Re-intent to play
          playAsPartner(dg, instance, state)

        case Some(reasoningModule) =>
          // Update perception
          reasoningModule.update(state)
          // Play the game
          reasoningModule.play()
      }
    }

  private[reasoning] def playCommunicationGame(
    communicationGame: CommunicationGame,
    state: Set[GameActionCommitment]): (Boolean, Option[DialogicEvent]) =
    {
      // Selection of the communication game reasoning module
      communicationGameReasoner.get(communicationGame) match {
        case None => // No algorithm is bound to this communication game
          // Load the right reasoning module
          loadReasoningModuleFor(
            communicationGame,
            createReasoningModule(communicationGame))
          // Re-intent to play
          playCommunicationGame(communicationGame, state)

        case Some(reasoningModule) =>
          // Update perception
          reasoningModule.update(state)
          // Play the game
          reasoningModule.play()
      }
    }
}
