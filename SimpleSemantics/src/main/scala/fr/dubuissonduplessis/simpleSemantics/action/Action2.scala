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
package fr.dubuissonduplessis.simpleSemantics.action

import fr.dubuissonduplessis.predicateCalculus.substitution.SubstitutionSet
import scala.collection.immutable.List
import fr.dubuissonduplessis.predicateCalculus.SubstitutionIdentity
import fr.dubuissonduplessis.predicateCalculus.Individual

abstract class Action2 extends AtomicAction
  with Negation[Action2]
  with SubstitutionIdentity[Action2] {
  /**
   * Constant (individual) argument of this action.
   * @return The constant (individual) argument of this action.
   */
  def first: Individual

  def second: Individual

  def terms: List[Individual] =
    List(first, second)
}

object Action2 {
  /**
   * Builds a 2-ary action from a name and two constant (individual) arguments.
   * @param name Name of the action.
   * @param first First constant (individual) argument
   * @param second Second constant (individual) argument
   * @return A 2-ary action from a name and two constant (individual) arguments.
   */
  def apply(name: String,
    first: Individual,
    second: Individual): Action2 =
    Action2Impl(name, first, second)

  def unapply(p: Action2): Option[(String, Individual, Individual)] =
    Some((p.name, p.first, p.second))

  private case class Action2Impl(
    name: String,
    first: Individual,
    second: Individual) extends Action2

}
