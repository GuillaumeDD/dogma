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

import fr.dubuissonduplessis.dogma.instantiable.Instantiable
import fr.dubuissonduplessis.dogma.event.Event
import fr.dubuissonduplessis.dogma.description.compositeDescription.PersistentConditional
import fr.dubuissonduplessis.dogma.operation.Operation
import fr.dubuissonduplessis.dogma.description.compositeDescription.Conditional
import fr.dubuissonduplessis.dogma.description.compositeDescription.Alternative

/**
 * Represents a description that could be simple or composite, which may be combined
 * to form composite descriptions.
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 */
abstract class EventsDescription extends Description
  with Instantiable[EventsDescription] {

  // Syntactic sugar
  /**
   * Builds an alternative combination (desc1|desc2|...|descn) from this description combined
   * with the given description.
   * @return an alternative combination (desc1|desc2|...|descn) from this description combined with the given description
   */
  def *|(desc: EventsDescription): Alternative

  // Syntactic sugar
  /**
   * Builds a conditional combination (this ==> op) from this description and a given operation.
   * @return a conditional combination (this ==> op) from this description and a given operation.
   */
  def ==>(op: Operation): Conditional =
    Conditional(this, op)
  /**
   * Builds a persistent conditional combination (this =*=> op) from this description and a given operation.
   * @return a persistent conditional combination (this =*=> op) from this description and a given operation
   */
  def =*=>(op: Operation): PersistentConditional =
    PersistentConditional(this, op)
}
