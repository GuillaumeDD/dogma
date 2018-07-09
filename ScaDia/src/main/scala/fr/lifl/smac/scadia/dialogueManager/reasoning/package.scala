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
package fr.lifl.smac.scadia.dialogueManager

import fr.dubuissonduplessis.dogma.gameInstance.DialogueGameInstances
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.GameManagerOperations
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.GameManager
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.CommitmentStoreQueries
import fr.dubuissonduplessis.dogma.gameInstance.GameInstances
import fr.dubuissonduplessis.dogma.commitment.Commitments
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.CommitmentStore
import fr.dubuissonduplessis.dogma.game.DialogueGames
import fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.ExplicitDogma
import fr.lifl.smac.scadia.DialogicalAgent

package object reasoning {
  /**
   * Requirements (expressed as a self-type) for a PatternReasoner
   */
  protected[reasoning]type PatternReasonerSelfType = DialogueManager with ExplicitDogma with DialogueGames with GameInstances with DialogueGameInstances with Commitments with CommitmentStore with CommitmentStoreQueries with GameManager with GameManagerOperations
  /**
   * Represents something that is a pattern reasoner
   */
  type PatternReasonerThing = PatternReasoner with PatternReasonerSelfType
  /**
   * Represents a dialogical agent able to reason over patterns
   */
  type PatternReasonerDialogicalAgent = DialogicalAgent with PatternReasonerThing
}
