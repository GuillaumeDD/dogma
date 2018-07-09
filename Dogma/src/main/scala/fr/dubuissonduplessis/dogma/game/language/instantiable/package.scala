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
package fr.dubuissonduplessis.dogma.game.language

import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
import fr.dubuissonduplessis.dogma.instantiable.Generator
import fr.dubuissonduplessis.dogma.instantiable.Instantiable
import fr.dubuissonduplessis.dogma.description.EventDescription
import fr.dubuissonduplessis.dogma.instantiable.Variable

/**
 * Provides abstract class and factories to work with instantiable dialogue game propositions as well as event descriptions.
 *
 * This package provides the definition of a variable representing an uninstantiated dialogue game proposition
 * (see [[fr.dubuissonduplessis.dogma.game.language.instantiable.GamePropositionVariable]]). Based on these variables,
 * this package implements two main concepts:
 *  - Completely uninstantiated composite dialogue game propositions (see [[fr.dubuissonduplessis.dogma.game.language.instantiable.VariableCombination]])
 *  - Semi-instantiated composite dialogue game propositions (see [[fr.dubuissonduplessis.dogma.game.language.instantiable.SemiInstantiatedVariableCombination]])
 *
 * These two concepts are illustrated by the following example:
 * {{{
 *   val v1 = GamePropositionVariable("G1")
 * val v2 = GamePropositionVariable("G2")
 *
 * // Completely uninstantiated composite dialogue game proposition
 * v1 ~> v2
 * //> res0: fr.dubuissonduplessis.dogma.instantiable.Instantiable[fr.dubuissonduplessis.dogma.game.language.instantiable.CompositeGamePropositionMatcher] =
 * // G1~>G2
 *
 * // Semi-instantiated composite dialogue game proposition
 * val gameProp = fr.dubuissonduplessis.dogma.game.language.GameProposition("interrogation", "How many roads must a man walk down?")
 * //> gameProp  : fr.dubuissonduplessis.dogma.game.language.GameProposition[String] =
 * //  interrogation(How many roads must a man walk down?)
 * v1 ~> gameProp
 * //> res1: fr.dubuissonduplessis.dogma.game.language.instantiable.CompositeGamePropositionMatcher =
 * //  G1~>interrogation(How many roads must a man walk down?)
 * }}}
 *
 *
 * This package provides default implementations for description of events using uninstantiated or semi-instantiated
 * composite dialogue game propositions:
 *  - to build event description based on uninstantiated composite dialogue game propositions,
 *    see [[fr.dubuissonduplessis.dogma.game.language.instantiable.AbstractGamePropositionEventDescription.apply]]
 *  - to build event description based on semi-instantiated composite dialogue game propositions,
 *    see [[fr.dubuissonduplessis.dogma.game.language.instantiable.CompositeGamePropositionMatcherEventDescription.apply]]
 *
 *
 * The following example shows some usages of such event descriptions:
 * {{{
 * import fr.dubuissonduplessis.dogma.dialogue.Interlocutor
 * import fr.dubuissonduplessis.dogma.dialogue.X
 * import fr.dubuissonduplessis.dogma.dialogue.Y
 * import fr.dubuissonduplessis.dogma.event.dialogueAct.StandardDialogueAct
 * import fr.dubuissonduplessis.dogma.game.language.AbstractGameProposition
 * // Example dialogue act
 * case class EnterTheDG(
 *    locutor: Interlocutor,
 *    content: AbstractGameProposition) extends StandardDialogueAct[AbstractGameProposition] {
 *    val name = "EnterTheDG"
 * }
 *
 * val g1 = GamePropositionVariable("G1")
 * val g2 = GamePropositionVariable("G2")
 *
 * // Builder for EnterTheDG
 * import fr.dubuissonduplessis.dogma.description._ // implicit conversion from event to event description
 * val enterTheDGBuilder: EventDescriptionBuilder =
 *    (dp: Interlocutor, name: String, prop: AbstractGameProposition) => EnterTheDG(dp, prop)
 *
 * // Build an abstract event description of a dialogue act 'EnterTheDG' which content is completely uninstantiated (but should be a sequence)
 * val enterTheDGDescription = AbstractGamePropositionEventDescription.apply(X, "EnterTheDG", g1 / g2, enterTheDGBuilder)
 * //> enterTheDGDescription  : fr.dubuissonduplessis.dogma.description.EventDescription =
 * // EnterTheDG(x, G1;G2)
 *
 * // Instantiate this description with some game proposition
 * import fr.dubuissonduplessis.dogma.game.language.GameProposition
 * import fr.dubuissonduplessis.dogma.instantiable.InstantiationSet
 * val gamePropInterro = GameProposition("interrogation", "Where is my mind?")
 * val gamePropInterro2 = GameProposition("interrogation", "How many seas must a white dove sail before she sleeps in the sand?")
 * // Build a sequence of two game propositions
 * val sequenceCombination = gamePropInterro2 / gamePropInterro
 * //> sequenceCombination  : fr.dubuissonduplessis.dogma.game.language.SequenceGameProposition =
 * // interrogation(How many seas must a white dove sail before she sleeps in the sand?);interrogation(Where is my mind?)
 *
 * val instantiationSet = InstantiationSet() + (g2, gamePropInterro)
 * //> instantiationSet  : fr.dubuissonduplessis.dogma.instantiable.InstantiationSet =
 * // G2 -> interrogation(Where is my mind?)
 * assert(enterTheDGDescription.isInstantiableWith(instantiationSet))
 * val semiInstantiatedDescription = enterTheDGDescription.instantiateWith(instantiationSet)
 * //> semiInstantiatedDescription  : fr.dubuissonduplessis.dogma.description.EventDescription =
 * // EnterTheDG(x, G1;interrogation(Where is my mind?))
 *
 * // This semi-instantiated description is in turn an instantiable and a generator
 * val newInstantiationSet = semiInstantiatedDescription.bindings(EnterTheDG(X, sequenceCombination))
 * //> newInstantiationSet  : fr.dubuissonduplessis.dogma.instantiable.InstantiationSet =
 * // G1 -> interrogation(How many seas must a white dove sail before she sleeps in the sand?)
 *
 * // Generation of the dialogue act
 * semiInstantiatedDescription.generate(EnterTheDG(X, sequenceCombination))
 * //> res2: fr.dubuissonduplessis.dogma.description.Description =
 * // EnterTheDG(x, interrogation(How many seas must a white dove sail before she sleeps in the sand?);interrogation(Where is my mind?))
 * }}}
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
package object instantiable {
  /**
   * Type of a semi-instantiated composite dialogue game proposition
   */
  //type CompositeGamePropositionMatcher = Instantiable[AbstractGameProposition] with Generator[AbstractGameProposition, AbstractGameProposition]
  type CompositeGamePropositionMatcher = SemiInstantiatedVariableCombination

  /**
   * Type of an event description builder for a [[fr.dubuissonduplessis.dogma.event.dialogueAct.StandardDialogueAct]].
   * It is built by giving:
   *  - the dialogue participant that should produce this dialogue act
   *  - the name of the dialogue act
   *  - the content of the dialogue (which should be a dialogue game proposition)
   */
  type EventDescriptionBuilder = (Interlocutor, String, AbstractGameProposition) => EventDescription

}
