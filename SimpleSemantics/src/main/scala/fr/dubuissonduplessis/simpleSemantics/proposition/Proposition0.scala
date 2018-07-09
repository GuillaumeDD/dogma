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
import fr.dubuissonduplessis.predicateCalculus._
import fr.dubuissonduplessis.predicateCalculus.substitution.SubstitutionSet
import scala.collection.immutable.List

/**
 * Represents a 0-ary predicate proposition.
 *
 */
abstract class Proposition0 extends Proposition
  with Negation[Proposition0]
  with Pred0[Proposition0]

object Proposition0 {
  /**
   * Builds a 0-ary predicate proposition from a name.
   * @param name Name of the 0-ary predicate proposition.
   * @return A 0-ary predicate proposition which name is name.
   */
  def apply(name: String): Proposition0 =
    Proposition0Impl(name)

  private case class Proposition0Impl(
    name: String) extends Proposition0 {
    override def toString: String =
      name + "."
  }
}
