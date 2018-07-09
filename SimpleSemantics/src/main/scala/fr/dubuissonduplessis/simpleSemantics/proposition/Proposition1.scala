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
 * Represents a 1-ary proposition with a '''constant (individual) argument'''.
 *
 */
abstract class Proposition1 extends Proposition
  with Negation[Proposition1]
  with SubstitutionIdentity[Proposition1] {
  /**
   * Constant (individual) argument of this proposition.
   * @return The constant (individual) argument of this proposition.
   */
  def arg: Individual

  def terms: List[Individual] =
    List(arg)
}

object Proposition1 {
  /**
   * Builds a 1-ary proposition from a name and a constant (individual) argument.
   * @param name Name of the proposition.
   * @param arg Constant (individual) argument
   * @return A 1-ary proposition from a name and a constant (individual) argument.
   */
  def apply(name: String,
    arg: Individual): Proposition1 =
    Proposition1Impl(name, arg)

  def unapply(p: Proposition1): Option[(String, Individual)] =
    Some((p.name, p.arg))

  private case class Proposition1Impl(
    name: String,
    arg: Individual) extends Proposition1

}
