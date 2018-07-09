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
package fr.dubuissonduplessis.dogma

/**
 * Provides classes for dealing with DIT++ dialogue act.
 * The DIT++ framework is described at the following web page: [[http://dit.uvt.nl/ DIT++ documentation]].
 *
 * ==Overview==
 * The core elements of this package includes:
 *  - The [[fr.dubuissonduplessis.dogma.ditpp.Dimension]] enumeration that defines available dimensions from
 *  the DIT++ taxonomy
 *  - The trait [[fr.dubuissonduplessis.dogma.ditpp.DITDialogueAct]] that augments dialogue acts with a dimension.
 *
 * The main classes to use are defined in the following sub-packages:
 *  - [[fr.dubuissonduplessis.dogma.ditpp.actionDiscussion]]: defines action-discussion dialogue acts
 *  - [[fr.dubuissonduplessis.dogma.ditpp.informationSeeking]]: defines information-seeking dialogue acts
 *  - [[fr.dubuissonduplessis.dogma.ditpp.informationTransfer]]: defines information-transfer dialogue acts
 *  - [[fr.dubuissonduplessis.dogma.ditpp.feedback]]: defines feedback dialogue acts
 *
 * Every dialogue act from this package comes with a factory method that makes it easy to instantiate an act:
 * {{{
 * // Definition of the dialogue participants
 * val alice = Interlocutor("Alice")
 * val bob = Interlocutor("Bob")
 *
 * import fr.dubuissonduplessis.dogma.ditpp.informationSeeking.SetQuestion
 * // Declaration of a SetQuestion produced by Alice
 * val questionAct = SetQuestion(alice, "Where are you?")
 *
 * import fr.dubuissonduplessis.dogma.ditpp.informationTransfer.Answer
 * // Declaration of an Answer produced by Bob
 * val answerAct = Answer(bob, "I am in your computer")
 * }}}
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 */
package object ditpp {

}
