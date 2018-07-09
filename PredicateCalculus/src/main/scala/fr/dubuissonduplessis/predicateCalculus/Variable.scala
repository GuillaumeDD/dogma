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
 * Represents a variable symbol.
 */
trait Variable extends Unifiable[AnyUnifiable] {
  def isUnifiableWith(exp: AnyUnifiable): Boolean =
    this.predEquals(exp) ||
      !exp.containsVariable(this)

  def predEquals(x: AnyUnifiable): Boolean =
    x match {
      case v: Variable =>
        this.name == x.name
      case _ => false
    }

  def unifyWith(
    x: AnyUnifiable,
    subSet: SubstitutionSet): Option[SubstitutionSet] =
    if (subSet.isBound(this)) { // we unify the substitution with x
      subSet.binding(this).get.unifyWith(x, subSet)
    } else {
      if (predEquals(x)) { // x and this are the same variable
        Some(subSet)
      } else {
        if (x.containsVariable(this)) {
          None
        } else {
          // we add a new substitution depending on the type
          Some(subSet + Substitution(this, x))
        }
      }
    }

  def substitute(subSet: SubstitutionSet): AnyUnifiable = {
    val simplifiedSubSet = SubstitutionSet.simplify(subSet)
    if (simplifiedSubSet.isBound(this)) {
      simplifiedSubSet.binding(this).get
    } else {
      this
    }
  }

  def containsVariable(): Boolean =
    true

  def containsVariable(v: Variable): Boolean =
    this.predEquals(v)
}

object Variable {
  def apply(name: String): Variable =
    DefaultVariable(name)

  private case class DefaultVariable(val name: String) extends Variable {
    override def toString: String =
      name
  }
}
