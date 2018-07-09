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
package fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.internalEvent.impl

import fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.internalEvent.InternalEventGenerator
import fr.dubuissonduplessis.dogma.event.InternalEvent
import fr.dubuissonduplessis.dogma.dialogue.Dialogue
import fr.dubuissonduplessis.dogma.dialogue.Interlocutor

/**
 * A deactivated internal event generator that never generates internal event.
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait DeactivatedInternalEventGenerator extends InternalEventGenerator {
  this: Dialogue =>
  private[conversationalGameBoard] def internalEvents(lastSpeaker: Option[Interlocutor]): Set[InternalEvent] =
    Set()

  private[conversationalGameBoard] def pastEvents: Set[InternalEvent] =
    Set()

  private[conversationalGameBoard] def popEvents(): Unit =
    {}
}
