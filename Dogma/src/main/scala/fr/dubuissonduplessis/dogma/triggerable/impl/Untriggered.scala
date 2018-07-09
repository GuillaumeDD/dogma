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
package fr.dubuissonduplessis.dogma.triggerable.impl

import fr.dubuissonduplessis.dogma.event.Event
import fr.dubuissonduplessis.dogma.triggerable.Triggerable
import fr.dubuissonduplessis.dogma.description.EventDescription
import fr.dubuissonduplessis.dogma.operation.Operation
import fr.dubuissonduplessis.dogma.description.Description

/**
 * Represents an untriggered triggerable.
 * An untriggered item is not persistent, never concerned, violated or satisfied by the occurrence of an event.
 * It does not expect nor forbid the occurrence of an event.
 * It does not provide an operation to execute.
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 */
trait Untriggered extends Triggerable {
  def persistent: Boolean = false

  def expects(a: Event): Boolean =
    false

  def isViolatedBy(e: Event): Boolean =
    false

  def isConcernedBy(e: Event): Boolean =
    false

  def isSatisfiedBy(e: Event): Boolean =
    false

  def operationToExecute(e: Event): Option[Operation] =
    None

  def expectedEvent(): List[EventDescription] =
    List()

  def expectedDescription(): Option[Description] =
    None
}
