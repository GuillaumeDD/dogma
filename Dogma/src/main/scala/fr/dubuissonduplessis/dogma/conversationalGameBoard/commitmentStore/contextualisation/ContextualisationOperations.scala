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

import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager._
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
import fr.dubuissonduplessis.dogma.event.dialogueAct.StandardDialogueAct
import com.typesafe.scalalogging.slf4j.LazyLogging
import fr.dubuissonduplessis.dogma.game.language.AbstractGameProposition
import fr.dubuissonduplessis.dogma.game.language.GameProposition
import fr.dubuissonduplessis.dogma.game.language.AbstractComposedGameProposition
import fr.dubuissonduplessis.dogma.game.DialogueGameInternalEvents
import fr.dubuissonduplessis.dogma.game.language._
import fr.dubuissonduplessis.dogma.description._
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.dialogueActs.accIn
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.dialogueActs.propOut
import fr.dubuissonduplessis.dogma.dialogue.impl.TwoInterlocutors
import fr.dubuissonduplessis.dogma.instantiable.impl.InstantiableProxy
import fr.dubuissonduplessis.dogma.instantiable.impl.InstantiableDuoProxy
import fr.dubuissonduplessis.dogma.instantiable.impl.Instantiated
import fr.dubuissonduplessis.dogma.game.language.instantiable.GamePropositionVariable
import fr.dubuissonduplessis.dogma.operation.Operation
import fr.dubuissonduplessis.dogma.operation.GenericOperationGenerator
import fr.dubuissonduplessis.dogma.instantiable.Variable
import fr.dubuissonduplessis.dogma.event.Event
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.dialogueActs.propIn
import fr.dubuissonduplessis.dogma.game.language.instantiable.CompositeGamePropositionMatcherEventDescription
import fr.dubuissonduplessis.dogma.game.language.instantiable.SemiInstantiatedVariableCombination

/**
 * Module providing high-level operations on a game manager and a commitment store by taking into account
 * the contextualisation game.
 * It extends the
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.GameManagerOperations GameManagerOperations]]
 * module that provides high-level operations on a game manager and a commitment store.
 *
 * High-level operations provided by this module include:
 *  - operations related to the '''entrance''' of a dialogue game:
 *  [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.ContextualisationOperations#CreateGame CreateGame]],
 *  [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.ContextualisationOperations#ConnectDialogueGamesWithEntryConditions ConnectDialogueGamesWithEntryConditions]],
 *  [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.ContextualisationOperations#ConnectDialogueGamesWithSuccessConditions ConnectDialogueGamesWithSuccessConditions]],
 *  [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.ContextualisationOperations#ExitGameGeneration ExitGameGeneration]], and
 *  [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.ContextualisationOperations#PropEntreeGeneration PropEntreeGeneration]]
 *  - operations related to the '''playing''' of a dialogue game:
 *  [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.ContextualisationOperations#PropEntreePersistence PropEntreePersistence]]
 *  - operations related to the '''exit''' of a dialogue game:
 *  [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.ContextualisationOperations#CancelGameWithContextualisation CancelGameWithContextualisation]] and
 *  [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.ContextualisationOperations#RemoveContextualisationCommitment RemoveContextualisationCommitment]]
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait ContextualisationOperations extends GameManagerOperations {
  this: GameManagerOperationsRequirements with ContextualisationGame with DialogueGameInternalEvents with TwoInterlocutors =>
  // Communication Game are singleton
  // TODO Improve this collection of singleton ContextualisationGame
  /**
   * @return the contextualisation communication game
   */
  private[contextualisation] def contextualisationGame: CommunicationGame =
    communicationGames.find(_.name == ContextualisationGame.name).get

  /**
   * Generates the internal event corresponding the achievement of the full entry conditions
   * of a given dialogue game proposition.
   *
   */
  private[contextualisation] def fullEntryConditionsEvent(
    gProp: AbstractGameProposition,
    speakers: (Interlocutor, Interlocutor)): FullEntryConditionsReached =
    gProp match {
      case PreSequenceGameProposition(g1, g2) =>
        fullEntryConditionsEvent(g1, speakers)
      case SequenceGameProposition(g1, g2) =>
        fullEntryConditionsEvent(g1, speakers)
      case EmbeddedGameProposition(g1, g2) =>
        fullEntryConditionsEvent(g2, speakers)
      case GameProposition(name, goal) =>
        FullEntryConditionsReached(
          dialogueGameInstanceFrom(name, goal, speakers))(None)
    }

  /**
   * Generates the internal event corresponding the achievement of the full exit conditions
   * of a given dialogue game proposition.
   *
   */
  private[contextualisation] def fullExitConditionsEvent(
    gProp: AbstractGameProposition,
    speakers: (Interlocutor, Interlocutor)): ExitConditionsReached =
    gProp match {
      case AbstractComposedGameProposition(g1, g2) =>
        fullExitConditionsEvent(g2, speakers)
      case GameProposition(name, goal) =>
        ExitConditionsReached(
          dialogueGameInstanceFrom(name, goal, speakers))(None)
    }

  /**
   * Generates the internal event corresponding the achievement of the full success conditions
   * of a given dialogue game proposition.
   *
   */
  private[contextualisation] def fullSuccessConditionsEvent(
    gProp: AbstractGameProposition,
    speakers: (Interlocutor, Interlocutor)): FullSuccessConditionsReached =
    gProp match {
      case AbstractComposedGameProposition(g1, g2) =>
        fullSuccessConditionsEvent(g2, speakers)
      case GameProposition(name, goal) =>
        FullSuccessConditionsReached(
          dialogueGameInstanceFrom(name, goal, speakers))(None)
    }

  /**
   * Determines if a given event(s) description contains a given dialogue game
   * proposition.
   * @return true if the description contains the simple game proposition, else false.
   *
   */
  private[contextualisation] def contains(
    desc: Description,
    simpleGame: AnyGameProposition): Boolean =
    {
      // Definition of the predicate that detects when
      // an event speak about this game
      val pred = (content: Event) => {
        content match {
          case da: StandardDialogueAct[_] =>
            da.content match {
              case gProp: AbstractGameProposition =>
                gProp.contains(simpleGame)
              case _ => false
            }
          case _ => false
        }
      }

      // Either the description is a dialogue act containing simpleGame...
      eventDescriptionMatches(desc, pred) || {
        // or it is a complex description with variables, e.g., propEntree(x, Ys << g0)
        desc match {
          case d: CompositeGamePropositionMatcherEventDescription[_] =>
            d.content match {
              case combination: SemiInstantiatedVariableCombination =>
                combination.prop0.contains(simpleGame)
              case _ =>
                false
            }
          case _ =>
            false
        }
      }
    }

  /**
   * Removes the contextualisation commitments corresponding to a given dialogue game.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   * @constructor create a new 'RemoveContextualisationCommitment' operation from a creator, a dialogue game proposition,
   * and a pair of dialogue participants
   * @param creator dialogue participant responsible for this operation
   * @param gameProp dialogue game proposition representing dialogue game
   * @param speakers dialogue participants involved in the dialogue game
   *
   */
  protected[contextualisation] case class RemoveContextualisationCommitment(
    creator: Interlocutor,
    gameProp: AnyGameProposition,
    speakers: (Interlocutor, Interlocutor))
    extends GameManagerOperation
    with InstantiableProxy[RemoveContextualisationCommitment, AnyGameProposition] {
    val (speaker01, speaker02) = speakers
    def apply(): Unit = {
      val game = dialogueGameInstanceFrom(
        gameProp.name,
        gameProp.goal,
        (speaker01, speaker02))

      val ctxCommitments =
        findCommitmentsWhere(
          // We kill commitment that speak about this game
          c => c match {
            case gc: GameActionCommitment =>
              contains(gc.content, gameProp)

            case _ => false
          }).filter(_.isActive)

      logger.info(s"Removing contextualisation commitment regarding: $game")
      val removeActions = for (c <- ctxCommitments)
        yield (Remove(creator, c))

      for (a <- removeActions) {
        a()
      }
    }

    // Instantiation
    def instantiable: AnyGameProposition = gameProp
    def update(a: AnyGameProposition): RemoveContextualisationCommitment =
      RemoveContextualisationCommitment(creator, a, speakers)

    override def toString: String = "removeContextualisationCommitment(" + creator + ", " + gameProp + ")"
  }

  /**
   * Factory for the [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.ContextualisationOperations#CancelGameWithContextualisation CancelGameWithContextualisation]] operation.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected[contextualisation] object CancelGameWithContextualisation {
    /**
     * Builds an uninstantiated [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.ContextualisationOperations#CancelGameWithContextualisation CancelGameWithContextualisation]]
     * operation with an unspecified dialogue game proposition.
     * @param theCreator dialogue participant responsible for this operation
     * @param theGameProp unspecified dialogue game proposition
     * @param speakers pair of dialogue participants
     *
     */
    def apply(theCreator: Interlocutor,
      theGameProp: Variable[AnyGameProposition],
      speakers: (Interlocutor, Interlocutor)): Operation =
      new GenericOperationGenerator[AnyGameProposition, CancelGameWithContextualisation] {
        val creator: Interlocutor = theCreator
        val variable: Variable[AnyGameProposition] = theGameProp
        val initiator: Interlocutor = speakers._1
        val partner: Interlocutor = speakers._2

        def buildFrom(prop: AnyGameProposition): CancelGameWithContextualisation =
          CancelGameWithContextualisation(
            creator,
            prop,
            (initiator, partner))

        override def toString: String = "cancelGame(" + creator + ", " + variable + ")"
      }
  }

  /**
   * Cancels a dialogue game and its contextualisation commitments.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   * @constructor create a new 'CancelGameWithContextualisation' operation from a creator, a dialogue game proposition,
   * and a pair of dialogue participants
   * @param creator dialogue participant responsible for this operation
   * @param gameProp dialogue game proposition representing dialogue game
   * @param speakers dialogue participants involved in the dialogue game
   * @see [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.GameManagerOperations#CancelGame CancelGame]] operation
   * @see [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.ContextualisationOperations#RemoveContextualisationCommitment RemoveContextualisationCommitment]] operation
   */
  protected[contextualisation] case class CancelGameWithContextualisation(
    creator: Interlocutor,
    gameProp: AnyGameProposition,
    speakers: (Interlocutor, Interlocutor))
    extends GameManagerOperation
    with InstantiableProxy[CancelGameWithContextualisation, AnyGameProposition] {
    val (speaker01, speaker02) = speakers

    def apply(): Unit = {
      RemoveContextualisationCommitment(creator, gameProp, speakers)()
      CancelGame(creator, gameProp, (speaker01, speaker02))()
    }

    // Instantiation
    def instantiable: AnyGameProposition = gameProp
    def update(a: AnyGameProposition): CancelGameWithContextualisation =
      CancelGameWithContextualisation(creator, a, speakers)

    override def toString: String = "cancelGame(" + creator + ", " + gameProp + ")"
  }

  /**
   * Connects the reaching of the entry conditions of a suggested dialogue game to an acceptance
   * move to enter this dialogue game.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   * @constructor create a new 'ConnectDialogueGamesWithEntryConditions' operation from a creator, a dialogue game proposition,
   * the initiator of the game and a partner
   * @param creator dialogue participant responsible for this operation
   * @param nextGameProp dialogue game proposition representing the suggested dialogue game
   *
   */
  protected[contextualisation] case class ConnectDialogueGamesWithEntryConditions(
    creator: Interlocutor,
    nextGameProp: AbstractGameProposition,
    initiator: Interlocutor,
    partner: Interlocutor)
    extends GameManagerOperation
    with InstantiableProxy[ConnectDialogueGamesWithEntryConditions, AbstractGameProposition]
    with LazyLogging {

    def apply(): Unit = {
      logger.debug(s"Linking: reaching entry conditions of $nextGameProp triggers an acceptance move for the entry in it.")

      Create(creator,
        C(contextualisationGame, initiator, partner,
          fullEntryConditionsEvent(nextGameProp, (initiator, partner)) ==>
            Create(creator, C(contextualisationGame, partner, initiator, accIn(partner, nextGameProp)))))()
    }

    // Instantiation
    def instantiable: AbstractGameProposition = nextGameProp
    def update(a: AbstractGameProposition): ConnectDialogueGamesWithEntryConditions =
      ConnectDialogueGamesWithEntryConditions(
        creator,
        a,
        initiator,
        partner)
  }

  /**
   * Connects the reaching of the success conditions of dialogue game to an acceptance
   * move to enter another dialogue game.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   * @constructor create a new 'ConnectDialogueGamesWithSuccessConditions' operation from a creator,
   * a dialogue game proposition representing the dialogue game that should succeed, a dialogue
   * game proposition representing the dialogue game that will be entered, the initiator of the game
   * and a partner
   * @param creator dialogue participant responsible for this operation
   * @param initGameProp the dialogue game that should succeed
   * @param nextGameProp the dialogue game that will be entered
   *
   */
  protected[contextualisation] case class ConnectDialogueGamesWithSuccessConditions(
    creator: Interlocutor,
    initGameProp: AbstractGameProposition,
    nextGameProp: AbstractGameProposition,
    initiator: Interlocutor,
    partner: Interlocutor)
    extends GameManagerOperation
    with InstantiableDuoProxy[ConnectDialogueGamesWithSuccessConditions, AbstractGameProposition, AbstractGameProposition]
    with LazyLogging {

    def apply(): Unit = {
      logger.debug(s"Reaching success conditions of $initGameProp triggers an acceptance move for the entry in $nextGameProp")
      Create(creator,
        C(contextualisationGame, initiator, partner,
          fullSuccessConditionsEvent(initGameProp, (initiator, partner)) ==>
            Create(creator, C(contextualisationGame, partner, initiator, accIn(partner, nextGameProp)))))()
    }

    // Instantiation
    protected def instantiable1: AbstractGameProposition =
      initGameProp
    protected def instantiable2: AbstractGameProposition =
      nextGameProp
    protected def update(
      instantiable1: AbstractGameProposition,
      instantiable2: AbstractGameProposition): ConnectDialogueGamesWithSuccessConditions =
      ConnectDialogueGamesWithSuccessConditions(
        creator,
        instantiable1,
        instantiable2,
        initiator,
        partner)
  }

  /**
   * Generates the contextualisation commitments related to the exit of a dialogue
   * game when failure or success conditions are reached.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   * @constructor create a new 'ExitGameGeneration' operation from a given dialogue game instance
   */
  protected[contextualisation] case class ExitGameGeneration(
    dialogueGameInstance: DialogueGameInstance)
    extends GameManagerOperation
    with Instantiated[ExitGameGeneration] {

    def apply(): Unit = {

      logger.info(s"Generating commitment for the prop.sortie of: $dialogueGameInstance")
      require(dialogueGameInstance.state == ContextualisationState.OPEN)
      val dialogueGame = dialogueGameInstance.dialogueGame
      for (interlocutor <- dialogueGame.dialogueParticipants) {
        Create.wrap( // Commitment generating prop.sortie(interlocutor, game)
          Set(C(contextualisationGame, interlocutor, otherLocutor(interlocutor),
            (FailureConditionsReached(dialogueGameInstance, interlocutor)(None) *| SuccessConditionsReached(dialogueGameInstance, interlocutor)(None)) ==>
              Create(interlocutor, C(contextualisationGame, interlocutor, otherLocutor(interlocutor),
                propOut(interlocutor, dialogueGameInstance.gameProposition))))), interlocutor)
          .foreach(_())
      }
    }

    override def toString: String = "exitGameGeneration(" + dialogueGameInstance + ")"
  }

  /**
   * Factory for the [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.ContextualisationOperations#CreateGame CreateGame]]
   * operation.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected[contextualisation] object CreateGame {
    /**
     * Builds an uninstantiated [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.ContextualisationOperations#CreateGame CreateGame]]
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
      new GenericOperationGenerator[AbstractGameProposition, CreateGame] {
        val creator: Interlocutor = theCreator
        val variable: GamePropositionVariable = theGameProp
        val initiator: Interlocutor = theInitiator
        val partner: Interlocutor = thePartner

        def buildFrom(prop: AbstractGameProposition): CreateGame =
          CreateGame(
            creator,
            prop,
            initiator,
            partner)

        override def toString: String = "create(" + creator + ", C({" + initiator + ", " + partner + "}, " + variable + "))"
      }
  }

  /**
   * Creates dialogue games from a dialogue game proposition.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   * @constructor create a new 'CreateGame' operation from a creator, a dialogue
   * game proposition, an initiator of the creation and a partner.
   * @param creator dialogue participant responsible for this operation
   * @param gameProp dialogue game proposition referring to the dialogue games that are being created
   * @param initiator initiator of the creation
   * @param partner partner of the creation
   */
  protected[contextualisation] case class CreateGame(
    creator: Interlocutor,
    gameProp: AbstractGameProposition,
    initiator: Interlocutor,
    partner: Interlocutor)
    extends GameManagerOperation
    with InstantiableProxy[CreateGame, AbstractGameProposition] {

    def otherSpeaker: Interlocutor =
      if (initiator == creator) {
        partner
      } else {
        initiator
      }

    def apply(): Unit = {
      require(gameProp.instantiated)
      gameProp match {
        // g1
        case p @ GameProposition(gameName, goal) =>
          val instance = dialogueGameInstanceFrom(
            gameName,
            goal,
            (initiator, partner))
          val dialogueGame = instance.dialogueGame
          logger.info(s"Adding an opened game: $dialogueGame$instance")

          val entryConditionsMet =
            dialogueGame.dialogueParticipants.forall(interlocutor => dialogueGame.getEntryConditionsFor(interlocutor)())
          // (g.dialogueParticipants foldLeft true)((result, elem) => result && g.getEntryConditionsFor(elem)(cgb))
          if (!entryConditionsMet)
            logger.warn(s"Entry conditions for game $instance are not met.")
          val newG = instance

          openGame(newG)
          /*Create(creator, C(Contextualisation, otherSpeaker, creator,
            new poursuit(otherSpeaker, GameProposition(gameName, goal)))),*/
          LoadGame(newG)()
          ExitGameGeneration(newG)()

        // g1 ~> g2
        case PreSequenceGameProposition(initGameProp, nextGameProp) =>
          // Here are the magic recursive creation call :)
          // We open the initiative game.
          CreateGame(creator, initGameProp, initiator, partner)()
          // Linking with the next game, here thanks to entry condition event
          ConnectDialogueGamesWithEntryConditions(
            creator,
            nextGameProp,
            initiator,
            partner)()

        // g1;g2
        case SequenceGameProposition(initGameProp, nextGameProp) =>
          // Here are the magic recursive creation call :)
          // We open the initiative game.
          CreateGame(creator, initGameProp, initiator, partner)()
          // Linking with the next game, here thanks to success condition event
          ConnectDialogueGamesWithSuccessConditions(
            creator,
            initGameProp,
            nextGameProp,
            initiator,
            partner)()

        // g1<g2
        case p @ EmbeddedGameProposition(embeddedGameProp, parentGameProp) =>
          CreateGame(creator, embeddedGameProp, initiator, partner)() // Game Creation
          Prioritize(creator, embeddedGameProp, parentGameProp, initiator, partner)()
      }
    }
    // Instantiation
    def instantiable: AbstractGameProposition = gameProp
    def update(a: AbstractGameProposition): CreateGame =
      CreateGame(creator, a, initiator, partner)

    override def toString: String = "create(" + creator + ", C({" + initiator + ", " + partner + "}, " + gameProp + "))"
  }

  /**
   * Factory for the [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.ContextualisationOperations#PropEntreeGeneration PropEntreeGeneration]]
   * operation.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected[contextualisation] object PropEntreeGeneration {
    /**
     * Builds an uninstantiated [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.ContextualisationOperations#PropEntreeGeneration PropEntreeGeneration]]
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
      new GenericOperationGenerator[AbstractGameProposition, PropEntreeGeneration] {
        val creator: Interlocutor = theCreator
        val variable: GamePropositionVariable = theGameProp
        val initiator: Interlocutor = theInitiator
        val partner: Interlocutor = thePartner

        def buildFrom(prop: AbstractGameProposition): PropEntreeGeneration =
          PropEntreeGeneration(
            creator,
            prop,
            initiator,
            partner)

        override def toString: String = "propEntreeGeneration(" + creator + ", " + variable +
          "{" + initiator + ", " + partner + "})"
      }
  }

  /**
   * Generates the contextualisation commitments related to the proposition of an
   * embedded dialogue game.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   * @constructor create a new 'PropEntreeGeneration' operation from a creator, a dialogue
   * game proposition, an initiator of the proposition and a partner.
   * @param creator dialogue participant responsible for this operation
   * @param gameProp dialogue game proposition referring to the dialogue games
   * @param initiator initiator of the proposition
   * @param partner partner of the proposition
   */
  protected[contextualisation] case class PropEntreeGeneration(
    creator: Interlocutor,
    gameProp: AbstractGameProposition,
    initiator: Interlocutor,
    partner: Interlocutor)
    extends GameManagerOperation
    with InstantiableProxy[PropEntreeGeneration, AbstractGameProposition] {

    def apply(): Unit = {
      PropEntreeGeneration0(
        creator, gameProp,
        initiator,
        partner)()
    }

    // Instantiation
    def instantiable: AbstractGameProposition = gameProp
    def update(a: AbstractGameProposition): PropEntreeGeneration =
      PropEntreeGeneration(creator, a, initiator, partner)

    override def toString: String = "propEntreeGeneration(" + creator + ", " + gameProp +
      "{" + initiator + ", " + partner + "})"

    private case class PropEntreeGeneration0(
      creator: Interlocutor,
      gameProp: AbstractGameProposition,
      initiator: Interlocutor,
      partner: Interlocutor)
      extends GameManagerOperation
      with InstantiableProxy[PropEntreeGeneration0, AbstractGameProposition] {

      def apply(): Unit = {
        require(gameProp.instantiated, gameProp)
        gameProp match {
          // g1
          case simpleGame: AnyGameProposition =>
            // We create the game instance from the game proposition
            logger.info(s"Generating propEntree for: $simpleGame")
            Create(initiator, C(contextualisationGame, initiator, partner,
              propIn(initiator, Ys << simpleGame)))()
            Create(initiator, C(contextualisationGame, partner, initiator,
              propIn(partner, Ys << simpleGame)))()
          case AbstractComposedGameProposition(g1, g2) =>
            // Case where g2 is properly suggested
            PropEntreeGeneration0(
              creator,
              g1,
              initiator,
              partner)()
        }
      }

      // Instantiation
      def instantiable: AbstractGameProposition = gameProp
      def update(a: AbstractGameProposition): PropEntreeGeneration0 =
        PropEntreeGeneration0(creator, a, initiator, partner)

      override def toString: String = "propEntreeGeneration0(" + creator + ", " + gameProp +
        "{" + initiator + ", " + partner + "})"
    }
  }
  /**
   * Factory for the [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.ContextualisationOperations#PropEntreePersistence PropEntreePersistence]]
   * operation.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected[contextualisation] object PropEntreePersistence {
    /**
     * Builds an uninstantiated [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.ContextualisationOperations#PropEntreePersistence PropEntreePersistence]]
     * operation with an unspecified dialogue game proposition.
     * @param theInitiator initiator of the proposition
     * @param theGameProp unspecified dialogue game proposition
     * @param thePartner partner of the proposition
     *
     */
    def apply(
      theInitiator: Interlocutor,
      theGameProp: GamePropositionVariable,
      thePartner: Interlocutor): Operation =
      new GenericOperationGenerator[AbstractGameProposition, PropEntreePersistence] {
        val variable: GamePropositionVariable = theGameProp
        val initiator: Interlocutor = theInitiator
        val partner: Interlocutor = thePartner

        def buildFrom(prop: AbstractGameProposition): PropEntreePersistence =
          PropEntreePersistence(
            initiator,
            prop,
            partner)

        override def toString: String = "propEntreePersistence(" + initiator + ", " + variable + ")"
      }
  }

  /**
   * Ensures the persistence of the contextualisation commitments related to
   * the proposition of an embedded dialogue game.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   * @constructor create a new 'PropEntreePersistence' operation from a dialogue
   * game proposition, an initiator of the proposition and a partner.
   * @param initiator initiator of the proposition
   * @param gameProp dialogue game proposition referring to the dialogue games
   * @param partner partner of the proposition
   */
  protected[contextualisation] case class PropEntreePersistence(
    initiator: Interlocutor,
    gameProp: AbstractGameProposition,
    partner: Interlocutor)
    extends GameManagerOperation
    with InstantiableProxy[PropEntreePersistence, AbstractGameProposition] {

    def apply(): Unit = {
      require(gameProp.instantiated, gameProp)
      gameProp match {
        case EmbeddedGameProposition(g1, g2) =>
          // Ensure that the speaker can propose another embedding with g2
          logger.info(s"Ensuring persistence of prop.entree($initiator,$gameProp) with ${propIn(initiator, Ys << g2)}")
          Create(initiator, C(contextualisationGame, initiator, partner,
            propIn(initiator, Ys << g2)))()

        case _ =>
        // Do nothing

      }
    }

    // Instantiation
    def instantiable: AbstractGameProposition = gameProp
    def update(a: AbstractGameProposition): PropEntreePersistence =
      PropEntreePersistence(initiator, a, partner)

    override def toString: String = "propEntreePersistence(" + initiator + ", " + gameProp + ")"
  }
}
