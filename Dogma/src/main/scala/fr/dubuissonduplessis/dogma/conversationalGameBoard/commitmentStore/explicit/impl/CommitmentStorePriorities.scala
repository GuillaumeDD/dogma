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

import fr.dubuissonduplessis.dogma.commitment.Commitments
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.CommitmentStore
import com.typesafe.scalalogging.slf4j.LazyLogging

/**
 * Partial module implementation of priority operations of a
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.CommitmentStore commitment store module]].
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait CommitmentStorePriorities extends CommitmentStore {
  self: Commitments =>

  private[conversationalGameBoard] def <(c1: AnyCommitment, c2: AnyCommitment): Boolean =
    priorityStore.<(c1, c2)

  private[conversationalGameBoard] def prio(c1: AnyCommitment, c2: AnyCommitment): Unit =
    {
      priorityStore = priorityStore.addPriority(Prio(c1, c2))
    }

  private var priorityStore: PriorityStore = PriorityStore()

  private case class Prio(c1: AnyCommitment, c2: AnyCommitment) {
    def <(ca: AnyCommitment, cb: AnyCommitment): Boolean =
      (ca == c1) && (cb == c2)
  }

  private case class PriorityStore private (
    priorities: Set[Prio],
    commitments: Set[AnyCommitment])
    extends LazyLogging {

    def addPriority(p: Prio): PriorityStore = {
      logger.info(s"Adding a priority: $p")
      new PriorityStore(priorities + p, commitments + p.c1 + p.c2)
    }

    def addPriorities(p: List[Prio]): PriorityStore = {
      logger.info(s"Adding priorities: ${p.mkString("\n", "\n", "")}")
      new PriorityStore(priorities ++ p, (p foldLeft commitments)((result, prio) => result + prio.c1 + prio.c2))
    }

    def removePriority(c: AnyCommitment): PriorityStore =
      new PriorityStore(priorities.filter(
        {
          case Prio(c1, c2) => (c1 != c) && (c2 != c)
        }), // we reconstruct the set of commitments
        (priorities foldLeft Set[AnyCommitment]())((result, prio) => result + prio.c1 + prio.c2))

    def <(ca: AnyCommitment, cb: AnyCommitment): Boolean = priorities.exists(_.<(ca, cb)) || <(ca, cb, commitments)

    private def <(ca: AnyCommitment, cb: AnyCommitment, partialCommitments: Set[AnyCommitment]): Boolean = {
      priorities.exists(_.<(ca, cb)) ||
        (((for {
          c <- partialCommitments
          if ((c != cb) && (c != ca))
          newCommitments = partialCommitments.filter(c => (c != ca) && (c != cb))
        } yield (
          {
            //if((<(ca, c, newCommitments) && <(c, cb, newCommitments)))
            //logger.info("<(%s, %s)=%s && <(%s, %s)=%s", ca, c, <(ca, c, newCommitments), c, cb, <(c, cb, newCommitments))
            (<(ca, c, newCommitments) && <(c, cb, newCommitments))
          })) foldLeft false)((result, test) => result || test))
    }
  }

  private object PriorityStore {
    def apply(): PriorityStore = new PriorityStore(Set(), Set())
  }
}
