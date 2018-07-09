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
package fr.dubuissonduplessis.dogma.ditpp

/**
 * Enumeration of the DIT++ dimensions.
 * To use a dimension, import the content of the object:
 * {{{
 * // Import the enumeration content
 * import Dimension._
 *
 * // Declare the dimension
 * val dim = AutoFeedback
 * }}}
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 */
object Dimension extends Enumeration {
  type Dimension = Value
  val DialogueStructureManagement, Task, AutoFeedback = Value
}
