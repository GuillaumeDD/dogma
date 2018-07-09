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
package fr.dubuissonduplessis.dogma.examples.gdd.app

import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.ContextualisationGame
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.ContextualisationOperations
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.PrintableCommitmentStore
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.PrintableGameManager
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.internalEvent.impl.DialogueGameInternalEventGenerator
import fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma
import fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.PrintableExplicitDogma
import fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.contextualisation.impl.ExplicitRefereeWithAllCombinationAllowed
import fr.dubuissonduplessis.dogma.conversationalGameBoard.impl.DefaultConversationalGameBoard
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
import fr.dubuissonduplessis.dogma.dialogue.impl.TwoInterlocutors
import fr.dubuissonduplessis.dogma.dialogueManager.ExplicitDialogueManager
import fr.dubuissonduplessis.dogma.event.DialogicEvent
import fr.dubuissonduplessis.dogma.event.ExtraDialogicEvent
import fr.dubuissonduplessis.dogma.examples.gdd.communicationGame.EvaluationGame
import fr.dubuissonduplessis.dogma.examples.gdd.dialogueGame.GDDBundle
import fr.dubuissonduplessis.dogma.examples.scenario.EventHandler
import fr.dubuissonduplessis.predicateCalculus.Individual
import fr.dubuissonduplessis.simpleSemantics.action.AtomicAction
import fr.dubuissonduplessis.simpleSemantics.proposition.Proposition
import fr.dubuissonduplessis.simpleSemantics.proposition.questionProposition.Fail
import fr.dubuissonduplessis.simpleSemantics.question.AltQuestion
import fr.dubuissonduplessis.simpleSemantics.question.Question
import fr.dubuissonduplessis.simpleSemantics.question.AbstractWhQuestion
import fr.dubuissonduplessis.simpleSemantics.question.YNQuestion
import fr.dubuissonduplessis.simpleSemantics.relations.Relevant
import fr.dubuissonduplessis.simpleSemantics.relations.Resolves
import scala.collection.immutable.Queue
import fr.dubuissonduplessis.dogma.event.Event
import fr.dubuissonduplessis.dogma.event.ExternalEvent
import fr.dubuissonduplessis.dogma.ditpp.informationTransfer.Inform

/**
 * Generic dialogue manager for a two-interlocutor interaction based on a simple semantics.
 * 
 * This dialogue manager is defined as follow:
 *  - number of interlocutor: 2
 *  - utterance semantics: simple semantics defined in PhD thesis of Guillaume DUBUISSON DUPLESSIS 
 *  - communication games: contextualisation, evaluation
 *  - dialogue games: action games (offer, request, suggestion), 
 *    propositional games (open interrogation, choice, verification, negative verification, y/n interrogation)
 *  - possible internal events: those related to dialogue games
 *  - extra-dialogic events: none
 *  - allowed combinations of dialogue games: all
 *  - management of:
 *     - forbidden events: crash
 *     - unexpected events: crash
 *     - non-priority events: crash
 *  - exceptional behaviour:
 *     - an inform act is always expected and always has priority
 *     - generation of acts always crashes   
 *     
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
abstract class DialogueManager
  // Scenario Event Handler
  extends EventHandler
  with ExplicitDialogueManager
  with TwoInterlocutors
  with DefaultConversationalGameBoard
  with DialogueGameInternalEventGenerator
  // Special Explicit Dogma
  with dogma.contextualisation.ExplicitReferee
  with ExplicitRefereeWithAllCombinationAllowed
  // Printing utilities
  with PrintableCommitmentStore
  with PrintableGameManager
  with PrintableExplicitDogma
  // Communication Games
  with EvaluationGame
  with ContextualisationGame
  with ContextualisationOperations
  // Games
  with GDDBundle {

  // DOGMA ACCOMMODATION FOR INFORM
  // An inform is expected whatever happen
  override protected def expectedEvent(da: DialogicEvent): Boolean =
    super.expectedEvent(da) ||
      (da match {
        case Inform(_, _) => true
        case _ => false
      })
  // An inform has always priority
  override protected def hasPriority(da: DialogicEvent): Boolean =
    super.hasPriority(da) ||
      (da match {
        case Inform(_, _) => true
        case _ => false
      })
  // ACTION SEMANTIC DEFINITION
  // Type Parametrisation
  type ActionContent = AtomicAction
  type ActionGoal = ActionContent

  protected def castActionGoal(o: Any): AtomicAction =
    o.asInstanceOf[AtomicAction]
  def actionNeg(action: AtomicAction): AtomicAction =
    action.neg

  // PROPOSITION SEMANTIC DEFINITION
  // Proposition
  type PropContent = Proposition
  def propNeg(s: Proposition): Proposition =
    s.neg

  def correction(p1: Proposition, p2: Proposition): Boolean =
    if (p1.arity == p2.arity) {
      if (p1.arity == 0) {
        (p1.name == "machine" && p2.name == "humain") || correction(p2, p1)
      } else {
        p1.name == p2.name
      }
    } else {
      false
    }

  protected def castPropositionalContent(o: Any): Proposition =
    o.asInstanceOf[Proposition]

  // Question supertype
  type QuestionType = Question
  protected def castQuestionGoal(o: Any): Question =
    o.asInstanceOf[Question]

  def fail(question: Question): Proposition = Fail(question)

  implicit val semCompatible = (ind: Individual, q: Question) => true
  def resolves = (p: Proposition, q: Question) =>
    Resolves(p, q)
  def relevant = (p: Proposition, q: Question) =>
    Relevant(p, q)

  // OpenQuestion type
  type OpenQuestionType = AbstractWhQuestion
  protected def castOpenQuestionGoal(o: Any): AbstractWhQuestion =
    o.asInstanceOf[AbstractWhQuestion]

  // YNQuestion type
  type YNQuestionType = YNQuestion
  protected def castYNQuestionGoal(o: Any): YNQuestion =
    o.asInstanceOf[YNQuestion]
  protected def ynQuestion(content: Proposition): YNQuestion =
    YNQuestion(content)

  // ChoiceQuestion type
  type ChoiceQuestionType = AltQuestion
  protected def castChoiceQuestionGoal(o: Any): AltQuestion =
    o.asInstanceOf[AltQuestion]

  // DIALOGUE SETUP
  protected def computeExtraDialogicEvent(evt: ExtraDialogicEvent): Unit = ???
  protected def computeForbidden(da: DialogicEvent): Unit = ???
  protected def computeUnexpected(da: DialogicEvent): Unit = ???
  protected def computeNonPriority(da: DialogicEvent): Unit = ???

  protected def computeStandard(da: DialogicEvent): Unit = {
    process(da)
  }

  // Initialization
  init()

  // Generation
  def nextEvents(): List[DialogicEvent] = ???

  // Printing utilities
  private def lastSpeakerToString: String =
    this.lastSpeaker match {
      case Some(speaker) => speaker.toString
      case _ => "unknown"
    }
  override def toString: String =
    "lastEvents=" + lastEvents.mkString("<", ", ", ">") + "\n" +
      "lastSpeaker=" + lastSpeakerToString + "\n" +
      gameManagerToString + "\n" +
      commitmentStoreToString +
      explicitDogmaToString

  // SCENARIO EVENT HANDLER SETUP
  // (redirecting to the event management of the dialogue manager)
  override def enqueue(e: ExternalEvent): Unit =
    super[ExplicitDialogueManager].enqueue(e)
  override def events: Queue[ExternalEvent] =
    super[ExplicitDialogueManager].events

  override def processFirstEvent(): Unit =
    super[ExplicitDialogueManager].processFirstEvent

  override def processAllEvents(): Unit =
    super[ExplicitDialogueManager].processAllEvents
}
