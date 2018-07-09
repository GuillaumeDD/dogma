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
 * Represents a 2-ary proposition with two '''constant (individual) arguments'''.
 *
 */
abstract class Proposition2 extends Proposition
  with Negation[Proposition2]
  with SubstitutionIdentity[Proposition2] {
  /**
   * Constant (individual) argument of this proposition.
   * @return The constant (individual) argument of this proposition.
   */
  def first: Individual

  def second: Individual

  def terms: List[Individual] =
    List(first, second)
}

object Proposition2 {
  /**
   * Builds a 2-ary proposition from a name and two constant (individual) arguments.
   * @param name Name of the proposition.
   * @param first First constant (individual) argument
   * @param second Second constant (individual) argument
   * @return A 2-ary proposition from a name and two constant (individual) arguments.
   */
  def apply(name: String,
    first: Individual,
    second: Individual): Proposition2 =
    Proposition2Impl(name, first, second)

  def unapply(p: Proposition2): Option[(String, Individual, Individual)] =
    Some((p.name, p.first, p.second))

  private case class Proposition2Impl(
    name: String,
    first: Individual,
    second: Individual) extends Proposition2

}
