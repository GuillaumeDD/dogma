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
 * Defines an instantiable proxy. The instantiation process is delegated to another instantiable that
 * is part of this instantiable.
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 * @tparam T type of the  instantiation process output of this instantiable
 * @tparam Content type of the instantiable on which the instantiation process is delegated.
 */
trait InstantiableProxy[+T, Content]
  extends Instantiable[T] {

  /**
   * Determines the instantiable on which the instantiation process is delegated.
   * @return the instantiable on which the instantiation process is delegated
   */
  protected def instantiable: Instantiable[Content]

  /**
   * Factory method that builds the result of the instantiation process from the
   * result of the instantiation process on the delegated part.
   * @param instantiable result of the instantiation process of the proxied instantiable
   * @return result of the instantiation process of this instantiable
   */
  protected def update(instantiable: Content): T

  def isInstantiableWith(s: InstantiationSet): Boolean =
    instantiable.isInstantiableWith(s)

  protected def instantiateWithImpl(s: InstantiationSet): T =
    update(instantiable.instantiateWith(s))

  def variables: Set[InstantiationVariable] =
    instantiable.variables
}
