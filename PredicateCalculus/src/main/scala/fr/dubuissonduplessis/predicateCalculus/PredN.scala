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
 * Represents a predicate expression. Two methods are worth noticing:
 *  - '''arity''': returns the arity (i.e., the number
 *  arguments of this predicate)
 *  - '''terms''': returns an ordered list of the arguments
 *  of this predicate.
 */
trait PredN extends Unifiable[PredN] {
  /**
   * Determines the number of arguments of this predicate.
   * @return the number of arguments in this predicate (>= 0).
   */
  def arity: Int =
    terms.size

  /**
   * Determines the arguments of this predicate.
   * @return An ordered list of the arguments of this predicate. It may be an empty list.
   */
  def terms: List[AnyUnifiable]

  /**
   * Ensures that x is compatible with this predicate, which means having the
   * same name and the same arity.
   * @param x Unifiable to be checked
   * @return true if x is compatible with this predicate, else false
   */
  def compatible(x: AnyUnifiable): Boolean =
    x match {
      case pred: PredN =>
        this.name == pred.name &&
          this.arity == pred.arity
      case _ => false
    }

  def isUnifiableWith(exp: AnyUnifiable): Boolean =
    exp match {
      case pred: PredN =>
        compatible(pred) &&
          { // We check the unifiability of arguments
            val zip = this.terms zip pred.terms
            zip.forall(
              pair => {
                val (e1, e2) = pair
                e1.isUnifiableWith(e2)
              })
          }
      case other =>
        other.isUnifiableWith(this)
    }

  def predEquals(x: AnyUnifiable): Boolean =
    x match {
      case pred: PredN =>
        this.name == pred.name &&
          this.arity == pred.arity &&
          {
            val pairTerms = this.terms zip pred.terms
            pairTerms.forall({
              pair => pair._1.predEquals(pair._2)
            })
          }
      case _ => false
    }

  def unifyWith(
    x: AnyUnifiable,
    subSet: SubstitutionSet): Option[SubstitutionSet] =
    x match {
      case ind: Individual =>
        None
      case v: Variable =>
        v.unifyWith(this, subSet)
      case pred: PredN =>
        if (compatible(pred)) {
          val zip = this.terms zip pred.terms
          val startSubSet: Option[SubstitutionSet] =
            Some(subSet)
          // For each pair of predicate arguments, we attempt a unification
          (startSubSet /: zip)(
            (result, elem) => result match {
              case None => None
              case Some(newSubSet) =>
                val (e1, e2) = elem
                e1.unifyWith(e2, newSubSet)
            })
        } else {
          None
        }
    }

  def containsVariable(): Boolean =
    terms.exists(_.containsVariable())

  def containsVariable(v: Variable): Boolean =
    terms.exists(_.containsVariable(v))

  override def toString: String =
    name + "(" +
      terms.mkString(", ") +
      ")"
}

object PredN {
  def apply(
    name: String,
    terms: List[AnyUnifiable]): PredN =
    DefaultPredN(name, terms)

  private case class DefaultPredN(
    val name: String,
    val terms: List[AnyUnifiable]) extends PredN {

    def substitute(subSet: SubstitutionSet): PredN = {
      DefaultPredN(
        name, terms.mapConserve(x => x.substitute(subSet).asInstanceOf[AnyUnifiable]))
    }
  }

}
