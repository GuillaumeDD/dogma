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

import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.GameManager
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.CommitmentStoreQueries
import fr.dubuissonduplessis.dogma.game.factory.GameRepositories
import fr.dubuissonduplessis.dogma.commitment.Commitments
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.CommitmentStore
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.GameManagerQueries
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.GameManagerOperations
import fr.dubuissonduplessis.dogma.game.CommunicationGames
import fr.dubuissonduplessis.dogma.game.DialogueGames
import fr.dubuissonduplessis.dogma.dialogue.Dialogue
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.CommitmentStoreOperations
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.internalEvent.InternalEventGenerator
import fr.dubuissonduplessis.dogma.game.condition.CommitmentConditions
import fr.dubuissonduplessis.dogma.commitment.SemanticTypes
import fr.dubuissonduplessis.dogma.gameInstance.DialogueGameInstances
import fr.dubuissonduplessis.dogma.gameInstance.GameInstances

/**
 * An abstract mutable commitment store with explicit commitments and a game manager.
 * It contains for each dialogue participant the propositional commitments (extra-dialogic) and the
 * action commitments (dialogic and extra-dialogic) he owes. It includes the set of dialogue games
 * in different contextualisation state on which dialogue participants are committed.
 *
 * The architecture of this commitment store can be broken down into 3 main parts:
 * the commitment-related part, the game-related part and the internal event-related
 * part.
 *
 * == Commitment-related part==
 * The commitment-related part is based on the definition of commitments that can
 * be found in the [[fr.dubuissonduplessis.dogma.commitment.Commitments Commitments module]].
 * It provides a [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.CommitmentStore CommitmentStore]]
 * for explicit commitments.
 *
 * == Game-related part==
 * The game-related part deals with communication games (defined in the
 * [[fr.dubuissonduplessis.dogma.game.CommunicationGames CommunicationGames module]])
 * and with dialogue games (defined in the
 * [[fr.dubuissonduplessis.dogma.game.DialogueGames DialogueGames module]]). These games
 * are made available through the [[fr.dubuissonduplessis.dogma.game.factory.GameRepositories GameRepositories module]].
 * The main module related to games of this package is the
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.GameManager GameManager module]].
 *
 * ==Internal event-related part==
 * The internal event-related part deals with the generation of [[fr.dubuissonduplessis.dogma.event.InternalEvent InternalEvent]].
 * The main module to this purpose is the
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.internalEvent.InternalEventGenerator InternalEventGenerator]].
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 * @see [[fr.dubuissonduplessis.dogma.conversationalGameBoard.ConversationalGameBoard ConversationalGameBoard]] implements
 * a conversational game board based on this ExplicitCommitmentStore.
 */
trait ExplicitCommitmentStore extends Dialogue
  with SemanticTypes
  with GameInstances
  with DialogueGameInstances
  with Commitments
  with DialogueGames
  with CommunicationGames
  with GameRepositories
  with CommitmentStore
  with CommitmentStoreOperations
  with CommitmentStoreQueries
  with GameManager
  with GameManagerOperations
  with GameManagerQueries
  with InternalEventGenerator
  with CommitmentConditions
