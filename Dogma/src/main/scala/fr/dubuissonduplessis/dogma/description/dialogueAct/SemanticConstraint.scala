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
package fr.dubuissonduplessis.dogma.description.dialogueAct

import fr.dubuissonduplessis.dogma.instantiable.Instantiable

/**
 * A constraint on a semantic content. It is a function from the semantic content type
 * to boolean.
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 * @tparam T type of the semantic content
 */
abstract class SemanticConstraint[T] extends Function1[T, Boolean]
  with Instantiable[SemanticConstraint[T]] {

  /**
   * Body of the semantic constraint that should be applied to a semantic content.
   * @return true if the semantic constraint is checked, else false
   */
  def apply(content: T): Boolean

  /**
   * Name of the semantic constraint.
   */
  def name: String
}
