/**
 * *****************************************************************************
 * Copyright (c) 2013 Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr> - initial API and implementation
 * ****************************************************************************
 */
package fr.dubuissonduplessis.simpleSemantics

/**
 * == Overview ==
 * Deals with '''elliptical''' utterances such as "yes", "no", "beer".
 * Short answers can be viewed as underspecified propositions. They are represented
 * as constants.
 *
 * This package includes the base class for representing short answers (see
 * [[fr.dubuissonduplessis.simpleSemantics.shortAnswer.ShortAns]]) as well as two
 * special types of short answers "yes" (see [[fr.dubuissonduplessis.simpleSemantics.shortAnswer.Yes]])
 * and "no" (see [[fr.dubuissonduplessis.simpleSemantics.shortAnswer.No]]).
 *
 */
package object shortAnswer
