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
package fr.dubuissonduplessis.dogma.game.factory

import fr.dubuissonduplessis.dogma.dialogue.Dialogue
import fr.dubuissonduplessis.dogma.gameInstance.GameInstances
import scala.collection.mutable
import fr.dubuissonduplessis.dogma.game.CommunicationGames
import fr.dubuissonduplessis.dogma.game.DialogueGames
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
import fr.dubuissonduplessis.dogma.game.language.GameProposition
import com.typesafe.scalalogging.slf4j.LazyLogging

/**
 * Implemented module that is a repository of dialogue game and communication game factories.
 *
 * A game repository captures the knowledge for the instantiation of dialogue games and communication games.
 * As such, it:
 *  - '''stores''' game factories (see methods [[fr.dubuissonduplessis.dogma.game.factory.GameRepositories#loadCommunicationGameFactory]]
 *    and [[fr.dubuissonduplessis.dogma.game.factory.GameRepositories#loadDialogueGameFactory]])
 *  - '''provides''' game factories (see methods [[fr.dubuissonduplessis.dogma.game.factory.GameRepositories#getCommunicationGame]] and
 *    [[fr.dubuissonduplessis.dogma.game.factory.GameRepositories#getDialogueGameFactory]])
 *
 * For those dialogue game factories which name is the dialogue game name that they create, the method
 * [[fr.dubuissonduplessis.dogma.game.factory.GameRepositories#createGameInstanceFrom]] helps obtaining an instance.
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait GameRepositories extends GameFactories with LazyLogging {
  this: GameFactoriesRequirements =>

  private val communicationGameFactories = mutable.Map[String, CommunicationGameFactory]()
  private val dialogueGameFactories = mutable.Map[String, DialogueGameFactory]()

  // Retrieving method
  /**
   * Retrieves the communication game factory associated with the given name
   *
   */
  protected def getCommunicationGame(name: String): CommunicationGameFactory =
    communicationGameFactories(name)
  /**
   * Retrieves all communication game factories
   * @return a list of pairs where the first element is the factory name and the second is the communication game factory
   */
  protected def getCommunicationGameFactories: List[(String, CommunicationGameFactory)] =
    communicationGameFactories.toList

  /**
   * Retrieves the dialogue game factory associated with the given name
   *
   */
  protected def getDialogueGameFactory(name: String): DialogueGameFactory =
    dialogueGameFactories(name)

  /**
   * @see [[fr.dubuissonduplessis.dogma.game.factory.GameRepositories#getDialogueGameFactory]]
   */
  protected def apply(name: String): DialogueGameFactory =
    getDialogueGameFactory(name)

  // Loading method
  /**
   * Loads a communication game factory into the system.
   * @note Precondition: should not already contain a communication factory with the same name
   */
  protected def loadCommunicationGameFactory(
    cGameFactory: CommunicationGameFactory): Unit =
    {
      require(!communicationGameFactories.contains(cGameFactory.name))
      communicationGameFactories += (cGameFactory.name -> cGameFactory)
    }

  /**
   * Loads a dialogue game factory into the system.
   * @note Precondition: should not already contain a dialogue factory with the same name
   */
  protected def loadDialogueGameFactory(
    dGameFactory: DialogueGameFactory): Unit =
    {
      require(!dialogueGameFactories.contains(dGameFactory.name))
      dialogueGameFactories += (dGameFactory.name -> dGameFactory)
    }

  /**
   * Helper method that creates an instance of DialogueGame from a game proposition, an initiator and a partner.
   * Note that the game proposition provides the name of the dialogue game and its goal.
   *
   * It does by:
   *  1. Recovering the dialogue game factory which name corresponds to the name of the dialogue game in the game proposition
   *  1. Checking that the goal is compliant with the dialogue game
   *  1. Creating the instance of dialogue game
   *
   */
  protected def createGameInstanceFrom[GoalType](
    gProp: GameProposition[GoalType],
    initiator: Interlocutor,
    partner: Interlocutor): DialogueGame = {
    require(interlocutors.contains(initiator), "Initiator " + initiator +
      " is not part of " + interlocutors)
    require(interlocutors.contains(partner), "Partner " + partner +
      " is not part of " + interlocutors)
    val factory = getDialogueGameFactory(gProp.name)
    if (factory.compliantGoal(gProp.goal)) {
      logger.debug(s"Goal ${gProp.goal} is compliant with game ${gProp.name}")
      factory(initiator, partner, gProp.goal)
    } else {
      throw new RuntimeException(s"Cannot create dialogue game from $gProp: incorrect goal")
    }
  }

}
