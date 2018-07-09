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

import com.typesafe.scalalogging.slf4j.LazyLogging
import fr.dubuissonduplessis.dogma.commitment.Commitments
import fr.dubuissonduplessis.dogma.gameInstance.GameInstances

/**
 * Commitment store module providing high-level access operations.
 *
 * Provided operations aim at querying the [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.CommitmentStore commitment store]]
 * in order to: retrieve or test the existence of commitments.
 *
 * Operations modifying the commitment store can be found in the
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.CommitmentStoreOperations CommitmentStoreOperations module]]
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait CommitmentStoreQueries extends LazyLogging {
  this: CommitmentStore with GameInstances with Commitments =>
  // Search queries

  /**
   * Computes all game commitments of a given game instance.
   *
   * @return all game commitments associated to the given game instance
   */
  protected def findGameCommitments(game: GameInstance): Set[AnyGameCommitment] =
    gameCommitments.filter(_.game == game)

  /**
   * Computes all game action commitments of a given game instance.
   *
   * @return all game action commitments associated to the given game instance
   */
  protected def findGameActionCommitments(game: GameInstance): Set[GameActionCommitment] =
    gameActionCommitments.filter(_.game == game)

  /**
   * Retrieves all commitments satisfying a predicate.
   *
   * @return a set of commitments which satisfy the given predicate
   */
  protected def findCommitmentsWhere(predicate: AnyCommitment => Boolean): Set[AnyCommitment] =
    commitments.filter(predicate)
  /**
   * Determines if it exists a commitment satisfying a predicate.
   *
   * @return true if it exists a commitment which satisfies a predicate, else false
   */
  protected def existsCommitmentsWhere(predicate: AnyCommitment => Boolean): Boolean =
    commitments.exists(predicate)

  /**
   * Computes all extra-dialogical action commitments satisfying a predicate.
   *
   * @return a set of extra-dialogical action commitments which satisfy the given predicate.
   */
  protected def findActionCommitmentsWhere(predicate: ActionCommitment => Boolean): Set[ActionCommitment] =
    extraDialogicalActionCommitments.filter(predicate)
  /**
   * Determines if it exists an extra-dialogical action commitments satisfying a predicate.
   *
   * @return true if it exists a commitment which satisfies a predicate, else false
   */
  protected def existsActionCommitmentsWhere(predicate: ActionCommitment => Boolean): Boolean =
    extraDialogicalActionCommitments.exists(predicate)

  /**
   * Computes all dialogical action commitments satisfying a predicate.
   *
   * @return a set of dialogical action commitments which satisfy the given predicate p.
   */
  protected def findGameActionCommitmentsWhere(predicate: GameActionCommitment => Boolean): Set[GameActionCommitment] =
    gameActionCommitments.filter(predicate)
  /**
   * Determines if it exists a dialogical action commitments satisfying a predicate.
   *
   * @return true if it exists a commitment which satisfies a predicate, else false
   */
  protected def existsGameActionCommitmentsWhere(predicate: GameActionCommitment => Boolean): Boolean =
    gameActionCommitments.exists(predicate)

  /**
   * Computes all propositional commitments satisfying a given predicate.
   *
   * @return a set of propositional commitments which satisfy the given predicate p.
   */
  protected def findPropositionalCommitmentsWhere(predicate: PropositionalCommitment => Boolean): Set[PropositionalCommitment] =
    propositionalCommitments.filter(predicate)
  /**
   * Determines if it exists a propositional commitments satisfying a predicate.
   *
   * @return true if it exists a commitment which satisfies a predicate, else false
   */
  protected def existsPropositionalCommitmentsWhere(predicate: PropositionalCommitment => Boolean): Boolean =
    propositionalCommitments.exists(predicate)

  /**
   * Support methods for the commitment store.
   * It tells if the commitment store contains a given commitment or not.
   * @param c commitment which presence is tested in the commitment store.
   * @return true if the commitment store contains the commitment, else false.
   */
  protected def |=(c: AnyCommitment): Boolean =
    commitments.contains(c)

}
