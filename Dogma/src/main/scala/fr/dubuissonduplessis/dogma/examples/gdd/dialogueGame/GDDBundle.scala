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
package fr.dubuissonduplessis.dogma.examples.gdd.dialogueGame

import fr.dubuissonduplessis.dogma.examples.gdd.dialogueGame.propositional.PropositionalGoal
import fr.dubuissonduplessis.dogma.examples.gdd.dialogueGame.action.ActionGoal
import fr.dubuissonduplessis.dogma.examples.gdd.dialogueGame.propositional.QuestionGoal
import fr.dubuissonduplessis.dogma.examples.gdd.dialogueGame.action.OfferGame
import fr.dubuissonduplessis.dogma.examples.gdd.dialogueGame.action.RequestGame
import fr.dubuissonduplessis.dogma.examples.gdd.dialogueGame.action.SuggestionGame
import fr.dubuissonduplessis.dogma.examples.gdd.dialogueGame.propositional.OpenInterrogationGame
import fr.dubuissonduplessis.dogma.examples.gdd.dialogueGame.propositional.ChoiceGame
import fr.dubuissonduplessis.dogma.examples.gdd.dialogueGame.propositional.VerificationGame
import fr.dubuissonduplessis.dogma.examples.gdd.dialogueGame.propositional.YNInterrogationGame
import fr.dubuissonduplessis.dogma.examples.gdd.dialogueGame.propositional.NegativeVerificationGame
import fr.dubuissonduplessis.dogma.examples.gdd.dialogueGame.propositional.AbstractVerificationGame

/**
 * Dialogue game module that aggregates all the dialogue games defined in this project.
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait GDDBundle
  // Semantic Requirements
  extends ActionGoal with PropositionalGoal with QuestionGoal
  // Action Dialogue Games
  with OfferGame
  with RequestGame
  with SuggestionGame
  // Question Game
  with OpenInterrogationGame
  with ChoiceGame
  with AbstractVerificationGame
  with VerificationGame
  with NegativeVerificationGame
  with YNInterrogationGame {
  this: DialogueGameModule =>
}
