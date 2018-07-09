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
package fr.dubuissonduplessis.dogma.examples.scenario

import scala.collection.immutable.Queue
import fr.dubuissonduplessis.dogma.event.ExternalEvent

/**
 * An abstract scenario event handler.
 * A scenario event handler is build around two tasks:
 *  - enqueue external events
 *  - consume external event
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
abstract class EventHandler {
  // Event handling
  def enqueue(e: ExternalEvent): Unit
  def events: Queue[ExternalEvent]

  /**
   * Consumes the first event of the queue.
   */
  def processFirstEvent(): Unit
  /**
   * Consumes all events of the queue.
   */
  def processAllEvents(): Unit
}
