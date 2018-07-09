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
package fr.dubuissonduplessis.dogma.ditpp.informationTransfer

import fr.dubuissonduplessis.dogma.event.dialogueAct.NonStandardDialogueAct
import fr.dubuissonduplessis.dogma.ditpp.impl.TaskDialogueAct
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
import fr.dubuissonduplessis.dogma.instantiable.Variable
import fr.dubuissonduplessis.dogma.description.EventDescription
import fr.dubuissonduplessis.dogma.description.dialogueAct.GenericNonStandardDialogueActEventDescription

case class DeclineCorrection[Content1, Content2](
  locutor: Interlocutor,
  content1: Content1,
  content2: Content2)
  extends NonStandardDialogueAct[Content1, Content2]
  with TaskDialogueAct {
  val name: String = DeclineCorrection.name

}

/**
 * Factory for [[fr.dubuissonduplessis.dogma.ditpp.informationTransfer.DeclineCorrection]] instances and descriptions of such events.
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 */
object DeclineCorrection {
  /**
   * Name of this dialogue act.
   */
  val name: String = "declineCorrection"

  /**
   * Build a description of this dialogue act with uninstantiated semantic contents.
   * @param interlocutor dialogue participant that produced this dialogue act
   * @param variable1 variable that represents the first uninstantiated semantic content of the dialogue act
   * @param variable2 variable that represents the second uninstantiated semantic content of the dialogue act
   * @tparam PropType type of the content of the "declineCorrection" dialogue act
   * @return a description of this "declineCorrection" dialogue act with an uninstantiated semantic content
   */
  def apply[PropType](
    interlocutor: Interlocutor,
    variable1: Variable[PropType],
    variable2: Variable[PropType]): EventDescription =
    GenericNonStandardDialogueActEventDescription(
      DeclineCorrection.name,
      interlocutor,
      variable1, variable2,
      (i: Interlocutor, c1: PropType, c2: PropType) => DeclineCorrection(i, c1, c2))
}