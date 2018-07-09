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
package fr.dubuissonduplessis.dogma.commitment.util

import fr.dubuissonduplessis.dogma.commitment.CommitmentsRequirements
import fr.dubuissonduplessis.dogma.commitment.Commitments
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
import fr.dubuissonduplessis.dogma.description.Description

/**
 * Module providing utilities to compute the LaTeX representation of a commitment.
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait CommitmentsToLaTeX extends SemanticTypesToLaTeX {
  this: Commitments with CommitmentsRequirements =>

  // LaTeX
  /**
   * Computes the LaTeX representation of a commitment.
   */
  protected def commitmentToLatex(c: AnyCommitment): String =
    c match {
      case pc: PropositionalCommitment =>
        propositionalCommitmentToLatex(pc)
      case ac: ActionCommitment =>
        actionCommitmentToLatex(ac)
      case gac: GameActionCommitment =>
        gameActionCommitmentToLatex(gac)
      case _ =>
        c.toString
    }

  import CommitmentState._

  /**
   * Determines if a commitment is new.
   * @return true if the commitment is new, else false
   */
  protected def isNew(c: AnyCommitment): Boolean =
    c.t == currentTime - 1

  // Helper methods
  private def commitmentStateToLatex(state: CommitmentState): String =
    state match {
      case Crt => """\stateCrt"""
      case Fal => """\stateFal"""
      case Ful => """\stateFul"""
      case Cnl => """\stateCnl"""
    }

  private def propositionalCommitmentToLatex(c: PropositionalCommitment): String =
    """\completeCommitment{""" + interlocutorToLatex(c.debtor) + """}{""" +
      propToLatex(c.content) +
      """}{""" + commitmentStateToLatex(c.state) + """}"""

  private def actionCommitmentToLatex(c: ActionCommitment): String =
    """\completeCommitment{""" + interlocutorToLatex(c.debtor) + """}{""" +
      actionToLatex(c.content) +
      """}{""" + commitmentStateToLatex(c.state) + """}"""

  private def gameActionCommitmentToLatex(c: GameActionCommitment): String =
    """\completeGameCommitment{""" + c.game.id + """}{""" +
      interlocutorToLatex(c.debtor) + """}{""" +
      descriptionToLatex(c.content) +
      """}{""" + commitmentStateToLatex(c.state) + """}"""

  private def interlocutorToLatex(speaker: Interlocutor): String =
    fr.dubuissonduplessis.dogma.dialogue.util.toLatex(speaker)
}
