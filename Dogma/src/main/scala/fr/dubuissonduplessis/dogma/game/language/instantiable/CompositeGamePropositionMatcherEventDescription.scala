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

import fr.dubuissonduplessis.dogma.event.dialogueAct.StandardDialogueAct
import fr.dubuissonduplessis.dogma.game.language.AbstractGameProposition
import fr.dubuissonduplessis.dogma.instantiable.InstantiationVariable
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.dialogueActs.propIn
import fr.dubuissonduplessis.dogma.instantiable.Instantiable
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
import fr.dubuissonduplessis.dogma.description.EventDescription
import fr.dubuissonduplessis.dogma.event.Event
import fr.dubuissonduplessis.dogma.description.Description
import fr.dubuissonduplessis.dogma.operation.Operation
import fr.dubuissonduplessis.dogma.instantiable.InstantiationSet

/**
 * Abstract single event description of a standard dialogue act description which content is a
 * semi-instantiated composite dialogue game proposition.
 *
 * This single event description is an instantiable and a generator.
 * It is meant to describe a [[fr.dubuissonduplessis.dogma.event.dialogueAct.StandardDialogueAct]].
 *
 * To build such a description,
 *    see [[fr.dubuissonduplessis.dogma.game.language.instantiable.CompositeGamePropositionMatcherEventDescription.apply]]
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 * @tparam GeneratedType type of the result of the instantiation process
 */
sealed abstract class CompositeGamePropositionMatcherEventDescription[GeneratedType <: EventDescription]
  extends EventDescription
  with Instantiable[GeneratedType] {
  /**
   * Locutor of the [[fr.dubuissonduplessis.dogma.event.dialogueAct.StandardDialogueAct]]
   */
  def locutor: Interlocutor
  /**
   * Name of the  [[fr.dubuissonduplessis.dogma.event.dialogueAct.StandardDialogueAct]]
   */
  def name: String
  /**
   * Semi-instantiated dialogue game proposition which represents the uninstantiated content of the
   * dialogue act.
   * @see [[fr.dubuissonduplessis.dogma.game.language.instantiable.GamePropositionVariable]]
   */
  def content: CompositeGamePropositionMatcher

  // Instantiable
  def isInstantiableWith(s: InstantiationSet): Boolean =
    content.isInstantiableWith(s)
  def variables: Set[InstantiationVariable] =
    content.variables

  // TO IMPLEMENT
  protected def build(
    locutor: Interlocutor,
    name: String,
    content: AbstractGameProposition): GeneratedType
  protected def instantiateWithImpl(s: InstantiationSet): GeneratedType =
    build(locutor, name, content.instantiateWith(s))

  // Generator
  def fits(t: Event): Boolean =
    isConcernedBy(t)

  protected def bindingsImpl(t: Event): InstantiationSet =
    t match {
      case da: StandardDialogueAct[_] =>
        da.content match {
          case prop: AbstractGameProposition =>
            content.bindings(prop)
        }
    }

  // Triggerable
  def expectedDescription(): Option[Description] =
    Some(this)

  def expectedEvent(): List[EventDescription] =
    List(this)

  def expects(evt: Event): Boolean =
    evt match {
      case da: StandardDialogueAct[_] =>
        // Check that the event description is correct
        if (da.locutor == locutor && da.name == name) {
          // If it is, check that the content fits the content description
          da.content match {
            case prop: AbstractGameProposition =>
              content.fits(prop)
            case _ =>
              false
          }
        } else {
          false
        }
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
 * Factory for [[fr.dubuissonduplessis.dogma.game.language.instantiable.CompositeGamePropositionMatcherEventDescription]].
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
object CompositeGamePropositionMatcherEventDescription {
  /**
   * Builds a [[fr.dubuissonduplessis.dogma.game.language.instantiable.CompositeGamePropositionMatcherEventDescription]]
   * @param interlocutor locutor of the [[fr.dubuissonduplessis.dogma.event.dialogueAct.StandardDialogueAct]]
   * @param actName name of the [[fr.dubuissonduplessis.dogma.event.dialogueAct.StandardDialogueAct]]
   * @param actContent uninstantiated dialogue game proposition which represents the uninstantiated content of the dialogue act.
   * @param eventDescriptionBuilder builder for the fully instantiated dialogue act
   * @see [[fr.dubuissonduplessis.dogma.game.language.instantiable.EventDescriptionBuilder]]
   */
  def apply(interlocutor: Interlocutor,
    actName: String,
    actContent: CompositeGamePropositionMatcher,
    eventDescriptionBuilder: EventDescriptionBuilder): CompositeGamePropositionMatcherEventDescription[EventDescription] =
    new CompositeGamePropositionMatcherEventDescription[EventDescription] {
      val locutor: Interlocutor = interlocutor
      val name: String = actName
      val content: CompositeGamePropositionMatcher = actContent

      protected def build(
        locutor: Interlocutor,
        name: String,
        content: AbstractGameProposition): EventDescription =
        // Build the event description
        eventDescriptionBuilder(locutor, name, content)
    }
}

