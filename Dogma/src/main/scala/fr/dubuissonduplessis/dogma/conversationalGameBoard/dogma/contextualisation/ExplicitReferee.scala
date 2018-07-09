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
package fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.contextualisation

import fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma
import fr.dubuissonduplessis.dogma.conversationalGameBoard.ConversationalGameBoard
import com.typesafe.scalalogging.slf4j.LazyLogging
import fr.dubuissonduplessis.dogma.event.DialogicEvent
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.dialogueActs.propIn
import fr.dubuissonduplessis.dogma.game.language.EmbeddedGameProposition
import fr.dubuissonduplessis.dogma.game.language.PreSequenceGameProposition
import fr.dubuissonduplessis.dogma.game.language.SequenceGameProposition
import fr.dubuissonduplessis.dogma.game.language.GameProposition
import fr.dubuissonduplessis.dogma.game.language.AbstractGameProposition

/**
 * Specialization of [[fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.ExplicitReferee ExplicitReferee]]
 * to take into account the
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.ContextualisationGame contextualisation game]].
 *
 * In this implementation, a
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.dialogueActs.propIn proposition to enter a dialogue game]]
 * is always expected '''provided that the proposed combination is valid'''. Combination validities must be
 * defined in the following methods:
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.contextualisation.ExplicitReferee#validEmbedding validEmbedding]],
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.contextualisation.ExplicitReferee#validPreSequence validPreSequence]],
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.contextualisation.ExplicitReferee#validSequence validSequence]].
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 * @see Default implementation can be found in [[fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.contextualisation.impl package impl]].
 */
trait ExplicitReferee extends dogma.ExplicitReferee
  with LazyLogging {
  this: ConversationalGameBoard =>
  override protected def expectedEvent(da: DialogicEvent): Boolean =
    {
      super.expectedEvent(da) ||
        // da is expected if it is a well-formed game entry proposition
        {
          da match {
            case propIn(interlocutor, content) =>
              content.asInstanceOf[AbstractGameProposition] match {
                case prop @ EmbeddedGameProposition(g1, g2) =>
                  // Suggested embedding must be valid and the contextualisation act must be expected
                  validEmbedding(prop) && super.expectedEvent(da)
                case prop @ PreSequenceGameProposition(g1, g2) =>
                  // Suggested pre-sequence must be valid and the contextualisation act must be expected                  
                  validPreSequence(prop) && super.expectedEvent(da)
                case prop @ SequenceGameProposition(g1, g2) =>
                  // Suggested sequence must be valid and the contextualisation act must be expected                  
                  validSequence(prop) && super.expectedEvent(da)
                case GameProposition(name, goal) =>
                  // A suggested dialogue game is always valid
                  true
                case _ =>
                  false
              }
            case _ => false
          }
        }
    }

  override protected def hasPriority(da: DialogicEvent): Boolean =
    super.hasPriority(da) ||
      // da is expected if it is a well-formed game entry proposition
      {
        da match {
          case propIn(interlocutor, content) =>
            content match {
              case GameProposition(name, goal) =>
                true
              case _ =>
                super.hasPriority(da)
            }
          case _ => false
        }
      }

  /**
   * Determines if a given embedding is valid.
   * @return true if the given embedding is valid, else false
   */
  protected def validEmbedding(da: EmbeddedGameProposition): Boolean
  /**
   * Determines if a given pre-sequence is valid.
   * @return true if the given pre-sequence is valid, else false
   */
  protected def validPreSequence(da: PreSequenceGameProposition): Boolean

  /**
   * Determines if a given sequence is valid.
   * @return true if the given sequence is valid, else false
   */
  protected def validSequence(da: SequenceGameProposition): Boolean
}
