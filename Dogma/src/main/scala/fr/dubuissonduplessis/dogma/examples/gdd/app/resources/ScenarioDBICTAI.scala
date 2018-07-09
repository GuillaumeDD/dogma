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
package fr.dubuissonduplessis.dogma.examples.gdd.app.resources

import scala.collection.immutable.List
import fr.dubuissonduplessis.dogma.examples.scenario.ScenariiDB
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
import fr.dubuissonduplessis.simpleSemantics.proposition.Proposition0
import fr.dubuissonduplessis.simpleSemantics.question.AltQuestion
import fr.dubuissonduplessis.simpleSemantics.proposition.Proposition1
import fr.dubuissonduplessis.predicateCalculus.Individual
import fr.dubuissonduplessis.simpleSemantics.action.Action1
import fr.dubuissonduplessis.dogma.examples.scenario.Scenario
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.dialogueActs.propIn
import fr.dubuissonduplessis.dogma.ditpp.informationSeeking.CheckQuestion
import fr.dubuissonduplessis.dogma.ditpp.informationTransfer.Confirm
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.dialogueActs.propOut
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.dialogueActs.accOut
import fr.dubuissonduplessis.dogma.ditpp.informationSeeking.ChoiceQuestion
import fr.dubuissonduplessis.dogma.ditpp.feedback.ExecNegativeAutoFB
import fr.dubuissonduplessis.dogma.game.language.GameProposition
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.dialogueActs.accIn
import fr.dubuissonduplessis.simpleSemantics.question.YNQuestion
import fr.dubuissonduplessis.dogma.ditpp.actionDiscussion.Offer
import fr.dubuissonduplessis.dogma.ditpp.actionDiscussion.AcceptOffer

trait ScenarioDBICTAI extends ScenariiDB {
  // Dialogue Game Name (helper)
  private val offerGameName = "Offer"
  private val choiceGameName = "Choice"
  private val verificationGameName = "Verification"

  // Interlocutors
  protected def interlocutor01: Interlocutor
  protected def interlocutor02: Interlocutor

  // SEMANTICS
  // Propositions for the example
  private val proposition01 = Proposition0("add_resource_type")
  private val proposition03 = Proposition1("resource_type", Individual("forum"))
  private val proposition02 = Proposition1("resource_type", Individual("databases"))
  private val proposition04 = Proposition1("resource_type", Individual("medical_study"))
  private val proposition05 = Proposition1("resource_type", Individual("picture"))
  private val proposition06 = Proposition1("resource_type", Individual("technical_report"))
  private val proposition07 = Proposition1("resource_type", Individual("periodical"))
  private val proposition08 = Proposition1("resource_type", Individual("document_for_patient"))

  // Choice question
  private val choiceQuestion =
    AltQuestion(proposition02, proposition03, proposition04, proposition05, proposition06, proposition07, proposition08)

  // Action
  private val action = Action1("add_resource_type", Individual("document_for_patient"))

  val verificationGame = GameProposition(verificationGameName, proposition01)
  val choiceInterrogationGame = GameProposition(choiceGameName, choiceQuestion)
  val offerGame = GameProposition(offerGameName, action)

  // Scenario definition (a sequence of dialogue act event)
  add("ICTAI : verification game, choice interrogation game and offer game",
    Scenario() :+
      // Verification game: ?add_resource_type
      propIn(interlocutor01, verificationGame) :+ // contextualisation: entry proposition
      accIn(interlocutor02, verificationGame) :+ // contextualisation: entry acceptance
      CheckQuestion(interlocutor01, YNQuestion(proposition01)) :+ // verification game: move by the initiator
      Confirm(interlocutor02, proposition01) :+ // verification game: reaction of the partner
      propOut(interlocutor01, verificationGame) :+ // contextualisation: exit proposition
      accOut(interlocutor02, verificationGame) :+ // contextualisation: exit acceptance
      // Choice interrogation game: {?resource_type(databases), ?resource_type(forum), ?resource_type(medical_study), ?resource_type(picture), ?resource_type(technical_report), ?resource_type(periodical), ?resource_type(document_for_patient)}
      propIn(interlocutor01, choiceInterrogationGame) :+ // contextualisation: entry proposition
      accIn(interlocutor02, choiceInterrogationGame) :+ // contextualisation: entry acceptance
      ChoiceQuestion(interlocutor01, choiceQuestion) :+ // interrogation game: move by the initiator
      ExecNegativeAutoFB(interlocutor02, choiceQuestion) :+ // interrogation game: reaction of the partner
      propOut(interlocutor01, choiceInterrogationGame) :+ // contextualisation: exit proposition
      accOut(interlocutor02, choiceInterrogationGame) :+ // contextualisation: exit acceptance
      // Offer game: add_resource_type(document_for_patient)
      propIn(interlocutor01, offerGame) :+ // contextualisation: entry proposition
      accIn(interlocutor02, offerGame) :+ // contextualisation: entry acceptance
      Offer(interlocutor01, action) :+ // offer game: move by the initiator
      AcceptOffer(interlocutor02, action) :+ // offer game: reaction of the partner
      propOut(interlocutor01, offerGame) :+ // contextualisation: exit proposition
      accOut(interlocutor02, offerGame)) // contextualisation: exit acceptance
}
