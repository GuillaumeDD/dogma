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
 * Abstract class that represents a "non-standard" dialogue act with two semantic contents.
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 * @tparam Content1 type of the first semantic content
 * @tparam Content2 type of the second semantic content
 */
abstract class NonStandardDialogueAct[Content1, Content2] extends DialogueAct {
  /**
   * Returns the first semantic content of the dialogue act
   * @return the first semantic content of the dialogue act
   */
  def content1: Content1

  /**
   * Returns the second semantic content of the dialogue act
   * @return the second semantic content of the dialogue act
   */
  def content2: Content2
}
