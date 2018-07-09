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
package fr.dubuissonduplessis.predicateCalculus.substitution

import fr.dubuissonduplessis.predicateCalculus._
/**
 * Represents a substitution from a [[fr.dubuissonduplessis.predicateCalculus.Variable]]
 * to another [[fr.dubuissonduplessis.predicateCalculus.Variable]].
 *
 */
abstract class VariableSubstitution extends Substitution {
  /**
   *  Returns the variable that should substitute the first variable.
   * @return The variable that should substitute the variable.
   */
  def binding: Variable
}

object VariableSubstitution {
  def apply(
    v1: Variable,
    v2: Variable): VariableSubstitution =
    VariableSubstitutionImpl(v1, v2)

  def unapply(subst: Substitution): Option[(Variable, Variable)] =
    subst match {
      case VariableSubstitutionImpl(v1, v2) => Some((v1, v2))
      case _ => None
    }

  protected[substitution] case class VariableSubstitutionImpl(
    variable: Variable,
    binding: Variable) extends VariableSubstitution
}
