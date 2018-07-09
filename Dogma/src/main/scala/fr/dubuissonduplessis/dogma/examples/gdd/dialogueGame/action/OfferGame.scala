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
import fr.dubuissonduplessis.dogma.ditpp.actionDiscussion.Offer
import fr.dubuissonduplessis.dogma.description._
import fr.dubuissonduplessis.dogma.ditpp.actionDiscussion.AcceptOffer
import fr.dubuissonduplessis.dogma.ditpp.actionDiscussion.DeclineOffer

/**
 * Dialogue game module for the offer game.
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait OfferGame {
  this: DialogueGameModule with ActionGoal =>
  object OfferGame {
    def apply(initiator: Interlocutor,
      partner: Interlocutor,
      goal: ActionGoal): DialogueGame =
      new OfferGame(initiator, partner, goal)

    val name: String = "Offer"
  }

  // Automatic loading of game
  private val factory =
    DialogueGameFactory.fromDialogueGame(
      OfferGame.name,
      OfferGame.apply _,
      castActionGoal _)

  loadDialogueGameFactory(factory)

  class OfferGame private (
    val initiator: Interlocutor,
    val partner: Interlocutor,
    val goal: ActionGoal) extends DialogueGame {

    import CommitmentState._

    val name: String = OfferGame.name

    type GoalType = ActionGoal

    import Role._

    // Entry and exit conditions
    def getEntryConditionsFor(speaker: Interlocutor): GameCondition =
      role(speaker) match {
        case INITIATOR =>
          InactiveActionCommitment(initiator, partner, goal) &&
            InactiveActionCommitment(initiator, partner, actionNeg(goal))
        case PARTNER =>
          GameCondition.success
      }

    def getSuccessExitConditionsFor(speaker: Interlocutor): GameCondition =
      role(speaker) match {
        case _ => ExistActionCommitment(C(initiator, partner, goal))
      }

    def getFailureExitConditionsFor(speaker: Interlocutor): GameCondition =
      role(speaker) match {
        case _ => ExistActionCommitment(C(initiator, partner, goal, Fal))
      }

    // Rules and effects
    def getRulesFor(speaker: Interlocutor)(instance: DialogueGameInstance): Set[AnyGameCommitment] =
      role(speaker) match {
        case INITIATOR =>
          Set(
            C(instance, initiator, partner,
              Offer(initiator, goal)))

        case PARTNER =>
          Set(
            C(instance, partner, initiator,
              Offer(initiator, goal) ==> Create(partner,
                C(instance, partner, initiator, AcceptOffer(partner, goal) *| DeclineOffer(partner, goal)))))

      }

    def getPropositionalEffectsFor(speaker: Interlocutor)(instance: DialogueGameInstance): Set[AnyGameCommitment] =
      role(speaker) match {
        case INITIATOR =>
          Set( // AcceptOffer and DeclineOffer
            C(instance, initiator, partner,
              AcceptOffer(partner, goal) ==> Create(initiator, C(initiator, partner, goal))),
            C(instance, initiator, partner,
              DeclineOffer(partner, goal) ==> Failure(initiator, C(initiator, partner, goal))))
        case PARTNER =>
          Set()
      }
  }
}
