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
package fr.dubuissonduplessis.dogma.description.dialogueAct

import fr.dubuissonduplessis.dogma.description.EventDescription
import fr.dubuissonduplessis.dogma.instantiable.Instantiable
import fr.dubuissonduplessis.dogma.instantiable.Generator
import fr.dubuissonduplessis.dogma.event.Event
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
import fr.dubuissonduplessis.dogma.instantiable.Variable
import fr.dubuissonduplessis.dogma.description.Description
import fr.dubuissonduplessis.dogma.operation.Operation
import fr.dubuissonduplessis.dogma.instantiable.InstantiationVariable
import fr.dubuissonduplessis.dogma.instantiable.InstantiationSet
import fr.dubuissonduplessis.dogma.event.dialogueAct.NonStandardDialogueAct

/**
 * Represents an abstract description of a single non-standard dialogue act event where the
 * semantic contents are under-specified.
 *
 * This abstract class is a common base for two higher-level classes:
 *  - [[fr.dubuissonduplessis.dogma.description.dialogueAct.NonStandardDialogueActEventDescription]]
 *  - [[fr.dubuissonduplessis.dogma.description.dialogueAct.ConstrainedNonStandardDialogueActEventDescription]]
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 * @see Non-standard dialogue acts: [[fr.dubuissonduplessis.dogma.event.dialogueAct.NonStandardDialogueAct]]
 * @see Factory: [[fr.dubuissonduplessis.dogma.description.dialogueAct.GenericNonStandardDialogueActEventDescription$ companion object]]
 *
 * @tparam VariableType1 type of the first semantic content of the described dialogue act
 * @tparam VariableType2 type of the second semantic content of the described dialogue act
 * @tparam GeneratedType type of the description once it is instantiated
 */
sealed abstract class GenericNonStandardDialogueActEventDescription[VariableType1, VariableType2, GeneratedType <: EventDescription]
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
  def content2: Variable[VariableType2]

  // Generator
  def bindingsImpl(evt: Event): InstantiationSet =
    evt match {
      case act: NonStandardDialogueAct[VariableType1, VariableType2] =>
        InstantiationSet() + (content1, act.content1) + (content2, act.content2)
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
          expectsImpl(da.content1, da.content2)
      case _ =>
        false
    }

  /**
   * Checks that this description expects a non-standard dialogue act (which has the same locutor and
   * the same name) given its two semantic contents
   * @param cont1 first semantic content of the non-standard dialogue act
   * @param cont2 second semantic content of the non-standard dialogue act
   * @return true if this description expects a non-standard dialogue act (which has the same locutor and
   * the same name) given its two semantic contents, else false
   */
  protected def expectsImpl(cont1: VariableType1, cont2: VariableType2): Boolean

  // Generator
  def fits(e: Event): Boolean =
    isConcernedBy(e)

  override def toString: String =
    s"$name($locutor, $content1, $content2)"
}

/**
 * Represents a non-standard dialogue act event description with under-specified,
 * unconstrained semantic contents.
 * This description fits an event if it is a [[fr.dubuissonduplessis.dogma.event.dialogueAct.NonStandardDialogueAct]]
 * with the same locutor and the same name.
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 * @tparam VariableType1 type of the first semantic content of the described dialogue act
 * @tparam VariableType2 type of the second semantic content of the described dialogue act
 * @see Factory: [[fr.dubuissonduplessis.dogma.description.dialogueAct.GenericNonStandardDialogueActEventDescription$ companion object]]
 */
sealed abstract class NonStandardDialogueActEventDescription[VariableType1, VariableType2]
  extends GenericNonStandardDialogueActEventDescription[VariableType1, VariableType2, EventDescription] {

  // Instantiable
  def variables: Set[InstantiationVariable] =
    Set(content1, content2)

  def isInstantiableWith(s: InstantiationSet): Boolean =
    content1.isInstantiableWith(s) || content2.isInstantiableWith(s)

  def instantiateWithImpl(s: InstantiationSet): EventDescription =
    (content1.isInstantiableWith(s), content2.isInstantiableWith(s)) match {
      case (true, true) => fullInstantiateWith(content1.instantiateWith(s), content2.instantiateWith(s))
      case (true, false) => leftInstantiateWith(content1.instantiateWith(s), content2)
      case (false, true) => rightInstantiateWith(content1, content2.instantiateWith(s))
      case (false, false) =>
        // Cannot happen since instantiateWithImpl is only called when
        // instantiability is assured
        throw new RuntimeException("Instantiation problem: this case is in theory impossible")
    }

  /**
   * Builds an event description of a non-standard dialogue act from two semantic contents
   * @param c1 first semantic content of the non-standard dialogue act
   * @param c2 second semantic content of the non-standard dialogue act
   */
  protected def fullInstantiateWith(c1: VariableType1, c2: VariableType2): EventDescription

  /**
   * Builds an event description of a non-standard dialogue act from its left semantic content
   * @param c1 first semantic content of the non-standard dialogue act
   * @param c2 uninstantiated second semantic content of the non-standard dialogue act
   */
  protected def leftInstantiateWith(c1: VariableType1, c2: Variable[VariableType2]): EventDescription

  /**
   * Builds an event description of a non-standard dialogue act from its right semantic content
   * @param c1 uninstantiated first semantic content of the non-standard dialogue act
   * @param c2 second semantic content of the non-standard dialogue act
   */
  protected def rightInstantiateWith(c1: Variable[VariableType1], c2: VariableType2): EventDescription

  protected def expectsImpl(cont1: VariableType1, cont2: VariableType2): Boolean =
    true
}

/**
 * Represents a non-standard dialogue act event description with under-specified, '''constrained''' semantic contents.
 * This description fits an event if it is a [[fr.dubuissonduplessis.dogma.event.dialogueAct.NonStandardDialogueAct]]
 * with the same locutor and the same name, and '''if the semantic contents of the event checks the constraint'''.
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 * @tparam VariableType1 type of the first semantic content of the described dialogue act
 * @tparam VariableType2 type of the second semantic content of the described dialogue act
 * @see Factory: [[fr.dubuissonduplessis.dogma.description.dialogueAct.GenericNonStandardDialogueActEventDescription$ companion object]]
 */
sealed abstract class ConstrainedNonStandardDialogueActEventDescription[VariableType1, VariableType2]
  extends GenericNonStandardDialogueActEventDescription[VariableType1, VariableType2, EventDescription] {

  /**
   * Semantic constraint about the semantic contents.
   */
  def constraint: SemanticConstraint[(VariableType1, VariableType2)]

  protected def expectsImpl(cont1: VariableType1, cont2: VariableType2): Boolean =
    constraint((cont1, cont2))

  // Instantiable
  def variables: Set[InstantiationVariable] =
    Set(content1, content2) ++ constraint.variables

  def isInstantiableWith(s: InstantiationSet): Boolean =
    (content1.isInstantiableWith(s) || content2.isInstantiableWith(s)) && constraint.isInstantiableWith(s)

  def instantiateWithImpl(s: InstantiationSet): EventDescription =
    (content1.isInstantiableWith(s), content2.isInstantiableWith(s)) match {
      case (true, true) => fullInstantiateWith(content1.instantiateWith(s), content2.instantiateWith(s), constraint.instantiateWith(s))
      case (true, false) => leftInstantiateWith(content1.instantiateWith(s), content2, constraint.instantiateWith(s))
      case (false, true) => rightInstantiateWith(content1, content2.instantiateWith(s), constraint.instantiateWith(s))
      case (false, false) =>
        // Cannot happen since instantiateWithImpl is only called when
        // instantiability is assured
        throw new RuntimeException("Instantiation problem: this case is in theory impossible")
    }

  /**
   * Builds an event description of a non-standard dialogue act from two semantic contents
   * @param c1 first semantic content of the non-standard dialogue act
   * @param c2 second semantic content of the non-standard dialogue act
   * @param constraint semantic constraint on the semantic contents
   */
  protected def fullInstantiateWith(c1: VariableType1, c2: VariableType2, constraint: SemanticConstraint[(VariableType1, VariableType2)]): EventDescription

  /**
   * Builds an event description of a non-standard dialogue act from its left semantic content
   * @param c1 first semantic content of the non-standard dialogue act
   * @param c2 uninstantiated second semantic content of the non-standard dialogue act
   * @param constraint semantic constraint on the semantic contents
   */
  protected def leftInstantiateWith(c1: VariableType1, c2: Variable[VariableType2], constraint: SemanticConstraint[(VariableType1, VariableType2)]): EventDescription

  /**
   * Builds an event description of a non-standard dialogue act from its right semantic content
   * @param c1 uninstantiated first semantic content of the non-standard dialogue act
   * @param c2 second semantic content of the non-standard dialogue act
   * @param constraint semantic constraint on the semantic contents
   */
  protected def rightInstantiateWith(c1: Variable[VariableType1], c2: VariableType2, constraint: SemanticConstraint[(VariableType1, VariableType2)]): EventDescription

  override def toString: String =
    super.toString + s" with $constraint"
}

/**
 * Factory for [[fr.dubuissonduplessis.dogma.description.dialogueAct.GenericNonStandardDialogueActEventDescription]].
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
object GenericNonStandardDialogueActEventDescription {
  /**
   * Builds an event description of a single non-standard dialogue act given the name of the dialogue act,
   * the speaker that should produce this act, a variable representing its first semantic content,
   * a variable representing its second semantic content, a builder of the non-standard dialogue act,
   * and a constraint.
   *
   * @tparam VariableType1 type of the first semantic content of the described dialogue act
   * @tparam VariableType2 type of the second semantic content of the described dialogue act
   * @param dialogueActName name of the dialogue act
   * @param interlocutor speaker that should produce this act
   * @param content1 a variable representing the first semantic content of this act
   * @param content2 a variable representing the second semantic content of this act
   * @param builder a builder of this dialogue act
   * @param contentConstraint the constraint about the instantiable semantic contents
   * @see [[fr.dubuissonduplessis.dogma.event.dialogueAct.NonStandardDialogueAct]]
   */
  def apply[VariableType1, VariableType2](
    dialogueActName: String,
    interlocutor: Interlocutor,
    content1: Variable[VariableType1],
    content2: Variable[VariableType2],
    builder: (Interlocutor, VariableType1, VariableType2) => EventDescription,
    contentConstraint: SemanticConstraint[(VariableType1, VariableType2)]): EventDescription =
    ConstrainedNonStandardDialogueActEventDescriptionImpl(
      dialogueActName,
      interlocutor,
      content1,
      content2: Variable[VariableType2],
      builder,
      contentConstraint)
  /**
   * Builds an event description of a single non-standard dialogue act given the name of the dialogue act,
   * the speaker that should produce this act, a variable representing its first semantic content,
   * a variable representing its second semantic content, and a builder of the non-standard dialogue act.
   *
   * @tparam VariableType1 type of the first semantic content of the described dialogue act
   * @tparam VariableType2 type of the second semantic content of the described dialogue act
   * @param dialogueActName name of the dialogue act
   * @param interlocutor speaker that should produce this act
   * @param content1 a variable representing the first semantic content of this act
   * @param content2 a variable representing the second semantic content of this act
   * @param builder a builder of this dialogue act
   * @see [[fr.dubuissonduplessis.dogma.event.dialogueAct.NonStandardDialogueAct]]
   */
  def apply[VariableType1, VariableType2](
    dialogueActName: String,
    interlocutor: Interlocutor,
    content1: Variable[VariableType1],
    content2: Variable[VariableType2],
    builder: (Interlocutor, VariableType1, VariableType2) => EventDescription): EventDescription =
    NonStandardDialogueActEventDescriptionImpl(
      dialogueActName,
      interlocutor,
      content1,
      content2,
      builder)

  private case class ConstrainedNonStandardDialogueActEventDescriptionImpl[VariableType1, VariableType2](
    name: String,
    locutor: Interlocutor,
    content1: Variable[VariableType1],
    content2: Variable[VariableType2],
    builder: (Interlocutor, VariableType1, VariableType2) => EventDescription,
    constraint: SemanticConstraint[(VariableType1, VariableType2)]) extends ConstrainedNonStandardDialogueActEventDescription[VariableType1, VariableType2] {

    protected def fullInstantiateWith(
      c1: VariableType1,
      c2: VariableType2,
      newConstraint: SemanticConstraint[(VariableType1, VariableType2)]): EventDescription =
      builder(locutor, c1, c2)

    protected def leftInstantiateWith(
      c1: VariableType1,
      c2: Variable[VariableType2],
      newConstraint: SemanticConstraint[(VariableType1, VariableType2)]): EventDescription =
      ConstrainedLeftNonStandardDialogueActEventDescription(
        locutor,
        name,
        c1,
        c2,
        builder,
        newConstraint)

    protected def rightInstantiateWith(
      c1: Variable[VariableType1],
      c2: VariableType2,
      newConstraint: SemanticConstraint[(VariableType1, VariableType2)]): EventDescription =
      ConstrainedRightNonStandardDialogueActEventDescription(
        locutor,
        name,
        c1,
        c2,
        builder,
        constraint)

  }

  private case class NonStandardDialogueActEventDescriptionImpl[VariableType1, VariableType2](
    name: String,
    locutor: Interlocutor,
    content1: Variable[VariableType1],
    content2: Variable[VariableType2],
    builder: (Interlocutor, VariableType1, VariableType2) => EventDescription)
    extends NonStandardDialogueActEventDescription[VariableType1, VariableType2] {

    protected def fullInstantiateWith(c1: VariableType1, c2: VariableType2): EventDescription =
      builder(locutor, c1, c2)

    protected def leftInstantiateWith(c1: VariableType1, c2: Variable[VariableType2]): EventDescription =
      LeftNonStandardDialogueActEventDescription(
        locutor,
        name,
        c1,
        c2,
        builder)

    protected def rightInstantiateWith(c1: Variable[VariableType1], c2: VariableType2): EventDescription =
      RightNonStandardDialogueActEventDescription(
        locutor,
        name,
        c1,
        c2,
        builder)
  }
}
