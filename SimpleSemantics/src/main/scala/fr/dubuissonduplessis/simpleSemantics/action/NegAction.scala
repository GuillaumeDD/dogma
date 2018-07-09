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
package fr.dubuissonduplessis.simpleSemantics.action
import fr.dubuissonduplessis.predicateCalculus._
import fr.dubuissonduplessis.predicateCalculus.substitution.SubstitutionSet

class NegAction[T <: AtomicAction] protected (p: T)
  extends AtomicAction {

  // We only deal with one level of negation
  require(!p.negation)

  def name: String = "~" + p.name
  def terms: List[AnyUnifiable] = p.terms

  def substitute(subSet: SubstitutionSet): NegAction[_ <: AtomicAction] =
    NegAction(p.substitute(subSet))

  // Negation
  override def neg: T = p
  override def negation: Boolean = true

  override def hashCode(): Int =
    59 * p.hashCode() + 59

  override def equals(other: Any): Boolean =
    other match {
      case nProp: NegAction[_] =>
        nProp.neg == p
      case _ => false
    }

  override def toString: String =
    "~" + p.toString
}

object NegAction {

  def unapply[T <: AtomicAction](p: NegAction[T]): Option[T] =
    Some(p.neg)

  def apply[T <: AtomicAction](p: T): NegAction[T] =
    new NegAction(p)
}
