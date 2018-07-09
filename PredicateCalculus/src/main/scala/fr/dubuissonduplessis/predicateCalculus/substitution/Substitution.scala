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
 * to another [[fr.dubuissonduplessis.predicateCalculus.Unifiable]].
 *
 * See also [[fr.dubuissonduplessis.predicateCalculus.substitution.VariableSubstitution]] that
 * represents the special case of a substitution from a [[fr.dubuissonduplessis.predicateCalculus.Variable]]
 * to a [[fr.dubuissonduplessis.predicateCalculus.Variable]].
 */
abstract class Substitution {
  /**
   * Returns the Variable part of the substitution.
   * @return The Variable part of the substitution.
   */
  def variable: Variable

  /**
   * Returns the Unifiable that should substitute the variable.
   * @return The Unifiable that should substitute the variable.
   */
  def binding: AnyUnifiable

  /**
   * Determines if this substitution is an entity substitution.
   * @return true if it represents a substitution of a variable by itself, else false
   */
  def identity: Boolean =
    variable.predEquals(binding)

  /**
   * @return A pair which first element is the Variable part of the substitution
   * and the second element is the Unifiable that should substitute the first element.
   */
  def toPair: (Variable, AnyUnifiable) =
    (variable, binding)
}

object Substitution {
  def apply(
    variable: Variable,
    binding: AnyUnifiable): Substitution =
    binding match {
      case v: Variable => VariableSubstitution.VariableSubstitutionImpl(variable, v)
      case other => InstantiationSubstitution(variable, binding)
    }

  protected[substitution] case class InstantiationSubstitution(
    variable: Variable,
    binding: AnyUnifiable) extends Substitution

  def unapply(subst: Substitution): Option[(Variable, AnyUnifiable)] =
    subst match {
      case InstantiationSubstitution(v, u) => Some((v, u))
      case _ => None
    }
}
