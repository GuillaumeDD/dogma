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

import scala.collection.mutable
import com.typesafe.scalalogging.slf4j.LazyLogging
import fr.dubuissonduplessis.dogma.commitment.Commitments
import fr.dubuissonduplessis.dogma.dialogue.Dialogue
import fr.dubuissonduplessis.dogma.event.Event
import fr.dubuissonduplessis.dogma.instantiable.Instantiable
import fr.dubuissonduplessis.dogma.instantiable.impl.InstantiableProxy
import fr.dubuissonduplessis.dogma.operation.Operation
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor

/**
 * Module providing high-level operations on commitments of a commitment store.
 *
 * High-level operations include:
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.CommitmentStoreOperations#Create creation]],
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.CommitmentStoreOperations#Cancel cancellation]],
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.CommitmentStoreOperations#Remove removal]],
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.CommitmentStoreOperations#Fulfill fulfillment]],
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.CommitmentStoreOperations#Violate violation]], and
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.CommitmentStoreOperations#Failure failure]].
 *
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait CommitmentStoreOperations extends LazyLogging {
  self: CommitmentStoreOperationsRequirements =>

  private[conversationalGameBoard] def processOnCommitmentStore(e: Event): Unit = {
    val operations = mutable.ListBuffer[CommitmentStoreOperation]()
    for (commitment <- this.commitments) {
      if (commitment.isConcernedBy(e)) {
        // Instantiation process
        val instC = commitment.generate(e)
        if (commitment.persistent) {
          add(instC)
        } else {
          substitute(commitment, instC)
        }

        if (instC.isViolatedBy(e)) {
          operations += Violate(instC)
        } else {
          if (!instC.persistent) {
            operations += Fulfill(instC)
          }

          instC.operationToExecute(e) match {
            case Some(op) =>
              op match {
                case correctOperation: CommitmentStoreOperation =>
                  operations += correctOperation
                case _ =>
                  logger.warn(s"Operation $op has not been applied on the commitment store.")
              }
            case None =>
          }
        }
      }
    }

    // We apply collected operations
    for (op <- operations.reverse) {
      op()
    }
  }

  /**
   * Abstract commitment store operation.
   *
   * A commitment store operation is a side-effect procedure modifying a mutable commitment store.
   * Its body is defined in its apply method.
   *
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected abstract class CommitmentStoreOperation extends Operation
    with Instantiable[CommitmentStoreOperation] {
    /**
     * Body of a commitment store operation.
     */
    def apply(): Unit
  }

  // Standard Operations
  // CANCEL
  /**
   * Cancels a commitment of the commitment store.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   * @constructor create a new 'Cancel' operation from a dialogue participant and a commitment
   * @param creator dialogue participant responsible for this operation
   * @param commitment commitment that is being cancelled
   */
  protected case class Cancel(
    creator: Interlocutor,
    commitment: AnyCommitment)
    extends CommitmentStoreOperation
    with InstantiableProxy[Cancel, AnyCommitment]
    with LazyLogging {

    def apply() = {
      logger.info(s"Cancelling commitment: $commitment")
      remove(commitment) //.add(commitment.cancelled.creationTime(Time.currentTime))
    }

    // Instantiation
    def instantiable: AnyCommitment = commitment
    def update(c: AnyCommitment): Cancel =
      Cancel(this.creator, c)

    override def toString: String = "cancel(" + creator + ", " + commitment + ")"
  }

  // CREATE
  /**
   * Creates a commitment in a commitment store.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   * @constructor create a new 'Create' operation from a dialogue participant and a commitment
   * @param creator dialogue participant responsible for this operation
   * @param commitment commitment that is being created
   */
  protected case class Create(
    creator: Interlocutor,
    commitment: AnyCommitment)
    extends CommitmentStoreOperation
    with InstantiableProxy[Create, AnyCommitment]
    with LazyLogging {

    def apply() = {
      logger.info(s"Adding a commitment: $commitment")
      if (!commitment.instantiated)
        logger.warn(s"Adding an uninstantiated commitment ! ($commitment)")

      add(commitment.updateTime(currentTime))
    }
    // Instantiation
    def instantiable: AnyCommitment = commitment
    def update(c: AnyCommitment): Create =
      Create(creator, c)

    override def toString: String = "create(" + creator + ", " + commitment.inactive + ")"
  }

  /**
   * Factory method for the [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.CommitmentStoreOperations#Create 'Create' operation]].
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected object Create {
    /**
     * Generates a list of [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.CommitmentStoreOperations#Create 'Create' operations]]
     * from a set of commitments.
     *
     * @param creator dialogue participant responsible for the creation of those commitments
     *
     */
    def wrap(commitments: Set[_ <: AnyCommitment], creator: Interlocutor): List[Create] =
      for (c <- commitments.toList) yield (Create(creator, c))
  }

  // FAILURE
  /**
   * Fails a commitment creation in the commitment store.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   * @constructor create a new 'Failure' operation from a dialogue participant and a commitment
   * @param creator dialogue participant responsible for this operation
   * @param commitment commitment that is being failed
   */
  protected case class Failure(
    creator: Interlocutor,
    commitment: AnyCommitment)
    extends CommitmentStoreOperation
    with InstantiableProxy[Failure, AnyCommitment]
    with LazyLogging {

    def apply() = {
      val newCommitment = commitment.failed.updateTime(currentTime)
      logger.info(s"Failure to jointly create commitment: $newCommitment")
      if (!commitment.instantiated)
        logger.warn(s"Failure : adding an uninstantiated commitment ! ($newCommitment)")

      remove(commitment)
      add(newCommitment)
    }
    // Instantiation
    def instantiable: AnyCommitment = commitment
    def update(c: AnyCommitment): Failure =
      Failure(creator, c)

    override def toString: String = "failure(" + creator + ", " + commitment.inactive + ")"
  }
  /**
   * Factory method for the [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.CommitmentStoreOperations#Failure 'Failure' operation]].
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   *
   */
  protected object Failure {
    /**
     * Generates a list of [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.CommitmentStoreOperations#Failure 'Failure' operations]]
     * from a set of commitments.
     *
     * @param creator dialogue participant responsible for the failure of those commitments
     *
     */
    def wrap(commitments: Set[_ <: AnyCommitment], creator: Interlocutor): List[Failure] =
      for (c <- commitments.toList) yield (Failure(creator, c))
  }

  // FULFILL
  /**
   * Fulfills a commitment of the commitment store.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   * @constructor create a new 'Fulfill' operation from a commitment
   * @param commitment commitment that is being fulfilled
   */
  protected case class Fulfill(
    commitment: AnyCommitment)
    extends CommitmentStoreOperation
    with InstantiableProxy[Fulfill, AnyCommitment]
    with LazyLogging {

    def apply() = {
      require(commitment.isActive)
      logger.info(s"Fulfilling commitment: $commitment")
      remove(commitment)
      add(commitment.fulfilled.updateTime(currentTime))
    }

    // Instantiation
    def instantiable: AnyCommitment = commitment
    def update(c: AnyCommitment): Fulfill =
      Fulfill(c)

    override def toString: String = "fulfill(" + commitment + ")"
  }

  /**
   * Removes a commitment in a commitment store.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   * @constructor create a new 'Remove' operation from a dialogue participant and a commitment
   * @param creator dialogue participant responsible for this operation
   * @param commitment commitment that is being removed
   */
  protected case class Remove(
    creator: Interlocutor,
    commitment: AnyCommitment)
    extends CommitmentStoreOperation
    with InstantiableProxy[Remove, AnyCommitment]
    with LazyLogging {

    def apply() = {
      logger.info(s"Removing commitment: $commitment")
      remove(commitment)
    }

    // Instantiation
    def instantiable: AnyCommitment = commitment
    def update(c: AnyCommitment): Remove =
      Remove(creator, c)

    override def toString: String = "remove(" + creator + ", " + commitment + ")"
  }

  /**
   * Violates a commitment of the commitment store.
   * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
   * @constructor create a new 'Violate' operation from a commitment
   * @param commitment commitment that is being violated
   */
  protected case class Violate(
    commitment: AnyCommitment)
    extends CommitmentStoreOperation
    with InstantiableProxy[Violate, AnyCommitment]
    with LazyLogging {

    def apply() = {
      require(commitment.isActive)
      logger.info(s"Violating commitment: $commitment")
      remove(commitment)
      add(commitment.violated.updateTime(currentTime))
    }

    // Instantiation
    def instantiable: AnyCommitment = commitment
    def update(c: AnyCommitment): Violate =
      Violate(c)

    override def toString: String = "violate(" + commitment + ")"
  }
}
