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
package fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore

import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.CommitmentStoreQueries
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.CommitmentStoreOperationsRequirements
import fr.dubuissonduplessis.dogma.game.factory.GameRepositories
import fr.dubuissonduplessis.dogma.gameInstance.GameInstances
import fr.dubuissonduplessis.dogma.commitment.Commitments
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.CommitmentStore
import fr.dubuissonduplessis.dogma.game.CommunicationGames
import fr.dubuissonduplessis.dogma.game.DialogueGames
import fr.dubuissonduplessis.dogma.dialogue.Dialogue
import fr.dubuissonduplessis.dogma.gameInstance.DialogueGameInstances

/**
 * Provides modules for dealing with a mutable game manager.
 *
 * The main class of this package is [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.GameManager GameManager]].
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
package object gameManager {
  /**
   * Module requirements for [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.GameManager]] as a type.
   */
  type GameManagerRequirements = Dialogue with GameInstances with DialogueGameInstances with DialogueGames with CommunicationGames
  /**
   * Module requirements for [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.GameManagerOperations]] as a type.
   */
  type GameManagerOperationsRequirements = CommitmentStoreOperationsRequirements with CommitmentStoreQueries with GameManager with GameManagerQueries with GameInstances with DialogueGameInstances with DialogueGames with CommunicationGames with GameRepositories
}
