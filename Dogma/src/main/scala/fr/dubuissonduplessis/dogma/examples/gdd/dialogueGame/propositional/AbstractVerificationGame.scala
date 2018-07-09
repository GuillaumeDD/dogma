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
package fr.dubuissonduplessis.dogma.examples.gdd.dialogueGame.propositional

import fr.dubuissonduplessis.dogma.examples.gdd.dialogueGame.DialogueGameModule
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor

/**
 * Abstract dialogue game module for Y/N-question-based dialogue games.
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait AbstractVerificationGame {
  this: DialogueGameModule with QuestionGoal with PropositionalGoal =>

  /**
   * Type that represents a Y/N question.
   */
  type YNQuestionType <: QuestionType

  /**
   * Factory to build a YNQuestion from a proposition.
   */
  protected def ynQuestion(content: PropContent): YNQuestionType

  /**
   * Helper method that should try to cast a given object into
   * the type of a Y/N question.
   * It should throw an exception if the cast is impossible.
   *
   */
  protected def castYNQuestionGoal(o: Any): YNQuestionType

  /**
   * Abstract dialogue game that defines the entry and exit conditions of a
   * Y/N-question-based dialogue game.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  abstract class AbstractVerificationGame extends DialogueGame {
    import Role._

    type GoalType = PropContent

    // Entry and exit conditions
    def getEntryConditionsFor(speaker: Interlocutor): GameCondition =
      role(speaker) match {
        case INITIATOR =>
          GameCondition.success
        case PARTNER =>
          InactivePropositionalCommitment(partner, initiator, goal) &&
            InactivePropositionalCommitment(partner, initiator, propNeg(goal)) &&
            InactivePropositionalCommitment(partner, initiator, fail(ynQuestion(goal)))

      }

    def getSuccessExitConditionsFor(speaker: Interlocutor): GameCondition =
      role(speaker) match {
        case _ => ExistPropositionalCommitment(C(partner, initiator, goal)) ||
          ExistPropositionalCommitment(C(partner, initiator, propNeg(goal)))

      }

    def getFailureExitConditionsFor(speaker: Interlocutor): GameCondition =
      role(speaker) match {
        case _ => ExistPropositionalCommitment(C(partner, initiator, fail(ynQuestion(goal))))
      }
  }
}
