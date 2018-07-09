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
package fr.dubuissonduplessis.predicateLogic

/**
 * Represents a BooleanExpression, i.e. an expression that can be negated.
 *
 */
trait BooleanExpression[T <: BooleanExpression[_]] {
  /**
   * Negate a BooleanExpression.
   * @return The negation of this BooleanExpression.
   */
  def neg: T

  /**
   * Determines if this expression is a negation.
   * @return True if this expression is a negation of an expression, else false
   */
  def negation: Boolean
}
