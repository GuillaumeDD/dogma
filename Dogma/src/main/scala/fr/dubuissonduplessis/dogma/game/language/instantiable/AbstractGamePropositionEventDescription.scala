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
package fr.dubuissonduplessis.dogma.game.language.instantiable

import fr.dubuissonduplessis.dogma.instantiable.impl.NonGenerator
import fr.dubuissonduplessis.dogma.instantiable.InstantiationVariable
import fr.dubuissonduplessis.dogma.instantiable.Instantiable
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
import fr.dubuissonduplessis.dogma.event.dialogueAct.DialogueAct
import fr.dubuissonduplessis.dogma.description.EventDescription
import fr.dubuissonduplessis.dogma.event.Event
import fr.dubuissonduplessis.dogma.description.Description
import fr.dubuissonduplessis.dogma.operation.Operation
import fr.dubuissonduplessis.dogma.instantiable.InstantiationSet

/**
 * Abstract single event description of a standard dialogue act description which content is an
 * uninstantiated composite dialogue game proposition.
 *
 * This single event description is an instantiable but not a generator.
 * It is meant to describe a [[fr.dubuissonduplessis.dogma.event.dialogueAct.StandardDialogueAct]].
 *
 * To build such a description,
 *    see [[fr.dubuissonduplessis.dogma.game.language.instantiable.AbstractGamePropositionEventDescription.apply]]
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 * @tparam GeneratedType type of the result of the instantiation process
 */
sealed abstract class AbstractGamePropositionEventDescription[GeneratedType <: EventDescription]
  extends EventDescription
  with Instantiable[GeneratedType]
  with NonGenerator[GeneratedType, Event] {

  /**
   * Locutor of the [[fr.dubuissonduplessis.dogma.event.dialogueAct.StandardDialogueAct]]
   */
  def locutor: Interlocutor
  /**
   * Name of the  [[fr.dubuissonduplessis.dogma.event.dialogueAct.StandardDialogueAct]]
   */
  def name: String
  /**
   * Uninstantiated dialogue game proposition which represents the uninstantiated content of the
   * dialogue act.
   * @see [[fr.dubuissonduplessis.dogma.game.language.instantiable.GamePropositionVariable]]
   */
  //def content: Instantiable[CompositeGamePropositionMatcher]
  def content: VariableCombination

  // Instantiable: forward to the Instantiable
  def isInstantiableWith(s: InstantiationSet): Boolean =
    content.isInstantiableWith(s)
  def variables: Set[InstantiationVariable] =
    content.variables

  // TO IMPLEMENT
  protected def instantiateWithImpl(s: InstantiationSet): GeneratedType

  // Triggerable
  def expectedDescription(): Option[Description] =
    Some(this)

  def expectedEvent(): List[EventDescription] =
    List(this)

  def expects(evt: Event): Boolean =
    evt match {
      case da: DialogueAct =>
        da.locutor == locutor && da.name == name
      case _ =>
        false
    }

  def isViolatedBy(e: Event): Boolean =
    false

  def isConcernedBy(e: Event): Boolean =
    expects(e)

  def operationToExecute(e: Event): Option[Operation] =
    None

  override def toString: String =
    s"$name($locutor, $content)"
}

/**
 * Factory for [[fr.dubuissonduplessis.dogma.game.language.instantiable.AbstractGamePropositionEventDescription]].
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
object AbstractGamePropositionEventDescription {
  /**
   * Builds an [[fr.dubuissonduplessis.dogma.game.language.instantiable.AbstractGamePropositionEventDescription]]
   * @param locutor locutor of the [[fr.dubuissonduplessis.dogma.event.dialogueAct.StandardDialogueAct]]
   * @param name name of the [[fr.dubuissonduplessis.dogma.event.dialogueAct.StandardDialogueAct]]
   * @param content uninstantiated dialogue game proposition which represents the uninstantiated content of the dialogue act.
   * @param eventDescriptionBuilder builder for the fully instantiated dialogue act
   * @see [[fr.dubuissonduplessis.dogma.game.language.instantiable.EventDescriptionBuilder]]
   */
  def apply(
    locutor: Interlocutor,
    name: String,
    //content: Instantiable[CompositeGamePropositionMatcher],
    content: VariableCombination,
    eventDescriptionBuilder: EventDescriptionBuilder): EventDescription =
    GenericInstantiable(locutor, name, content, eventDescriptionBuilder)

  private case class GenericInstantiable(
    locutor: Interlocutor,
    name: String,
    //content: Instantiable[CompositeGamePropositionMatcher],
    content: VariableCombination,
    eventDescriptionBuilder: EventDescriptionBuilder)
    extends AbstractGamePropositionEventDescription[CompositeGamePropositionMatcherEventDescription[EventDescription]] {
    protected def instantiateWithImpl(s: InstantiationSet): CompositeGamePropositionMatcherEventDescription[EventDescription] =
      CompositeGamePropositionMatcherEventDescription(
        locutor,
        name,
        content.instantiateWith(s),
        eventDescriptionBuilder)
  }
}
