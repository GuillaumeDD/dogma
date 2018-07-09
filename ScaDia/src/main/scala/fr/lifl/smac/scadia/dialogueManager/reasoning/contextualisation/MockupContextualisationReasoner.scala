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
package fr.lifl.smac.scadia.dialogueManager.reasoning.contextualisation

import fr.lifl.smac.scadia.dialogueManager.reasoning.PatternReasonerThing
import fr.dubuissonduplessis.dogma.event.DialogicEvent
import fr.dubuissonduplessis.dogma.game.language.AbstractGameProposition
import fr.dubuissonduplessis.dogma.game.language.AnyGameProposition
import fr.lifl.smac.scadia.dialogueManager.reasoning.PatternReasonerDialogicalAgent

trait MockupContextualisationReasoner extends ContextualisationPatternReasoner {
  this: PatternReasonerDialogicalAgent =>

  protected class MockupPlayerContextualisation(
    instance: GameInstance) extends PlayerContextualisation(instance) {
    protected def playNothingToDo(): Option[DialogicEvent] =
      {
        println(s"$me just doesn't know what to do in the contextualisation game")
        considerDeactivation()
        None
      }

    protected def playSuccessfulEntry(gProp: AnyGameProposition): Option[DialogicEvent] =
      {
        println(s"$me accepts to play the dialogue game since the entry conditions are met")
        Some(generateAccIn(gProp))
      }

    protected def playFailureEntry(gProp: AnyGameProposition): Option[DialogicEvent] =
      {
        println(s"$me refuses to play the dialogue game since the entry conditions are NOT met")
        Some(generateRefIn(gProp))
      }

    protected def playEmbedding(parentGProp: AbstractGameProposition): Option[DialogicEvent] =
      {
        println(s"$me could embbed a dialogue game but does nothing")
        None
      }

    protected def playExitConditionsReached(gProp: AnyGameProposition): Option[DialogicEvent] =
      {
        println(s"$me proposes to stop the dialogue game since exit conditions are met")
        Some(generatePropOut(gProp))
      }

    protected def playSuccessExitConditionsReached(gProp: AnyGameProposition): Option[DialogicEvent] =
      {
        println(s"$me accepts to stop the dialogue game since exit conditions are met")
        Some(generateAccOut(gProp))
      }

    protected def playFailureExitConditionsReached(gProp: AnyGameProposition): Option[DialogicEvent] =
      {
        println(s"$me refuses to stop the dialogue game since exit conditions are NOT met")
        Some(generateRefOut(gProp))
      }
  }
}
