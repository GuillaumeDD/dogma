/*******************************************************************************
 * Copyright (c) 2014 Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr> - initial API and implementation
 ******************************************************************************/
package fr.dubuissonduplessis.dogma.description.dialogueAct

import fr.dubuissonduplessis.dogma.description.EventDescription
import fr.dubuissonduplessis.dogma.instantiable.InstantiationVariable
import fr.dubuissonduplessis.dogma.event.dialogueAct.NonStandardDialogueAct
import fr.dubuissonduplessis.dogma.instantiable.Instantiable
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
import fr.dubuissonduplessis.dogma.instantiable.Generator
import fr.dubuissonduplessis.dogma.event.Event
import fr.dubuissonduplessis.dogma.instantiable.Variable
import fr.dubuissonduplessis.dogma.description.Description
import fr.dubuissonduplessis.dogma.operation.Operation
import fr.dubuissonduplessis.dogma.instantiable.InstantiationSet

/**
 * Represents an abstract description of a single non standard dialogue act event where the
 * first semantic content is under-specified.
 *
 * 
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 * @see [[fr.dubuissonduplessis.dogma.event.dialogueAct.NonStandardDialogueAct]]
 * @tparam VariableType1 type of the first semantic content of the described dialogue act
 * @tparam VariableType2 type of the second semantic content of the described dialogue act
 * @tparam GeneratedType type of the description once it is instantiated
 */
sealed abstract class GenericRightNonStandardDialogueActEventDescription[VariableType1, VariableType2, GeneratedType <: EventDescription]
  extends EventDescription
  with Instantiable[GeneratedType]
  with Generator[GeneratedType, Event] {
  /**
   * Locutor of the dialogue act
   *
   */
  def locutor: Interlocutor

  /**
   * '''Unique''' name of the dialogue act.
   */
  def name: String

  /**
   * Variable representing the uninstantiated semantic content
   *
   */
  def content1: Variable[VariableType1]

  /**
   * Variable representing the uninstantiated semantic content
   *
   */
  def content2: VariableType2

  // Instantiable
  def variables: Set[InstantiationVariable] =
    Set(content1)

  def isInstantiableWith(s: InstantiationSet): Boolean =
    content1.isInstantiableWith(s)

  def instantiateWithImpl(s: InstantiationSet): GeneratedType =
    fullInstantiateWith(content1.instantiateWith(s), content2)

  /**
   * Builds an event description of a non-standard dialogue act from two semantic contents
   * @param c1 first semantic content of the non-standard dialogue act
   * @param c2 second semantic content of the non-standard dialogue act
   */
  protected def fullInstantiateWith(c1: VariableType1, c2: VariableType2): GeneratedType

  // Generator
  def bindingsImpl(evt: Event): InstantiationSet =
    evt match {
      case act: NonStandardDialogueAct[VariableType1, VariableType2] =>
        InstantiationSet() + (content1, act.content1)
      case _ =>
        InstantiationSet()
    }

  // Triggerable
  def expectedDescription(): Option[Description] =
    Some(this)

  def expectedEvent(): List[EventDescription] =
    List(this)

  def isViolatedBy(e: Event): Boolean =
    false

  def isConcernedBy(e: Event): Boolean =
    expects(e)

  def operationToExecute(e: Event): Option[Operation] =
    None

  def expects(evt: Event): Boolean =
    evt match {
      case da: NonStandardDialogueAct[VariableType1, VariableType2] =>
        da.locutor == locutor && da.name == name &&
          da.content2 == content2 &&
          expectsImpl(da.content1)
      case _ =>
        false
    }

  /**
   * Checks that this description expects a non-standard dialogue act (which has the same locutor,
   * the same name and the same second semantic content) given its first semantic content
   * @param cont1 first semantic content of the non-standard dialogue act
   * @return true if this description expects a non-standard dialogue act (which has the same locutor,
   * the same name and the same second semantic content) given its first semantic content, else false
   */
  protected def expectsImpl(cont1: VariableType1): Boolean

  // Generator
  def fits(e: Event): Boolean =
    isConcernedBy(e)

  override def toString: String =
    s"$name($locutor, $content1, $content2)"
}

sealed abstract class RightNonStandardDialogueActEventDescription[VariableType1, VariableType2, GeneratedType <: EventDescription]
  extends GenericRightNonStandardDialogueActEventDescription[VariableType1, VariableType2, GeneratedType] {
  protected def expectsImpl(cont1: VariableType1): Boolean =
    true
}

object RightNonStandardDialogueActEventDescription {
  def apply[VariableType1, VariableType2](
    interlocutor: Interlocutor,
    dialogueActName: String,
    c1: Variable[VariableType1],
    c2: VariableType2,
    builder: (Interlocutor, VariableType1, VariableType2) => EventDescription): RightNonStandardDialogueActEventDescription[VariableType1, VariableType2, EventDescription] =
    new RightNonStandardDialogueActEventDescription[VariableType1, VariableType2, EventDescription] {
      def locutor: Interlocutor =
        interlocutor

      def name: String =
        dialogueActName

      def content1: Variable[VariableType1] =
        c1

      def content2: VariableType2 =
        c2
      protected def fullInstantiateWith(c1: VariableType1, c2: VariableType2): EventDescription =
        builder(locutor, c1, c2)
    }
}

sealed abstract class ConstrainedRightNonStandardDialogueActEventDescription[VariableType1, VariableType2, GeneratedType <: EventDescription]
  extends GenericRightNonStandardDialogueActEventDescription[VariableType1, VariableType2, GeneratedType] {
  def constraint: SemanticConstraint[(VariableType1, VariableType2)]

  protected def expectsImpl(cont1: VariableType1): Boolean =
    constraint((cont1, content2))

  override def toString: String =
    super.toString + s" with $constraint"
}

object ConstrainedRightNonStandardDialogueActEventDescription {
  def apply[VariableType1, VariableType2](
    interlocutor: Interlocutor,
    dialogueActName: String,
    c1: Variable[VariableType1],
    c2: VariableType2,
    builder: (Interlocutor, VariableType1, VariableType2) => EventDescription,
    semanticConstraint: SemanticConstraint[(VariableType1, VariableType2)]): ConstrainedRightNonStandardDialogueActEventDescription[VariableType1, VariableType2, EventDescription] =
    new ConstrainedRightNonStandardDialogueActEventDescription[VariableType1, VariableType2, EventDescription] {
      def constraint: SemanticConstraint[(VariableType1, VariableType2)] =
        semanticConstraint

      def locutor: Interlocutor =
        interlocutor

      def name: String =
        dialogueActName

      def content1: Variable[VariableType1] =
        c1

      def content2: VariableType2 =
        c2

      protected def fullInstantiateWith(c1: VariableType1, c2: VariableType2): EventDescription =
        builder(locutor, c1, c2)
    }
}
