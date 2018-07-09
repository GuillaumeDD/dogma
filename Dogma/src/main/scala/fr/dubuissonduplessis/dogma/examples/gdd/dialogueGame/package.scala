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
package fr.dubuissonduplessis.dogma.examples.gdd

import fr.dubuissonduplessis.dogma.dialogue.Dialogue
import fr.dubuissonduplessis.dogma.game.CommunicationGames
import fr.dubuissonduplessis.dogma.game.factory.GameRepositories
import fr.dubuissonduplessis.dogma.gameInstance.GameInstances
import fr.dubuissonduplessis.dogma.game.DialogueGames
import fr.dubuissonduplessis.dogma.game.condition.CommitmentConditions
import fr.dubuissonduplessis.dogma.commitment.Commitments
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.CommitmentStoreOperations
import fr.dubuissonduplessis.dogma.commitment.SemanticTypes
import fr.dubuissonduplessis.dogma.gameInstance.DialogueGameInstances

/**
 * Provides dialogue game definitions as dialogue game modules.
 *
 * ==Overview==
 * Two kinds of dialogue game are defined depending on their goal which may be an 
 * [[fr.dubuissonduplessis.dogma.examples.gdd.dialogueGame.action action]] or a 
 * [[fr.dubuissonduplessis.dogma.examples.gdd.dialogueGame.propositional proposition/question]].
 * Note that the semantic constraints related to the type of the dialogue game goal are defined in special modules.
 * 
 * Dialogue games are defined in '''dialogue game modules'''.
 * A dialogue game module:
 *  - defines a dialogue game,
 *  - defines a factory for the defined dialogue games, and
 *  - registers the dialogue game factory.
 * 
 * When a dialogue game module is mixed-in, the dialogue game is automatically available and can be used in the
 * interaction.
 *         
 * This package provides a special module that aggregates all the defined dialogue games: 
 * [[fr.dubuissonduplessis.dogma.examples.gdd.dialogueGame.GDDBundle GDDBundle]].
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
package object dialogueGame {
  /**
   * Requirements for a dialogue game module expressed as a type.
   */
  type DialogueGameModule = GameRepositories with Dialogue with SemanticTypes with Commitments with CommitmentStoreOperations with DialogueGames with CommunicationGames with GameInstances with DialogueGameInstances with CommitmentConditions
}
