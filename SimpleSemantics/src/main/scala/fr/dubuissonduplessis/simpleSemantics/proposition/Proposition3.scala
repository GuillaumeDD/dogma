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
package fr.dubuissonduplessis.simpleSemantics.proposition

import fr.dubuissonduplessis.predicateLogic.BooleanExpression
import fr.dubuissonduplessis.predicateCalculus.Unifiable
import fr.dubuissonduplessis.predicateCalculus.substitution.SubstitutionSet
import scala.collection.immutable.List
import fr.dubuissonduplessis.predicateCalculus.Individual
import fr.dubuissonduplessis.predicateCalculus.SubstitutionIdentity

/**
 * Represents a 3-ary proposition with three '''constant (individual) arguments'''.
 *
 */
abstract class Proposition3 extends Proposition
  with Negation[Proposition3]
  with SubstitutionIdentity[Proposition3] {
  /**
   * Constant (individual) argument of this proposition.
   * @return The constant (individual) argument of this proposition.
   */
  def first: Individual

  def second: Individual

  def third: Individual

  def terms: List[Individual] =
    List(first, second, third)
}

object Proposition3 {
  /**
   * Builds a 3-ary proposition from a name and three constant (individual) arguments.
   * @param name Name of the proposition.
   * @param first First constant (individual) argument
   * @param second Second constant (individual) argument
   * @param third Third constant (individual) argument
   * @return A 2-ary proposition from a name and two constant (individual) arguments.
   */
  def apply(name: String,
    first: Individual,
    second: Individual,
    third: Individual): Proposition3 =
    Proposition3Impl(name, first, second, third)

  def unapply(p: Proposition3): Option[(String, Individual, Individual, Individual)] =
    Some((p.name, p.first, p.second, p.third))

  private case class Proposition3Impl(
    name: String,
    first: Individual,
    second: Individual,
    third: Individual) extends Proposition3

}
