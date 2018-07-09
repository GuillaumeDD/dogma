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

import fr.dubuissonduplessis.dogma.commitment.Commitments
import fr.dubuissonduplessis.dogma.description.Description
import fr.dubuissonduplessis.dogma.event.Event

/**
 * Mutable commitment store.
 *
 * A commitment store is a partially ordered set of commitments. This module provides the basic operations
 * of a commitment store: access, addition, removal and prioritarization of commitments. Higher level operations
 * are defined by modules [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.CommitmentStoreQueries CommitmentStoreQueries]]
 * and [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.CommitmentStoreOperations CommitmentStoreOperations]].
 *
 * @see Commitments: [[fr.dubuissonduplessis.dogma.commitment.Commitments]]
 * @see Partial module implementation of access, addition and remove operations: [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.impl.CommitmentStoreLike]]
 * @see Partial module implementation of priority operations: [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.impl.CommitmentStorePriorities]]
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait CommitmentStore {
  self: CommitmentStoreRequirements =>
  // Executor
  // Implemented in CommitmentStoreOperations
  /**
   * Transforms the commitment store by the occurrence of a given event.
   */
  private[conversationalGameBoard] def processOnCommitmentStore(e: Event): Unit

  // Transformations
  /**
   * Prioritizes the first commitment over the second.
   * @note the commitment store should contain the two commitments
   */
  private[commitmentStore] def prio(c1: AnyCommitment, c2: AnyCommitment): Unit

  /**
   * Adds a list of commitments.
   */
  private[commitmentStore] def add(commitments: List[AnyCommitment]): Unit =
    for (commitment <- commitments) {
      add(commitment)
    }

  /**
   * Adds a commitment.
   */
  private[commitmentStore] def add(c: AnyCommitment): Unit = c match {
    case comm: GameActionCommitment => add(comm)
    case comm: ActionCommitment => add(comm)
    case comm: PropositionalCommitment => add(comm)
    case c => throw new Error("Unknown commitment: " + c)
  }

  private[commitmentStore] def add(c: ActionCommitment): Unit
  private[commitmentStore] def add(c: PropositionalCommitment): Unit
  private[commitmentStore] def add(c: GameActionCommitment): Unit

  /**
   * Removes a commitment from the commitment store
   */
  private[commitmentStore] def remove(c: AnyCommitment): Unit = c match {
    case comm: GameActionCommitment => remove(comm)
    case comm: ActionCommitment => remove(comm)
    case comm: PropositionalCommitment => remove(comm)
    case c => throw new Error("Unknown commitment: " + c)
  }
  private[commitmentStore] def remove(c: ActionCommitment): Unit
  private[commitmentStore] def remove(c: PropositionalCommitment): Unit
  private[commitmentStore] def remove(c: GameActionCommitment): Unit

  /**
   * Substitutes the first commitment with the second one.
   * Once executed, the commitment store contains the second commitment and does not contain the
   * first one.
   * @note the commitment store should contain the first commitment
   */
  private[commitmentStore] def substitute(c1: AnyCommitment, c2: AnyCommitment): Unit =
    {
      require(commitments.contains(c1))
      remove(c1)
      add(c2)
    }

  // Access
  // TODO Check the utility of the asInstanceOf[Set[Commitment]]
  /**
   * Set of all commitments
   */
  private[conversationalGameBoard] def commitments: Set[AnyCommitment] =
    extraDialogicalActionCommitments.asInstanceOf[Set[AnyCommitment]] ++
      propositionalCommitments.asInstanceOf[Set[AnyCommitment]] ++
      gameCommitments.asInstanceOf[Set[AnyCommitment]]

  /**
   * Set of all extra-dialogical action commitments
   */
  private[conversationalGameBoard] def extraDialogicalActionCommitments: Set[ActionCommitment]

  /**
   * Set of all (extra-dialogical) propositional commitments
   */
  private[conversationalGameBoard] def propositionalCommitments: Set[PropositionalCommitment]

  /**
   * Set of all dialogicial game commitments
   */
  private[conversationalGameBoard] def gameCommitments: Set[AnyGameCommitment] =
    gameActionCommitments.map(c => c)

  /**
   * Set of all dialogical action commitments
   */
  private[conversationalGameBoard] def gameActionCommitments: Set[GameActionCommitment]

  /**
   * Determines if the first commitment has priority over the second one.
   * @note the commitment store should contain both commitments
   * @return true if the first commitment has priority over the second one, else false
   */
  private[conversationalGameBoard] def <(c1: AnyCommitment, c2: AnyCommitment): Boolean
}
