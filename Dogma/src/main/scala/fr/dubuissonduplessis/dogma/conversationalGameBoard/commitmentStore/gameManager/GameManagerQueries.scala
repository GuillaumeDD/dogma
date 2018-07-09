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
package fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager

import fr.dubuissonduplessis.dogma.game.DialogueGames
import fr.dubuissonduplessis.dogma.game.CommunicationGames
import fr.dubuissonduplessis.dogma.dialogue.Dialogue
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
import fr.dubuissonduplessis.dogma.game.language.GameProposition
import fr.dubuissonduplessis.dogma.gameInstance.GameInstances
import fr.dubuissonduplessis.dogma.gameInstance.DialogueGameInstances

/**
 * Game manager module providing high-level access operations.
 *
 * Provided operations aim at querying the [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.GameManager GameManager]]
 * in order to:
 *  - determine salient game instances and embedded games in a given game
 *  - retrieve dialogue game instances
 *
 * It provides an implicit ordering among game instances.
 *
 * Operations modifying the game manager can be found in the
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.GameManagerOperations GameManagerOperations module]].
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait GameManagerQueries {
  this: Dialogue with GameManager with GameInstances with DialogueGameInstances with DialogueGames with CommunicationGames =>

  /**
   * Sorts game instances by ascending time.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected implicit object GameInstanceOrdering extends Ordering[GameInstance] {
    def compare(g1: GameInstance, g2: GameInstance): Int =
      (time(g1) - time(g2)).toInt
  }

  // Implements: fr.dubuissonduplessis.dogma.gameInstance.GameInstances
  /**
   * @see [[fr.dubuissonduplessis.dogma.gameInstance.GameInstances#gameInstanceOrdering]]
   */
  protected def gameInstanceOrdering: Ordering[GameInstance] =
    GameInstanceOrdering

  /**
   * Determines the set of dialogue game instances embedded in a given game instance.
   *
   */
  private[conversationalGameBoard] def embeddedGames(gParent: GameInstance): Set[DialogueGameInstance] =
    activeDialogueGames.filter(embeddedIn(_, gParent))

  /**
   * Determines if a given game instance is salient.
   * @return true if the game instance is salient, else false
   */
  private[conversationalGameBoard] def salient(g: GameInstance): Boolean = {
    /*    
     import GameInstanceOrdering._
    openedGames.forall(_ <= g)
    */
    // A game instance is salient if it is open and has no embedded games
    (embeddedGames(g).filter(state(_) == ContextualisationState.OPEN).isEmpty)
  }

  /**
   * Determines the set of salient games (dialogue game and communication game).
   *
   */
  private[conversationalGameBoard] def salientGames: Set[GameInstance] =
    openedGames.filter(salient(_))

  /**
   * Determines the set of salient dialogue games.
   */
  private[conversationalGameBoard] def salientDialogueGames: Set[DialogueGameInstance] =
    this.openedDialogueGames.filter(salient(_))

  /**
   * Retrieves a communication from its ID, if it exists.
   */
  private[conversationalGameBoard] def communicationGameFrom(id: GameID): Option[CommunicationGame] = {
    val commGamesWithID =
      communicationGames.filter(_.id == id)
    val sizeOfCommGamesWithID = commGamesWithID.size
    require(sizeOfCommGamesWithID <= 1,
      s"Something is really wrong with communication game ids: $commGamesWithID")
    if (sizeOfCommGamesWithID == 0) {
      None
    } else {
      Some(commGamesWithID.iterator.next)
    }
  }

  /**
   * Retrieves a dialogue game instance from a dialogue game name, a dialogue game goal,
   * an initiator and a partner (if it exists).
   * @param name dialogue game name
   * @param goal dialogue game goal
   * @param initiator dialogue participant who is the initiator of the dialogue game
   * @param partner dialogue participant who is the partner in the dialogue game
   *
   */
  private[conversationalGameBoard] def dialogueGameInstanceFrom[GoalType](
    name: String,
    goal: GoalType,
    initiator: Interlocutor,
    partner: Interlocutor): Option[DialogueGameInstance] = {
    val dgInstanceWithNameGoal =
      dialogueGameInstances.filter(instance =>
        {
          val dialogueGame = getDialogueGame(instance)
          (dialogueGame.name == name) &&
            (dialogueGame.goal == goal) &&
            dialogueGame.isInitiator(initiator) &&
            dialogueGame.isPartner(partner)
        })
    val sizeOfdgInstanceWithNameGoal = dgInstanceWithNameGoal.size
    require(sizeOfdgInstanceWithNameGoal <= 1,
      s"Something is really wrong with dialogue game instance names and goals: $dgInstanceWithNameGoal")
    if (sizeOfdgInstanceWithNameGoal == 0) {
      None
    } else {
      Some(dgInstanceWithNameGoal.iterator.next)
    }
  }

  // Speakers are unordered
  /**
   * Retrieves a dialogue game instance from a dialogue game name, a dialogue game goal,
   * and two dialogue participants.
   *
   * @note Only to be used if you know the dialogue participants without knowing who is the
   * initiator and who is the partner, '''and if you are sure that this instance exists'''
   * (else consider
   * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.GameManagerQueries#getDialogueGameInstanceFrom this method]]).
   * @param name dialogue game name
   * @param goal dialogue game goal
   * @param speakers dialogue participants playing this game
   *
   */
  private[conversationalGameBoard] def dialogueGameInstanceFrom[GoalType](
    name: String,
    goal: GoalType,
    speakers: (Interlocutor, Interlocutor)): DialogueGameInstance = {
    val (speaker01, speaker02) = speakers
    val games =
      (dialogueGameInstanceFrom(name, goal, speaker01, speaker02),
        dialogueGameInstanceFrom(name, goal, speaker02, speaker01))
    games match {
      case (None, None) =>
        throw new Error(s"Oups, can't find the game instance you're talking about: ${GameProposition(name, goal)} \n speakers: {$speaker01, $speaker02}\n")
      case (Some(g), None) => g
      case (None, Some(g)) => g
      case (Some(g1), Some(g2)) =>
        throw new Error(s"I can't decide which to choose between: $g1 ; $g2")
    }
  }

  /**
   * Retrieves a dialogue game instance from a dialogue game name, a dialogue game goal,
   * and two dialogue participants (if it exists).
   *
   * @note Only to be used if you know the dialogue participants without knowing who is the
   * initiator and who is the partner.
   * @param name dialogue game name
   * @param goal dialogue game goal
   * @param speakers dialogue participants playing this game
   *
   */
  private[conversationalGameBoard] def getDialogueGameInstanceFrom[GoalType](
    name: String,
    goal: GoalType,
    speakers: (Interlocutor, Interlocutor)): Option[DialogueGameInstance] = {
    val (speaker01, speaker02) = speakers
    val games =
      (dialogueGameInstanceFrom(name, goal, speaker01, speaker02),
        dialogueGameInstanceFrom(name, goal, speaker02, speaker01))
    games match {
      case (None, None) => None
      case (Some(g), None) => Some(g)
      case (None, Some(g)) => Some(g)
      case (Some(g1), Some(g2)) =>
        throw new Error(s"I can't decide which to choose between: $g1 ; $g2")
    }
  }
}
