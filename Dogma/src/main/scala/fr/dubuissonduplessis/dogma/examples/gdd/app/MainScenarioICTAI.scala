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

import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
import fr.dubuissonduplessis.dogma.examples.gdd.app.resources.ScenarioDBICTAI
import fr.dubuissonduplessis.dogma.examples.scenario.ScenarioEventHandler
import fr.dubuissonduplessis.dogma.conversationalGameBoard.util.LaTeXExport
import fr.dubuissonduplessis.simpleSemantics.action.AtomicAction
import fr.dubuissonduplessis.simpleSemantics.proposition.Proposition
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
import fr.dubuissonduplessis.dogma.description.Description
import fr.dubuissonduplessis.dogma.event.InternalEvent
import fr.dubuissonduplessis.dogma.event.ExtraDialogicEvent
import fr.dubuissonduplessis.dogma.operation.Operation
import fr.dubuissonduplessis.dogma.description.dialogueAct.SemanticConstraint
import fr.dubuissonduplessis.dogma.examples.gdd.dialogueGame.util.GDDBundleToLatex
import fr.dubuissonduplessis.simpleSemantics.Sentence
import javax.xml.bind.TypeConstraintException
import fr.dubuissonduplessis.dogma.examples.scenario.impl.ScenarioRunnerWithNoInteraction

object MainScenarioICTAI extends App
  with ScenarioDBICTAI
  with ScenarioRunnerWithNoInteraction {
  // Dialogue Manager embedded into an EventHandler
  case class DialogueManagerEventHandler(
    interlocutor01: Interlocutor,
    interlocutor02: Interlocutor) extends DialogueManager
    // LaTeX utilities
    with LaTeXExport
    with GDDBundleToLatex {

    // Members declared in fr.dubuissonduplessis.dogma.commitment.util.SemanticTypesToLaTeX
    protected def actionToLatex(prop: AtomicAction): String =
      fr.dubuissonduplessis.simpleSemantics.util.toLatex(prop)

    protected def propToLatex(prop: Proposition): String =
      fr.dubuissonduplessis.simpleSemantics.util.toLatex(prop)

    protected def domainSemanticContentToLatex(content: Any): String =
      content match {
        case s: Sentence =>
          fr.dubuissonduplessis.simpleSemantics.util.toLatex(s)
        case _ =>
          throw new TypeConstraintException(s"Unable to compute a LaTeX representation of $content")
      }

    protected def extraDialogicEventToLatex(evt: ExtraDialogicEvent): String =
      ???
  }

  // Interlocutors speaking
  protected def interlocutor01: Interlocutor = Interlocutor("Ex")
  protected def interlocutor02: Interlocutor = Interlocutor("U")

  // Factory to create a scenario event handler
  def defaultBuilder() = () => DialogueManagerEventHandler(interlocutor01, interlocutor02)

  // Recover the scenarii DB and play
  play()
}
