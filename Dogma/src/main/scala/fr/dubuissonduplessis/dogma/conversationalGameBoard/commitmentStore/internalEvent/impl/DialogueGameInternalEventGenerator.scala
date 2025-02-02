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
package fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.internalEvent.impl

import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.internalEvent.InternalEventGenerator
import scala.collection.mutable
import fr.dubuissonduplessis.dogma.event.InternalEvent
import fr.dubuissonduplessis.dogma.game.DialogueGameInternalEvents
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.GameManager
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.GameManagerQueries
import fr.dubuissonduplessis.dogma.dialogue.Dialogue
import fr.dubuissonduplessis.dogma.game.DialogueGames
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
import fr.dubuissonduplessis.dogma.gameInstance.DialogueGameInstances
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.GameManagerOperations

/**
 * An internal event generator that computes internal events related to the entry and
 * exit conditions of suggested and opened dialogue games.
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 * @see [[fr.dubuissonduplessis.dogma.game.DialogueGameInternalEvents]]
 */
trait DialogueGameInternalEventGenerator extends InternalEventGenerator
  with DialogueGameInternalEvents {
  this: Dialogue with GameManager with GameManagerOperations with DialogueGameInstances =>

  private val pastComputedEvents = mutable.Set[InternalEvent]()
  private val generatedEvents = mutable.Set[InternalEvent]()

  private[conversationalGameBoard] def pastEvents: Set[InternalEvent] = pastComputedEvents.toSet

  private def addEvent(e: InternalEvent): Unit =
    pastComputedEvents += e

  private def addEvents(evts: Set[InternalEvent]): Unit =
    for (e <- evts) {
      addEvent(e)
    }

  private[conversationalGameBoard] def popEvents(): Unit = {
    addEvents(generatedEvents.toSet)
    generatedEvents.clear
  }

  private[conversationalGameBoard] def internalEvents(lastSpeaker: Option[Interlocutor]): Set[InternalEvent] = {
    generatedEvents ++= gameEvents(lastSpeaker)
    generatedEvents.toSet -- pastEvents
  }

  /**
   * Computes events that may be generated by dialogue game entry and/or exit
   * (failure/success) conditions.
   * @return events that may be generated by dialogue game entry and/or exit (failure/success) conditions
   */
  private[conversationalGameBoard] def gameEvents(lastSpeaker: Option[Interlocutor]): Set[InternalEvent] =
    successConditionsReachedEvent(lastSpeaker) ++
      failureConditionsReachedEvent(lastSpeaker) ++
      entryConditionsReachedEvent(lastSpeaker) ++
      fullEntryConditionsReachedEvent(lastSpeaker) ++
      fullSuccessConditionsReachedEvent(lastSpeaker) ++
      fullFailureConditionsReachedEvent(lastSpeaker) ++
      fullExitConditionsReachedEvent(lastSpeaker)

  private def successConditionsReachedEvent(lastSpeaker: Option[Interlocutor]): Set[InternalEvent] = //Set[SuccessConditionsReached] =
    (for { // Event: SuccessConditionsReached
      g <- openedDialogueGames.toList
      dialogueGame = g.dialogueGame
      dp <- dialogueGame.dialogueParticipants
      if (dialogueGame.getSuccessExitConditionsFor(dp)())
    } yield (SuccessConditionsReached(g, dp)(lastSpeaker))).toSet

  private def failureConditionsReachedEvent(lastSpeaker: Option[Interlocutor]): Set[InternalEvent] = //Set[FailureConditionsReached] =
    (for { // Event: FailureConditionsReached
      g <- openedDialogueGames.toList
      dialogueGame = g.dialogueGame
      dp <- dialogueGame.dialogueParticipants
      if (dialogueGame.getFailureExitConditionsFor(dp)())
    } yield (FailureConditionsReached(g, dp)(lastSpeaker))).toSet

  private def entryConditionsReachedEvent(lastSpeaker: Option[Interlocutor]): Set[InternalEvent] = // Set[EntryConditionsReached] =
    (for { // Event: EntryConditionsReached
      g <- suggestedDialogueGames // Here, we only treat suggested games
      dialogueGame = g.dialogueGame
      dp <- dialogueGame.dialogueParticipants
      if (dialogueGame.getEntryConditionsFor(dp)())
    } yield (EntryConditionsReached(g, dp)(lastSpeaker))).toSet

  private def fullEntryConditionsReachedEvent(lastSpeaker: Option[Interlocutor]): Set[InternalEvent] = // Set[FullEntryConditionsReached] =
    (for { // Event: FullEntryConditionsReached
      g <- suggestedDialogueGames // Here, we only treat suggested games
      dialogueGame = g.dialogueGame
      if ( // Conjonction of entry conditions is met
        (dialogueGame.dialogueParticipants foldLeft true)((result, dp) => result && dialogueGame.getEntryConditionsFor(dp)()))
    } yield (FullEntryConditionsReached(g)(lastSpeaker))).toSet

  private def fullSuccessConditionsReachedEvent(lastSpeaker: Option[Interlocutor]): Set[InternalEvent] = // Set[FullSuccessConditionsReached] =
    (for { // Event: FullSuccessConditionsReached
      g <- openedDialogueGames.toList
      dialogueGame = g.dialogueGame
      if ( // Disjonction of success conditions is met
        (dialogueGame.dialogueParticipants foldLeft false)((result, dp) => result || dialogueGame.getSuccessExitConditionsFor(dp)()))
    } yield (FullSuccessConditionsReached(g)(lastSpeaker))).toSet

  private def fullFailureConditionsReachedEvent(lastSpeaker: Option[Interlocutor]): Set[InternalEvent] = //Set[FullFailureConditionsReached] =
    (for { // Event: FullFailureConditionsReached
      g <- openedDialogueGames.toList
      dialogueGame = g.dialogueGame
      if ( // Disjonction of failure conditions is met
        (dialogueGame.dialogueParticipants foldLeft false)((result, dp) => result || dialogueGame.getFailureExitConditionsFor(dp)()))
    } yield (FullFailureConditionsReached(g)(lastSpeaker))).toSet

  private def fullExitConditionsReachedEvent(lastSpeaker: Option[Interlocutor]): Set[InternalEvent] = // Set[ExitConditionsReached] =
    (for { // Event: ExitConditionsReached
      g <- openedDialogueGames.toList
      dialogueGame = g.dialogueGame
      if ( // Disjonction of exit conditions is met
        (dialogueGame.dialogueParticipants foldLeft false)((result, dp) => result || dialogueGame.getSuccessExitConditionsFor(dp)() || dialogueGame.getFailureExitConditionsFor(dp)()))
    } yield (ExitConditionsReached(g)(lastSpeaker))).toSet
}
