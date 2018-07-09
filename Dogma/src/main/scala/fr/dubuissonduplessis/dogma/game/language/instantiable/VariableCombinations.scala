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
package fr.dubuissonduplessis.dogma.game.language.instantiable

import fr.dubuissonduplessis.dogma.game.language.AbstractGameProposition
import fr.dubuissonduplessis.dogma.instantiable.InstantiationVariable
import fr.dubuissonduplessis.dogma.game.language.PreSequenceGameProposition
import fr.dubuissonduplessis.dogma.instantiable.Instantiable
import fr.dubuissonduplessis.dogma.instantiable.Generator
import fr.dubuissonduplessis.dogma.instantiable.Variable
import fr.dubuissonduplessis.dogma.instantiable.InstantiationSet
import fr.dubuissonduplessis.dogma.game.language.SequenceGameProposition
import fr.dubuissonduplessis.dogma.game.language.EmbeddedGameProposition
import fr.dubuissonduplessis.dogma.game.language.CombinationSymbol
import fr.dubuissonduplessis.dogma.game.language.CombinationSymbol._

/**
 * Abstract representation of a completely uninstantiated composite dialogue game proposition.
 *
 * Specialisations include:
 *  - Pre-sequence combination (see [[fr.dubuissonduplessis.dogma.game.language.instantiable.PreSequenceCombination]])
 *  - Sequence combination (see [[fr.dubuissonduplessis.dogma.game.language.instantiable.SequenceCombination]])
 *  - Embedding combination (see [[fr.dubuissonduplessis.dogma.game.language.instantiable.EmbeddedCombination]])
 *
 * Such combinations can easily be built directly from [[fr.dubuissonduplessis.dogma.game.language.instantiable.GamePropositionVariable]].
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
sealed abstract class VariableCombination extends Instantiable[CompositeGamePropositionMatcher] {
  /**
   * First uninstantiated dialogue game proposition
   */
  def v1: Variable[AbstractGameProposition]
  /**
   * Second uninstantiated dialogue game proposition
   */
  def v2: Variable[AbstractGameProposition]

  // Implementation info required
  protected def build(v1: Variable[AbstractGameProposition],
    prop: AbstractGameProposition): CompositeGamePropositionMatcher

  protected def instantiateWithImpl(s: InstantiationSet): CompositeGamePropositionMatcher =
    build(v1, v2.instantiateWith(s))
  def isInstantiableWith(s: InstantiationSet): Boolean =
    v2.isInstantiableWith(s)
  def variables: Set[InstantiationVariable] =
    Set(v1, v2)

  /**
   * Symbol of this combination
   */
  val symbol: CombinationSymbol
  override def toString = s"$v1${CombinationSymbol.toString(symbol)}$v2"
}

/**
 * Abstract representation of semi-instantiated composite dialogue game proposition.
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
sealed abstract class SemiInstantiatedVariableCombination
  extends Instantiable[AbstractGameProposition]
  with Generator[AbstractGameProposition, AbstractGameProposition] {

  /**
   * First uninstantiated dialogue game proposition
   */
  def v1: Variable[AbstractGameProposition]
  /**
   * Second instantiated dialogue game proposition
   */
  def prop0: AbstractGameProposition

  def isInstantiableWith(s: InstantiationSet): Boolean =
    v1.isInstantiableWith(s)
  def variables: Set[InstantiationVariable] =
    Set(v1)

  // TO IMPLEMENT
  protected def build(
    prop1: AbstractGameProposition,
    prop0: AbstractGameProposition): AbstractGameProposition

  protected def instantiateWithImpl(s: InstantiationSet): AbstractGameProposition =
    build(v1.instantiateWith(s), prop0)
  // Generator
  // TO IMPLEMENT
  protected def cast(t: AbstractGameProposition): Option[(AbstractGameProposition, AbstractGameProposition)]
  def fits(t: AbstractGameProposition): Boolean =
    cast(t) match {
      case Some((g1, g2)) =>
        g2 == prop0
      case None =>
        false
    }

  protected def bindingsImpl(t: AbstractGameProposition): InstantiationSet =
    // Implementation info required
    cast(t) match {
      case Some((g1, g2)) =>
        InstantiationSet() + (v1, g1)
      case None =>
        throw new RuntimeException(s"Unable to build bindings for $t")
    }

  /**
   * Symbol of this combination
   */
  val symbol: CombinationSymbol
  override def toString = s"$v1${CombinationSymbol.toString(symbol)}$prop0"
}

/**
 * Extractor of semi-instantiated dialogue game proposition.
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
object SemiInstantiatedVariableCombination {
  /**
   * Extractor of semi-instantiated dialogue game proposition from any instantiable resulting in an
   * [[fr.dubuissonduplessis.dogma.game.language.AbstractGameProposition]].
   * @return An option consisting in a pair containing the combination symbol as well as the instantiated dialogue
   * game proposition if the given instantiable is a semi-instantiated variable combination, or None
   */
  def unapply(o: Instantiable[AbstractGameProposition]): Option[(CombinationSymbol, AbstractGameProposition)] =
    o match {
      case combination: SemiInstantiatedVariableCombination =>
        Some((combination.symbol, combination.prop0))
      case _ =>
        None
    }
}

/**
 * Completely uninstantiated pre-sequence combination of dialogue game propositions.
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
protected[instantiable] case class PreSequenceCombination(
  v1: Variable[AbstractGameProposition],
  v2: Variable[AbstractGameProposition]) extends VariableCombination {

  protected[instantiable] def build(
    v1: Variable[AbstractGameProposition],
    prop: AbstractGameProposition): CompositeGamePropositionMatcher =
    PreSequenceBis(v1, prop)

  val symbol: CombinationSymbol = PreSequence

  private case class PreSequenceBis(
    v1: Variable[AbstractGameProposition],
    prop0: AbstractGameProposition)
    extends SemiInstantiatedVariableCombination {

    protected def build(prop1: AbstractGameProposition, prop0: AbstractGameProposition): AbstractGameProposition =
      PreSequenceGameProposition(prop1, prop0)

    // Generator
    protected def cast(t: AbstractGameProposition): Option[(AbstractGameProposition, AbstractGameProposition)] =
      t match {
        case PreSequenceGameProposition(g1, g2) =>
          Some((g1, g2))
        case _ =>
          None
      }

    val symbol: CombinationSymbol = PreSequenceCombination.this.symbol
  }
}

/**
 * Completely uninstantiated sequence combination of dialogue game propositions.
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
protected[instantiable] case class SequenceCombination(
  v1: Variable[AbstractGameProposition],
  v2: Variable[AbstractGameProposition]) extends VariableCombination {

  protected[instantiable] def build(
    v1: Variable[AbstractGameProposition],
    prop: AbstractGameProposition): CompositeGamePropositionMatcher =
    SequenceBis(v1, prop)

  val symbol: CombinationSymbol = Sequence

  private case class SequenceBis(
    v1: Variable[AbstractGameProposition],
    prop0: AbstractGameProposition)
    extends SemiInstantiatedVariableCombination {

    protected def build(
      prop1: AbstractGameProposition,
      prop0: AbstractGameProposition): AbstractGameProposition =
      SequenceGameProposition(prop1, prop0)

    // Generator
    protected def cast(t: AbstractGameProposition): Option[(AbstractGameProposition, AbstractGameProposition)] =
      t match {
        case SequenceGameProposition(g1, g2) =>
          Some((g1, g2))
        case _ =>
          None
      }

    val symbol: CombinationSymbol = SequenceCombination.this.symbol
  }
}

/**
 * Completely uninstantiated embedding combination of dialogue game propositions.
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
protected[instantiable] case class EmbeddedCombination(
  v1: Variable[AbstractGameProposition],
  v2: Variable[AbstractGameProposition]) extends VariableCombination {

  protected[instantiable] def build(
    v1: Variable[AbstractGameProposition],
    prop: AbstractGameProposition): CompositeGamePropositionMatcher =
    EmbeddedBis(v1, prop)

  val symbol: CombinationSymbol = Embedding

  private case class EmbeddedBis(
    v1: Variable[AbstractGameProposition],
    prop0: AbstractGameProposition)
    extends SemiInstantiatedVariableCombination {

    protected def build(
      prop1: AbstractGameProposition,
      prop0: AbstractGameProposition): AbstractGameProposition =
      EmbeddedGameProposition(prop1, prop0)

    // Generator
    protected def cast(t: AbstractGameProposition): Option[(AbstractGameProposition, AbstractGameProposition)] =
      t match {
        case EmbeddedGameProposition(g1, g2) =>
          Some((g1, g2))
        case _ =>
          None
      }

    val symbol: CombinationSymbol = EmbeddedCombination.this.symbol
  }
}
