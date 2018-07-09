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
package fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.impl

import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.GameManager
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.impl.CommitmentStorePriorities
import fr.dubuissonduplessis.dogma.commitment.Commitments
import fr.dubuissonduplessis.dogma.gameInstance.GameInstances
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.GameManagerQueries

/**
 * Overloads the commitment priority relations to take into account dialogue games.
 * This implementation overloads the standard priority relation implemented in
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.impl.CommitmentStorePriorities]]
 * to add a special case for game commitments: they are in priority relations if it exists a priority
 * relation between their dialogue games.
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait GameManagerPriorities extends CommitmentStorePriorities {
  this: Commitments with GameManager with GameManagerQueries with GameInstances =>

  override private[conversationalGameBoard] def <(c1: AnyCommitment, c2: AnyCommitment): Boolean =
    c1 match {
      case c1g: GameCommitment[_] =>
        c2 match {
          case c2g: GameCommitment[_] =>
            // If c1 and c2 are game commitment, we check the priority
            // between game first
            embeddedIn(c1g.game, c2g.game) || super.<(c1, c2)
          case _ =>
            // They are not game commitments, we check priority in the priority store
            super.<(c1, c2)
        }
      case _ =>
        // They are not game commitments, we check priority in the priority store
        super.<(c1, c2)
    }
}
