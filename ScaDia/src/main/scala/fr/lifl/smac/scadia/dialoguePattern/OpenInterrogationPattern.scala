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
package fr.lifl.smac.scadia.dialoguePattern

import fr.dubuissonduplessis.dogma.examples.gdd.dialogueGame.DialogueGameModule
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
import fr.dubuissonduplessis.dogma.ditpp.informationSeeking.SetQuestion
import fr.dubuissonduplessis.dogma.ditpp.feedback.ExecNegativeAutoFB
import fr.dubuissonduplessis.dogma.description._
import fr.dubuissonduplessis.dogma.ditpp.informationTransfer.Answer

trait OpenInterrogationPattern {
  this: DialogueGameModule with PropositionalGoal =>
  protected object OpenInterrogationGame {
    def apply(initiator: Interlocutor,
      partner: Interlocutor,
      goal: QuestionType): DialogueGame =
      new OpenInterrogationGame(initiator, partner, goal)

    val name: String = "OpenInterrogation"
  }

  // Automatic loading of game
  private val factory =
    DialogueGameFactory.fromDialogueGame(
      OpenInterrogationGame.name,
      OpenInterrogationGame.apply _,
      castQuestionGoal _)

  loadDialogueGameFactory(factory)

  /**
   * Represents the dialogue pattern:
   * setQuestion(initiator, q) ==> answer(partner, p) with resolves(p, q) | execNegativeAutoFB(partner, q)
   */
  protected class OpenInterrogationGame private (
    val initiator: Interlocutor,
    val partner: Interlocutor,
    val goal: QuestionType) extends DialogueGame
    with QuestionConstraint {

    type GoalType = QuestionType

    val name: String = OpenInterrogationGame.name
    def gameGoal: QuestionType = goal

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
              SetQuestion(initiator, goal)))
        case PARTNER =>
          Set(
            // SetQuestion ==> Answer | ExecNegativeAutoFB
            C(instance, partner, initiator,
              SetQuestion(initiator, goal) ==> Create(partner,
                C(instance, partner, initiator,
                  Answer(partner, P, resolvesConstraint(P)) *|
                    ExecNegativeAutoFB(partner, goal)))))
      }

    def getPropositionalEffectsFor(speaker: Interlocutor)(instance: DialogueGameInstance): Set[AnyGameCommitment] =
      role(speaker) match {
        case INITIATOR =>
          Set()
        case PARTNER =>
          Set(
            // Answer
            C(instance, partner, initiator,
              Answer(partner, P) =*=> Create(partner, C(partner, initiator, P))),
            // ExecNegativeAutoFB
            C(instance, partner, initiator,
              ExecNegativeAutoFB(partner, goal) ==> Create(partner, C(partner, initiator, fail(goal)))))

      }

  }
}
