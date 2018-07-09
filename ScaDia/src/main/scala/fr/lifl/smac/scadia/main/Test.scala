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
package fr.lifl.smac.scadia.main

import fr.lifl.smac.scadia.Environment
import fr.lifl.smac.scadia.dialogueManager.reasoning.openInterrogation.strategy.impl.TrivialInterpretationAnswer
import fr.lifl.smac.scadia.dialogueManager.reasoning.openInterrogation.strategy.impl.CredulousEvaluation
import fr.lifl.smac.scadia.dialogueManager.reasoning.openInterrogation.strategy.impl.IntrovertInterpretationQuestion
import fr.lifl.smac.scadia.dialogueManager.reasoning.openInterrogation.strategy.impl.Lyer
import fr.lifl.smac.scadia.dialogueManager.reasoning.openInterrogation.strategy.impl.SkepticalEvaluation
import fr.lifl.smac.scadia.dialogueManager.reasoning.openInterrogation.strategy.impl.SocialInterpretationQuestion
import fr.lifl.smac.scadia.dialogueManager.reasoning.openInterrogation.strategy.impl.Honest
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.dialogueActs.propIn
import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.dialogueActs.accIn
import fr.dubuissonduplessis.dogma.game.language.GameProposition
import scala.io.StdIn._

object Test extends App with Environment {
  // User input to select the scenario
  var continue = true
  var first = true
  var value = 1
  do {
    if (!first) {
      println("Please, enter 1, 2 or 3.")
    }
    println("Choices:")
    println("1) Bob asks John")
    println("2) John asks Bob")
    println("3) John asks Bob AND Bob asks John")
    try {
      first = false
      value = readInt()
      value match {
        case 1 | 2 | 3 =>
          continue = false
        case _ =>
          continue = true
      }
      continue = false
    } catch {
      case _: Throwable =>
    }
  } while (continue)

  // Agent initialisation
  val (bob, john) = value match {
    case 1 =>
      // Agents definition: bob and john
      val bob1 = new AsciiDialogicalAgent("Bob", "John") // Initiator Reaction
      with TrivialInterpretationAnswer with CredulousEvaluation // Partner Reaction
      with Lyer with IntrovertInterpretationQuestion {
        def init(env: Environment): Unit = {
          sendAll(env, List(propIn(me, GameProposition("OpenInterrogation", 'A'))))
        }
      }

      val john1 = new AsciiDialogicalAgent("John", "Bob") // Initiator Reaction
      with TrivialInterpretationAnswer with SkepticalEvaluation // Partner Reaction
      with Honest with SocialInterpretationQuestion {
        def init(env: Environment): Unit = {}
      }

      (bob1, john1)
    case 2 =>
      // Agents definition: bob and john
      val bob2 = new AsciiDialogicalAgent("Bob", "John") // Initiator Reaction
      with TrivialInterpretationAnswer with CredulousEvaluation // Partner Reaction
      with Lyer with IntrovertInterpretationQuestion {
        def init(env: Environment): Unit = {}
      }

      val john2 = new AsciiDialogicalAgent("John", "Bob") // Initiator Reaction
      with TrivialInterpretationAnswer with SkepticalEvaluation // Partner Reaction
      with Honest with SocialInterpretationQuestion {
        def init(env: Environment): Unit = {
          sendAll(env, List(propIn(me, GameProposition("OpenInterrogation", 'B'))))
        }
      }

      (bob2, john2)

    case 3 =>
      // Agents definition: bob and john
      val bob2 = new AsciiDialogicalAgent("Bob", "John") // Initiator Reaction
      with TrivialInterpretationAnswer with CredulousEvaluation // Partner Reaction
      with Lyer with IntrovertInterpretationQuestion {
        def init(env: Environment): Unit = {
          sendAll(env, List(propIn(me, GameProposition("OpenInterrogation", 'A'))))
        }
      }

      val john2 = new AsciiDialogicalAgent("John", "Bob") // Initiator Reaction
      with TrivialInterpretationAnswer with SkepticalEvaluation // Partner Reaction
      with Honest with SocialInterpretationQuestion {
        def init(env: Environment): Unit = {
          sendAll(env, List(propIn(me, GameProposition("OpenInterrogation", 'B'))))
        }
      }

      (bob2, john2)
    case _ => ???
  }

  // Subscription
  subscribe(bob)
  subscribe(john)

  // Init
  init()

  // Run
  run()
}
