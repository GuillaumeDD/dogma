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
package fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma

import com.typesafe.scalalogging.slf4j.LazyLogging
import fr.dubuissonduplessis.dogma.event.Event
import fr.dubuissonduplessis.dogma.conversationalGameBoard.ConversationalGameBoard
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
import scala.collection.immutable.SortedMap
import fr.dubuissonduplessis.dogma.event.DialogicEvent
import fr.dubuissonduplessis.dogma.description.Description
import fr.dubuissonduplessis.dogma.event.ExtraDialogicEvent
import fr.dubuissonduplessis.dogma.description.EventDescription
import fr.dubuissonduplessis.dogma.description.compositeDescription.Negation

/**
 * Conventional behaviour management module that exploits the
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.ConversationalGameBoard conversational game board]]
 * in order to identify the salient [[fr.dubuissonduplessis.dogma.description event(s) descriptions]].
 * This module makes the exploitation of the conversational game board by providing primitives to:
 *  - determine the expected event(s) descriptions (e.g., via methods
 *  [[fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.ConventionalBehaviourManager#expectedEventsByInterlocutor expectedEventsByInterlocutor]],
 *  [[fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.ConventionalBehaviourManager#expectedDescriptionByInterlocutor expectedDescriptionByInterlocutor]],
 *  [[fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.ConventionalBehaviourManager#expectedDescriptionByGameInstance expectedDescriptionByGameInstance]],
 *  [[fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.ConventionalBehaviourManager#expectedEventsByGameInstance expectedEventsByGameInstance]])
 *  - determine the forbidden event(s) descriptions (see method
 *  [[fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.ConventionalBehaviourManager#forbiddenEvents forbiddenEvents]])
 *  - access the current dialogue games (see
 *  [[fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.ConventionalBehaviourManager#DialogueGameSpace DialogueGameSpace]])
 *  - access the current communication games (see
 *  [[fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.ConventionalBehaviourManager#communicationGameSpace communicationGameSpace]])
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 */
trait ConventionalBehaviourManager
  extends LazyLogging {
  this: ConversationalGameBoard with ExplicitReferee =>

  /**
   * Only keeps commitments which content is a
   * [[fr.dubuissonduplessis.dogma.description.compositeDescription.Negation Negation]].
   *
   */
  private def filterNegation(
    actionCommitments: Set[_ <: Commitment[Description]]): Set[EventDescription] =
    actionCommitments.map(_.content).collect {
      case Negation(a) =>
        a
    }
  /**
   * Computes all single event descriptions that are expected by games.
   */
  protected def expectedEvents: Set[EventDescription] =
    gameActionCommitments
      .toList
      .filter(_.isActive)
      .flatMap(_.expectedEvent)
      .toSet

  /**
   * Computes all event(s) description that are expected by communication games.
   */
  protected def expectedDescriptionByCommunicationGames: List[Description] =
    gameActionCommitments
      .filter(_.isActive)
      .filter(!_.game.isDialogueGame)
      .toList
      .sorted(CommitmentOrdering.reverse)
      .flatMap(_.expectedDescription)

  /**
   * Computes all extra-dialogic actions that are expected.
   * @return a map that gives for each dialogue participant the list of extra-dialogic actions
   * expected from him (ordered by decreasing time).
   */
  protected def expectedExtraDialogicalActions: Map[Interlocutor, List[ActionContent]] =
    extraDialogicalActionCommitments
      .filter(_.isActive)
      .groupBy(_.debtor)
      .mapValues(commitmentSet =>
        commitmentSet.toList
          .sorted(CommitmentOrdering.reverse)
          .map(_.content)
          .distinct)
      .filterNot(keyValue => keyValue._2.isEmpty)
      .withDefaultValue(List())

  /**
   * Computes all single event descriptions that are expected by games, and regroup them
   * by their contracted time.
   * @return a sorted map by decreasing time that gives for each time the list of the
   * single event descriptions that are expected by games
   */
  protected def expectedEventsByTime: Map[Time, List[EventDescription]] =
    SortedMap(gameActionCommitments
      .toList
      .filter(_.isActive)
      .groupBy(_.t)
      .mapValues(commitmentSet =>
        commitmentSet
          .flatMap(_.expectedEvent))
      .filter({
        case (time, value) => !value.isEmpty
      })
      .toSeq: _*)(implicitly[Ordering[Time]].reverse)

  /**
   * Computes all single event descriptions that are expected by games, and regroup them
   * by the dialogue participant who is committed to produce these events.
   * @return a map that gives for each dialogue participant a list of the single event
   * descriptions that are expected from him (ordered by decreasing time)
   */
  protected def expectedEventsByInterlocutor: Map[Interlocutor, List[EventDescription]] =
    gameActionCommitments
      .filter(_.isActive)
      .groupBy(_.debtor)
      .mapValues(commitmentSet =>
        commitmentSet.toList
          .sorted(CommitmentOrdering.reverse)
          .flatMap(_.expectedEvent)
          .distinct)
      .filterNot(keyValue => keyValue._2.isEmpty)
      .withDefaultValue(List())

  /**
   * Computes all event(s) descriptions that are expected by games, and regroup them
   * by the dialogue participant who is committed to produce these events.
   * @return a map that gives for each dialogue participant a list of the event(s)
   * descriptions that are expected from him (ordered by decreasing time)
   */
  protected def expectedDescriptionByInterlocutor: Map[Interlocutor, List[Description]] =
    gameActionCommitments
      .filter(_.isActive)
      .groupBy(_.debtor)
      .mapValues(commitmentSet => commitmentSet.toList
        .sorted(CommitmentOrdering.reverse)
        .flatMap(_.expectedDescription)
        .distinct)
      .filterNot(keyValue => keyValue._2.isEmpty)
      .withDefaultValue(List())

  /**
   * Computes all single event descriptions that are expected by games, and regroup them
   * by game instance (communication game and dialogue game).
   * @return a map that gives for each game instance a list of the single event
   * descriptions that are expected (ordered by decreasing time)
   */
  protected def expectedEventsByGameInstance: Map[GameInstance, List[EventDescription]] =
    gameActionCommitments
      .filter(_.isActive) // We only keep active commitments	
      .groupBy(_.game) // We retrieve game action commitments associated to the game instance
      .mapValues(commitmentSet =>
        commitmentSet.toList
          .sorted(CommitmentOrdering.reverse) // We sort commitment by descending creation time 
          .flatMap(_.expectedEvent) // We retrieve the expected executable actions
          .distinct)
      .filterNot(keyValue => keyValue._2.isEmpty) // We only keep those that have expected action
      .withDefaultValue(List())

  /**
   * Computes all single event descriptions that are expected by dialogue games,
   * and regroup them by dialogue game instance.
   * @return a map that gives for each dialogue game instance a list of the single
   * event descriptions that are expected (ordered by decreasing time)
   */
  protected def expectedEventsByDialogueGameInstance: Map[DialogueGameInstance, List[EventDescription]] =
    gameActionCommitments
      .filter(_.isActive) // We only keep active commitments
      .filter(_.game.isDialogueGame)
      .groupBy(_.game) // We retrieve game action commitments associated to the game instance
      .mapValues(commitmentSet =>
        commitmentSet.toList
          .sorted(CommitmentOrdering.reverse) // We sort commitment by descending creation time 
          .flatMap(_.expectedEvent) // We retrieve the expected executable actions
          .distinct)
      .filterNot(keyValue => keyValue._2.isEmpty) // We only keep those that have expected action
      .asInstanceOf[Map[DialogueGameInstance, List[EventDescription]]]
      .withDefaultValue(List())

  /**
   * Computes all event(s) descriptions that are expected by games, and regroup them
   * by game instance (communication game and dialogue game).
   * @return a map that gives for each game instance a list of the event(s)
   * descriptions that are expected (ordered by decreasing time)
   */
  protected def expectedDescriptionByGameInstance: Map[GameInstance, List[Description]] =
    gameActionCommitments
      .filter(_.isActive) // We only keep active commitments	
      .groupBy(_.game) // We retrieve game action commitments associated to the game instance
      .mapValues(commitmentSet =>
        commitmentSet.toList
          .sorted(CommitmentOrdering.reverse) // We sort commitment by descending creation time
          .flatMap(_.expectedDescription) // We retrieve the expected action
          .distinct)
      .filterNot(keyValue => keyValue._2.isEmpty)
      .withDefaultValue(List()) // We only keep those game instances that have expected action

  /**
   * Computes all single event descriptions that are forbidden by games, and regroup them
   * by the dialogue participant who is committed to not produce these events.
   * @return a map that gives for each dialogue participant a list of the single event
   * descriptions that are forbidden for him
   */
  protected def forbiddenEvents: Map[Interlocutor, List[EventDescription]] =
    gameActionCommitments
      .filter(_.isActive)
      .filter(_.content.isNegation) // We only keep negation action commitment
      .groupBy(_.debtor) // We group the action commitment by debtor...
      .mapValues(filterNegation(_).toList) //... and we only keep dialogue acts
      .filterNot(keyValue => keyValue._2.isEmpty)
      .withDefaultValue(List())

  /**
   * Computes all single event descriptions that are forbidden by games,
   * and regroup them by game instance.
   * @return a map that gives for each game instance a list of the single
   * event descriptions that are forbidden
   */
  protected def forbiddenEventsByGameInstance: Map[GameInstance, List[EventDescription]] =
    gameActionCommitments
      .filter(_.isActive)
      .filter(_.content.isNegation) // We only keep negation action commitment
      .groupBy(_.game) // We group the action commitment by debtor...
      .mapValues(c => filterNegation(c.asInstanceOf[Set[Commitment[Description]]]).toList) //... and we only keep dialogue acts
      .filterNot(keyValue => keyValue._2.isEmpty)
      .withDefaultValue(List())

  /**
   * Represents a condensed view of the current dialogue games (suggested, opened, closed and salient).
   * @param suggestedDialogueGames a sorted list of suggested dialogue game instances by decreasing activity time
   * @param openedDialogueGames a sorted list of opened dialogue game instances by decreasing activity time
   * @param closedDialogueGames a sorted list of closed dialogue game instances by decreasing activity time
   * @param salientDialogueGames a sorted list of salient dialogue game instances by decreasing activity time
   * @see [[fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.ConventionalBehaviourManager#dialogueGameSpace Method dialogueGameSpace]]
   * is a factory of a DialogueGameSpace.
   */
  protected case class DialogueGameSpace private[ConventionalBehaviourManager] (
    suggestedDialogueGames: List[DialogueGameInstance],
    openedDialogueGames: List[DialogueGameInstance],
    closedDialogueGames: List[DialogueGameInstance],
    salientDialogueGames: List[DialogueGameInstance])

  /**
   * Computes the current dialogue game space.
   *
   */
  protected def dialogueGameSpace: DialogueGameSpace = {
    def toDialogueGameInfo(dialogueGames: Set[DialogueGameInstance]): List[DialogueGameInstance] =
      dialogueGames
        .toList
        .sortWith((instance1, instance2) => instance1.lastActivityTime > instance2.lastActivityTime)

    DialogueGameSpace(
      toDialogueGameInfo(suggestedDialogueGames),
      toDialogueGameInfo(openedDialogueGames),
      toDialogueGameInfo(closedDialogueGames),
      // Salient Dialogue Game
      toDialogueGameInfo(salientDialogueGames))

  }

  /**
   * Computes the current communication game space.
   * @return a sorted list of communication game instances by decreasing activity time
   */
  protected def communicationGameSpace: List[CommunicationGame] =
    communicationGames.toList
      .sortWith((instance1, instance2) => instance1.lastActivityTime > instance2.lastActivityTime)
}
