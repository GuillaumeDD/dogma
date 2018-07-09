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
package fr.dubuissonduplessis.predicateCalculus
import substitution._

/**
 * Represents a symbol that is unifiable.
 * @param <T> Type T of the output of the substitute method (it must be a unifiable).
 */
trait Unifiable[+T <: Unifiable[_]] extends PCExpression {
  /**
   * Returns the name of the symbol.
   * @return Returns the name of the symbol.
   */
  def name: String

  def substitute(subSet: SubstitutionSet): T

  /**
   * Determines if this unifiable can be unified with exp.
   * @param exp
   * @return true if this unifiable can be unified with exp else false
   */
  def isUnifiableWith(exp: AnyUnifiable): Boolean

  /**
   * Determines the [[fr.dubuissonduplessis.predicateCalculus.substitution.SubstitutionSet]] that realizes the unification between this unifiable
   * and x starting with the substitution set subSet.
   * @param x Unifiable that is to be unified with this unifiable starting with the substitution
   * set subSet.
   * @param subSet Substitution set from which the unification must start.
   * @return An option containing a [[fr.dubuissonduplessis.predicateCalculus.substitution.SubstitutionSet]] that realizes the unification starting
   * with the substitution set subSet between this unifiable and x  if unification is possible,
   * else None.
   */
  def unifyWith(
    x: AnyUnifiable,
    subSet: SubstitutionSet): Option[SubstitutionSet]

  /**
   * Determines the [[fr.dubuissonduplessis.predicateCalculus.substitution.SubstitutionSet]] that realizes the unification between this unifiable
   * and x.
   * @param x Unifiable that is to be unified with this unifiable.
   * @return An option containing a [[fr.dubuissonduplessis.predicateCalculus.substitution.SubstitutionSet]] that realizes the unification
   * between this unifiable and x if unification is possible, else None.
   */
  def unifyWith(x: AnyUnifiable): Option[SubstitutionSet] =
    unifyWith(x, SubstitutionSet())

  /**
   * Determines the equality with predicate x. Equality is determined as follow :
   * <ul>
   * <li> individual1 predEquals individual2 iff individual1.name == individual2.name,
   * else predEquals returns false when an individual is involved.
   * <li> var1 predEquals var2 iff var1.name == var2.name,
   * else predEquals returns false when a variable is involved.
   * <li> predA predEquals predB iff predA.name == predB.name and predA.arity == predB.arity
   * (which we refer to arity) and for all arguments (1 <= i <= arity), predA(i) predEquals predB(i)
   * </ul>
   * @param x Predicate which should be tested for equality.
   * @return true if this Unifiable and x are equal, else false
   */
  def predEquals(x: AnyUnifiable): Boolean
}
