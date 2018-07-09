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
 * Provides '''abstract base classes''' for dealing with events.
 *
 * == Overview ==
 * Every event inherits of the abstract superclass [[fr.dubuissonduplessis.dogma.event.Event]].
 * Events are broken down into two parts:
 *  - Internal events (see [[fr.dubuissonduplessis.dogma.event.InternalEvent]])
 *  - External events (see [[fr.dubuissonduplessis.dogma.event.ExternalEvent]])
 *
 * Internal events are generated internally by the dialogue manager whereas external events
 * comes from the 'outside world' such as an interpretation module. For instance, events
 * produced by the occurrence of dialogue acts are external events.
 *
 * Should you wish to:
 *  - define your own dialogic event, see [[fr.dubuissonduplessis.dogma.event.DialogicEvent]]
 *  - define your own dialogue act event, see [[fr.dubuissonduplessis.dogma.event.dialogueAct.DialogueAct]]
 *  - define your own extra-dialogic event, see [[fr.dubuissonduplessis.dogma.event.ExtraDialogicEvent]]
 *  - define your own internal event, see [[fr.dubuissonduplessis.dogma.event.InternalEvent]]
 *
 *  @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 */
package object event {

}
