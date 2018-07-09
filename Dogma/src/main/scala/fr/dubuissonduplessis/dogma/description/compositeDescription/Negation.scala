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
import com.typesafe.scalalogging.slf4j.LazyLogging
import fr.dubuissonduplessis.dogma.instantiable.Instantiable
import fr.dubuissonduplessis.dogma.description.EventsDescription
import fr.dubuissonduplessis.dogma.description.EventDescription
import fr.dubuissonduplessis.dogma.description.Description
import fr.dubuissonduplessis.dogma.operation.Operation
import fr.dubuissonduplessis.dogma.instantiable.impl.InstantiableProxy
import fr.dubuissonduplessis.dogma.instantiable.impl.GeneratorProxy

/**
 * Represents a negation of a single event description.
 * For instance:
 * {{{
 * // Import predefined interlocutor
 * import fr.dubuissonduplessis.dogma.dialogue.X
 *
 * // Import the "Answer" dialogue act event
 * import fr.dubuissonduplessis.dogma.ditpp.informationTransfer.Answer
 *
 * // Creations of a description through an implicit conversion
 * import fr.dubuissonduplessis.dogma.description.EventDescription
 * val description1: EventDescription = Answer(X, "scala")
 *
 * description1.expects(Answer(X, "scala"))
 * //> res0: Boolean = true
 * description1.forbids(Answer(X, "scala"))
 * //> res1: Boolean = false
 *
 * // Creation of a negation
 * val neg1 = description1.neg
 * //> neg1  : fr.dubuissonduplessis.dogma.description.EventDescription =
 * //  neg(answer(x, scala))
 *
 * neg1.expects(Answer(X, "scala"))
 * //> res2: Boolean = false
 * neg1.forbids(Answer(X, "scala"))
 * //> res3: Boolean = true
 * }}}
 *
 * ==Instantiation process==
 * The following table details some results of the instantiation and generation process of
 * the description that takes the form: '''neg(desc)'''.
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
 *  	<strong>Delegated to desc</strong> <br/>
 *   	Equivalent to: desc.isInstantiableWith(s)
 *  </td>
 * </tr>
 * <tr>
 * 	<td style="border-width: 1px; padding: 3px; border: 1px solid black; text-align: right">variables</td>
 *  <td style="border-width: 1px; padding: 3px; border: 1px solid black; text-align: left">
 *  	<strong>Delegated to desc</strong> <br/>
 *   	Equivalent to: desc.variables
 *  </td>
 * </tr>
 * <tr>
 * 	<td style="border-width: 1px; padding: 3px; border: 1px solid black; text-align: right">instantiateWith</td>
 *  <td style="border-width: 1px; padding: 3px; border: 1px solid black; text-align: left">
 *  	<strong>Delegated to desc</strong> <br/>
 *   	Equivalent to: Negation(desc.instantiateWith(s))
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
 * @constructor Creates a negation of a single event description.
 * @param desc The negated single event description
 *
 */
case class Negation(desc: EventDescription)
  extends EventDescription
  with InstantiableProxy[Negation, EventDescription]
  with LazyLogging {

  require(!desc.isNegation,
    s"Impossible to create a negation with $desc which is already a negation. Call the 'neg' method.")

  // Generator
  def fits(e: Event): Boolean =
    isConcernedBy(e)

  def isConcernedBy(e: Event): Boolean =
    desc.isConcernedBy(e)

  def isViolatedBy(e: Event): Boolean =
    desc.isConcernedBy(e)

  def expects(e: Event): Boolean =
    false

  def operationToExecute(e: Event): Option[Operation] =
    None

  def expectedEvent(): List[EventDescription] = List()
  def expectedDescription(): Option[Description] =
    None

  // Negation
  override def isNegation: Boolean =
    true
  override def neg: EventDescription =
    desc

  // Instantiation
  protected def instantiable: EventDescription =
    desc
  protected def update(instantiable: EventDescription): Negation =
    Negation(instantiable)

  // Generator
  protected def bindingsImpl(t: Event): InstantiationSet =
    desc.bindings(t)

  override def toString: String = "neg(" + desc + ")"
}
