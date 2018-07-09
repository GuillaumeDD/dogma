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
package fr.dubuissonduplessis.predicateCalculus
import fr.dubuissonduplessis.predicateCalculus.substitution.SubstitutionSet

/**
 * Represents a predicate calculus symbol.
 *
 */
trait PCExpression {
  /**
   * Substitute all variables with their values determined by the [[fr.dubuissonduplessis.predicateCalculus.substitution.SubstitutionSet]].
   * For example, the substitute method called on predicate love(john, X) with
   * the substitution set (X -> mary), returns love(john, mary).
   * @param subSet The SubstitutionSet consisting of map Variable -> Value.
   * @return The new Unifiable with all variables contained in the SubstitutionSet
   * substituted by their value.
   */
  def substitute(subSet: SubstitutionSet): PCExpression

  /**
   * Determines if this Unifiable involves a [[fr.dubuissonduplessis.predicateCalculus.Variable]].
   * @return true if this Unifiable contains a [[fr.dubuissonduplessis.predicateCalculus.Variable]], else false.
   */
  def containsVariable(): Boolean

  /**
   * Determines if this Unifiable contains [[fr.dubuissonduplessis.predicateCalculus.Variable]] v :
   * <ul>
   * <li> [[fr.dubuissonduplessis.predicateCalculus.Individual]]: they never contain variable
   * <li> [[fr.dubuissonduplessis.predicateCalculus.Variable]]: they contain v iff (this predEquals v)
   * <li> [[fr.dubuissonduplessis.predicateCalculus.PredN]]: they contain v iff there exists at least one argument arg
   * such as arg.containsVariable(v).
   * </ul>
   * @param v Variable to be researched.
   * @return true if this Unifiable contains Variable v, else false.
   */
  def containsVariable(v: Variable): Boolean
}
