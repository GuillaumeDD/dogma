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
package fr.dubuissonduplessis.dogma.conversationalGameBoard.impl

import fr.dubuissonduplessis.dogma.conversationalGameBoard.ConversationalGameBoard
import fr.dubuissonduplessis.dogma.event.ExternalEvent
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
import fr.dubuissonduplessis.dogma.event.Event
import scala.collection.mutable
import fr.dubuissonduplessis.dogma.event.DialogicEventFromLocutor
import com.typesafe.scalalogging.slf4j.LazyLogging

/**
 * Default implementation of the evolution process of a
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.ConversationalGameBoard ConversationalGameBoard]].
 *
 * The [[fr.dubuissonduplessis.dogma.conversationalGameBoard.impl.DefaultConversationalGameBoard#process process]]
 * algorithm goes as follow:
 *  1. The event is saved into the history and the last speaker is computed.
 *  1. The evolution of the [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.CommitmentStore commitment store]] is
 *      delegated to its own process
 *  1. Internal events are generated, and triggers commitment store evolution (in the same way as the previous step).
 *  1. The time is incremented.
 *
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait DefaultConversationalGameBoard
  extends EventsStore
  with LazyLogging {
  this: ConversationalGameBoard =>

  /**
   * Loads the registered communication games.
   * @see [[fr.dubuissonduplessis.dogma.game.factory.GameRepositories]]
   */
  protected def init(): Unit =
    {
      logger.info("Loading communication games...")
      for ((communicationGameName, factory) <- this.getCommunicationGameFactories) {
        logger.info(s"Loading communication game: $communicationGameName")
        loadCommunicationGameRules(communicationGameName)
      }
      // Advancement of the time
      goToFuture
    }

  /**
   * Process the evolution of the conversational game board given the occurrence of a
   * given external event.
   */
  protected def process(e: ExternalEvent): Unit =
    {
      saveEvent(e)

      processOnCommitmentStore(e)

      // Internal Events Generation
      val internalEventsSet = internalEvents(lastSpeaker)
      logger.info(s"Generating internal events: ${internalEventsSet.mkString(", ")}")
      for (internalEvent <- internalEventsSet) {
        processOnCommitmentStore(internalEvent)
      }
      popEvents

      goToFuture
    }

}
