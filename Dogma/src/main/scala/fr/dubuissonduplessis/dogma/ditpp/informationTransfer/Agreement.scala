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

import fr.dubuissonduplessis.dogma.event.dialogueAct.StandardDialogueAct
import fr.dubuissonduplessis.dogma.ditpp.impl.TaskDialogueAct
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
import fr.dubuissonduplessis.dogma.description.dialogueAct.DialogueActEventDescription
import fr.dubuissonduplessis.dogma.instantiable.Variable
import fr.dubuissonduplessis.dogma.description.EventDescription
import fr.dubuissonduplessis.dogma.event.Event

case class Agreement[PropType](
  locutor: Interlocutor,
  content: PropType) extends StandardDialogueAct[PropType]
  with TaskDialogueAct {
  val name: String = Agreement.name
}

/**
 * Factory for [[fr.dubuissonduplessis.dogma.ditpp.informationTransfer.Agreement]] instances and descriptions of such events.
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 */
object Agreement {
  /**
   * Name of this dialogue act.
   */
  val name: String = "agreement"

  /**
   * Build a description of this dialogue act with an uninstantiated semantic content.
   * @param interlocutor dialogue participant that produced this dialogue act
   * @param variable variable that represents the uninstantiated semantic content of the dialogue act
   * @tparam PropType type of the content of the "agreement" dialogue act
   * @return a description of this "agreement" dialogue act with an uninstantiated semantic content
   */
  def apply[PropType](
    interlocutor: Interlocutor,
    variable: Variable[PropType]): EventDescription =
    new DialogueActEventDescription[PropType, EventDescription] {
      def name: String = Agreement.name
      def locutor: Interlocutor = interlocutor
      def content: Variable[PropType] = variable

      protected def variableBindingFrom(evt: Event): Option[(Variable[PropType], PropType)] =
        evt match {
          case Agreement(locutor, cont) =>
            Some((content, cont.asInstanceOf[PropType]))
        }

      protected def buildFrom(content: PropType): EventDescription =
        Agreement(locutor, content)
    }
}