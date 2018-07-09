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
package fr.dubuissonduplessis.dogma.dialogue.impl

import fr.dubuissonduplessis.dogma.dialogue.Dialogue
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor

/**
 * Implementation of the [[fr.dubuissonduplessis.dogma.dialogue.Dialogue]] module for a two-party dialogue.
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait TwoInterlocutors extends Dialogue {
  /**
   * First dialogue participant of the interaction
   */
  protected def interlocutor01: Interlocutor
  /**
   * Second dialogue participant of the interaction
   */
  protected def interlocutor02: Interlocutor

  protected def interlocutors: Set[Interlocutor] = Set(interlocutor01, interlocutor02)

  /**
   * Determines the other dialogue participant, given one dialogue participant
   */
  protected def otherLocutor(i: Interlocutor): Interlocutor = {
    require(interlocutors.contains(i))
    if (i == interlocutor01) {
      interlocutor02
    } else {
      interlocutor01
    }
  }
}
