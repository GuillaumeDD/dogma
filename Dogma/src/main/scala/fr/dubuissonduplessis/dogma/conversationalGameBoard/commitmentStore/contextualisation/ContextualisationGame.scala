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
package fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation

import fr.dubuissonduplessis.dogma.game.CommunicationGames
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
import fr.dubuissonduplessis.dogma.commitment.Commitments
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.dialogueActs._
import fr.dubuissonduplessis.dogma.game.factory.GameRepositories
import fr.dubuissonduplessis.dogma.gameInstance.GameInstances
import fr.dubuissonduplessis.dogma.dialogue.Dialogue
import fr.dubuissonduplessis.dogma.game.language.instantiable.GamePropositionVariable
import fr.dubuissonduplessis.dogma.instantiable.Variable
import fr.dubuissonduplessis.dogma.game.language.AnyGameProposition

/**
 * Module defining the contextualisation communication game and registering a factory to load
 * this communication game.
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait ContextualisationGame {
  this: Dialogue with Commitments with GameRepositories with GameInstances with ContextualisationOperations with CommunicationGames =>
  // Factory
  /**
   * Factory for the
   * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.ContextualisationGame#ContextualisationGame contextualisation communication game]].
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected object ContextualisationGame extends CommunicationGameFactory {
    val name: String = "contextualisation"
    def apply(interlocutors: Set[Interlocutor]): CommunicationGame =
      new ContextualisationGame(interlocutors.toList: _*)
  }

  // Load Communication Game Factory
  loadCommunicationGameFactory(ContextualisationGame)

  // Variables Declaration
  /**
   * Variable representing a dialogue game proposition or a combination of dialogue
   * game propositions.
   */
  private[contextualisation] val Xs = GamePropositionVariable("Xs")
  /**
   * Variable representing a dialogue game proposition or a combination of dialogue
   * game propositions.
   */
  private[contextualisation] val Ys = GamePropositionVariable("Ys")
  /**
   * Variable representing a simple dialogue game proposition.
   */
  private[contextualisation] val X = Variable[AnyGameProposition]("X")

  /**
   * Contextualisation communication game for a dialogue involving two dialogue participants.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected class ContextualisationGame protected (speakers: Interlocutor*)
    extends CommunicationGame {

    require(speakers.toSet.size == 2)
    // TODO To be changed when dialogue will be multiparty
    protected def otherSpeaker(speaker: Interlocutor): Interlocutor =
      (dialogueParticipants - speaker).iterator.next

    def id: GameID = GameID("ctx")
    def name: String = ContextualisationGame.name

    def dialogueParticipants: Set[Interlocutor] = speakers.toSet

    def getRulesFor(speaker: Interlocutor): Set[AnyGameCommitment] = Set(
      // prop.entrée(_, g) -> acc.entree(_.other, g) | ref.entree(_.other, g) | prop.entree(_.other, g2 ~> g1) | prop.entree(_.other, g2 ; g1)
      C(this, speaker, otherSpeaker(speaker),
        propIn(speaker, Xs) =*=> Create(speaker,
          C(this, otherSpeaker(speaker), speaker,
            accIn(otherSpeaker(speaker), Xs) *| refIn(otherSpeaker(speaker), Xs)
              *| propIn(otherSpeaker(speaker), Ys ~> Xs)
              *| propIn(otherSpeaker(speaker), Ys / Xs)))),
      // prop.entrée(_, g) -> add g as a suggested game
      C(this, speaker, otherSpeaker(speaker),
        propIn(speaker, Xs) =*=>
          CreateSuggestedGame(speaker, Xs, speaker, otherSpeaker(speaker))),
      C(this, speaker, otherSpeaker(speaker),
        propIn(speaker, Xs) =*=>
          PropEntreePersistence(speaker, Xs, otherSpeaker(speaker))),
      // ref.entrée(_, g) -> remove g from suggested games
      // Further detail in RemoveSuggestedGame definition
      C(this, speaker, otherSpeaker(speaker),
        refIn(speaker, Xs) =*=>
          RemoveSuggestedGame(speaker, Xs, otherSpeaker(speaker), speaker)),

      // acc.entree(_, g) -> poursuit(_.other, g)
      // TODO

      // poursuit(_, g) -> poursuit(_.other, g) | prop.entrée(_.other, g2 < g1)
      /*C(this, speaker, otherSpeaker(speaker),
      poursuit(speaker, X) =*=>
        Create(speaker, C(this, otherSpeaker(speaker), speaker,
          poursuit(otherSpeaker(speaker), X)
            *| propEntree(otherSpeaker(speaker), Ys << X)))),*/

      // acc.entree(_, g) -> create(_, C({x, y}, g))
      C(this, speaker, otherSpeaker(speaker),
        accIn(speaker, Xs) =*=>
          CreateGame(speaker, Xs, otherSpeaker(speaker), speaker)),
      C(this, speaker, otherSpeaker(speaker),
        accIn(speaker, Xs) =*=>
          PropEntreeGeneration(speaker, Xs, otherSpeaker(speaker), speaker)),

      // prop.sortie(_, g) -> acc.sortie(_.other, g) | ref.sortie(_.other, g)
      C(this, speaker, otherSpeaker(speaker),
        propOut(speaker, X) =*=> Create(speaker,
          C(this, otherSpeaker(speaker), speaker,
            accOut(otherSpeaker(speaker), X) *| refOut(otherSpeaker(speaker), X)))),

      // acc.sortie(_, g) -> cancelGame(_, g)
      C(this, speaker, otherSpeaker(speaker),
        accOut(speaker, X) =*=>
          // TODO Determining the initiator and the partner seems difficult here, need some checking
          CancelGameWithContextualisation(speaker, X, (speaker, otherSpeaker(speaker)))))
  }
}
