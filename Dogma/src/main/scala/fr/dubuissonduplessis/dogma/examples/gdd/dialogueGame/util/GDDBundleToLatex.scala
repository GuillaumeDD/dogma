/*******************************************************************************
 * Copyright (c) 2014 Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr> - initial API and implementation
 ******************************************************************************/
package fr.dubuissonduplessis.dogma.examples.gdd.dialogueGame.util

import fr.dubuissonduplessis.dogma.examples.gdd.dialogueGame.GDDBundle
import fr.dubuissonduplessis.dogma.description.dialogueAct.SemanticConstraint
import fr.dubuissonduplessis.dogma.conversationalGameBoard.util.ConversationalGameBoardToLaTeX
import fr.dubuissonduplessis.dogma.dialogue.impl.TwoInterlocutors
import fr.dubuissonduplessis.dogma.conversationalGameBoard.ConversationalGameBoard

trait GDDBundleToLatex {
  this: GDDBundle with ConversationalGameBoard with TwoInterlocutors with ConversationalGameBoardToLaTeX =>

  protected def semanticConstraintToLatex[T](constraint: SemanticConstraint[T]): String =
    constraint match {
      case CorrectionConstraint(v1, v2) =>
        """\relationCorrection{""" +
          fr.dubuissonduplessis.dogma.instantiable.util.toLatex(v1) +
          """}{""" +
          fr.dubuissonduplessis.dogma.instantiable.util.toLatex(v2) +
          """}"""
      case SemiInstantiatedCorrectionConstraint(prop, v2) =>
        """\relationCorrection{""" +
          semanticContentToLatex(prop) +
          """}{""" +
          fr.dubuissonduplessis.dogma.instantiable.util.toLatex(v2) +
          """}"""
      case StrictlyRelevantConstraint(q, v) =>
        """\strictlyRelevant{""" +
          fr.dubuissonduplessis.dogma.instantiable.util.toLatex(v) +
          """}{""" +
          semanticContentToLatex(q) +
          """}"""
      case RelevantConstraint(q, v) =>
        """\relevant{""" +
          fr.dubuissonduplessis.dogma.instantiable.util.toLatex(v) +
          """}{""" +
          semanticContentToLatex(q) +
          """}"""
      case ResolvesConstraint(q, v) =>
        """\resolves{""" +
          fr.dubuissonduplessis.dogma.instantiable.util.toLatex(v) +
          """}{""" +
          semanticContentToLatex(q) +
          """}"""
    }
}
