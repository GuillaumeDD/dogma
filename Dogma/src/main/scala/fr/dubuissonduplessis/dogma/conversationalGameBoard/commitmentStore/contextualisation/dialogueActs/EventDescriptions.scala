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
package fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.dialogueActs

import fr.dubuissonduplessis.dogma.description.EventDescription
import fr.dubuissonduplessis.dogma.instantiable.impl.InstantiableProxy
import fr.dubuissonduplessis.dogma.instantiable.Generator
import fr.dubuissonduplessis.dogma.description.Description
import fr.dubuissonduplessis.dogma.event.Event
import fr.dubuissonduplessis.dogma.instantiable.Variable
import fr.dubuissonduplessis.dogma.event.dialogueAct.DialogueAct
import fr.dubuissonduplessis.dogma.instantiable.InstantiationSet
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
import fr.dubuissonduplessis.dogma.game.language.AbstractGameProposition
import fr.dubuissonduplessis.dogma.operation.Operation
import fr.dubuissonduplessis.dogma.game.language._
import fr.dubuissonduplessis.dogma.description.dialogueAct.GenericDialogueActEventDescription

object Main extends App {
  val gameProp = GameProposition("gameName", 42)
  val x = Interlocutor("x")
  val y = Interlocutor("y")
  val V = Variable[AbstractGameProposition]("V")
  val description1 = accIn(x, V)
  val description2: EventDescription = accIn(x, gameProp)
  val description =
    description1 *| description2
  println(description)

  // Fits the dialogue act accEntree
  assert(description1.fits(accIn(x, gameProp)))
  assert(description2.fits(accIn(x, gameProp)))
  assert(description.fits(accIn(x, gameProp)))

  assert(!description1.fits(accIn(y, gameProp))) // Wrong interlocutor
  assert(!description2.fits(accIn(y, gameProp))) // Wrong interlocutor
  assert(!description.fits(accIn(y, gameProp))) // Wrong interlocutor

  assert(!description1.fits(refIn(y, gameProp))) // Wrong act, wrong interlocutor
  assert(!description2.fits(refIn(y, gameProp))) // Wrong aInong interlocutor
  assert(!description.fits(refIn(y, gameProp))) //  Wrong act, wrong interlocutor

  println(description.generate(accIn(x, gameProp)))

  println("-----")

  // PROP SORTIE
  val U = Variable[AnyGameProposition]("U")
  val descriptionA = propOut(x, U)
  val descriptionB = accOut(y, U) *| refOut(y, U)
  println(descriptionA)
  println(descriptionB)
  assert(!descriptionA.fits(accIn(x, gameProp))) // Incorrect act
  assert(!descriptionA.fits(propOut(y, gameProp))) // Incorrect interlocutor

  assert(descriptionA.fits(propOut(x, gameProp))) // Correct act
  assert(descriptionB.isInstantiableWith(descriptionA.bindings(propOut(x, gameProp)))) // Instantiation
  println(descriptionB.instantiateWith(descriptionA.bindings(propOut(x, gameProp))))
}
