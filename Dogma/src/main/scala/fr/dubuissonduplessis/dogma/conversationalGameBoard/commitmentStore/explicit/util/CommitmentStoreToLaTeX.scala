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
package fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.util

import fr.dubuissonduplessis.dogma.commitment.util.CommitmentsToLaTeX
import fr.dubuissonduplessis.dogma.commitment.Commitments
import fr.dubuissonduplessis.dogma.commitment.CommitmentsRequirements
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.CommitmentStoreRequirements
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.CommitmentStore
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
import fr.dubuissonduplessis.dogma.util.PredefLaTeX

/**
 * Module providing utilities to compute the LaTeX representation of a commitment store.
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait CommitmentStoreToLaTeX extends CommitmentsToLaTeX {
  this: CommitmentStore with CommitmentStoreRequirements with Commitments with CommitmentsRequirements =>

  import PredefLaTeX._

  protected def propCommitmentsToLatex(x: Interlocutor, y: Interlocutor): String =
    """
    	\detailEngagementProp{
    """ +
      propositionalCommitmentStoreToString(x, CommitmentState.Crt) +
      """
    	}{	
    """ +
      propositionalCommitmentStoreToString(y, CommitmentState.Crt) +
      """
    	}	
    """

  protected def actionCommitmentsToLatex(x: Interlocutor, y: Interlocutor): String =
    """
    	\detailEngagementAction{
    """ +
      extraDialogicalActionCommitmentStoreToString(x, CommitmentState.Crt) +
      """	
    	}{	
    """ +
      extraDialogicalActionCommitmentStoreToString(y, CommitmentState.Crt) +
      """
    	}{	
    """ +
      extraDialogicalActionCommitmentStoreToString(x, CommitmentState.Fal) +
      """
    	}{	
    """ +
      extraDialogicalActionCommitmentStoreToString(y, CommitmentState.Fal) +
      """
    	}
    """

  protected def dialogicActionCommitmentsToLatex(x: Interlocutor, y: Interlocutor): String =
    """
    	\detailEngagementDialogicAction{
    """ +
      dialogicalActionCommitmentStoreToString(x, CommitmentState.Crt) +
      """
    	}{	
    """ +
      dialogicalActionCommitmentStoreToString(y, CommitmentState.Crt) +
      """
    	}	
    """

  protected def commitmentStoreToLatex(locutor: Interlocutor): String =
    """
    % Engagements de """ +
      locutor +
      """
    \engagementDetailBis{ 
      % Engagement propositionnel Crt
      """ +
      propositionalCommitmentStoreToString(locutor, CommitmentState.Crt) +
      """
    }{ 
      % Engagement propositionnel Cnl
      """ +
      propositionalCommitmentStoreToString(locutor, CommitmentState.Cnl) +
      """
    }{ 
      % Engagement en action dialogique Crt
      """ +
      dialogicalActionCommitmentStoreToString(locutor, CommitmentState.Crt) +
      """
    }{ 
      % Engagement en action extra-dialogique Crt
      """ +
      extraDialogicalActionCommitmentStoreToString(locutor, CommitmentState.Crt) +
      """
    }{ 
      % Engagement en action extra-dialogique Fal    
      """ +
      extraDialogicalActionCommitmentStoreToString(locutor, CommitmentState.Fal) +
      """
    }
    """

  private def propositionalCommitmentToLatex(c: PropositionalCommitment): String =
    propToLatex(c.content)

  private def dialogicalActionCommitmentToLatex(c: GameActionCommitment): String =
    """\engagement{""" + c.game.id + """}{""" + descriptionToLatex(c.content) + """}"""

  private def extraDialogicalActionCommitmentToLatex(c: ActionCommitment): String =
    actionToLatex(c.content)

  import CommitmentState._
  private def propositionalCommitmentStoreToString(locutor: Interlocutor, state: CommitmentState): String = {
    val (newCommitments, oldCommitments) =
      this.propositionalCommitments
        .filter(_.debtor == locutor) // We filter propositional commitments for the right interlocutor
        .filter(_.state == state) // We filter commitments by state
        .toList
        .sorted(CommitmentOrdering.reverse) // We sort the list by descending creation time
        .partition(isNew(_))

    val latexCommitments =
      // We wrap new commitment in a special LaTeX macros
      newCommitments.map(c => newWrapping(propositionalCommitmentToLatex(c))) ++
        // We turn each commitment into its latex representation
        oldCommitments.map(propositionalCommitmentToLatex(_))
    if (latexCommitments.isEmpty) {
      emptySet
    } else {
      toLatexSet(latexCommitments, "")
    }
  }

  private def dialogicalActionCommitmentStoreToString(locutor: Interlocutor, state: CommitmentState): String = {
    val (newCommitments, oldCommitments) =
      this.gameActionCommitments // We only consider dialogical commitments
        .filter(_.game.isDialogueGame) // We only consider dialogue games
        .filter(_.debtor == locutor) // We filter for the right interlocutor
        .filter(_.state == state) // We filter commitments by state
        .toList
        .sorted(CommitmentOrdering.reverse) // We sort the list by descending creation time
        .partition(isNew(_))

    val latexCommitments =
      // We wrap new commitment in a special LaTeX macros
      newCommitments.map(c => newWrapping(dialogicalActionCommitmentToLatex(c))) ++
        // We turn each commitment into its latex representation
        oldCommitments.map(dialogicalActionCommitmentToLatex(_))

    if (latexCommitments.isEmpty) {
      emptySet
    } else {
      toLatexSet(latexCommitments, "\\\\\n")
    }
  }

  private def extraDialogicalActionCommitmentStoreToString(locutor: Interlocutor, state: CommitmentState): String = {
    val (newCommitments, oldCommitments) =
      this.extraDialogicalActionCommitments // We only consider extra-dialogical commitments
        .filter(_.debtor == locutor) // We filter for the right interlocutor
        .filter(_.state == state) // We filter commitments by state
        .toList
        .sorted(CommitmentOrdering.reverse) // We sort the list by descending creation time
        .partition(isNew(_))

    val latexCommitments =
      // We wrap new commitment in a special LaTeX macros
      newCommitments.map(c => newWrapping(extraDialogicalActionCommitmentToLatex(c))) ++
        // We turn each commitment into its latex representation
        oldCommitments.map(extraDialogicalActionCommitmentToLatex(_))

    if (latexCommitments.isEmpty) {
      emptySet
    } else {
      toLatexSet(latexCommitments, "")
    }
  }
}
