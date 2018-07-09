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
package fr.lifl.smac.scadia.main

import fr.lifl.smac.scadia.DialogicalAgent
import fr.lifl.smac.scadia.dialogueManager.DialogueManager
import fr.lifl.smac.scadia.AgentState._
import fr.lifl.smac.scadia.dialogueManager.reasoning.DefaultPatternReasoner
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor

abstract class AsciiDialogicalAgent(agentName: String, partnerName: String)
  extends DialogicalAgent
  with DialogueManager // DOGMA capabilities
  with DefaultPatternReasoner // Pattern Reasoner capabilites
  // Ascii capabilites
  with Ascii {
  // Dialogical Agent Info
  def name: String = agentName

  // Dialogue Situation Settings
  def me: Interlocutor = Interlocutor(agentName)
  protected def interlocutor01: Interlocutor = me
  protected def interlocutor02: Interlocutor = Interlocutor(partnerName)

  // Semantics Definition
  // Proposition are Int
  protected type PropContent = Int
  // Question are Char
  protected type QuestionType = Char
  // Negation of a proposition is its opposite
  // Arbitrarily chosen...
  protected def propNeg(p: Int): Int =
    -p
  // q: Question, proposition Fail(q) is the opposite of the ASCII code of the Char
  // Arbitrarily chosen...
  protected def fail(question: Char): Int =
    -question.toInt
  // Resolves relation : p resolves q if it is a positive integer (or zero)
  protected def resolves = (p: Int, q: Char) =>
    p >= 0

  // Require for internal casting
  protected def castQuestionGoal(o: Any): Char =
    o.asInstanceOf[Char]

  // Initialization
  init()
}
