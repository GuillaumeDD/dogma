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
package fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
import fr.dubuissonduplessis.dogma.event.Event
import fr.dubuissonduplessis.dogma.game.language.AbstractGameProposition
import fr.dubuissonduplessis.dogma.game.language.CombinationSymbol
import fr.dubuissonduplessis.dogma.description.Description
import fr.dubuissonduplessis.dogma.game.language.instantiable.CompositeGamePropositionMatcherEventDescription
import fr.dubuissonduplessis.dogma.game.language.instantiable.SemiInstantiatedVariableCombination
import fr.dubuissonduplessis.dogma.game.language.CombinationSymbol._

/**
 * Provides the contextualisation dialogue acts and their factories.
 *
 * Contextualisation dialogue acts translate the negotiation metaphor describing the entry,
 * playing and exit of a dialogue game. This package includes the following dialogue acts:
 *  - entry phase: [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.dialogueActs.propIn propIn]],
 *  [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.dialogueActs.accIn accIn]],
 *  and [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.dialogueActs.refIn refIn]]
 *  - playing phase: [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.dialogueActs.continue continue]]
 *  - exit phase: [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.dialogueActs.propOut propOut]],
 *  [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.dialogueActs.accOut accOut]],
 *  and [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.dialogueActs.refOut refOut]]
 *
 * This package provides two contextualisation dialogue act extractors from [[fr.dubuissonduplessis.dogma.event.Event Event]]
 * and from [[fr.dubuissonduplessis.dogma.description.Description Description]].
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
package object dialogueActs {
  /**
   * Extracts a contextualisation act from an event.
   * @return an optional triple consisting of the locutor, the name of the
   * contextualisation act and its semantic content
   *
   */
  def unapply(evt: Event): Option[(Interlocutor, String, AbstractGameProposition)] =
    evt match {
      case propIn(dp, gProp) =>
        Some((dp, propIn.name, gProp))
      case accIn(dp, gProp) =>
        Some((dp, accIn.name, gProp))
      case refIn(dp, gProp) =>
        Some((dp, refIn.name, gProp))
      case propOut(dp, gProp) =>
        Some((dp, propOut.name, gProp))
      case accOut(dp, gProp) =>
        Some((dp, accOut.name, gProp))
      case refOut(dp, gProp) =>
        Some((dp, refOut.name, gProp))
      case continue(dp, gProp) =>
        Some((dp, continue.name, gProp))
      case _ =>
        None
    }

  /**
   * Extracts elements from a
   * [[fr.dubuissonduplessis.dogma.game.language.instantiable.CompositeGamePropositionMatcherEventDescription CompositeGamePropositionMatcherEventDescription]]
   * representing a contextualisation dialogue acts.
   *
   * @return an optional 5-tuple containing the name of the contextualisation acts as a String, the locutor of the dialogue act,
   * the combination symbol of the abstract game proposition, and the abstract game proposition used in the combination
   */
  def unapply(desc: Description): Option[(String, Interlocutor, CombinationSymbol, AbstractGameProposition)] = {
    desc match {
      case act: CompositeGamePropositionMatcherEventDescription[_] =>
        act.content match {
          case SemiInstantiatedVariableCombination(symbol, gProp) =>
            Some((act.name, act.locutor, symbol, gProp))
          case _ => None
        }
      case _ =>
        None
    }
  }
}
