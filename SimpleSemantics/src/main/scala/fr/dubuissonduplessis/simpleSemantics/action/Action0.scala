/*******************************************************************************
 * Copyright (c) 2014 Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr> - initial API and implementation
 ******************************************************************************/
package fr.dubuissonduplessis.simpleSemantics.action

import fr.dubuissonduplessis.predicateCalculus.Pred0

abstract class Action0 extends AtomicAction
  with Negation[Action0]
  with Pred0[Action0]

object Action0 {
  /**
   * Builds a 0-ary predicate action from a name.
   * @param name Name of the 0-ary predicate action.
   * @return A 0-ary predicate action which name is name.
   */
  def apply(name: String): Action0 =
    Action0Impl(name)

  private case class Action0Impl(
    name: String) extends Action0 {
    override def toString: String =
      name + "."
  }
}
