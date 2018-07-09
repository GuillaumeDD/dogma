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
package fr.dubuissonduplessis.dogma.dialogueManager

import fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.ExplicitDogma
import fr.dubuissonduplessis.dogma.event.Event
import scala.collection.immutable.Queue
import fr.dubuissonduplessis.dogma.conversationalGameBoard.ConversationalGameBoard
import fr.dubuissonduplessis.dogma.event.DialogicEvent
import fr.dubuissonduplessis.dogma.event.ExternalEvent

/**
 * Abstract dialogue manager based on a [[fr.dubuissonduplessis.dogma.conversationalGameBoard.ConversationalGameBoard ConversationalGameBoard]].
 *
 * The role of the dialogue manager is twofold.
 * He is responsible for updating the information state following the intervention of
 * dialogical events from the user or the system ('''interpretative role'''), and
 * regulates the communicative behavior of the system ('''generative role''').
 *
 * ==Interpretative Role==
 * The dialogue manager handles a '''processing queue of external events'''. It represents events
 * that have not yet been interpreted by the dialogue manager: these events are awaiting for being
 * considered by the interpretative process. It is possible to add external events to the processing
 * queue and to [[fr.dubuissonduplessis.dogma.dialogueManager.DialogueManager#events access]] this queue.
 * [[fr.dubuissonduplessis.dogma.dialogueManager.impl Sub-package impl]] provides a default implementation
 * of the event handling part.
 *
 * The dialogue manager applies an '''interpretative process''' which updates the information-state
 * on the basis of the occurrence of an external event. It must be defined in the abstract
 * [[fr.dubuissonduplessis.dogma.dialogueManager.DialogueManager#process process method]]. This process is
 * called in the helper methods
 * [[fr.dubuissonduplessis.dogma.dialogueManager.DialogueManager#processFirstEvent processFirstEvent]] and
 * [[fr.dubuissonduplessis.dogma.dialogueManager.DialogueManager#processAllEvents processAllEvents]].
 * This process may take advantage of Dogma on the basis of the
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.ExplicitReferee referee]] module (which is
 * what is done in the [[fr.dubuissonduplessis.dogma.dialogueManager.ExplicitDialogueManager ExplicitDialogueManager]]).
 *
 *
 * ==Generative Role==
 * The dialogue manager applies a '''generative process''' which determines the communicative
 * behaviour of the system. It must be defined in the abstract
 * [[fr.dubuissonduplessis.dogma.dialogueManager.DialogueManager#nextEvents nextEvents method]].
 * This process may take advantage of Dogma on the basis of the
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.ConventionalBehaviourManager conventional behaviour manager]]
 * module.
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 * @see [[fr.dubuissonduplessis.dogma.dialogueManager.ExplicitDialogueManager]]
 */
trait DialogueManager extends ConversationalGameBoard {
  // Event handling
  /**
   * Adds a given external event in the processing queue of the
   * dialogue manager.
   */
  protected def enqueue(e: ExternalEvent): Unit
  /**
   * Adds multiple given external events in the processing queue
   * of the dialogue manager.
   */
  protected def enqueue(evts: ExternalEvent*): Unit =
    for (e <- evts) {
      enqueue(e)
    }

  /**
   * The processing queue of external events of the dialogue
   * manager.
   */
  protected def events: Queue[ExternalEvent]

  /**
   * Removes the first external events of the processing queue
   * of the dialogue manager.
   */
  private[dialogueManager] def popEvent: Unit

  // Interpretation
  /**
   * Interpretative process of the dialogue manager which updates the
   * information-state on the basis of an external event.
   */
  private[dialogueManager] def processEvent(e: ExternalEvent): Unit

  /**
   * Determines if it exists an event in the processing queue that has
   * not been processed.
   * @return true if it exists an event in the processing queue that has
   * not been processed, else false
   */
  protected def hasEvent(): Boolean =
    !events.isEmpty

  /**
   * Processes the first event in the processing queue with the
   * [[fr.dubuissonduplessis.dogma.dialogueManager.DialogueManager#process interpretation algorithm]].
   */
  protected def processFirstEvent(): Unit =
    {
      require(!events.isEmpty)
      // Get the first event
      val firstEvent = events.head

      // Compute the new information state
      processEvent(firstEvent)
      // Pop the first event
      popEvent
    }

  /**
   * Processes all events in the processing queue with the
   * [[fr.dubuissonduplessis.dogma.dialogueManager.DialogueManager#process interpretation algorithm]].
   */
  protected def processAllEvents(): Unit = {
    while (this.hasEvent) {
      this.processFirstEvent
    }
  }

  // Generation
  /**
   * Implements the generative process of the dialogue manager.
   * @return a list of dialogic event that should be produced
   */
  protected def nextEvents(): List[DialogicEvent]

}
