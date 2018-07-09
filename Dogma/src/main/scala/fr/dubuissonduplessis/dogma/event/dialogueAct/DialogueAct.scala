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
package fr.dubuissonduplessis.dogma.event.dialogueAct

import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
import fr.dubuissonduplessis.dogma.event.DialogicEventFromLocutor

/**
 * Abstract class that represents a dialogue act without semantic content (e.g., "greetings", "bye", etc.).
 * Such an act is identified by a '''unique''' name.
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 */
abstract class DialogueAct extends DialogicEventFromLocutor {
  /**
   * '''Unique''' name of a dialogue act.
   * @return the name of the dialogue act
   */
  def name: String
}
