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

import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.CommitmentStoreOperations
import fr.dubuissonduplessis.dogma.dialogue.Dialogue
import fr.dubuissonduplessis.dogma.commitment.Commitments
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.CommitmentStore
import fr.dubuissonduplessis.dogma.instantiable.impl.InstantiableProxy
import fr.dubuissonduplessis.dogma.instantiable.impl.InstantiableDuoProxy
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.CommitmentStoreQueries
import com.typesafe.scalalogging.slf4j.LazyLogging
import fr.dubuissonduplessis.dogma.gameInstance.GameInstances
import fr.dubuissonduplessis.dogma.game.DialogueGames
import fr.dubuissonduplessis.dogma.game.factory.GameRepositories
import fr.dubuissonduplessis.dogma.instantiable.impl.Instantiated
import fr.dubuissonduplessis.dogma.game.CommunicationGames
import fr.dubuissonduplessis.dogma.instantiable.Instantiable
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
import fr.dubuissonduplessis.dogma.game.language._
import fr.dubuissonduplessis.dogma.game.language.instantiable.GamePropositionVariable
import fr.dubuissonduplessis.dogma.operation.Operation
import fr.dubuissonduplessis.dogma.instantiable.InstantiationSet
import fr.dubuissonduplessis.dogma.instantiable.InstantiationVariable
import fr.dubuissonduplessis.dogma.instantiable.Variable
import fr.dubuissonduplessis.dogma.event.Event
import scala.collection.mutable
import fr.dubuissonduplessis.dogma.operation.GenericOperationGenerator

/**
 * Module providing high-level operations on a game manager and a commitment store.
 * It extends the
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.CommitmentStoreOperations CommitmentStoreOperations]]
 * module that provides high-level operations on a commitment store.
 *
 * High-level operations on a game manager include:
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.GameManagerOperations#CreateSuggestedGame creation]]
 * of suggested dialogue games,
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.GameManagerOperations#RemoveSuggestedGame removal]]
 * of suggested dialogue games,
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.GameManagerOperations#CancelGame closure]]
 * of a dialogue game,
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.GameManagerOperations#LoadGame loading]]
 * of a dialogue game rules and effects,
 * and
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.GameManagerOperations#Prioritize prioritization]]
 * of dialogue games.
 *
 *
 * It provides extension methods for
 * [[fr.dubuissonduplessis.dogma.gameInstance.GameInstances#GameInstance GameInstance]]
 * and
 * [[fr.dubuissonduplessis.dogma.gameInstance.DialogueGameInstances#DialogueGameInstance DialogueGameInstance]].
 *
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 * @see [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.CommitmentStore CommitmentStore]] module
 * @see [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.GameManager GameManager]] module
 */
trait GameManagerOperations extends CommitmentStoreOperations {
  self: GameManagerOperationsRequirements =>

  // TODO move this code in the event module dealing with CommitmentStore
  override private[conversationalGameBoard] def processOnCommitmentStore(e: Event): Unit = {
    // Save event in the past of dialogue game instances
    saveEventInDialogueGameInstance(e)

    // Apply the standard algorithm
    super.processOnCommitmentStore(e)
  }

  private def saveEventInDialogueGameInstance(
    e: Event): Unit =
    {
      // Instance detection
      val gameInstances =
        this.gameActionCommitments
          .filter(_.isConcernedBy(e))
          .collect(
            gComm => gComm.game match {
              case instance: DialogueGameInstance => instance
            })

      // Save event
      for (instance <- gameInstances) {
        logger.debug(s"Saving event $e in the past of dialogue game instance $instance")
        instance.savePast(e)
      }
    }

  /**
   * Provides extension method for a [[fr.dubuissonduplessis.dogma.gameInstance.GameInstances#GameInstance GameInstance]].
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected implicit class RichGameInstance(val gameInstance: GameInstance) {
    /**
     * Determines the last time which has seen a modification of the given game instance.
     */
    def time: Time =
      GameManagerOperations.this.time(gameInstance)

    /**
     * Computes the last activity time of a game instance.
     *
     */
    def lastActivityTime: Time =
      {
        val gameCommitments = findGameActionCommitments(gameInstance)
        if (gameCommitments.isEmpty) {
          logger.error(s"Unable to find a last activity time for game: $gameInstance")
          -1L
        } else {
          gameCommitments.maxBy(_.t).t
        }
      }

    /**
     * Computes all the game action commitments contextualized by this game instance.
     *
     */
    def relatedCommitments: Set[GameActionCommitment] =
      findGameActionCommitments(gameInstance)
  }

  /**
   * Provides extension methods for a
   * [[fr.dubuissonduplessis.dogma.gameInstance.DialogueGameInstances#DialogueGameInstance DialogueGameInstance]].
   *
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   * @param dgInstance The dialogue game instance
   */
  protected implicit class RichDialogueGameInstance(val dgInstance: DialogueGameInstance) {
    /**
     * Determines the dialogue game associated to this instance.
     */
    val dialogueGame = GameManagerOperations.this.getDialogueGame(dgInstance)
    /**
     * Type of the goal of the dialogue game associated to this instance.
     */
    type Goal = dialogueGame.GoalType
    /**
     * Computes a game proposition referring to this
     * [[fr.dubuissonduplessis.dogma.gameInstance.DialogueGameInstances#DialogueGameInstance DialogueGameInstance]].
     */
    def gameProposition: GameProposition[Goal] = {
      GameProposition[Goal](dialogueGame.name, dialogueGame.goal)
    }

    /**
     * Determines the contextualisation state of the dialogue game instance.
     */
    def state: ContextualisationState =
      GameManagerOperations.this.state(dgInstance)

    /**
     * Clears the history of events of this dialogue game instance (side-effect).
     */
    def clearPast(): Unit =
      GameManagerOperations.this.clearPast(dgInstance)

    /**
     * Computes the history of events of this dialogue game instance.
     */
    def getPast: List[Event] =
      GameManagerOperations.this.getPast(dgInstance)

    /**
     * Adds a given event to the past history of this dialogue game instance (side-effect).
     */
    def savePast(e: Event): Unit =
      GameManagerOperations.this.savePast(e, dgInstance)

    /**
     * Computes the dialogue game state of this dialogue game instance for a dialogue participant.
     *
     * @note the given dialogue participant should either be the initiator or the partner of the game
     *
     */
    def gameStateFor(loc: Interlocutor): GameState =
      GameManagerOperations.this.gameStateFor(dgInstance, loc)

    /**
     * Determines if this dialogue game instance is in contextualisation state 'suggested'
     * @return true if this dialogue game instance is in contextualisation state 'suggested', else false
     */
    def isSuggested: Boolean =
      GameManagerOperations.this.suggestedDialogueGames.contains(dgInstance)
    /**
     * Determines if this dialogue game instance is in contextualisation state 'opened'
     * @return true if this dialogue game instance is in contextualisation state 'opened', else false
     */
    def isOpened: Boolean =
      GameManagerOperations.this.openedDialogueGames.contains(dgInstance)
    /**
     * Determines if this dialogue game instance is in contextualisation state 'closed'
     * @return true if this dialogue game instance is in contextualisation state 'closed', else false
     */
    def isClosed: Boolean =
      GameManagerOperations.this.closedDialogueGames.contains(dgInstance)
  }

  /**
   * Loads the rules of a communication game (via side-effects).
   * @param name unique name of the communication game
   * @return the communication game that has been loaded
   */
  protected[conversationalGameBoard] def loadCommunicationGameRules(name: String): CommunicationGame =
    {
      val game = getCommunicationGame(name)(interlocutors)
      // We load the communication game as opened
      addCommunicationGame(game)
      // For each interlocutors, we load the rules associated with the game
      interlocutors.foreach {
        interlocutor =>
          Create.wrap(game.getRulesFor(interlocutor), interlocutor)
            .foreach(_())
      }
      // Return the instance
      game
    }

  /**
   * Abstract game manager operation.
   *
   * A game manager operation is a side-effect procedure modifying a mutable commitment store and
   * a mutable game manager.
   * Its body is defined in its apply method.
   *
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected abstract class GameManagerOperation extends CommitmentStoreOperation
    with Instantiable[GameManagerOperation] {

    /**
     * Determines if a combination of dialogue games is in contextualisation state 'suggested'.
     * @param gProp combination of dialogue games
     * @param speakers dialogue participants of the game
     * @return true if it is in contextualisation state 'suggested', else false
     */
    def isSuggested(
      gProp: AbstractGameProposition,
      speakers: (Interlocutor, Interlocutor)): Boolean =
      gProp match {
        case PreSequenceGameProposition(g1, g2) =>
          isSuggested(g1, speakers) && isSuggested(g2, speakers)
        case SequenceGameProposition(g1, g2) =>
          isSuggested(g1, speakers) && isSuggested(g2, speakers)
        case EmbeddedGameProposition(g1, g2) =>
          isSuggested(g1, speakers) && isOpened(g2, speakers)
        case GameProposition(name, goal) =>
          val gInstance = getDialogueGameInstanceFrom(name, goal, speakers)
          gInstance match {
            case None => false
            case Some(g) => g.isSuggested
          }
      }

    /**
     * Determines if a combination of dialogue games is in contextualisation state 'opened'.
     * @param gProp combination of dialogue games
     * @param speakers dialogue participants of the game
     * @return true if it is in contextualisation state 'opened', else false
     */
    def isOpened(
      gProp: AbstractGameProposition,
      speakers: (Interlocutor, Interlocutor)): Boolean =
      gProp match {
        case PreSequenceGameProposition(g1, g2) =>
          isOpened(g1, speakers) && isSuggested(g2, speakers)
        case SequenceGameProposition(g1, g2) =>
          isOpened(g1, speakers) && isSuggested(g2, speakers)
        case EmbeddedGameProposition(g1, g2) =>
          isOpened(g1, speakers) && isOpened(g2, speakers)
        case GameProposition(name, goal) =>
          val gInstance = getDialogueGameInstanceFrom(name, goal, speakers)
          gInstance match {
            case None => false
            case Some(g) => g.isOpened
          }
      }

    /**
     * Determines if a combination of dialogue games is properly constructed by considering
     * the contextualisation states of each individual dialogue games.
     * @param gProp combination of dialogue games
     * @param speakers dialogue participants of the game
     * @return true if it is properly constructed, else false
     */
    def isProperlyConstructed(
      gProp: AbstractGameProposition,
      speakers: (Interlocutor, Interlocutor)): Boolean =
      gProp match {
        case PreSequenceGameProposition(g1, g2) =>
          isProperlyConstructed(g1, speakers) &&
            isSuggested(g2, speakers)
        case SequenceGameProposition(g1, g2) =>
          isProperlyConstructed(g1, speakers) &&
            isSuggested(g2, speakers)
        case EmbeddedGameProposition(g1, g2) =>
          isProperlyConstructed(g1, speakers) &&
            isOpened(g2, speakers)
        case GameProposition(name, goal) =>
          // A game proposition is always properly suggested
          // TODO maybe test whether it is nor opened, nor closed
          true
      }

    /**
     * Retrieves the dialogue game instances corresponding to a dialogue game proposition.
     * @param abstractGameProp dialogue game proposition
     * @param speakers dialogue participants
     * @return list of the dialogue game instances corresponding to a dialogue game proposition
     */
    def dialogueGameInstancesFrom(
      abstractGameProp: AbstractGameProposition,
      speakers: (Interlocutor, Interlocutor)): List[DialogueGameInstance] =
      abstractGameProp match {
        case AbstractComposedGameProposition(g1, g2) =>
          dialogueGameInstancesFrom(g1, speakers) ++
            dialogueGameInstancesFrom(g2, speakers)
        case GameProposition(name, goal) =>
          List(dialogueGameInstanceFrom(name, goal, speakers))
      }

    /**
     * Retrieves a dialogue game instance from a dialogue game proposition,
     * an initiator and a partner (if it exists).
     * @param gProp dialogue game proposition
     * @param initiator dialogue participant who is the initiator of the dialogue game
     * @param partner dialogue participant who is the partner in the dialogue game
     *
     */
    def gameInstanceFrom[GoalType](
      gProp: GameProposition[GoalType],
      initiator: Interlocutor,
      partner: Interlocutor): DialogueGameInstance =
      dialogueGameInstanceFrom(gProp.name, gProp.goal, initiator, partner) match {
        case None =>
          throw new Error("Oups, can't find the game instance you're talking about: " +
            gProp + "\n" +
            "initiator: " + initiator + "\n" +
            "partner: " + partner + "\n")
        case Some(x) => x
      }
  }

  /**
   * Cancels a dialogue game from the game manager and its related commitments from the commitment store.
   * This operation cancels every game commitment related to the given dialogue game, and calls the
   * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.GameManagerOperations#CloseGame CloseGame]]
   * operation to close the dialogue game.
   *
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   * @constructor create a new 'CancelGame' operation from a creator, a dialogue game proposition,
   * and a pair of dialogue participants
   * @param creator dialogue participant responsible for this operation
   * @param gameProp dialogue game proposition representing dialogue games to be cancelled
   * @param speakers dialogue participants involved in the dialogue game
   */
  protected case class CancelGame(
    creator: Interlocutor,
    gameProp: AnyGameProposition,
    speakers: (Interlocutor, Interlocutor))
    extends GameManagerOperation
    with InstantiableProxy[CancelGame, AnyGameProposition]
    with LazyLogging {

    val (speaker01, speaker02) = speakers

    def apply(): Unit = {
      val game = dialogueGameInstanceFrom(
        gameProp.name,
        gameProp.goal,
        (speaker01, speaker02))

      val commitments = findGameCommitments(game).filter(_.state == CommitmentState.Crt)

      logger.info(s"Cancelling game: $game")
      val cancelActions =
        // We cancel embedded games
        (for (g <- embeddedGames(game)) yield (CancelGame(creator, g.gameProposition, speakers))) ++
          // We cancel commitments
          (for (c <- commitments) yield (Cancel(creator, c))) ++
          Set(CloseGame(creator, gameProp, speakers))
      // Execution of operations
      cancelActions.foreach(_())
    }

    // Instantiation
    def instantiable: AnyGameProposition = gameProp
    def update(a: AnyGameProposition): CancelGame =
      CancelGame(creator, a, speakers)

    override def toString: String = "cancelGame(" + creator + ", " + gameProp + ")"
  }

  /**
   * Closes a dialogue game in the game manager.
   * @see [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.GameManagerOperations#CancelGame CancelGame]]
   * operation aims at removing game commitments from the commitment store.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   * @constructor create a new 'CloseGame' operation from a creator, a dialogue game proposition,
   * and a pair of dialogue participants
   * @param creator dialogue participant responsible for this operation
   * @param gameProp dialogue game proposition representing dialogue games to be cancelled
   * @param speakers dialogue participants involved in the dialogue game
   *
   */
  protected[gameManager] case class CloseGame(
    creator: Interlocutor,
    gameProp: AnyGameProposition,
    speakers: (Interlocutor, Interlocutor))
    extends GameManagerOperation
    with InstantiableProxy[CloseGame, AnyGameProposition]
    with LazyLogging {

    val (speaker01, speaker02) = speakers

    def apply(): Unit = {
      require(gameProp.instantiated)
      val g = dialogueGameInstanceFrom(
        gameProp.name,
        gameProp.goal,
        (speaker01, speaker02))
      // See also [[CancelGame]]
      logger.info(s"Closing game: $g")
      closeGame(g)
    }

    // Instantiation
    def instantiable: AnyGameProposition = gameProp
    def update(i: AnyGameProposition): CloseGame =
      CloseGame(creator, i, speakers)

    override def toString: String = "closeGame(" + creator + ", C({" + creator + "}, " + gameProp + "))"
  }

  /**
   * Loads the rules and effects of a given dialogue game instance.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   * @constructor create a new 'LoadGame' operation from a given dialogue game instance
   * @note Precondition: the contextualisation state of the dialogue game instance should be 'open'
   */
  protected case class LoadGame(dialogueGameInstance: DialogueGameInstance)
    extends GameManagerOperation
    with Instantiated[LoadGame] {

    def apply(): Unit = {
      logger.info(s"Loading a game: $dialogueGameInstance")
      require(dialogueGameInstance.state == ContextualisationState.OPEN)
      // Get the dialogue game from the instance
      val dialogueGame = dialogueGameInstance.dialogueGame

      dialogueGame.dialogueParticipants.foreach {
        interlocutor =>
          // Load the rules
          (Create.wrap(dialogueGame.getRulesFor(interlocutor)(dialogueGameInstance), interlocutor) ++
            // Load the effects
            Create.wrap(dialogueGame.getPropositionalEffectsFor(interlocutor)(dialogueGameInstance), interlocutor))
            // Apply
            .foreach(_())
      }
    }

    override def toString: String = "loadGame(" + dialogueGameInstance + ")"
  }

  /**
   * Prioritizes embedded dialogue games over parent dialogue games.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   * @constructor create a new 'Prioritize' operation from a given dialogue game instance
   * @param creator dialogue participant responsible for this operation
   * @param embeddedGameProp dialogue game proposition referring to the embedded dialogue games
   * @param parentGameProp dialogue game proposition referring to the parent dialogue games
   *
   */
  protected case class Prioritize(
    creator: Interlocutor,
    embeddedGameProp: AbstractGameProposition,
    parentGameProp: AbstractGameProposition,
    initiator: Interlocutor,
    partner: Interlocutor)
    extends GameManagerOperation
    with InstantiableDuoProxy[Prioritize, AbstractGameProposition, AbstractGameProposition]
    with LazyLogging {

    def apply(): Unit = {
      logger.info(s"Prioritizing $embeddedGameProp over $parentGameProp")

      val embeddedGameInstances =
        dialogueGameInstancesFrom(embeddedGameProp, (initiator, partner))
      val parentGameInstances =
        dialogueGameInstancesFrom(parentGameProp, (initiator, partner))

      // Helper method
      def prioritize(
        embeddedGameInstances: List[DialogueGameInstance],
        parentGameInstances: List[DialogueGameInstance]): Unit =
        parentGameInstances match {
          case List() =>
          case parentGameInstance :: last =>
            // For each parent game, we prioritize the embedded game
            embeddedGameInstances.foreach {
              embeddedGameInstance =>
                embedded(embeddedGameInstance, parentGameInstance) // We inform of the embedding of the game
                // We prioritize commitments
                prio(embeddedGameInstance, parentGameInstance)
            }
            // We prioritize with the rest of last
            prioritize(
              embeddedGameInstances,
              last)
        }

      prioritize(
        embeddedGameInstances,
        parentGameInstances)
    }

    // Instantiation
    def instantiable1: AbstractGameProposition =
      embeddedGameProp
    def instantiable2: AbstractGameProposition =
      parentGameProp
    def update(
      instantiable1: AbstractGameProposition,
      instantiable2: AbstractGameProposition): Prioritize =
      Prioritize(creator,
        instantiable1,
        instantiable2,
        initiator,
        partner)
  }

  /**
   * Factory for the
   * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.GameManagerOperations#CreateSuggestedGame CreateSuggestedGame]]
   * operation.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected object CreateSuggestedGame {
    /**
     * Builds an uninstantiated [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.GameManagerOperations#CreateSuggestedGame CreateSuggestedGame]]
     * operation with an unspecified dialogue game proposition.
     * @param theCreator dialogue participant responsible for this operation
     * @param theGameProp unspecified dialogue game proposition
     * @param theInitiator initiator of the dialogue games
     * @param thePartner partner of the dialogue games
     *
     */
    def apply(theCreator: Interlocutor,
      theGameProp: GamePropositionVariable,
      theInitiator: Interlocutor,
      thePartner: Interlocutor): Operation =
      new GenericOperationGenerator[AbstractGameProposition, CreateSuggestedGame] {
        val creator: Interlocutor = theCreator
        val variable: GamePropositionVariable = theGameProp
        val initiator: Interlocutor = theInitiator
        val partner: Interlocutor = thePartner

        def buildFrom(prop: AbstractGameProposition): CreateSuggestedGame =
          CreateSuggestedGame(
            creator,
            prop,
            initiator,
            partner)

        override def toString: String = "addSuggestedGame(" + creator + ", " + variable +
          "{" + initiator + ", " + partner + "})"
      }
  }

  /**
   * Creates suggested dialogue games from a dialogue game proposition.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   * @constructor create a new 'CreateSuggestedGame' operation from a creator, a dialogue
   * game proposition, an initiator of the creation and a partner.
   * @param creator dialogue participant responsible for this operation
   * @param gameProp dialogue game proposition referring to the dialogue games that are being suggested
   * @param initiator initiator of the creation
   * @param partner partner of the creation
   *
   */
  protected case class CreateSuggestedGame(
    creator: Interlocutor,
    gameProp: AbstractGameProposition,
    initiator: Interlocutor,
    partner: Interlocutor)
    extends GameManagerOperation
    with InstantiableProxy[CreateSuggestedGame, AbstractGameProposition] {

    def apply(): Unit = {
      CreateSuggestedGame0(
        creator, gameProp,
        initiator,
        partner)()
      // We push the game prop in the suggested game space      
      pushSuggestedGame(gameProp)
    }

    // Instantiation
    def instantiable: AbstractGameProposition = gameProp
    def update(a: AbstractGameProposition): CreateSuggestedGame =
      CreateSuggestedGame(creator, a, initiator, partner)

    override def toString: String = "addSuggestedGame(" + creator + ", " + gameProp +
      "{" + initiator + ", " + partner + "})"

    private case class CreateSuggestedGame0(
      creator: Interlocutor,
      gameProp: AbstractGameProposition,
      initiator: Interlocutor,
      partner: Interlocutor)
      extends GameManagerOperation
      with InstantiableProxy[CreateSuggestedGame0, AbstractGameProposition]
      with LazyLogging {

      def apply(): Unit = {
        require(gameProp.instantiated, gameProp)

        if (isProperlyConstructed(gameProp, (initiator, partner)) &&
          !isSuggested(gameProp, (initiator, partner))) {
          // We are in the case where all games in the proposition are
          // correctly instantiated.

          gameProp match {
            // g1
            case simpleGame: AnyGameProposition =>
              // Creation of the dialogue game from the game proposition
              val g = createGameInstanceFrom(simpleGame, initiator, partner)
              // Creation of the dialogue game instance ID
              val gameInstanceID = DialogueGameInstance(initiator, partner)

              logger.info(s"Adding a suggested game: $g")

              // LazyLogging a warning if entry conditions are not met for each participant
              val entryConditionsMet =
                g.dialogueParticipants.forall(
                  interlocutor => g.getEntryConditionsFor(interlocutor)())
              if (!entryConditionsMet)
                logger.warn(s"Entry conditions for game $g are not met.")
              // Register the dialogue game with its instance
              addSuggestedGame(gameInstanceID, g)

            case AbstractComposedGameProposition(g1, g2) =>
              // Case where g2 is properly suggested
              CreateSuggestedGame0(
                creator,
                g1,
                initiator,
                partner)()
          }
        } else {
          logger.error(s"Accommodation is not managed for $gameProp")
        }
      }

      // Instantiation
      def instantiable: AbstractGameProposition = gameProp
      def update(a: AbstractGameProposition): CreateSuggestedGame0 =
        CreateSuggestedGame0(creator, a, initiator, partner)

      override def toString: String = "addSuggestedGame(" + creator + ", " + gameProp +
        "{" + initiator + ", " + partner + "})"
    }
  }

  /**
   * Factory for the
   * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.GameManagerOperations#RemoveSuggestedGame RemoveSuggestedGame]]
   * operation.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected object RemoveSuggestedGame {
    /**
     * Builds an uninstantiated [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.GameManagerOperations#RemoveSuggestedGame RemoveSuggestedGame]]
     * operation with an unspecified dialogue game proposition.
     * @param theCreator dialogue participant responsible for this operation
     * @param theGameProp unspecified dialogue game proposition
     * @param theInitiator initiator of the dialogue games
     * @param thePartner partner of the dialogue games
     *
     */
    def apply(theCreator: Interlocutor,
      theGameProp: GamePropositionVariable,
      theInitiator: Interlocutor,
      thePartner: Interlocutor): Operation =
      new GenericOperationGenerator[AbstractGameProposition, RemoveSuggestedGame] {
        val creator: Interlocutor = theCreator
        val variable: GamePropositionVariable = theGameProp
        val initiator: Interlocutor = theInitiator
        val partner: Interlocutor = thePartner

        def buildFrom(prop: AbstractGameProposition): RemoveSuggestedGame =
          RemoveSuggestedGame(
            creator,
            prop,
            initiator,
            partner)

        override def toString: String = "rmSuggestedGame(" + creator + ", " + variable + ")"
      }
  }

  /**
   * Removes suggested dialogue games from a dialogue game proposition.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   * @constructor create a new 'RemoveSuggestedGame' operation from a creator, a dialogue
   * game proposition, an initiator of the creation and a partner.
   * @param creator dialogue participant responsible for this operation
   * @param gameProp dialogue game proposition referring to the suggested dialogue games that are being removed
   * @param initiator initiator of the creation
   * @param partner partner of the creation
   *
   */
  protected case class RemoveSuggestedGame(
    creator: Interlocutor,
    gameProp: AbstractGameProposition,
    initiator: Interlocutor,
    partner: Interlocutor)
    extends GameManagerOperation
    with InstantiableProxy[RemoveSuggestedGame, AbstractGameProposition] {

    def apply(): Unit = {
      RemoveSuggestedGame0(
        creator,
        gameProp,
        initiator,
        partner)()

      clearSuggestedGame(gameProp)
    }
    // Instantiation
    def instantiable: AbstractGameProposition = gameProp
    def update(a: AbstractGameProposition): RemoveSuggestedGame =
      RemoveSuggestedGame(creator, a, initiator, partner)

    override def toString: String = "rmSuggestedGame(" + creator + ", " + gameProp + ")"

    private case class RemoveSuggestedGame0(
      creator: Interlocutor,
      gameProp: AbstractGameProposition,
      initiator: Interlocutor,
      partner: Interlocutor)
      extends GameManagerOperation
      with InstantiableProxy[RemoveSuggestedGame0, AbstractGameProposition] {
      def apply(): Unit = {
        require(gameProp.instantiated)
        gameProp match {
          // g1
          case simpleGame: AnyGameProposition =>
            val g = gameInstanceFrom(simpleGame, initiator, partner)
            logger.info(s"Removing a suggested game: $g")
            removeSuggestedGame(g)

          // TODO: use ComposedGameProposition instead ?
          // g1 ~> g2
          case preSeqGame: PreSequenceGameProposition =>
            RemoveSuggestedGame0(creator, preSeqGame.g1, initiator, partner)()
          // g1;g2
          case seqGame: SequenceGameProposition =>
            RemoveSuggestedGame0(creator, seqGame.g1, initiator, partner)()
          case other =>
            throw new Error("Unknown game proposition: " + other)
        }
      }
      // Instantiation
      def instantiable: AbstractGameProposition = gameProp
      def update(a: AbstractGameProposition): RemoveSuggestedGame0 =
        RemoveSuggestedGame0(creator, a, initiator, partner)

      override def toString: String = "rmSuggestedGame(" + creator + ", " + gameProp + ")"
    }
  }

}
