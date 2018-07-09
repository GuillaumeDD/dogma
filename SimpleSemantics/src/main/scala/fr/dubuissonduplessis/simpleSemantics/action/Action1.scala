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

abstract class Action1 extends AtomicAction
  with Negation[Action1]
  with SubstitutionIdentity[Action1] {
  /**
   * Constant (individual) argument of this proposition.
   * @return The constant (individual) argument of this proposition.
   */
  def arg: Individual

  def terms: List[Individual] =
    List(arg)
}

object Action1 {
  /**
   * Builds a 1-ary action from a name and a constant (individual) argument.
   * @param name Name of the action.
   * @param arg Constant (individual) argument
   * @return A 1-ary action from a name and a constant (individual) argument.
   */
  def apply(name: String,
    arg: Individual): Action1 =
    Action1Impl(name, arg)

  def unapply(p: Action1): Option[(String, Individual)] =
    Some((p.name, p.arg))

  private case class Action1Impl(
    name: String,
    arg: Individual) extends Action1
}
