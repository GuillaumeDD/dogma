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
import fr.dubuissonduplessis.dogma.description.dialogueAct.ConstrainedDialogueActEventDescription
import fr.dubuissonduplessis.dogma.event.Event
import fr.dubuissonduplessis.dogma.description.dialogueAct.SemanticConstraint

case class Inform[PropType](
  locutor: Interlocutor,
  content: PropType) extends StandardDialogueAct[PropType]
  with TaskDialogueAct {
  val name: String = Inform.name
}

/**
 * Factory for [[fr.dubuissonduplessis.dogma.ditpp.informationTransfer.Inform]] instances and descriptions of such events.
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 */
object Inform {
  /**
   * Name of this dialogue act.
   */
  val name: String = "inform"

  /**
   * Build a description of this dialogue act with an uninstantiated semantic content.
   * @param interlocutor dialogue participant that produced this dialogue act
   * @param variable variable that represents the uninstantiated semantic content of the dialogue act
   * @tparam PropType type of the content of the "inform" dialogue act
   * @return a description of this "inform" dialogue act with an uninstantiated semantic content
   */
  def apply[PropType](
    interlocutor: Interlocutor,
    variable: Variable[PropType]): EventDescription =
    new DialogueActEventDescription[PropType, EventDescription] {
      def name: String = Inform.name
      def locutor: Interlocutor = interlocutor
      def content: Variable[PropType] = variable

      protected def variableBindingFrom(evt: Event): Option[(Variable[PropType], PropType)] =
        evt match {
          case Inform(locutor, cont) =>
            Some((content, cont.asInstanceOf[PropType]))
        }

      protected def buildFrom(content: PropType): EventDescription =
        Inform(locutor, content)
    }

  /**
   * Build a description of this dialogue act with an uninstantiated semantic content and a constraint on the semantic content.
   * @param interlocutor dialogue participant that produced this dialogue act
   * @param v variable that represents the uninstantiated semantic content of the dialogue act
   * @param contentConstraint the constraint about the instantiable semantic content
   * @tparam PropType type of the content of the "inform" dialogue act
   * @return a '''constrained''' description of this "inform" dialogue act with an uninstantiated semantic content
   */
  def apply[PropType](
    interlocutor: Interlocutor,
    v: Variable[PropType],
    contentConstraint: SemanticConstraint[PropType]): EventDescription =
    new ConstrainedDialogueActEventDescription[PropType, EventDescription] {
      def name: String = Answer.name
      def locutor: Interlocutor = interlocutor
      def content: Variable[PropType] = v

      def constraint: SemanticConstraint[PropType] = contentConstraint

      protected def variableBindingFrom(evt: Event): Option[(Variable[PropType], PropType)] =
        evt match {
          case Answer(locutor, cont) =>
            Some((content, cont.asInstanceOf[PropType]))
        }

      protected def buildFrom(content: PropType, constraint: SemanticConstraint[PropType]): EventDescription =
        Inform(locutor, content)
    }

}