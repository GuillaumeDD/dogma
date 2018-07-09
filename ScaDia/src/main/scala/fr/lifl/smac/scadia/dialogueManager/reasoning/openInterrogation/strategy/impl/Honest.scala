/**
 * *****************************************************************************
 * Copyright (c) 2014 Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Maxime MORGE
 *     Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr> - initial API and implementation
 * ****************************************************************************
 */
package fr.lifl.smac.scadia.dialogueManager.reasoning.openInterrogation.strategy.impl

import fr.lifl.smac.scadia.dialogueManager.reasoning.PatternReasonerDialogicalAgent
import fr.lifl.smac.scadia.dialogueManager.reasoning.openInterrogation.strategy.ContentGeneratorStrategy
import fr.lifl.smac.scadia.ability.Reasoning

trait Honest extends ContentGeneratorStrategy {
  self: PatternReasonerDialogicalAgent with Reasoning =>
  protected type ReasoningInput >: QuestionType
  protected type ReasoningOutput <: PropContent
  // TODO Check why the lower and upper bound are not working alo
  protected def generateContent(q: QuestionType): PropContent =
    compute(q.asInstanceOf[ReasoningInput]).asInstanceOf[PropContent]
}
