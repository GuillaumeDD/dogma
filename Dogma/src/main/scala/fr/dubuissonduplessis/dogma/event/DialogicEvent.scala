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
package fr.dubuissonduplessis.dogma.event

/**
 * Abstract base class that represents an external dialogic event (e.g., an occurrence of
 * a dialogue act, a signal from an interpretation module such as 'TIME_OUT', 'PERCEPT_FAIL', etc.).
 *
 * It is worth noticing that the child abstract class [[fr.dubuissonduplessis.dogma.event.DialogicEventFromLocutor]]
 * aims at representing events '''produced by a specific locutor''', such as a dialogue act.
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 */
abstract class DialogicEvent extends ExternalEvent
