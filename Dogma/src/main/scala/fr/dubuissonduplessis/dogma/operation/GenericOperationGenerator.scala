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
package fr.dubuissonduplessis.dogma.operation

import fr.dubuissonduplessis.dogma.instantiable.Instantiable
import fr.dubuissonduplessis.dogma.instantiable.Variable
import fr.dubuissonduplessis.dogma.instantiable.InstantiationSet
import fr.dubuissonduplessis.dogma.instantiable.InstantiationVariable

/**
 * Represents an abstract uninstantiated operation parametrized by
 * one variable.
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 * @tparam VariableType type of the uninstantiated parameter of the operation
 * @tparam GeneratedType type of the operation
 */
abstract class GenericOperationGenerator[VariableType, GeneratedType <: Operation]
  extends Operation
  with Instantiable[GeneratedType] {

  def variable: Variable[VariableType]

  def isInstantiableWith(s: InstantiationSet): Boolean =
    variable.isInstantiableWith(s)
  // TO IMPLEMENT
  /**
   * Factory method that builds the instantiated operation from a given
   * parameter.
   */
  protected def buildFrom(prop: VariableType): GeneratedType

  protected def instantiateWithImpl(s: InstantiationSet): GeneratedType =
    buildFrom(variable.instantiateWith(s))
  def variables: Set[InstantiationVariable] =
    variable.variables
}
