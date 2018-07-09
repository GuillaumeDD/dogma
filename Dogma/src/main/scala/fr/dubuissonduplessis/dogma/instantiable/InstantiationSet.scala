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
import fr.dubuissonduplessis.dogma.instantiable._

/**
 * Defines the instantiation set of the instantiation process. An instantiation set stores
 * the bindings between a variable and its instantiation. These bindings are used in the
 * instantiation process that substitutes a variable with its binding.
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 */
abstract class InstantiationSet {
  /**
   * Determines if the instantiation set is empty.
   * @return True if the instantiation set is empty, else false
   */
  def isEmpty: Boolean
  /**
   * Determines if the instantiation set contains a binding for a given variable.
   * @return True if the instantiation set contains a binding, else false.
   */
  def isBound(v: InstantiationVariable): Boolean
  /**
   * Determines the binding of a given variable.
   * @return An option that contains a binding for the given variable if it exists, else an empty option
   */
  def binding[T](v: Variable[T]): Option[T]
  /**
   * Adds a binding to an instantiation set.
   * @param v variable
   * @param e bindings of the variable
   * @return the new instantiation set with the new binding
   */
  def +[T](v: Variable[T], e: T): InstantiationSet
  /**
   * Merges two instantiation set.
   */
  def ++(instSet: InstantiationSet): InstantiationSet
  /**
   * Retrieves all the bindings of an instantiation set as a list of pairs
   */
  protected def allBindings: List[(InstantiationVariable, Any)]
}

/**
 * Factory for [[fr.dubuissonduplessis.dogma.instantiable.InstantiationSet]].
 *
 */
object InstantiationSet {
  /**
   * Creates a new empty instantiation set.
   */
  def apply(): InstantiationSet =
    InstantiationSetImpl(Map())

  // Default Implemented Substitution Set
  private case class InstantiationSetImpl(bindings: Map[InstantiationVariable, Any])
    extends InstantiationSet {
    def isEmpty: Boolean =
      bindings.isEmpty
    def isBound(v: InstantiationVariable): Boolean =
      bindings.contains(v)

    def +[T](v: Variable[T], e: T): InstantiationSet =
      InstantiationSetImpl(bindings + (v -> e))

    def binding[T](v: Variable[T]): Option[T] =
      bindings.get(v) match {
        case None => None
        case Some(sub) =>
          Some(sub.asInstanceOf[T]) // This cast is safe (see '+' method which ensures the correct type)
      }

    def ++(instSet: InstantiationSet): InstantiationSet =
      InstantiationSetImpl(bindings ++ instSet.allBindings.toMap)

    protected def allBindings: List[(InstantiationVariable, Any)] =
      bindings.toList

    override def toString: String =
      bindings.mkString("\n")
  }
}
