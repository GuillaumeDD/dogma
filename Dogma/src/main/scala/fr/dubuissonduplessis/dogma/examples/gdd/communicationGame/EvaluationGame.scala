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
package fr.dubuissonduplessis.dogma.examples.gdd.communicationGame

import fr.dubuissonduplessis.dogma.game.factory.GameRepositories
import fr.dubuissonduplessis.dogma.gameInstance.GameInstances
import fr.dubuissonduplessis.dogma.commitment.Commitments
import fr.dubuissonduplessis.dogma.dialogue.Dialogue
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
import fr.dubuissonduplessis.dogma.game.CommunicationGames
import fr.dubuissonduplessis.dogma.ditpp.informationTransfer.Disagreement
import fr.dubuissonduplessis.dogma.instantiable.Variable
import fr.dubuissonduplessis.dogma.ditpp.informationTransfer.Inform
import fr.dubuissonduplessis.dogma.ditpp.informationTransfer.Answer
import fr.dubuissonduplessis.dogma.ditpp.informationTransfer.Agreement
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.CommitmentStoreOperations
import fr.dubuissonduplessis.dogma.examples.gdd.dialogueGame.propositional.PropositionalGoal
import fr.dubuissonduplessis.dogma.commitment.SemanticTypes
import fr.dubuissonduplessis.dogma.ditpp.informationTransfer.Correction
import fr.dubuissonduplessis.dogma.ditpp.informationTransfer.AcceptCorrection
import fr.dubuissonduplessis.dogma.ditpp.informationTransfer.DeclineCorrection

/**
 * Communication game module for the evaluation game.
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait EvaluationGame {
  this: Dialogue with Commitments with SemanticTypes with CommitmentStoreOperations with GameRepositories with GameInstances with CommunicationGames with PropositionalGoal =>
  // Factory
  protected object EvaluationGame extends CommunicationGameFactory {
    val name: String = "evaluation"
    def apply(interlocutors: Set[Interlocutor]): CommunicationGame =
      new EvaluationGame(interlocutors.toList: _*)
  }
  // Load Communication Game Factory
  loadCommunicationGameFactory(EvaluationGame)

  class EvaluationGame private (speakers: Interlocutor*)
    extends CommunicationGame {

    require(speakers.toSet.size == 2)
    // TODO To be changed when dialogue will be multiparty
    protected def otherSpeaker(speaker: Interlocutor): Interlocutor =
      (dialogueParticipants - speaker).iterator.next

    val id: GameID = GameID("ev")
    val name: String = "evaluation"

    // TODO: see the interests of method dialogueParticipants
    def dialogueParticipants: Set[Interlocutor] = speakers.toSet

    def getRulesFor(speaker: Interlocutor): Set[AnyGameCommitment] = Set(
      // InformativeAct(_, p) -> C(_,p)
      C(this, speaker, otherSpeaker(speaker),
        Inform(speaker, P) *| Answer(speaker, P) *| Agreement(speaker, P)
          =*=>
          Create(speaker, C(speaker, otherSpeaker(speaker), P))),
      // InformativeAct(_, p) -> Agreement(other(_), p) | Disagreement(other(_), p)
      C(this, speaker, otherSpeaker(speaker),
        Inform(speaker, P) *|
          Answer(speaker, P)
          =*=>
          Create(speaker, C(this, speaker, otherSpeaker(speaker),
            Agreement(otherSpeaker(speaker), P) *|
              Disagreement(otherSpeaker(speaker), P) *|
              Correction(otherSpeaker(speaker), P, P1, CorrectionConstraint(P, P1))))),
      // Disconfirm/Disagreement(_, p) -> C(_, ~p)
      C(this, speaker, otherSpeaker(speaker),
        Disagreement(speaker, P)
          =*=>
          Create(speaker, C(speaker, otherSpeaker(speaker), negP))),

      C(this, speaker, otherSpeaker(speaker),
        Correction(speaker, P1, P2) =*=> Create(speaker, C(this, speaker, otherSpeaker(speaker),
          AcceptCorrection(otherSpeaker(speaker), P1, P2) *| DeclineCorrection(otherSpeaker(speaker), P1, P2)))),
      // Correction is a Disagreement
      C(this, speaker, otherSpeaker(speaker),
        Correction(speaker, P1, P2) =*=> Create(speaker, C(speaker, otherSpeaker(speaker), negP1))),
      // Correction commits on the correction
      C(this, speaker, otherSpeaker(speaker),
        Correction(speaker, P1, P2) =*=> Create(speaker, C(speaker, otherSpeaker(speaker), P2))),
      // AcceptCorrection commits on the correction
      C(this, speaker, otherSpeaker(speaker),
        AcceptCorrection(speaker, P1, P2) =*=> Create(speaker, C(speaker, otherSpeaker(speaker), P2))),
      // AcceptCorrection cancels the corrected proposition
      C(this, speaker, otherSpeaker(speaker),
        AcceptCorrection(speaker, P1, P2) =*=> Cancel(speaker, C(speaker, otherSpeaker(speaker), P1))))
  }
}
