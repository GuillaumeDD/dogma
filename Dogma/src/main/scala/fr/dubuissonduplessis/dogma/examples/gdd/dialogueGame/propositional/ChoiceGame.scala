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
import fr.dubuissonduplessis.dogma.description._
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
import fr.dubuissonduplessis.dogma.ditpp.informationSeeking.ChoiceQuestion
import fr.dubuissonduplessis.dogma.ditpp.informationTransfer.Answer
import fr.dubuissonduplessis.dogma.ditpp.feedback.ExecNegativeAutoFB

/**
 * Dialogue game module for the choice game.
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait ChoiceGame {
  this: DialogueGameModule with QuestionGoal with PropositionalGoal =>

  /**
   * Type that represents a choice question
   */
  type ChoiceQuestionType <: QuestionType
  protected def castChoiceQuestionGoal(o: Any): ChoiceQuestionType

  object ChoiceGame {
    def apply(initiator: Interlocutor,
      partner: Interlocutor,
      goal: ChoiceQuestionType): DialogueGame =
      new ChoiceGame(initiator, partner, goal)

    val name: String = "Choice"
  }

  // Automatic loading of game
  private val factory =
    DialogueGameFactory.fromDialogueGame(
      ChoiceGame.name,
      ChoiceGame.apply _,
      castChoiceQuestionGoal _)

  loadDialogueGameFactory(factory)

  class ChoiceGame private (
    val initiator: Interlocutor,
    val partner: Interlocutor,
    val goal: ChoiceQuestionType) extends DialogueGame
    with QuestionConstraint {

    type GoalType = ChoiceQuestionType

    val name: String = ChoiceGame.name
    def gameGoal: ChoiceQuestionType = goal

    import Role._

    // Entry and exit conditions
    def getEntryConditionsFor(speaker: Interlocutor): GameCondition =
      role(speaker) match {
        case INITIATOR =>
          GameCondition.success
        case PARTNER =>
          // Entry conditions are reached if the partner is not committed to either :
          // - a proposition that resolves the goal question, or
          // - a Fail(goal) proposition.
          InactivePropositionalCommitment(
            partner,
            initiator,
            resolves(_, goal)) &&
            InactivePropositionalCommitment(partner, initiator, fail(goal))
      }

    def getSuccessExitConditionsFor(speaker: Interlocutor): GameCondition =
      role(speaker) match {
        case _ =>
          // Success conditions are reached if the partner is committed to a proposition that
          // resolves the goal question
          ExistPropositionalCommitment(
            partner,
            initiator,
            resolves(_, goal))
      }

    def getFailureExitConditionsFor(speaker: Interlocutor): GameCondition =
      role(speaker) match {
        case _ =>
          ExistPropositionalCommitment(C(partner, initiator, fail(goal)))
      }

    // Rules and effects
    def getRulesFor(speaker: Interlocutor)(instance: DialogueGameInstance): Set[AnyGameCommitment] =
      role(speaker) match {
        case INITIATOR =>
          Set(
            // SetQuestion
            C(instance, initiator, partner,
              ChoiceQuestion(initiator, goal)))

        case PARTNER =>
          Set(
            // SetQuestion ==> Answers
            C(instance, partner, initiator,
              ChoiceQuestion(initiator, goal) ==> Create(partner,
                C(instance, partner, initiator,
                  Answer(partner, P, relevantConstraint(P)) *|
                    ExecNegativeAutoFB(partner, goal)))),
            // Answer (relevant) =*=> Answers
            C(instance, partner, initiator,
              Answer(partner, P, strictlyRelevantConstraint(P)) =*=> Create(partner,
                C(instance, partner, initiator,
                  Answer(partner, P2, relevantConstraint(P2)) *|
                    ExecNegativeAutoFB(partner, goal)))))
      }

    def getPropositionalEffectsFor(speaker: Interlocutor)(instance: DialogueGameInstance): Set[AnyGameCommitment] =
      role(speaker) match {
        case INITIATOR =>
          Set()
        case PARTNER =>
          Set(
            // Answer
            // C(instance, partner, initiator,
            //  Answer(partner, P) =*=> Create(partner, C(partner, initiator, P))),
            // ExecNegativeAutoFB
            C(instance, partner, initiator,
              ExecNegativeAutoFB(partner, goal) ==> Create(partner, C(partner, initiator, fail(goal)))))
      }
  }
}
