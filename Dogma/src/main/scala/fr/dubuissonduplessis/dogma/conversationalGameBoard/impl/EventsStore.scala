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
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
import fr.dubuissonduplessis.dogma.event.DialogicEventFromLocutor
import fr.dubuissonduplessis.dogma.event.Event
import scala.collection.mutable

/**
 * Default implementation of an event store for a
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.ConversationalGameBoard ConversationalGameBoard]].
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait EventsStore {
  this: ConversationalGameBoard =>
  protected def lastSpeaker: Option[Interlocutor] = lastSpeaker_

  protected def lastEvents: List[Event] = lastEvents_.toList

  protected def lastMoves: List[Move] = lastMoves_.toList

  private var lastEvents_ = mutable.ListBuffer[Event]()
  private var lastMoves_ = mutable.ListBuffer[Move]()
  private var lastSpeaker_ : Option[Interlocutor] = None

  private[impl] def saveEvent(e: Event): Unit = {
    // Save the event
    lastEvents_.prepend(e)
    // Save the speaker
    e match {
      case evt: DialogicEventFromLocutor =>
        lastMoves_.prepend(Move(evt.locutor, currentTime, evt))
        lastSpeaker_ = Some(evt.locutor)
      case _ =>
        lastSpeaker_ = None
    }
  }
}
