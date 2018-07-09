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
import fr.dubuissonduplessis.dogma.triggerable.Triggerable
import fr.dubuissonduplessis.dogma.instantiable.Generator

/**
 * Abstract base class representing a description of event(s).
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 */
abstract class Description
  extends Triggerable
  with Generator[Description, Event] {

  def persistent: Boolean = false

  // isTriggeredBy <=> isSatisfiedBy
  def isSatisfiedBy(e: Event): Boolean =
    isConcernedBy(e) && !isViolatedBy(e) && !persistent

  // Negation
  /**
   * Determines if this description is a negation, or not
   * @return true if this description is a negation, else false
   */
  def isNegation: Boolean =
    false
}
  
