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

import fr.dubuissonduplessis.dogma.instantiable.impl.InstantiableProxy
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
import fr.dubuissonduplessis.dogma.event.dialogueAct.DialogueAct
import fr.dubuissonduplessis.dogma.instantiable.Generator
import fr.dubuissonduplessis.dogma.description.EventDescription
import fr.dubuissonduplessis.dogma.event.Event
import fr.dubuissonduplessis.dogma.instantiable.Variable
import fr.dubuissonduplessis.dogma.description.Description
import fr.dubuissonduplessis.dogma.operation.Operation
import fr.dubuissonduplessis.dogma.instantiable.InstantiationSet
import fr.dubuissonduplessis.dogma.event.dialogueAct.StandardDialogueAct
import fr.dubuissonduplessis.dogma.instantiable.impl.InstantiableDuoProxy

/**
 * Represents an abstract description of a single dialogue act event where the '''semantic content''' is
 * under-specified (see [[fr.dubuissonduplessis.dogma.event.dialogueAct.StandardDialogueAct]]).
 *
 * This abstract class is a common base for two higher-level classes:
 *  - [[fr.dubuissonduplessis.dogma.description.dialogueAct.DialogueActEventDescription]]
 *  - [[fr.dubuissonduplessis.dogma.description.dialogueAct.ConstrainedDialogueActEventDescription]]
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 * @tparam VariableType type of the semantic content of the described dialogue act
 * @tparam GeneratedType type of the description once it is instantiated
 */
sealed abstract class GenericDialogueActEventDescription[VariableType, GeneratedType <: EventDescription]
  extends EventDescription
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
  def content: Variable[VariableType]

  // Generator
  /**
   * Helper method for [[fr.dubuissonduplessis.dogma.description.dialogueAct.GenericDialogueActEventDescription#bindingsImpl]].
   * Its purpose is to compute the variable/semantic content binding from a given event, if it exists.
   * @return an option consisting of the variable/semantic content binding from a given event if it exists, else false
   */
  protected def variableBindingFrom(evt: Event): Option[(Variable[VariableType], VariableType)]

  protected def bindingsImpl(t: Event): InstantiationSet =
    variableBindingFrom(t) match {
      case None =>
        InstantiationSet()
      case Some((v, content)) =>
        InstantiationSet() + (v, content)
    }

  // Triggerable
  def expectedDescription(): Option[Description] =
    Some(GenericDialogueActEventDescription.this)

  def expectedEvent(): List[EventDescription] =
    List(GenericDialogueActEventDescription.this)

  def isViolatedBy(e: Event): Boolean =
    false

  def isConcernedBy(e: Event): Boolean =
    expects(e)

  def operationToExecute(e: Event): Option[Operation] =
    None

  // Generator
  def fits(e: Event): Boolean =
    isConcernedBy(e)

  override def toString: String =
    s"$name($locutor, $content)"
}

/**
 * Represents a dialogue act event description with an under-specified, unconstrained semantic content.
 * This description fits an event if it is a [[fr.dubuissonduplessis.dogma.event.dialogueAct.StandardDialogueAct]]
 * with the same locutor and the same name.
 *
 * For instance:
 * {{{
 * import fr.dubuissonduplessis.dogma.instantiable.Variable
 *
 * // Import default interlocutor
 * import fr.dubuissonduplessis.dogma.dialogue.X
 *
 * // Import answer which includes a factory to build DialogueActEventDescription
 * import fr.dubuissonduplessis.dogma.ditpp.informationTransfer.Answer
 * import fr.dubuissonduplessis.dogma.ditpp.informationTransfer.Inform
 *
 * // Build a DialogueActEventDescription
 * val answerDescription = Answer(X, Variable[Int]("V"))
 * //> answerDescription  : fr.dubuissonduplessis.dogma.description.EventDescription =
 * //  answer(x, V)
 *
 * // Some tests
 * answerDescription.fits(Answer(X, 42))
 * //> res0: Boolean = true
 * answerDescription.fits(Answer(X, -42))
 * //> res1: Boolean = true
 *
 * // Does not work because it is not the same dialogue act event (inform != answer)
 * answerDescription.fits(Inform(X, 42))
 * //> res2: Boolean = false
 * }}}
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 * @tparam VariableType type of the semantic content of the described dialogue act
 * @tparam GeneratedType type of the description once it is instantiated
 */
abstract class DialogueActEventDescription[VariableType, GeneratedType <: EventDescription]
  extends GenericDialogueActEventDescription[VariableType, GeneratedType]
  with InstantiableProxy[GeneratedType, VariableType] {

  def expects(evt: Event): Boolean =
    evt match {
      case da: DialogueAct =>
        da.locutor == locutor && da.name == name
      case _ =>
        false
    }

  // Instantiation
  protected def instantiable: Variable[VariableType] =
    content

  protected def update(content: VariableType): GeneratedType =
    buildFrom(content)

  /**
   * Factory method that builds the dialogue act description once the semantic content
   * is specified.
   *
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   * @param content specified semantic content of the dialogue act
   * @return the dialogue act description once the semantic content is specified.
   *
   */
  protected def buildFrom(content: VariableType): GeneratedType

}

/**
 * Represents a dialogue act event description with an under-specified, '''constrained''' semantic content.
 * This description fits an event if it is a [[fr.dubuissonduplessis.dogma.event.dialogueAct.StandardDialogueAct]]
 * with the same locutor and the same name, and '''if the semantic content of the event checks the constraint'''.
 *
 * For instance:
 * {{{
 * import fr.dubuissonduplessis.dogma.instantiable.Variable
 *
 * // Import default interlocutor
 * import fr.dubuissonduplessis.dogma.dialogue.X
 *
 * // Import answer which includes a factory to build DialogueActEventDescription
 * import fr.dubuissonduplessis.dogma.ditpp.informationTransfer.Answer
 * import fr.dubuissonduplessis.dogma.ditpp.informationTransfer.Inform
 *
 * // Build a ConstrainedDialogueActEventDescription
 * import fr.dubuissonduplessis.dogma.instantiable.impl.Instantiated
 * val constraint = new SemanticConstraint[Int] with Instantiated[SemanticConstraint[Int]] {
 *    def name: String = "positive"
 *    def apply(n: Int): Boolean = n > 0
 * }
 *
 * val answerDescription = Answer(X, Variable[Int]("V"), constraint)
 *
 * // Some tests
 * answerDescription.fits(Answer(X, 42))
 * //> res0: Boolean = true
 *
 * // Does not work because < 0
 * answerDescription.fits(Answer(X, -42))
 * //> res1: Boolean = false
 *
 * // Does not work because it is not the same dialogue act event (inform != answer)
 * answerDescription.fits(Inform(X, 42))
 * //> res2: Boolean = false
 * }}}
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 * @tparam VariableType type of the semantic content of the described dialogue act
 * @tparam GeneratedType type of the description once it is instantiated
 */
abstract class ConstrainedDialogueActEventDescription[VariableType, GeneratedType <: EventDescription]
  extends GenericDialogueActEventDescription[VariableType, GeneratedType]
  with InstantiableDuoProxy[GeneratedType, VariableType, SemanticConstraint[VariableType]] {

  /**
   * Predicate representing the constraint about the semantic content the dialogue act event.
   * @return true if the semantic content fulfills the constraint, else false
   */
  def constraint: SemanticConstraint[VariableType]

  def expects(evt: Event): Boolean =
    evt match {
      case da: StandardDialogueAct[VariableType] =>
        da.locutor == locutor && da.name == name &&
          // Check the constraint
          constraint(da.content)
      case _ =>
        false
    }

  // Instantiable
  protected def instantiable1: Variable[VariableType] =
    content
  protected def instantiable2: SemanticConstraint[VariableType] =
    constraint

  protected def update(content: VariableType, constraint: SemanticConstraint[VariableType]): GeneratedType =
    buildFrom(content, constraint)

  /**
   * Factory method that builds the dialogue act description once the semantic content
   * and the constraint are specified.
   *
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   * @param content specified semantic content of the dialogue act
   * @return the dialogue act description once the semantic content is specified.
   *
   */
  protected def buildFrom(content: VariableType, constraint: SemanticConstraint[VariableType]): GeneratedType

  override def toString: String =
    s"$name($locutor, $content) with " + constraint.toString
}
