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

import fr.dubuissonduplessis.dogma.instantiable.Variable
import fr.dubuissonduplessis.dogma.game.language.AbstractGameProposition
import fr.dubuissonduplessis.dogma.instantiable.Instantiable
import fr.dubuissonduplessis.dogma.game.language.AnyGameProposition

/**
 * A special case of instantiation variable representing an uninstantiated dialogue game proposition.
 * Such a variable can be combined to form pre-sequence combination (~>), sequence combination (/) and
 * embedding combination (<<). These combinations can be completely uninstantiated or semi-instantiated.
 * For instance:
 * {{{
 * val v1 = GamePropositionVariable("G1")
 * val v2 = GamePropositionVariable("G2")
 *
 * // Completely uninstantiated composite dialogue game proposition
 * v1 ~> v2
 * //> res0: ... = G1~>G2
 * v1 / v2
 * //> res1: ... = G1;G2
 * v1 << v2
 * //> res2: ... = G1<G2
 * }}}
 *
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
sealed abstract class GamePropositionVariable extends Variable[AbstractGameProposition] {
  /**
   * Builds a completely uninstantiated pre-sequence combination of dialogue game propositions.
   */
  def ~>(v2: GamePropositionVariable): VariableCombination =
    PreSequenceCombination(this, v2)
  /**
   * Builds a completely uninstantiated sequence combination of dialogue game propositions.
   */
  def /(v2: GamePropositionVariable): VariableCombination =
    SequenceCombination(this, v2)
  /**
   * Builds a completely uninstantiated embdding combination of dialogue game propositions.
   */
  def <<(v2: GamePropositionVariable): VariableCombination =
    EmbeddedCombination(this, v2)

  /**
   * Builds a sem-instantiated pre-sequence combination of dialogue game propositions.
   */
  def ~>(g0: AbstractGameProposition): CompositeGamePropositionMatcher =
    PreSequenceCombination(this, GamePropositionVariable("Fake")).build(this, g0)
  /**
   * Builds a sem-instantiated sequence combination of dialogue game propositions.
   */
  def /(g0: AbstractGameProposition): CompositeGamePropositionMatcher =
    SequenceCombination(this, GamePropositionVariable("Fake")).build(this, g0)
  /**
   * Builds a sem-instantiated embedding combination of dialogue game propositions.
   */
  def <<(g0: AbstractGameProposition): CompositeGamePropositionMatcher =
    EmbeddedCombination(this, GamePropositionVariable("Fake")).build(this, g0)
}

/**
 * Factory for dialogue game proposition variable.
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
object GamePropositionVariable {
  /**
   * Builds a dialogue game proposition variable with a given name.
   */
  def apply(varName: String): GamePropositionVariable =
    new GamePropositionVariable {
      def name: String = varName
    }
}
