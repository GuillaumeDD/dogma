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
package fr.dubuissonduplessis.dogma.examples.gdd.dialogueGame.action

import fr.dubuissonduplessis.dogma.examples.gdd.dialogueGame.DialogueGameModule
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
import fr.dubuissonduplessis.dogma.ditpp.actionDiscussion.Request
import fr.dubuissonduplessis.dogma.ditpp.actionDiscussion.Request
import fr.dubuissonduplessis.dogma.ditpp.actionDiscussion.AcceptRequest
import fr.dubuissonduplessis.dogma.ditpp.actionDiscussion.DeclineRequest
import fr.dubuissonduplessis.dogma.description._

/**
 * Dialogue game module for the request game.
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait RequestGame {
  this: DialogueGameModule with ActionGoal =>

  object RequestGame {
    val name: String = "Request"
    def apply(initiator: Interlocutor,
      partner: Interlocutor,
      goal: ActionGoal): DialogueGame =
      new RequestGame(initiator, partner, goal)
  }

  // Automatic loading of game
  private val factory =
    DialogueGameFactory.fromDialogueGame(
      RequestGame.name,
      RequestGame.apply _,
      castActionGoal _)

  loadDialogueGameFactory(factory)

  class RequestGame private (
    val initiator: Interlocutor,
    val partner: Interlocutor,
    val goal: ActionGoal) extends DialogueGame {

    import CommitmentState._

    val name: String = RequestGame.name

    type GoalType = ActionGoal

    import Role._

    // Entry and exit conditions
    def getEntryConditionsFor(speaker: Interlocutor): GameCondition =
      role(speaker) match {
        case INITIATOR =>
          GameCondition.success
        case PARTNER =>
          InactiveActionCommitment(partner, initiator, goal) &&
            InactiveActionCommitment(partner, initiator, actionNeg(goal))

      }

    def getSuccessExitConditionsFor(speaker: Interlocutor): GameCondition =
      role(speaker) match {
        case _ => ExistActionCommitment(C(partner, initiator, goal))
      }

    def getFailureExitConditionsFor(speaker: Interlocutor): GameCondition =
      role(speaker) match {
        case _ => ExistActionCommitment(C(partner, initiator, goal, Fal))
      }

    // Rules and effects
    def getRulesFor(speaker: Interlocutor)(instance: DialogueGameInstance): Set[AnyGameCommitment] =
      role(speaker) match {
        case INITIATOR =>
          Set(
            C(instance, initiator, partner,
              Request(initiator, goal)))
        case PARTNER =>
          Set(
            C(instance, partner, initiator,
              Request(initiator, goal) ==> Create(partner,
                C(instance, partner, initiator, AcceptRequest(partner, goal) *| DeclineRequest(partner, goal)))))

      }

    def getPropositionalEffectsFor(speaker: Interlocutor)(instance: DialogueGameInstance): Set[AnyGameCommitment] =
      role(speaker) match {
        case INITIATOR =>
          Set()
        case PARTNER =>
          Set(
            // AcceptRequest and DeclineRequest
            C(instance, partner, initiator,
              AcceptRequest(partner, goal) ==> Create(partner, C(partner, initiator, goal))),
            C(instance, partner, initiator,
              DeclineRequest(partner, goal) ==> Failure(partner, C(partner, initiator, goal))))

      }
  }
}
