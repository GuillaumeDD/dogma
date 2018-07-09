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
package fr.dubuissonduplessis.dogma.instantiable.impl

import fr.dubuissonduplessis.dogma.instantiable._
import scala.collection.immutable.Set
import fr.dubuissonduplessis.dogma.instantiable.InstantiationSet

/**
 * Defines an instantiable proxy. The instantiation process is delegated to two other instantiables that
 * are part of this instantiable.
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 * @tparam T type of the  instantiation process output of this instantiable
 * @tparam Content1 type of the first instantiable on which the instantiation process is delegated.
 * @tparam Content2 type of the second instantiable on which the instantiation process is delegated.
 */
trait InstantiableDuoProxy[+T, Content1, Content2]
  extends Instantiable[T] {

  /**
   * Determines the first instantiable on which the instantiation process is delegated.
   * @return the first instantiable on which the instantiation process is delegated
   */
  protected def instantiable1: Instantiable[Content1]
  /**
   * Determines the second instantiable on which the instantiation process is delegated.
   * @return the second instantiable on which the instantiation process is delegated
   */
  protected def instantiable2: Instantiable[Content2]
  /**
   * Factory method that builds the result of the instantiation process from the
   * results of the two other instantiation processes.
   * @param instantiable1 result of the instantiation process of the first proxied instantiable
   * @param instantiable2 result of the instantiation process of the second proxied instantiable
   * @return result of the instantiation process of this instantiable
   */
  protected def update(instantiable1: Content1, instantiable2: Content2): T

  def isInstantiableWith(s: InstantiationSet): Boolean =
    instantiable1.isInstantiableWith(s) && instantiable2.isInstantiableWith(s)

  protected def instantiateWithImpl(s: InstantiationSet): T =
    update(instantiable1.instantiateWith(s), instantiable2.instantiateWith(s))

  def variables: Set[InstantiationVariable] =
    instantiable1.variables ++ instantiable2.variables
}
