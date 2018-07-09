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
package fr.dubuissonduplessis.dogma.event
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor

/**
 * Abstract base class that represents a dialogic event produced by an interlocutor.
 *
 * It is worth noticing that the child abstract class [[fr.dubuissonduplessis.dogma.event.dialogueAct.DialogueAct]]
 * aims at representing events consequent to the occurrence of a dialogue act.
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 */
abstract class DialogicEventFromLocutor extends DialogicEvent {
  /**
   * Returns the dialogue participant that produced this event.
   * @return the dialogue participant that produced this event
   */
  def locutor: Interlocutor
}
