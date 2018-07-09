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
package fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit

import fr.dubuissonduplessis.dogma.dialogue.Dialogue
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
import scala.collection.immutable.SortedMap
import fr.dubuissonduplessis.dogma.commitment.Commitments
import fr.dubuissonduplessis.dogma.gameInstance.GameInstances

/**
 * Module providing a default printing method for a commitment store.
 *
 * The main method of this module is:
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.PrintableCommitmentStore#commitmentStoreToString commitmentStoreToString]].
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait PrintableCommitmentStore {
  this: Dialogue with CommitmentStore with CommitmentStoreQueries with Commitments with GameInstances =>
  // Printing methods
  /**
   * Configuration option to print the time slot of commitments.
   * @note default value is 'false'
   * @return true if the time slot should be printed, else false
   */
  protected var showTime: Boolean = false
  private def mkSimpleString(c: AnyCommitment): String =
    c.content.toString + (if (showTime) " |time=" + c.t + "|" else "")

  private def mkStringCommitmentsFor(x: Interlocutor): String = {
    def mkStringDialogicalActionCommitmentsFor(x: Interlocutor): String = {
      val m =
        this.gameActionCommitments // We only consider dialogical commitments
          .filter(_.debtor == x) // We filter the interlocutor
          .filter(_.isActive) // We only select active commitments
          .groupBy(_.game)
          .mapValues(commitmentSet =>
            commitmentSet.toList.sorted(CommitmentOrdering.reverse) // We order commitments by descending creation time
              .map(c => mkSimpleString(c)) // We print them
              .mkString("{", ", ", "}"))

      m.keySet // We retrieve the set of game instances
        .toList.sorted(gameInstanceOrdering.reverse) // we turn the set into a list
        .map(gameInstanceWithCommitment => // We print each line gameInstance/list of commitments
          gameInstanceWithCommitment.id.id + ": " + m(gameInstanceWithCommitment))
        .mkString("\n\t\t\t")
    }

    def mkStringExtraDialogicalActionCommitmentsFor(x: Interlocutor): String =
      this.extraDialogicalActionCommitments // We only consider extra-dialogical commitments
        .filter(_.debtor == x) // We filter the interlocutor
        .filter(_.isActive) // We only select active commitments
        .toList.sorted(CommitmentOrdering.reverse) // We order them by descending creation time
        .map(c => mkSimpleString(c)) // We print them
        .mkString("{", ", ", "}")

    def propositionalCommitmentStoreToString: String =
      SortedMap(this.propositionalCommitments
        .filter(_.debtor == x) // We filter propositional commitments for the right interlocutor
        .toList.groupBy(_.state).toSeq: _*) // We group commitments by state
        .map(stateCommitments => "\n\t\t" + stateCommitments._1 + ": " +
          stateCommitments._2.sorted(CommitmentOrdering.reverse)
          .map(c => mkSimpleString(c))
          .mkString("{", ",", "}")) // We print the state and all commitments in this state
        .mkString
    "\n\tProposition=" +
      propositionalCommitmentStoreToString +
      "\n\t" +
      "Action=" +
      "\n\t\tDialogical Action Commitment=\n\t\t\t" +
      mkStringDialogicalActionCommitmentsFor(x) +
      "\n\t\tExtra-Dialogical Commitments=\n\t\t\t" +
      mkStringExtraDialogicalActionCommitmentsFor(x)
  }

  /**
   * Computes a String representation of the commitment store.
   */
  protected def commitmentStoreToString: String =
    (for (x <- this.interlocutors)
      yield ("C" + x + "=" + mkStringCommitmentsFor(x) + "\n")).mkString("\n")
}
