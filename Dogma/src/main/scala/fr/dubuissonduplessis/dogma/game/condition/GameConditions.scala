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
package fr.dubuissonduplessis.dogma.game.condition

/**
 * Module providing the definitions and implementations of dialogue game conditions.
 *
 * The main abstract class is [[fr.dubuissonduplessis.dogma.game.condition.GameConditions#GameCondition]] which defines
 * a game condition.
 *
 * Game conditions can be combined with the following operators: && (and), || (or).
 * Game conditions can also be negated.
 * Game conditions are being used in the definition of dialogue games (see [[fr.dubuissonduplessis.dogma.game.DialogueGames]]).
 *
 * Factories provides way to generate special cases conditions such as:
 * {{{
 * // Condition that is always true
 * val condition1 = GameCondition.success
 * // Condition that is always false
 * val condition2 = GameCondition.failure
 * }}}
 *
 * This module is specialized by [[fr.dubuissonduplessis.dogma.game.condition.CommitmentConditions]]. This latter
 * provides primitives to easily define game conditions related to commitments (see package [[fr.dubuissonduplessis.dogma.commitment]]).
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait GameConditions {
  self: GameConditionsRequirements =>

  /**
   * Abstract dialogue game condition.
   *
   * A dialogue game condition represents a condition on the information state, which is either
   * true or false once it is computed.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected abstract class GameCondition {
    /**
     * Computes the condition.
     * @return true if the condition is fulfilled, else false
     */
    def apply(): Boolean

    /**
     * Infix "and" operator for game conditions.
     * @param gc right operand
     * @return a new game condition which computes the conjonction of its operands
     */
    def &&(gc: GameCondition): GameCondition =
      new &&(this, gc)

    /**
     * Infix "or" operator for game conditions.
     * @param gc right operand
     * @return a new game condition which computes the disjonction of its operands
     */
    def ||(gc: GameCondition): GameCondition =
      new ||(this, gc)

    /**
     * Negates a game conditions.
     * @return a new game condition which computes the negation of the negated game condition
     */
    def neg: GameCondition =
      Neg(this)
  }

  /**
   * Factories for game conditions.
   *
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected object GameCondition {
    /**
     * Creates an always true game condition
     *
     */
    def success: GameCondition =
      SuccessGameCondition
    /**
     * Creates an always false game condition
     *
     */
    def failure: GameCondition =
      FailureGameCondition

    private object SuccessGameCondition extends GameCondition {
      def apply(): Boolean =
        true

      override def toString: String = ""
    }

    private object FailureGameCondition extends GameCondition {
      def apply(): Boolean =
        false

      override def toString: String = "X"
    }
  }

  private abstract class BinaryCondition extends GameCondition {
    def gc01: GameCondition
    def gc02: GameCondition
  }

  private case class &&(gc01: GameCondition, gc02: GameCondition) extends BinaryCondition {
    def apply(): Boolean =
      gc01() && gc02()

    override def toString: String = "(" + gc01 + ") && (" + gc02 + ")"
  }

  private case class Neg(gc: GameCondition) extends GameCondition {
    def apply(): Boolean =
      !gc()

    override def neg: GameCondition =
      gc

    override def toString: String = "(! " + gc + ")"
  }

  private case class ||(gc01: GameCondition, gc02: GameCondition) extends BinaryCondition {
    def apply(): Boolean =
      gc01() || gc02()

    override def toString: String = "(" + gc01 + ") || (" + gc02 + ")"
  }
}
