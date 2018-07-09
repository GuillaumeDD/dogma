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
package fr.dubuissonduplessis.dogma.instantiable

/**
 * Defines an instantiable in the instantiation process.
 *
 * Default implementations are available for the following cases:
 *  - an instantiated instantiable (see [[fr.dubuissonduplessis.dogma.instantiable.impl.Instantiated]])
 *  - a proxy of instantiable (see [[fr.dubuissonduplessis.dogma.instantiable.impl.InstantiableProxy]] and [[fr.dubuissonduplessis.dogma.instantiable.impl.InstantiableDuoProxy]])
 *
 * ==Example==
 * {{{
 * case class Bicycle(bikerName: String)
 *
 * // EmptyBicycle is instantiable as a bicycle
 * case class EmptyBicycle(v: Variable[String]) extends Instantiable[Bicycle] {
 *
 *    def isInstantiableWith(s: InstantiationSet): Boolean =
 *       v.isInstantiableWith(s)
 *
 *    def variables: Set[InstantiationVariable] =
 *       Set(v)
 *
 *    protected def instantiateWithImpl(s: InstantiationSet): Bicycle =
 *       Bicycle(v.instantiateWith(s))
 * }
 *
 * // Creation of an empty bicycle
 * val x = Variable("X")
 * val emptyBike = EmptyBicycle(x)
 *
 * val instSet01 = InstantiationSet()
 * val instSet02 = InstantiationSet() + (Variable("X"), "Alice")
 *
 * // Test instantiability
 * emptyBike.isInstantiableWith(instSet01)
 * //> res0: Boolean = false
 * emptyBike.isInstantiableWith(instSet02)
 * //> res1: Boolean = true
 *
 * // Instantiation
 * emptyBike.instantiateWith(instSet02)
 * //> res2: Bicycle = Bicycle(Alice)
 * }}}
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 * @tparam T type of the  instantiation process output of this instantiable
 */
trait Instantiable[+T] {
  /**
   * Determines if the instantiation process can produce an output given an instantiation set.
   * This predicate is a precondition of the method [[fr.dubuissonduplessis.dogma.instantiable.Instantiable#instantiateWith]].
   *
   * @param s instantiation set to be used by the instantiation process
   * @return True if the instantiation process can produce an output, else false
   */
  def isInstantiableWith(s: InstantiationSet): Boolean

  /**
   * Realizes the instantiation process.
   * Implements the template method pattern, see method [[fr.dubuissonduplessis.dogma.instantiable.Instantiable#instantiateWithImpl]]
   * to implement the instantiation process.
   * @note Precondition: isInstantiableWith(s) must be true.
   * @param s instantiation set to be used by the instantiation process
   * @return the instantiated object produced by the instantiation process
   */
  def instantiateWith(s: InstantiationSet): T =
    {
      require(isInstantiableWith(s), s"$this is not instantiable with set: $s")
      instantiateWithImpl(s)
    }

  /**
   * Realizes the instantiation process.
   * This method is called by [[fr.dubuissonduplessis.dogma.instantiable.Instantiable#instantiateWith]]
   * after checking the preconditions.
   * It is totally safe to assume the instantiability of this instantiable with the given instantiation set
   * in this method.
   * @param s instantiation set to be used by the instantiation process
   * @return the instantiated object produced by the instantiation process
   */
  protected def instantiateWithImpl(s: InstantiationSet): T

  /**
   * Determines the set of variables contained in this instantiable.
   * @return set of instantiation variables contained in this instantiable.
   */
  def variables: Set[InstantiationVariable]

  /**
   * Determines if this instantiable contains one or more variables.
   * @return true if this instantiable contains one or more variables, else false.
   */
  def containsInstantiationVariable: Boolean =
    variables.size > 0

  /**
   * Determines if this instantiable is already instantiated (i.e., does not contain variables) or not.
   * @return true if this instantiable contains one or more variables, else false.
   */
  def instantiated: Boolean =
    !containsInstantiationVariable
}
