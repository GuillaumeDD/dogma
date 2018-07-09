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

import fr.dubuissonduplessis.dogma.conversationalGameBoard.ConversationalGameBoard
import com.typesafe.scalalogging.slf4j.LazyLogging
import fr.dubuissonduplessis.dogma.event.ExtraDialogicEvent
import fr.dubuissonduplessis.dogma.event.DialogicEvent
import fr.dubuissonduplessis.dogma.event.Event

/**
 * Referee module that makes it possible to determine the compliance of a
 * dialogic event with the commitment store.
 * It accesses the [[fr.dubuissonduplessis.dogma.conversationalGameBoard.ConversationalGameBoard conversational game board]]
 * to determine if the dialogic event is
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.ExplicitReferee#forbidden forbidden]],
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.ExplicitReferee#compliant compliant]],
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.ExplicitReferee#expected expected]],
 * and
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.ExplicitReferee#hasPriority priority]].
 *
 * Method [[fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.ExplicitReferee#cases cases]]
 * determines the [[fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.ExplicitReferee#Case case]]
 * that is applicable to a dialogic event.
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 * @see Specialization [[fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.contextualisation.ExplicitReferee contextualisation.ExplicitReferee]]
 * takes into account the contextualisation game.
 *
 */
trait ExplicitReferee
  extends LazyLogging {
  this: ConversationalGameBoard =>

  /**
   * Cases that may happen in a dialogue manager process based on communication and dialogue
   * games regarding the occurrence of an external dialogic event.
   * It includes the following cases:
   *  - FORBIDDEN: the event is forbidden by the conversational board
   *  - UNEXPECTED: the event is unexpected by the conversational board
   *  - NONPRIORITY: the event is expected, but does not have the priority in the conversational board
   *  - STANDARD: the event is allowed, expected and has priority in the conversational board
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected object Case extends Enumeration {
    type Case = Value
    val FORBIDDEN, UNEXPECTED, NONPRIORITY, STANDARD = Value
  }

  import Case._

  /**
   * Computes the status of a given dialogic event relatively to the conversational board.
   */
  protected def cases(da: DialogicEvent): Case =
    if (forbidden(da)) {
      FORBIDDEN
    } else if (!expectedEvent(da)) {
      UNEXPECTED
    } else if (!hasPriority(da)) {
      NONPRIORITY
    } else {
      STANDARD
    }

  /**
   * Determines if a given event is expected by the conversational board.
   * @return true if the given event is expected, else false
   */
  protected def expected(e: Event): Boolean =
    e match {
      case act: DialogicEvent =>
        expectedEvent(act)
      case execAction: ExtraDialogicEvent =>
        gameActionCommitments.exists(
          c => c.expects(execAction))
      case _ => false
    }

  /**
   * Determines if a dialogic event is compliant with the conversational board.
   *
   * @return true if the dialogic event is compliant, else false
   */
  protected def compliant(da: DialogicEvent): Boolean =
    {
      val (violatedCommitment, complyingCommitment) =
        commitments // We only deal with action commitments...
          .filter(_.expectsOrForbids(da)) // ... that are concerned by dialogue act
          .partition(_.isViolatedBy(da))
      violatedCommitment.toList match {
        case List() =>
          true // Notice that a commitment may be compliant BUT unexpected
        case violatedComms =>
          complyingCommitment.toList match {
            case List() =>
              // There only exist violated commitments
              false
            case nonEmptyList =>
              // We check that it exists a complying commitments that has priority 
              // over all the violated ones
              nonEmptyList.exists(
                cCompliant => violatedComms.forall(cVio => <(cCompliant, cVio)))
          }
      }
    }

  /**
   * Determines if a given dialogic event is forbidden by the conversational board.
   *
   * @return true if the given dialogic event is forbidden, else false
   */
  protected def forbidden(da: DialogicEvent): Boolean =
    !compliant(da)

  /**
   * Determines if a given dialogic event is expected by the conversational board.
   *
   * @return true if the given dialogic event is expected, else false
   */
  protected def expectedEvent(da: DialogicEvent): Boolean =
    !commitments
      .filter(_.expects(da))
      .isEmpty

  /**
   * Computes the set of game instances that expects a given dialogic event.
   *
   */
  protected def expectedBy(da: DialogicEvent): Set[GameInstance] =
    gameCommitments // We only deal with game commitments...
      .filter(_.expects(da)) // ... that are expecting the dialogue act...
      .map(_.game) // ... and we only keeps GameInstance

  /**
   * Computes the set of dialogue game instance that expects a given dialogic event.
   *
   */
  protected def expectedByDialogueGame(da: DialogicEvent): Set[DialogueGameInstance] =
    expectedBy(da).collect({
      case instance: DialogueGameInstance =>
        instance
    })

  /**
   * Determines if a given dialogic event has priority regarding the conversational board.
   *
   * @return true if the given dialogic event has priority, else false
   */
  protected def hasPriority(da: DialogicEvent): Boolean =
    {
      // The dialogue act is prioritary if it is expected by a game that is salient
      logger.debug(s"Dialogue act $da is expected by ${expectedBy(da).mkString("{", ", ", "}")}")
      val gameInstancesExpectingDA = expectedBy(da)
      /*
       * Not necessary with the new definition of salience
      // Either the dialogue act is expected by a communication game...
      cgb.communicationGames.exists(gameInstancesExpectingDA.contains(_)) ||
      */
      gameInstancesExpectingDA.exists(g => salient(g)) // ... or the dialogue act is expected by a salient game
    }

}
