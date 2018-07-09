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
package fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.contextualisation.impl

import fr.dubuissonduplessis.dogma.game.language.EmbeddedGameProposition
import fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.contextualisation
import fr.dubuissonduplessis.dogma.conversationalGameBoard.ConversationalGameBoard
import fr.dubuissonduplessis.dogma.game.language.PreSequenceGameProposition
import fr.dubuissonduplessis.dogma.game.language.SequenceGameProposition

/**
 * Implementation of module [[fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.contextualisation.ExplicitReferee contextualisation.ExplicitReferee]]
 * where all combinations are valid.
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait ExplicitRefereeWithAllCombinationAllowed extends contextualisation.ExplicitReferee {
  this: ConversationalGameBoard =>
  protected def validEmbedding(da: EmbeddedGameProposition): Boolean =
    true
  protected def validPreSequence(da: PreSequenceGameProposition): Boolean =
    true
  protected def validSequence(da: SequenceGameProposition): Boolean =
    true
}
