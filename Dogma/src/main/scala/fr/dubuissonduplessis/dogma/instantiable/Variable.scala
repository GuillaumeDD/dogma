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
import com.typesafe.scalalogging.slf4j.LazyLogging

/**
 * Defines a variable in the instantiation process.
 * A variable is identified by its name. Two instances of variable with the same name refers to
 * the same variable in the instantiation process.
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 * @tparam T type of the instantiation process output of this variable
 */
trait Variable[+T] extends Instantiable[T] {

  /**
   * Determines the name of the variable.
   * A variable is identified by its name.
   * @return the name of the variable
   */
  def name: String

  protected def instantiateWithImpl(s: InstantiationSet): T =
    s.binding(this).get

  def isInstantiableWith(s: InstantiationSet): Boolean =
    s.binding(this) match {
      case Some(value) => true
      case None => false
    }

  def variables: Set[InstantiationVariable] =
    Set(this)

  override def equals(other: Any): Boolean =
    other match {
      case that: Variable[_] =>
        name == that.name
      case _ => false
    }
  override def hashCode: Int =
    name.hashCode()

  override def toString: String = name
}

/**
 * Factories for [[fr.dubuissonduplessis.dogma.instantiable.Variable]].
 * ==Creation example==
 * {{{
 * // Variable creation
 * val v1 = Variable[String]("X")
 * //> v1  : fr.dubuissonduplessis.dogma.instantiable.variable.Variable[String] = X
 *
 * // Instantiation Set creation
 * val instSet1 = InstantiationSet() + (Variable[String]("X"), "Scala")
 * //> instSet1  : fr.dubuissonduplessis.dogma.instantiable.InstantiationSet
 * // Set = X -> Scala
 *
 * // Instantiation process
 * v1.instantiateWith(instSet1)
 * //> res0: String = Scala
 * }}}
 *
 * ==Advanced creation example==
 * {{{
 * // Function creation
 * val f = (s: String) => s.toUpperCase()
 * //> f  : String => String = &lt;function1&gt;
 *
 * // Variable creation
 * val v2 = Variable.withTransformation[String]("Y")(f)
 * //> v2  : fr.dubuissonduplessis.dogma.instantiable.variable.Variable[String]
 *
 * // Instantiation Set creation
 * val instSet2 = InstantiationSet() + (Variable[String]("Y"), "Scala")
 * //> instSet2  : fr.dubuissonduplessis.dogma.instantiable.InstantiationSet
 * // Set = Y -> Scala
 *
 * // Instantiation process
 * v2.instantiateWith(instSet2)
 * //> res1: String = SCALA
 * }}}
 */
object Variable extends LazyLogging {
  /**
   * Creates a variable with a given name.
   * @param n name of the variable
   */
  def apply[T](n: String): Variable[T] =
    new Variable[T] {
      def name: String = n
    }

  /**
   * Creates a variable with a given name and a transformation function.
   * The instantiation process is augmented by the application of the transformation
   * function on the standard result.
   * @param n name of the variable
   * @param neg transformation function
   */
  def withTransformation[T](n: String)(f: T => T): Variable[T] =
    new Variable[T] {
      def name: String = n
      override protected def instantiateWithImpl(s: InstantiationSet): T = {
        val result = f(s.binding(this).get)
        logger.debug(s"Instantiation of a negated variable: $this -> $result")
        result
      }
      override def toString: String = s"¬$name"
    }
}
