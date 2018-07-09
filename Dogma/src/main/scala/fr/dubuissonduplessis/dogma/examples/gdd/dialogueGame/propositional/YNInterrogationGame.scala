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
import fr.dubuissonduplessis.dogma.description._
import fr.dubuissonduplessis.dogma.ditpp.informationSeeking.PropositionalQuestion
import fr.dubuissonduplessis.dogma.ditpp.informationTransfer.Answer
import fr.dubuissonduplessis.dogma.ditpp.feedback.ExecNegativeAutoFB

/**
 * Dialogue game module for the Y/N interrogation game.
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait YNInterrogationGame {
  this: DialogueGameModule with AbstractVerificationGame with QuestionGoal with PropositionalGoal =>

  object YNInterrogationGame {
    def apply(initiator: Interlocutor,
      partner: Interlocutor,
      goal: PropContent): DialogueGame =
      new YNInterrogationGame(initiator, partner, goal)

    val name: String = "YNInterrogation"
  }

  // Automatic loading of game
  private val factory =
    DialogueGameFactory.fromDialogueGame(
      YNInterrogationGame.name,
      YNInterrogationGame.apply _,
      castPropositionalContent _)

  loadDialogueGameFactory(factory)

  class YNInterrogationGame private (
    val initiator: Interlocutor,
    val partner: Interlocutor,
    val goal: PropContent) extends AbstractVerificationGame {

    val name: String = YNInterrogationGame.name
    def gameGoal: PropContent = goal

    import Role._

    // Rules and effects
    def getRulesFor(speaker: Interlocutor)(instance: DialogueGameInstance): Set[AnyGameCommitment] =
      role(speaker) match {
        case INITIATOR =>
          Set(
            C(instance, initiator, partner,
              PropositionalQuestion(initiator, ynQuestion(goal))))

        case PARTNER =>
          Set(
            // PropositionalQuestion
            C(instance, partner, initiator,
              PropositionalQuestion(initiator, ynQuestion(goal)) ==> Create(partner,
                C(instance, partner, initiator,
                  Answer(partner, goal) *|
                    Answer(partner, propNeg(goal)) *|
                    ExecNegativeAutoFB(partner, ynQuestion(goal))))))

      }

    def getPropositionalEffectsFor(speaker: Interlocutor)(instance: DialogueGameInstance): Set[AnyGameCommitment] =
      role(speaker) match {
        case INITIATOR =>
          Set()
        case PARTNER =>
          Set(
            // ExecNegativeAutoFB
            C(instance, partner, initiator,
              ExecNegativeAutoFB(partner, ynQuestion(goal)) ==> Create(partner, C(partner, initiator, fail(ynQuestion(goal))))))
      }
  }
}
