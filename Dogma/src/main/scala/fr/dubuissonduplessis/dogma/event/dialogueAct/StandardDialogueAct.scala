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

/**
 * Abstract class that represents a "standard" dialogue act with a semantic content.
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 * @tparam Content type of the semantic content
 */
abstract class StandardDialogueAct[Content] extends DialogueAct {
  /**
   * Returns the semantic content of the standard dialogue act
   * @return the semantic content of the standard dialogue act
   */
  def content: Content

  override def toString: String =
    s"$name($locutor, $content)"
}
