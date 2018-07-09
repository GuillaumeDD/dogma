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
import substitution._
/**
 * Represents a constant symbol.
 */
trait Individual extends Unifiable[Individual]
  with SubstitutionIdentity[Individual] {

  def isUnifiableWith(exp: AnyUnifiable): Boolean =
    exp match {
      case v: Variable =>
        true
      case other =>
        predEquals(other)
    }

  def predEquals(x: AnyUnifiable): Boolean =
    x match {
      case ind: Individual =>
        this.name == ind.name
      case _ =>
        false
    }

  def unifyWith(
    x: AnyUnifiable,
    subSet: SubstitutionSet): Option[SubstitutionSet] =
    x match {
      case ind: Individual =>
        if (predEquals(ind)) {
          Some(subSet)
        } else {
          None
        }
      case v: Variable =>
        v.unifyWith(this, subSet)
      case pred: PredN =>
        None
    }

  def containsVariable(): Boolean =
    false

  def containsVariable(v: Variable): Boolean =
    false
}

object Individual {
  def apply(name: String): Individual =
    DefaultIndividual(name)

  private case class DefaultIndividual(val name: String) extends Individual {
    override def toString: String =
      name
  }
}
