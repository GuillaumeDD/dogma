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
package fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.impl

import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.CommitmentStore
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.CommitmentStoreRequirements
import fr.dubuissonduplessis.dogma.dialogue.Dialogue
import fr.dubuissonduplessis.dogma.commitment.Commitments
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
import scala.collection.mutable

/**
 * Partial module implementation of access, addition and remove operations of a
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.CommitmentStore commitment store module]].
 * Note that it does not implement priority between commitments that may depend on (dialogue or communication) games.
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait CommitmentStoreLike extends CommitmentStore {
  self: Dialogue with CommitmentStoreRequirements =>

  private case class SimpleCommitmentStore(
    propositionalCommitments: Set[PropositionalCommitment] = Set(),
    extraDialogicalActionCommitments: Set[ActionCommitment] = Set(),
    gameActionCommitments: Set[GameActionCommitment] = Set()) {

    def clear(): SimpleCommitmentStore =
      update(List())

    def update(
      propositionalCommitments: Set[PropositionalCommitment] = this.propositionalCommitments,
      nonGameActionCommitments: Set[ActionCommitment] = this.extraDialogicalActionCommitments,
      gameActionCommitments: Set[GameActionCommitment] = this.gameActionCommitments): SimpleCommitmentStore =
      SimpleCommitmentStore(propositionalCommitments, nonGameActionCommitments, gameActionCommitments)

    def update(
      commitments: List[AnyCommitment]): SimpleCommitmentStore =
      update(Set(), Set(), Set()).add(commitments)

    def update(comms: Set[AnyCommitment]): SimpleCommitmentStore =
      update(Set(), Set(), Set()).add(comms.toList)

    def add(c: AnyCommitment): SimpleCommitmentStore =
      c match {
        case comm: GameActionCommitment => add(comm)
        case comm: ActionCommitment => add(comm)
        case comm: PropositionalCommitment => add(comm)
        case c => throw new Error("Unknown commitment: " + c)
      }

    def add(c: List[AnyCommitment]): SimpleCommitmentStore =
      c match {
        case List() => this
        case c :: last => this.add(c).add(last)
      }

    def add(c: ActionCommitment): SimpleCommitmentStore = {
      update(nonGameActionCommitments = this.extraDialogicalActionCommitments + c)
    }

    def add(c: PropositionalCommitment): SimpleCommitmentStore = {
      update(propositionalCommitments = this.propositionalCommitments + c)
    }

    def add(c: GameActionCommitment): SimpleCommitmentStore = {
      update(gameActionCommitments = this.gameActionCommitments + c)
    }

    def remove(c: ActionCommitment): SimpleCommitmentStore =
      update(nonGameActionCommitments = this.extraDialogicalActionCommitments - c)

    def remove(c: PropositionalCommitment): SimpleCommitmentStore =
      update(propositionalCommitments = this.propositionalCommitments - c)

    def remove(c: GameActionCommitment): SimpleCommitmentStore =
      update(gameActionCommitments = this.gameActionCommitments - c)
  }

  private val locutorCommitmentStores = mutable.Map[Interlocutor, SimpleCommitmentStore]()
  // Map initialization
  for (dp <- interlocutors) {
    locutorCommitmentStores.put(dp, SimpleCommitmentStore())
  }
  locutorCommitmentStores.withDefaultValue(SimpleCommitmentStore())

  // Access
  private[conversationalGameBoard] def extraDialogicalActionCommitments: Set[ActionCommitment] =
    (for (speaker <- interlocutors)
      yield (locutorCommitmentStores(speaker).extraDialogicalActionCommitments))
      .flatten

  private[conversationalGameBoard] def propositionalCommitments: Set[PropositionalCommitment] =
    (for (speaker <- interlocutors)
      yield (locutorCommitmentStores(speaker).propositionalCommitments))
      .flatten

  private[conversationalGameBoard] def gameActionCommitments: Set[GameActionCommitment] =
    (for (speaker <- interlocutors)
      yield (locutorCommitmentStores(speaker).gameActionCommitments))
      .flatten

  // Transformations
  private[commitmentStore] def add(c: ActionCommitment): Unit = {
    require(interlocutors.contains(c.debtor),
      "Speaker " + c.debtor + " is not part of this commitment store")
    locutorCommitmentStores.update(c.debtor,
      locutorCommitmentStores(c.debtor).add(c))
  }
  private[commitmentStore] def add(c: PropositionalCommitment): Unit = {
    require(interlocutors.contains(c.debtor),
      "Speaker " + c.debtor + " is not part of this commitment store")
    locutorCommitmentStores.update(c.debtor,
      locutorCommitmentStores(c.debtor).add(c))
  }
  private[commitmentStore] def add(c: GameActionCommitment): Unit = {
    require(interlocutors.contains(c.debtor),
      "Speaker " + c.debtor + " is not part of this commitment store")
    locutorCommitmentStores.update(c.debtor,
      locutorCommitmentStores(c.debtor).add(c))
  }

  private[commitmentStore] def remove(c: ActionCommitment): Unit = {
    require(interlocutors.contains(c.debtor),
      "Speaker " + c.debtor + " is not part of this commitment store")
    locutorCommitmentStores.update(c.debtor,
      locutorCommitmentStores(c.debtor).remove(c))
  }
  private[commitmentStore] def remove(c: PropositionalCommitment): Unit = {
    require(interlocutors.contains(c.debtor),
      "Speaker " + c.debtor + " is not part of this commitment store")
    locutorCommitmentStores.update(c.debtor,
      locutorCommitmentStores(c.debtor).remove(c))
  }
  private[commitmentStore] def remove(c: GameActionCommitment): Unit = {
    require(interlocutors.contains(c.debtor),
      "Speaker " + c.debtor + " is not part of this commitment store")
    locutorCommitmentStores.update(c.debtor,
      locutorCommitmentStores(c.debtor).remove(c))
  }
}
