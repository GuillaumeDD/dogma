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
package fr.dubuissonduplessis.dogma
import fr.dubuissonduplessis.dogma.event.Event
import fr.dubuissonduplessis.dogma.instantiable.impl.Instantiated
import fr.dubuissonduplessis.dogma.instantiable.impl.InstantiatedGenerator
import fr.dubuissonduplessis.dogma.operation.Operation

/**
 * Provides classes, traits and functions for dealing with descriptions of event.
 * Also provides implicit for converting [[fr.dubuissonduplessis.dogma.event.Event]] into
 * [[fr.dubuissonduplessis.dogma.description.EventDescription]].
 *
 * ==Overview==
 * In broad outline, description can be viewed as '''"simple"''' description of a single event, and as
 * '''composite''' description that combines simple ones.
 *
 * ==="Simple" event description===
 * The main class of simple description is [[fr.dubuissonduplessis.dogma.description.EventDescription]].
 * Note that an [[fr.dubuissonduplessis.dogma.event.Event]] is implicitly an [[fr.dubuissonduplessis.dogma.description.EventDescription]].
 * Sub-package [[fr.dubuissonduplessis.dogma.description.dialogueAct]] provides classes to deal with more complex
 * description of single events. Here follows an example which help to grasp the idea of simple event description:
 * {{{
 * // Import predefined interlocutor
 * import fr.dubuissonduplessis.dogma.dialogue.X
 * import fr.dubuissonduplessis.dogma.dialogue.Y
 *
 * // Import the "Answer" dialogue act event
 * import fr.dubuissonduplessis.dogma.ditpp.informationTransfer.Answer
 *
 * // Creation of a description through an implicit conversion
 * val description: EventDescription = Answer(X, "scala")
 * //> description  : fr.dubuissonduplessis.dogma.description.EventDescription = answer(x , scala)
 *
 * // Some expectation examples of this description
 * description.expects(Answer(Y, "java"))
 * //> res0: Boolean = false
 * description.expects(Answer(X, "scala"))
 * //> res1: Boolean = true
 * }}}
 *
 * ===Composite event description===
 * Composite event descriptions combine single event description to form advanced descriptions. There are
 * explained in the sub-package [[fr.dubuissonduplessis.dogma.description.compositeDescription]].
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 */
package object description {
  /**
   * Implicit conversion from [[fr.dubuissonduplessis.dogma.event.Event]] to [[fr.dubuissonduplessis.dogma.description.EventDescription]].
   *
   */
  implicit class EventToEventDescription(val event: Event)
    extends StraightforwardEventDescription

  /**
   * Tries to generate an event from a straightforward event description.
   *
   * Note that this function relies on the abstract class [[fr.dubuissonduplessis.dogma.description.StraightforwardEventDescription]].
   * @return A non-empty option consisting of the event if the description is a straightforward event description, else an empty option.
   */
  def eventFromEventDescription(description: EventDescription): Option[Event] =
    description match {
      case d: StraightforwardEventDescription =>
        Some(d.event)
      case _ =>
        None
    }

  /**
   * Tries to generate events from a description.
   * This function considers [[fr.dubuissonduplessis.dogma.description.EventDescription]] expected by this description and
   * tries to turn these event descriptions into event thanks to [[fr.dubuissonduplessis.dogma.description#eventFromEventDescription]].
   *
   * Note that this function relies on the abstract class [[fr.dubuissonduplessis.dogma.description.StraightforwardEventDescription]].
   */
  def eventsFromDescription(desc: Description): List[Event] =
    desc.expectedEvent.flatMap(eventFromEventDescription(_))

  /**
   * Checks if it exists an expected event from this description that verifies a predicate.
   */
  def eventDescriptionMatches(
    desc: Description,
    pred: Event => Boolean): Boolean =
    eventsFromDescription(desc).exists(pred)

}
