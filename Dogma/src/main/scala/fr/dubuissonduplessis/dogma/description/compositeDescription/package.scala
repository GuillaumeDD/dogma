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
package fr.dubuissonduplessis.dogma.description
/**
 * Provides classes to deal with composite event descriptions which combine single event description
 * to form advanced descriptions.
 *
 * ==Overview==
 * Composite abstract descriptions includes [[fr.dubuissonduplessis.dogma.description.compositeDescription.Alternative]],
 * [[fr.dubuissonduplessis.dogma.description.compositeDescription.Conditional]], [[fr.dubuissonduplessis.dogma.description.compositeDescription.PersistentConditional]],
 * and [[fr.dubuissonduplessis.dogma.description.compositeDescription.Negation]].
 *
 * For instance:
 * {{{
 * // Import predefined interlocutor
 * import fr.dubuissonduplessis.dogma.dialogue.X
 *
 * // Import the "Answer" dialogue act event
 * import fr.dubuissonduplessis.dogma.ditpp.informationTransfer.Answer
 * import fr.dubuissonduplessis.dogma.ditpp.feedback.ExecNegativeAutoFB
 *
 * // Creations of a description through an implicit conversion
 * import fr.dubuissonduplessis.dogma.description.EventDescription
 * val description1: EventDescription = Answer(X, "scala")
 * val description2: EventDescription = Answer(X, "java")
 * val description3: EventDescription = ExecNegativeAutoFB(X, "scala or java?")
 *
 * // Build up an alternative
 * val alt = description1 *| description2 *| description3
 * //> alt  : fr.dubuissonduplessis.dogma.description.compositeDescription.Alternative =
 * //  answer(x, scala)|answer(x, java)|execNegativeAutoFB(x, scala or java?)
 *
 * // Try some expectations of an alternative
 * alt.expects(Answer(X, "scala"))
 * //> res0: Boolean = true
 * alt.expects(Answer(X, "java"))
 * //> res1: Boolean = true
 * alt.expects(ExecNegativeAutoFB(X, "scala or java?"))
 * //> res2: Boolean = true
 * // Wrong semantic content
 * alt.expects(Answer(X, "prolog"))
 * //> res3: Boolean = false
 * }}}
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 */
package object compositeDescription {

}
