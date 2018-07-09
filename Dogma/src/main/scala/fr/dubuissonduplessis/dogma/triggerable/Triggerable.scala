/**
 * *****************************************************************************
 * Copyright (c) 2013 Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr> - initial API and implementation
 * ****************************************************************************
 */
package fr.dubuissonduplessis.dogma.triggerable

import fr.dubuissonduplessis.dogma.event.Event
import fr.dubuissonduplessis.dogma.operation.Operation
import fr.dubuissonduplessis.dogma.description.EventDescription
import fr.dubuissonduplessis.dogma.description.Description

/**
 * Represents a triggerable, that is to say an abstract item that is related to
 * the occurrence of [[fr.dubuissonduplessis.dogma.event.Event]].
 * Basically, triggerable expects or forbids the occurrence of an event.
 *
 * A triggerable possesses certain properties regarding event occurrence:
 *  - it can be '''persistent''' or not
 *  - it can be '''affected''', '''violated''' or '''satisfied''' by the occurrence of an event
 *  - it can be '''satisfied''' by the occurrence of an event
 *  - it can provide an [[fr.dubuissonduplessis.dogma.operation.Operation]] to be executed
 *
 * Default implementations are available for an untriggered triggerable (see [[fr.dubuissonduplessis.dogma.triggerable.impl.Untriggered]]).
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 */
trait Triggerable {
  /**
   * Determines if this triggerable is persistent.
   * A persistent triggerable is never satisfied (see [[fr.dubuissonduplessis.dogma.triggerable.Triggerable#isSatisfiedBy]]).
   * @return true if this triggerable is persistent, else false
   */
  def persistent: Boolean

  /**
   * Determines if this triggerable is affected by the occurrence of the given event.
   * This function helps discriminating triggerables that are concerned by a given event
   * from those that are not.
   *
   * A triggerable that is concerned by an event can be '''violated''' by this event
   * (see [[fr.dubuissonduplessis.dogma.triggerable.Triggerable#isViolatedBy]]), can expect
   * this event (see [[fr.dubuissonduplessis.dogma.triggerable.Triggerable#expects]]) or
   * forbids it (see [[fr.dubuissonduplessis.dogma.triggerable.Triggerable#forbids]]).
   * @return true if this triggerable is concerned by the given event, else false
   */
  def isConcernedBy(e: Event): Boolean

  /**
   * Determines if this triggerable is violated by the occurrence of the given event.
   * A triggerable that is violated by the occurrence of an event forbids it
   * (see [[fr.dubuissonduplessis.dogma.triggerable.Triggerable#forbids]]).
   * @return true if this triggerable is violated by the occurrence of the given event, else false
   */
  def isViolatedBy(e: Event): Boolean

  /**
   * Determines if the occurrence of the given event satisfies this triggerable.
   * Note that a persistent triggerable is never satisfied.
   * @return true if this triggerable is satisfied by the given event, else false
   */
  def isSatisfiedBy(e: Event): Boolean

  /**
   * Determines if this triggerable expects the given event
   * @return true if the given event is expected by this triggerable, else false
   */
  def expects(e: Event): Boolean

  /**
   * Determines if this triggerable forbids the given event
   * @return true if the given event is forbidden by this triggerable, else false
   */
  def forbids(e: Event): Boolean =
    isConcernedBy(e) && isViolatedBy(e)

  /**
   * Computes the disjunction of [[fr.dubuissonduplessis.dogma.triggerable.Triggerable#expects]]
   * and [[fr.dubuissonduplessis.dogma.triggerable.Triggerable#forbids]].
   * @return the disjunction of [[fr.dubuissonduplessis.dogma.triggerable.Triggerable#expects]]
   * and [[fr.dubuissonduplessis.dogma.triggerable.Triggerable#forbids]].
   */
  def expectsOrForbids(e: Event): Boolean =
    expects(e) || forbids(e)

  /**
   * Computes the operation to execute in case of the occurrence of a given event.
   * @return an option consisting of an operation to be executed if it exists, else an empty option
   */
  def operationToExecute(e: Event): Option[Operation]

  /**
   * Computes a list of event description that are
   * expected by this triggerable. As such, this method breaks the
   * knowledge about the combination dependencies relative to this triggerable.
   * @return a list of event descriptions that are expected (that may be empty).
   */
  def expectedEvent(): List[EventDescription]

  /**
   * Computes a description of event(s) that is expected.
   * If nothing is expected, it returns an empty option.
   * @return a description of an action that is expected, or an empty option if
   * nothing is expected
   */
  def expectedDescription(): Option[Description]
}
