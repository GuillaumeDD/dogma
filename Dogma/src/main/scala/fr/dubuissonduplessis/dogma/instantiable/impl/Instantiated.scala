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

import fr.dubuissonduplessis.dogma.instantiable.Instantiable
import fr.dubuissonduplessis.dogma.instantiable.InstantiationVariable
import fr.dubuissonduplessis.dogma.instantiable.InstantiationSet

/**
 * Default implementation of an instantiable that is instantiated (i.e., with no variables).
 * An "instantiated" instantiable defines a fixed point in the instantiable process.
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 * @tparam This type of this instantiable
 */
trait Instantiated[+This] extends Instantiable[This] {
  this: This =>

  protected def instantiateWithImpl(s: InstantiationSet): This =
    this

  def variables: Set[InstantiationVariable] =
    Set()

  def isInstantiableWith(s: InstantiationSet): Boolean =
    true
}
