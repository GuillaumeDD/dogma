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
package fr.dubuissonduplessis.predicateCalculus.substitution
import fr.dubuissonduplessis.predicateCalculus._

import com.typesafe.scalalogging.slf4j.LazyLogging

/**
 * Represents a set of [[fr.dubuissonduplessis.predicateCalculus.substitution.Substitution]].
 *
 */
trait SubstitutionSet {
  /**
   * Checks if a variable is involved in a substitution contained in this set.
   * @param v Variable which is queried for a binding
   * @return true if variable v has a binding (i.e., a substitution), else false
   */
  def isBound(v: Variable): Boolean

  /**
   * Adds a substitution to the set.
   * @param subst Substitution to add.
   * @return A new substitution set containing subst.
   */
  def +(subst: Substitution): SubstitutionSet

  /**
   * Recovers the [[fr.dubuissonduplessis.predicateCalculus.Unifiable]] that should substitue
   * the variable v if it exists.
   * @param v Variable which is queried for a binding
   * @return None if a binding does not exist for v, else the bind Unifiable to v.
   */
  def binding(v: Variable): Option[AnyUnifiable]

  /**
   * Recovers the set of all substitutions contained in this set.
   * @return The set of substitution contained in the substitution set.
   */
  def substitutions: Set[Substitution]

  /**
   * Recovers the set of all substitutions contained in this set that are '''not''' Variable to Variable substitution.
   * @return The set of substitutions from a Variable to an [[fr.dubuissonduplessis.predicateCalculus.Individual]]
   *  or a [[fr.dubuissonduplessis.predicateCalculus.PredN]].
   */
  def directSubstitutions: Set[Substitution]

  /**
   * Recovers the set of all substitutions contained in this set that are Variable to Variable substitution.
   * @return The set of substitutions from a Variable to an another Variable.
   */
  def variableOnlySubstitutions: Set[VariableSubstitution]
}

object SubstitutionSet {
  def apply(): SubstitutionSet =
    SubstitutionSetImpl(Map())

  def apply(substitutions: Set[Substitution]): SubstitutionSet =
    SubstitutionSetImpl(substitutions.map(s => (s.variable, s)).toMap)

  // Default Implemented Substitution Set
  private case class SubstitutionSetImpl(bindings: Map[Variable, Substitution])
    extends SubstitutionSet
    with LazyLogging {
    def isBound(v: Variable): Boolean =
      bindings.contains(v)

    def +(subst: Substitution): SubstitutionSet =
      if (!subst.variable.predEquals(subst.binding)) {
        SubstitutionSetImpl(bindings + (subst.variable -> subst))
      } else {
        this
      }

    def binding(v: Variable): Option[AnyUnifiable] =
      bindings.get(v) match {
        case None => None
        case Some(sub) => Some(sub.binding)
      }

    def substitutions: Set[Substitution] =
      bindings.map(kv => kv._2).toSet

    def directSubstitutions: Set[Substitution] =
      bindings.values.collect(sub =>
        sub match {
          case subst @ Substitution(v, s) => subst
        }).toSet

    def variableOnlySubstitutions: Set[VariableSubstitution] =
      bindings.values.collect(sub =>
        sub match {
          case VariableSubstitution(v1, v2) =>
            VariableSubstitution(v1, v2)
        }).toSet

    override def toString: String =
      bindings.mkString("\n")
  }

  /**
   * Simplifies the substitution set by transforming each VariableSubstitution (v1 -> v2)
   * to a Substitution with a binding to an individual or a predicate if possible. For example,
   * a substitution set (v1 -> v2, v2 -> v3, v3 -> love(john, mary)) where v1, v2, v3 are variables
   *  will be simplified as (v1 -> love(john, mary), v2 -> love(john, mary), v3 -> love(john, mary)).
   * @param subSet The substitution set to be simplified.
   * @return The simplified substitution set.
   */
  def simplify(subSet: SubstitutionSet): SubstitutionSet = {
    // This function constructs bags of variables that should have the same value
    def buildBags(varSub: List[VariableSubstitution], bags: Bags): Bags = {
      if (varSub.isEmpty) {
        bags
      } else {
        buildBags(varSub.tail, bags.add(varSub.head))
      }
    }

    // This function checks if a value can be attributed to a variable in bag b
    def value(b: Bag): Option[AnyUnifiable] = {
      val directSubSet = SubstitutionSet(subSet.directSubstitutions)
      b.elements.find(
        variable => directSubSet.isBound(variable)) match {
          case None => None
          case Some(v) => directSubSet.binding(v)
        }
    }
    // This function generates the bindings
    def bagBindingToSubstitution(bagBinding: (Bag, Option[AnyUnifiable])): List[Substitution] = {
      // This function generates substitution in order to keep the
      // key/value architecture of the map of the substitution set.
      // List(X, Y, Z) will generate substitutions : X/Y, Y/Z
      def generateFromList(variables: List[Variable]): List[Substitution] =
        if (variables.size <= 1) {
          List()
        } else {
          val first = variables.head
          val tail = variables.tail
          Substitution(first, tail.head) +: generateFromList(tail)
        }
      bagBinding match {
        // we keep the binding between variables
        case (bag, None) =>
          generateFromList(bag.elements)
        case (bag, Some(value)) =>
          for (v <- bag.elements) yield (Substitution(v, value))
      }
    }
    //logger.debug("Simplify called on %s", this)
    // We build bags of equivalent variables
    //logger.debug("Variable only substitutions: %s", variableOnlySubstitutions.toList)
    if (subSet.variableOnlySubstitutions.size > 0) {
      val bags = buildBags(subSet.variableOnlySubstitutions.toList, Bags(List()))
      //logger.debug("Bags of equivalent variable: %s", bags)

      // We research if we can attribute a value to a variable in a bag
      val bagBindings = for (bag <- bags.bags) yield ((bag, value(bag)))
      //logger.debug("Bags bindings: %s", bagBindings.mkString("\n"))

      // We generate bindings
      val newBindings =
        (bagBindings foldLeft List[Substitution]())(
          (result, bagBinding) => result ++ bagBindingToSubstitution(bagBinding))
      //logger.debug("Adding the new bindings %s to %s", newBindings, SubstitutionSetImpl(directSubstitutions.map(x => (x.variable, x)).toMap))
      val result = (newBindings foldLeft subSet)(
        (substitutionSet, sub) => substitutionSet + sub)
      //logger.debug("Resulting in %s", result)
      result
    } else {
      subSet
    }
  }

  private case class Bag(elements: List[Variable]) {
    def contains(v: Variable): Boolean =
      elements.exists(_.predEquals(v))

    // Addition by assuring unicity
    def add(v: Variable): Bag = {
      if (this.contains(v)) {
        this
      } else {
        Bag(v +: elements)
      }
    }

    def merge(b: Bag): Bag =
      (b.elements foldLeft this)((result, elem) => result.add(elem))
  }

  private case class Bags(bags: List[Bag]) {
    def contains(v: Variable): Boolean =
      bags.exists(_.contains(v))

    def add(vSub: VariableSubstitution): Bags = {
      (getBag(vSub.variable), getBag(vSub.binding)) match {
        case (None, None) =>
          // We create a new bag with v1 and v2
          this.addBag(Bag(List(vSub.variable, vSub.binding)))
        case (Some(b), None) =>
          // we add v2 to b
          Bags((b.add(vSub.binding) +: (bags.filterNot(_ == b))))
        case (None, Some(b)) =>
          // we add v1 to b
          Bags((b.add(vSub.variable) +: (bags.filterNot(_ == b))))
        case (Some(b1), Some(b2)) =>
          // we merge b1 and b2
          Bags((b1.merge(b2) +: (bags.filterNot(_ == b1)
            .filterNot(_ == b2))))
      }
    }

    def add(v: Variable): Bags = {
      getBag(v) match {
        case None =>
          // we had a new bag
          addBag(Bag(List(v)))
        case Some(bag) =>
          // we modify the existing bag
          Bags((bag.add(v)) +: (bags.filterNot(_ == bag)))
      }
    }

    def addBag(b: Bag): Bags =
      Bags(b +: bags)

    def getBag(v: Variable): Option[Bag] =
      bags.find(_.contains(v))
  }
}
