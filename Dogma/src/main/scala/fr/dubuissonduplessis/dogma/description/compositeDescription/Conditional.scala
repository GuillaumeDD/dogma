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
package fr.dubuissonduplessis.dogma.description.compositeDescription

import fr.dubuissonduplessis.dogma.event.Event
import fr.dubuissonduplessis.dogma.instantiable.InstantiationSet
import fr.dubuissonduplessis.dogma.instantiable.Variable
import fr.dubuissonduplessis.dogma.instantiable.Instantiable
import fr.dubuissonduplessis.dogma.description.EventsDescription
import fr.dubuissonduplessis.dogma.description.Description
import fr.dubuissonduplessis.dogma.operation.Operation
import fr.dubuissonduplessis.dogma.description.EventDescription
import fr.dubuissonduplessis.dogma.instantiable.impl.InstantiableDuoProxy
import fr.dubuissonduplessis.dogma.instantiable.impl.GeneratorProxy

/**
 * Represents a conditional description.
 * Conditional descriptions take the following form : (desc1|desc2|...|descn) ==> op where each "desci"
 * is a description of a single event, and "op" is an [[fr.dubuissonduplessis.dogma.operation.Operation]].
 * For instance:
 * {{{
 *   // Import predefined interlocutor
 * import fr.dubuissonduplessis.dogma.dialogue.X
 *
 * // Import the "Answer" dialogue act event
 * import fr.dubuissonduplessis.dogma.ditpp.informationTransfer.Answer
 * import fr.dubuissonduplessis.dogma.ditpp.feedback.ExecNegativeAutoFB
 *
 * // Creations of a description through an implicit conversion
 * import fr.dubuissonduplessis.dogma.description.EventDescription
 * val description1: EventDescription = Answer(X, "scala")
 * val description2: EventDescription = Answer(X, "java")
 * val description3: EventDescription = ExecNegativeAutoFB(X, "scala or java?")
 *
 * // Build up an alternative
 * val alt = description1 *| description2 *| description3
 *
 * // Mockup operation
 * import fr.dubuissonduplessis.dogma.operation.Operation
 * import fr.dubuissonduplessis.dogma.instantiable.impl.Instantiated
 * val op = new Operation with Instantiated[Operation] {
 * override def toString: String = "MyOperation"
 * }
 *
 * // Conditional description
 * val conditional = alt ==> op
 * //> conditional  : fr.dubuissonduplessis.dogma.description.compositeDescription.Conditional =
 * // answer(x, scala)|answer(x, java)|execNegativeAutoFB(x, scala or java?)=>MyOperation
 * }}}
 *
 * ==Instantiation process==
 * The following table details some results of the instantiation and generation process of
 * the description that takes the form: '''desc ==> op'''.
 * <table style="border-collapse: collapse; border: 1px solid black"border="2">
 * <thead>
 * <tr>
 * 	<th scope="col" style="background-color: #CCCCCC; border-width: 1px; padding: 3px; padding-top: 7px; border: 1px solid black; text-align: left">
 *  	<strong>Operation</strong>
 *  </th>
 * 	<th scope="col" style="background-color: #CCCCCC; border-width: 1px; padding: 3px; padding-top: 7px; border: 1px solid black; text-align: left">
 *  	<strong>Results</strong>
 *  </th>
 * </tr>
 * </thead>
 * <tbody>
 * <tr>
 * 	<td style="border-width: 1px; padding: 3px; border: 1px solid black; text-align: right">isInstantiableWith</td>
 *  <td style="border-width: 1px; padding: 3px; border: 1px solid black; text-align: left">
 *  	<strong>Delegated to desc and op</strong> <br/>
 *   	Equivalent to: desc.isInstantiableWith(s) && op.isInstantiableWith(s)
 *  </td>
 * </tr>
 * <tr>
 * 	<td style="border-width: 1px; padding: 3px; border: 1px solid black; text-align: right">variables</td>
 *  <td style="border-width: 1px; padding: 3px; border: 1px solid black; text-align: left">
 *  	<strong>Delegated to desc and op</strong> <br/>
 *   	Equivalent to: desc.variables ++ op.variables
 *  </td>
 * </tr>
 * <tr>
 * 	<td style="border-width: 1px; padding: 3px; border: 1px solid black; text-align: right">instantiateWith</td>
 *  <td style="border-width: 1px; padding: 3px; border: 1px solid black; text-align: left">
 *  	<strong>Delegated to desc and op</strong> <br/>
 *   	Equivalent to: desc.instantiateWith(s) ==> op.instantiateWith(s)
 *  </td>
 * </tr>
 * <tr>
 * 	<td style="border-width: 1px; padding: 3px; border: 1px solid black; text-align: right">fits</td>
 *  <td style="border-width: 1px; padding: 3px; border: 1px solid black; text-align: left">
 *  	<strong>Delegated to desc</strong> <br/>
 *   	Equivalent to: desc.fits(e)
 *  </td>
 * </tr>
 * <tr>
 * 	<td style="border-width: 1px; padding: 3px; border: 1px solid black; text-align: right">bindings</td>
 *  <td style="border-width: 1px; padding: 3px; border: 1px solid black; text-align: left">
 *  	<strong>Delegated to desc</strong> <br/>
 *   	Equivalent to: desc.bindings(e)
 *  </td>
 * </tr>
 * </tbody>
 * </table>
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 * @constructor Creates a new conditional description.
 * @param premisse Premisse which may be a single event description, or an alternative description
 * @param op an operation
 */
case class Conditional(premisse: EventsDescription, op: Operation)
  extends Description
  with InstantiableDuoProxy[Conditional, EventsDescription, Operation]
  with GeneratorProxy[Description, Event, Description] {

  def isConcernedBy(e: Event): Boolean =
    premisse.isConcernedBy(e)

  def isViolatedBy(e: Event): Boolean =
    false

  def expects(e: Event): Boolean =
    false

  def expectedEvent(): List[EventDescription] =
    List()

  def expectedDescription(): Option[Description] =
    None

  def operationToExecute(e: Event): Option[Operation] =
    //if (isTriggeredBy(a)) {
    if (isConcernedBy(e)) {
      Some(op)
    } else {
      None
    }

  // Instantiation
  protected def instantiable1: EventsDescription =
    premisse
  protected def instantiable2: Operation =
    op
  protected def update(instantiable1: EventsDescription, instantiable2: Operation): Conditional =
    Conditional(instantiable1, instantiable2)

  // Generator
  def generator: EventsDescription =
    premisse

  override def toString: String = premisse + "=>" + op
}
