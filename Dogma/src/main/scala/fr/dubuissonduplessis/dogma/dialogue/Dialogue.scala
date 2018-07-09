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
package fr.dubuissonduplessis.dogma.dialogue

/**
 * Module providing global definition of the dialogue situation in terms of dialogue participants
 * and time.
 *
 * A default implementation of this module for two-party dialogue is [[fr.dubuissonduplessis.dogma.dialogue.impl.TwoInterlocutors]].
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait Dialogue {

  /**
   * Dialogue participants taking part in the interaction
   */
  protected def interlocutors: Set[Interlocutor]
  /**
   * Number of dialogue participants
   */
  protected def nbInterlocutors = interlocutors.size
  /**
   * Determines the dialogue participants taking part in this interaction, different from the given participant
   * @return the dialogue participants taking part in this interaction, different from the given participant
   */
  protected def otherLocutors(i: Interlocutor): Set[Interlocutor] = {
    require(interlocutors.contains(i))
    interlocutors - i
  }

  // Time
  /**
   * Time representation
   */
  type Time = Long
  private var t: Time = _

  /**
   * Current time
   */
  def currentTime: Time = t
  /**
   * Advances the time
   */
  protected def goToFuture: Unit = t += 1
  /**
   * Resets the time
   */
  protected def reset: Unit =
    t = 0
}
