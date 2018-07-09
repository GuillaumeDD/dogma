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
package fr.dubuissonduplessis.dogma.dialogueManager.impl

import fr.dubuissonduplessis.dogma.dialogueManager.DialogueManager
import fr.dubuissonduplessis.dogma.event.Event
import scala.collection.immutable.Queue
import scala.collection.mutable
import fr.dubuissonduplessis.dogma.event.ExternalEvent

/**
 * Default implementation of the event handling part of a [[fr.dubuissonduplessis.dogma.dialogueManager.DialogueManager DialogueManager]].
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait EventHandler {
  this: DialogueManager =>

  /**
   * @see [[fr.dubuissonduplessis.dogma.dialogueManager.DialogueManager]]
   */
  protected def enqueue(e: ExternalEvent): Unit =
    {
      events_.enqueue(e)
    }

  /**
   * @see [[fr.dubuissonduplessis.dogma.dialogueManager.DialogueManager]]
   */
  private[dialogueManager] def popEvent: Unit =
    {
      if (!events.isEmpty) {
        events_.dequeue
      }
    }

  private val events_ = mutable.Queue[ExternalEvent]()

  /**
   * @see [[fr.dubuissonduplessis.dogma.dialogueManager.DialogueManager]]
   */
  protected def events: Queue[ExternalEvent] = Queue(events_ : _*)
}
