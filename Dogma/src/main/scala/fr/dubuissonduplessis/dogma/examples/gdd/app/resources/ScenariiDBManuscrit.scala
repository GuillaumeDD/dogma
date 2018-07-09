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
import fr.dubuissonduplessis.simpleSemantics.action.Action1
import fr.dubuissonduplessis.predicateCalculus.Individual
import fr.dubuissonduplessis.dogma.game.language.GameProposition
import fr.dubuissonduplessis.dogma.examples.scenario.Scenario
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.dialogueActs.propIn
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.dialogueActs.accIn
import fr.dubuissonduplessis.dogma.ditpp.actionDiscussion.Request
import fr.dubuissonduplessis.dogma.ditpp.actionDiscussion.AcceptRequest
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.dialogueActs.propOut
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.dialogueActs.accOut
import fr.dubuissonduplessis.simpleSemantics.question.AltQuestion
import fr.dubuissonduplessis.simpleSemantics.question.YNQuestion
import fr.dubuissonduplessis.simpleSemantics.proposition.Proposition0
import fr.dubuissonduplessis.dogma.ditpp.informationSeeking.ChoiceQuestion
import fr.dubuissonduplessis.dogma.ditpp.informationTransfer.Answer
import fr.dubuissonduplessis.dogma.ditpp.informationTransfer.Agreement
import fr.dubuissonduplessis.simpleSemantics.proposition.Proposition1
import fr.dubuissonduplessis.dogma.ditpp.informationSeeking.PropositionalQuestion
import fr.dubuissonduplessis.dogma.ditpp.informationSeeking.CheckQuestion
import fr.dubuissonduplessis.dogma.ditpp.informationTransfer.Confirm
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.dialogueActs.refIn
import fr.dubuissonduplessis.simpleSemantics.question.ParametrizedWhQuestion2
import fr.dubuissonduplessis.dogma.ditpp.informationSeeking.SetQuestion
import fr.dubuissonduplessis.dogma.ditpp.feedback.ExecNegativeAutoFB
import fr.dubuissonduplessis.dogma.ditpp.informationTransfer.Inform
import fr.dubuissonduplessis.dogma.ditpp.informationTransfer.Correction
import fr.dubuissonduplessis.dogma.ditpp.informationTransfer.AcceptCorrection

/**
 * Database of the interaction scenario appearing in the PhD thesis of Guillaume DUBUISSON DUPLESSIS.
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait ScenariiDBManuscrit extends ScenariiDB {
  // Dialogue Game Name
  protected val requestGameName = "Request"
  protected val choiceGameName = "Choice"
  protected val ynInterrogationGameName = "YNInterrogation"
  protected val verificationGameName = "Verification"
  protected val openInterrogationGameName = "OpenInterrogation"

  // Interlocutors
  protected def interlocutor01: Interlocutor
  protected def interlocutor02: Interlocutor

  // Request/AcceptRequest Game
  protected val actionAjouterPred = Action1("ajouterMotCle", Individual("grippe"))
  protected val gamePropositionRequest01 =
    GameProposition(requestGameName, actionAjouterPred)

  val scenario01 = ("Request: Request/AcceptRequest",
    Scenario() :+
    (propIn(interlocutor01, gamePropositionRequest01)) :+
    (accIn(interlocutor02, gamePropositionRequest01)) :+
    (Request(interlocutor01, actionAjouterPred)) :+
    (AcceptRequest(interlocutor02, actionAjouterPred)) :+
    (propOut(interlocutor01, gamePropositionRequest01)) :+
    (accOut(interlocutor02, gamePropositionRequest01)))

  // ChoiceQuestion: patient, etudiant, medecin	
  protected def buildChoiceQuestion: AltQuestion =
    AltQuestion(List(
      // motcle
      YNQuestion(Proposition0("patient")),
      // qualificatif
      YNQuestion(Proposition0("etudiant")),
      // metaterme
      YNQuestion(Proposition0("medecin"))))

  protected val gamePropositionChoice01 = GameProposition(
    choiceGameName, buildChoiceQuestion)

  val scenario02 = ("Choice : question/relevant answers/resolving answer",
    Scenario() :+
    // Verification Game : CheckQuestion/Answer
    (propIn(interlocutor01, gamePropositionChoice01)) :+
    (accIn(interlocutor02, gamePropositionChoice01)) :+
    (ChoiceQuestion(interlocutor01, buildChoiceQuestion)) :+
    (Answer(interlocutor02, Proposition0("medecin").neg)) :+
    (Agreement(interlocutor01, Proposition0("medecin").neg)) :+
    (Answer(interlocutor02, Proposition0("patient"))) :+
    (Agreement(interlocutor01, Proposition0("patient"))) :+
    (propOut(interlocutor01, gamePropositionChoice01)) :+
    (accOut(interlocutor02, gamePropositionChoice01)))

  // CheckQuestion embedded in a propositional question
  protected val gamePropositionalQuestion01 = GameProposition(
    ynInterrogationGameName,
    Proposition1("definition", Individual("eczema")))
  protected val gamePropositionVerifQuestion01 = GameProposition(
    verificationGameName,
    Proposition0("medecin"))
  val scenario03 = ("CheckQuestion embedded in a propositional question",
    Scenario() :+
    // Verification Game : PropositionalQuestion / Answer
    (propIn(interlocutor01, gamePropositionalQuestion01)) :+
    (accIn(interlocutor02, gamePropositionalQuestion01)) :+
    (PropositionalQuestion(interlocutor01, YNQuestion(Proposition1("definition", Individual("eczema"))))) :+
    // Embedding
    (propIn(interlocutor02, gamePropositionVerifQuestion01 << gamePropositionalQuestion01)) :+
    (accIn(interlocutor01, gamePropositionVerifQuestion01 << gamePropositionalQuestion01)) :+
    (CheckQuestion(interlocutor02, YNQuestion(Proposition0("medecin")))) :+
    (Confirm(interlocutor01, Proposition0("medecin"))) :+
    (propOut(interlocutor02, gamePropositionVerifQuestion01)) :+
    (accOut(interlocutor01, gamePropositionVerifQuestion01)) :+
    // End of embedding
    (Answer(interlocutor02, Proposition1("definition", Individual("eczema")))) :+
    (propOut(interlocutor01, gamePropositionalQuestion01)) :+
    (accOut(interlocutor02, gamePropositionalQuestion01)))

  val scenario04 = ("Request: ref.entree",
    Scenario() :+
    (propIn(interlocutor01, gamePropositionRequest01)) :+
    (refIn(interlocutor02, gamePropositionRequest01)))

  val q = ParametrizedWhQuestion2("synonyme", Individual("eczema"))
  val gamePropositionOpenInterrogation01 = GameProposition(
    openInterrogationGameName,
    ParametrizedWhQuestion2("synonyme", Individual("eczema")))

  val scenario05 = ("WhQuestion: je ne sais pas",
    Scenario() :+
    (propIn(interlocutor01, gamePropositionOpenInterrogation01)) :+
    (accIn(interlocutor02, gamePropositionOpenInterrogation01)) :+
    (SetQuestion(interlocutor01, q)) :+
    (ExecNegativeAutoFB(interlocutor02, q)) :+
    (propOut(interlocutor01, gamePropositionOpenInterrogation01)) :+
    (accOut(interlocutor02, gamePropositionOpenInterrogation01)))

  val scenario06 = ("Correction : inform/correction/acceptCorrection",
    Scenario() :+
    // Correction : inform/correction/acceptCorrection
    (Inform(interlocutor01, Proposition0("humain"))) :+
    (Correction(interlocutor02,
      Proposition0("humain"),
      Proposition0("machine"))) :+
      (AcceptCorrection(interlocutor01,
        Proposition0("humain"),
        Proposition0("machine"))))

  val allScenarii = scenario01 ::
    scenario02 ::
    scenario03 ::
    scenario04 ::
    scenario05 ::
    scenario06 :: List()

  add(allScenarii)
}
