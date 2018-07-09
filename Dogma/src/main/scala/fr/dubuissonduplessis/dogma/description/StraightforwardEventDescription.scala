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
package fr.dubuissonduplessis.dogma.description

import fr.dubuissonduplessis.dogma.event.Event
import fr.dubuissonduplessis.dogma.instantiable.impl.InstantiatedGenerator
import fr.dubuissonduplessis.dogma.operation.Operation

/**
 * Abstract base class representing a straightforward event description.
 * This description describes a single event, that is embedded into the description.
 * Note that it exists an implicit conversion from [[fr.dubuissonduplessis.dogma.event.Event]] to
 * [[fr.dubuissonduplessis.dogma.description.EventDescription]] which makes use of this abstract class (see
 * [[fr.dubuissonduplessis.dogma.description]]).
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 */
abstract class StraightforwardEventDescription extends EventDescription
  with InstantiatedGenerator[StraightforwardEventDescription, Event] {
  /**
   * Determines the event which is described.
   * @return the event which is described
   */
  def event: Event

  // Generator
  def fits(e: Event): Boolean =
    isConcernedBy(e)

  def expectedDescription(): Option[Description] =
    Some(this)

  def expectedEvent(): List[EventDescription] =
    List(this)

  def expects(evt: Event): Boolean =
    event == evt

  def isViolatedBy(e: Event): Boolean =
    false

  def isConcernedBy(e: Event): Boolean =
    expects(e)

  def operationToExecute(e: Event): Option[Operation] =
    None

  override def hashCode(): Int =
    event.hashCode()
  override def equals(o: Any): Boolean =
    o match {
      case desc: StraightforwardEventDescription =>
        event == desc.event
      case _ =>
        false
    }

  override def toString: String = event.toString()
}
