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
package fr.dubuissonduplessis.dogma.examples.gdd.dialogueGame.propositional

import fr.dubuissonduplessis.dogma.examples.gdd.dialogueGame.DialogueGameModule
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
import fr.dubuissonduplessis.dogma.ditpp.informationSeeking.CheckQuestion
import fr.dubuissonduplessis.dogma.description._
import fr.dubuissonduplessis.dogma.ditpp.informationSeeking.PosiCheck
import fr.dubuissonduplessis.dogma.ditpp.informationTransfer.Confirm
import fr.dubuissonduplessis.dogma.ditpp.informationTransfer.Disconfirm
import fr.dubuissonduplessis.dogma.ditpp.feedback.ExecNegativeAutoFB

/**
 * Dialogue game module for the verification game.
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait VerificationGame {
  this: DialogueGameModule with AbstractVerificationGame with QuestionGoal with PropositionalGoal =>

  object VerificationGame {
    def apply(initiator: Interlocutor,
      partner: Interlocutor,
      goal: PropContent): DialogueGame =
      new VerificationGame(initiator, partner, goal)

    val name: String = "Verification"
  }

  // Automatic loading of game
  private val factory =
    DialogueGameFactory.fromDialogueGame(
      VerificationGame.name,
      VerificationGame.apply _,
      castPropositionalContent _)

  loadDialogueGameFactory(factory)

  class VerificationGame private (
    val initiator: Interlocutor,
    val partner: Interlocutor,
    val goal: PropContent) extends AbstractVerificationGame {

    val name: String = VerificationGame.name
    def gameGoal: PropContent = goal

    import Role._

    // Rules and effects
    def getRulesFor(speaker: Interlocutor)(instance: DialogueGameInstance): Set[AnyGameCommitment] =
      role(speaker) match {
        case INITIATOR =>
          Set(
            C(instance, initiator, partner,
              CheckQuestion(initiator, ynQuestion(goal)) *|
                PosiCheck(initiator, ynQuestion(goal))))

        case PARTNER =>
          Set(
            // CheckQuestion et PosiCheck
            C(instance, partner, initiator,
              CheckQuestion(initiator, ynQuestion(goal)) *| PosiCheck(initiator, ynQuestion(goal)) ==>
                Create(partner, C(instance, partner, initiator,
                  Confirm(partner, goal) *|
                    Disconfirm(partner, goal) *|
                    ExecNegativeAutoFB(partner, ynQuestion(goal))))))

      }

    def getPropositionalEffectsFor(speaker: Interlocutor)(instance: DialogueGameInstance): Set[AnyGameCommitment] =
      role(speaker) match {
        case INITIATOR =>
          Set()
        case PARTNER =>
          Set(
            // Confirm
            C(instance, partner, initiator,
              Confirm(partner, goal) ==> Create(partner, C(partner, initiator, goal))),
            // Disconfirm
            C(instance, partner, initiator,
              Disconfirm(partner, goal) ==> Create(partner, C(partner, initiator, propNeg(goal)))),
            // ExecNegativeAutoFB
            C(instance, partner, initiator,
              ExecNegativeAutoFB(partner, ynQuestion(goal)) ==> Create(partner, C(partner, initiator, fail(ynQuestion(goal))))))
      }
  }
}
