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
import fr.dubuissonduplessis.dogma.description.compositeDescription.Negation
import fr.dubuissonduplessis.dogma.description.compositeDescription.Alternative

/**
 * Represents a description of a single event.
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 */
abstract class EventDescription extends EventsDescription
  with Instantiable[EventDescription] {

  // Syntactic sugar
  def *|(desc: EventsDescription): Alternative =
    Alternative(this, desc)

  /**
   * Computes the negation of this description
   * @return the negation of this description
   */
  def neg: EventDescription =
    Negation(this)
}
