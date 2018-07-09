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
import fr.dubuissonduplessis.dogma.examples.scenario.ScenarioEventHandler
import fr.dubuissonduplessis.dogma.examples.gdd.app.resources.ScenariiDBManuscrit
import fr.dubuissonduplessis.simpleSemantics.question.WhQuestion
import fr.dubuissonduplessis.simpleSemantics.question.ParametrizedWhQuestion2
import fr.dubuissonduplessis.predicateCalculus.Individual
import fr.dubuissonduplessis.dogma.examples.scenario.impl.ScenarioRunnerWithUser

object MainScenario extends App
  with ScenariiDBManuscrit
  with ScenarioRunnerWithUser {
  // Dialogue Manager embedded into an EventHandler
  case class DialogueManagerEventHandler(
    interlocutor01: Interlocutor,
    interlocutor02: Interlocutor) extends DialogueManager

  // Interlocutors speaking
  protected def interlocutor01: Interlocutor = Interlocutor("A")
  protected def interlocutor02: Interlocutor = Interlocutor("B")

  // Factory to create a scenario event handler
  def defaultBuilder() = () => DialogueManagerEventHandler(interlocutor01, interlocutor02)

  // Play
  play()
}
