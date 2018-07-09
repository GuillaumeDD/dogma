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
package fr.dubuissonduplessis.dogma.game.language

import fr.dubuissonduplessis.dogma.instantiable.impl.InstantiableDuoProxy
import fr.dubuissonduplessis.dogma.game.language.CombinationSymbol._

/**
 * Abstract base class to build binary composite game proposition.
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 * @tparam This type of the composite game propositions
 */
abstract class AbstractComposedGameProposition[This <: AbstractComposedGameProposition[This]]
  extends AbstractGameProposition
  with InstantiableDuoProxy[AbstractComposedGameProposition[This], AbstractGameProposition, AbstractGameProposition] {

  /**
   * First part of this composite dialogue game proposition
   */
  def g1: AbstractGameProposition
  /**
   * Second part of this composite dialogue game proposition
   */
  def g2: AbstractGameProposition

  /**
   * Returns a pair (g1, g2) if gProp is an AbstractComposedGameProposition of the exact
   * same type as this, else None.
   * @param gProp Proposition to be compared to the composed game proposition.
   * @return a pair (g1, g2) if gProp is an AbstractComposedGameProposition of the exact
   * same type as this, else None.
   */
  protected def patternMatch(gProp: AbstractGameProposition): Option[(AbstractGameProposition, AbstractGameProposition)]

  /**
   * Factory that builds a composite dialogue game proposition of type This.
   */
  protected def build(
    g1: AbstractGameProposition,
    g2: AbstractGameProposition): This

  // left associativity transformation
  def leftAssociative(): This =
    patternMatch(g2) match {
      case Some((g21, g22)) =>
        build(
          leftAddition(g21, g1),
          g22).leftAssociative
      case None =>
        build(g1.leftAssociative,
          g2.leftAssociative)
    }

  /**
   * Builds a proposition of type This by left-adding gAdd to gBase
   */
  protected def leftAddition(
    gBase: AbstractGameProposition,
    gAdd: AbstractGameProposition): This =
    patternMatch(gBase) match {
      case Some((g11, g12)) =>
        build(leftAddition(g11, gAdd), g12)
      case None =>
        build(gAdd, gBase)
    }

  // right associativity transformation
  def rightAssociative(): This =
    patternMatch(g1) match {
      // g1 is not "simple", parenthesis have been used
      case Some((g11, g12)) =>
        build(
          g11,
          rightAddition(g12, g2)).rightAssociative
      case None =>
        build(g1.rightAssociative,
          g2.rightAssociative)
    }

  /**
   * Builds a proposition of type This by right-adding gAdd to gBase
   */
  protected def rightAddition(
    gBase: AbstractGameProposition,
    gAdd: AbstractGameProposition): AbstractGameProposition =
    patternMatch(gBase) match {
      case Some((g11, g12)) =>
        build(g11, rightAddition(g12, gAdd))
      // Leaf case
      case None =>
        build(gBase, gAdd)
    }

  def dropLeft(): Option[AbstractGameProposition] = {
    val thisPropRightAssociative = rightAssociative()
    thisPropRightAssociative.g1.dropLeft() match {
      case Some(gProp) =>
        Some(build(gProp, thisPropRightAssociative.g2))
      case None =>
        Some(thisPropRightAssociative.g2)
    }
  }

  def getLeft(): Option[AnyGameProposition] = {
    val thisPropRightAssociative = rightAssociative()
    thisPropRightAssociative.g1 match {
      case gProp @ GameProposition(name, goal) =>
        Some(gProp)
      case AbstractComposedGameProposition(g11, g12) =>
        thisPropRightAssociative.g1.getLeft()
      case other =>
        throw new Error("This is not expected: " + other)
    }
  }

  def contains(prop: AbstractGameProposition): Boolean =
    this == prop || g1.contains(prop) || g2.contains(prop)

  // Instantiation
  protected def instantiable1: AbstractGameProposition =
    g1
  protected def instantiable2: AbstractGameProposition =
    g2
  protected def update(
    instantiable1: AbstractGameProposition,
    instantiable2: AbstractGameProposition): This =
    build(g1, g2)

  /**
   * Symbol of this combination of dialogue game proposition
   */
  def symbol: CombinationSymbol
  override def toString: String = g1 + CombinationSymbol.toString(symbol) + g2 // "(" + g1 + ")" + symbol + "(" + g2 + ")"
}

/**
 * Provides extractor for composite dialogue game proposition
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
object AbstractComposedGameProposition {
  /**
   * Extractor of a binary composite dialogue game proposition that returns its first and second dialogue game proposition.
   */
  def unapply(gProp: AbstractComposedGameProposition[_]): Option[(AbstractGameProposition, AbstractGameProposition)] =
    Some((gProp.g1, gProp.g2))
}

/**
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
case class EmbeddedGameProposition(
  g1: AbstractGameProposition,
  g2: AbstractGameProposition) extends AbstractComposedGameProposition[EmbeddedGameProposition] {

  def build(
    g1: AbstractGameProposition,
    g2: AbstractGameProposition): EmbeddedGameProposition =
    EmbeddedGameProposition(g1, g2)

  def patternMatch(gProp: AbstractGameProposition): Option[(AbstractGameProposition, AbstractGameProposition)] =
    gProp match {
      case EmbeddedGameProposition(g1, g2) =>
        Some((g1, g2))
      case _ => None
    }
  def symbol: CombinationSymbol = Embedding
}

/**
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 */
case class PreSequenceGameProposition(
  g1: AbstractGameProposition,
  g2: AbstractGameProposition) extends AbstractComposedGameProposition[PreSequenceGameProposition] {

  def build(
    g1: AbstractGameProposition,
    g2: AbstractGameProposition): PreSequenceGameProposition =
    PreSequenceGameProposition(g1, g2)

  def patternMatch(gProp: AbstractGameProposition): Option[(AbstractGameProposition, AbstractGameProposition)] =
    gProp match {
      case PreSequenceGameProposition(g1, g2) =>
        Some((g1, g2))
      case _ => None
    }
  def symbol: CombinationSymbol = PreSequence
}

/**
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
case class SequenceGameProposition(
  g1: AbstractGameProposition,
  g2: AbstractGameProposition) extends AbstractComposedGameProposition[SequenceGameProposition] {

  def build(
    g1: AbstractGameProposition,
    g2: AbstractGameProposition): SequenceGameProposition =
    SequenceGameProposition(g1, g2)

  def patternMatch(gProp: AbstractGameProposition): Option[(AbstractGameProposition, AbstractGameProposition)] =
    gProp match {
      case SequenceGameProposition(g1, g2) =>
        Some((g1, g2))
      case _ => None
    }

  def symbol: CombinationSymbol = Sequence
}
