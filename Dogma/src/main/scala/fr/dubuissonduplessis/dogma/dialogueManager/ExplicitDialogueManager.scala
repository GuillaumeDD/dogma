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
package fr.dubuissonduplessis.dogma.dialogueManager

import fr.dubuissonduplessis.dogma.dialogueManager.impl.EventHandler
import fr.dubuissonduplessis.dogma.event.DialogicEvent
import fr.dubuissonduplessis.dogma.event.ExternalEvent
import fr.dubuissonduplessis.dogma.event.ExtraDialogicEvent
import fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.ConventionalBehaviourManager
import fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.ExplicitReferee

/**
 * An abstract [[fr.dubuissonduplessis.dogma.dialogueManager.DialogueManager dialogue manager]]
 * taking advantage of the [[fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma modules]]
 * defined by Dogma.
 *
 * This abstract class provides a partial implementation of a dialogue manager that takes
 * advantage of the [[fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.ExplicitReferee referee]]
 * module and the [[fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.ConventionalBehaviourManager conventional behaviour manager]]
 * module defined by Dogma. It also uses the default implementation of the
 * [[fr.dubuissonduplessis.dogma.dialogueManager.impl.EventHandler event handler]].
 * The main interest of this class lies in its partial interpretation algorithm.
 *
 * ==Interpretation Algorithm==
 * The main advantage of this dialogue manager is to break down the interpretation process
 * of the dialogue manager into several [[fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.ExplicitReferee#Case cases]].
 * Thanks to the [[fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.ExplicitReferee referee]] module, it
 * computes which case applies at a given '''dialogic''' event. The interpretation process comes down to define
 * what to do:
 *  - if the event is forbidden:
 *  [[fr.dubuissonduplessis.dogma.dialogueManager.ExplicitDialogueManager#computeForbidden computeForbidden]]
 *  - if the event is unexpected:
 *  [[fr.dubuissonduplessis.dogma.dialogueManager.ExplicitDialogueManager#computeUnexpected computeUnexpected]]
 *  - if the event is not priority:
 *  [[fr.dubuissonduplessis.dogma.dialogueManager.ExplicitDialogueManager#computeNonPriority computeNonPriority]]
 *  - if the event is allowed, expected and priority:
 *  [[fr.dubuissonduplessis.dogma.dialogueManager.ExplicitDialogueManager#computeStandard computeStandard]]
 *
 * The standard case (compliant) can take advantage of the predefined
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.impl.DefaultConversationalGameBoard#process process algorithm]]
 * defined in the
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.impl.DefaultConversationalGameBoard default implementation of a conversational game board]].
 *
 * The interpretation process of an '''extra-dialogic''' event is delegated to the
 * [[fr.dubuissonduplessis.dogma.dialogueManager.ExplicitDialogueManager#computeExtraDialogicEvent computeExtraDialogicEvent]]
 * method.
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
trait ExplicitDialogueManager
  extends DialogueManager
  with ExplicitReferee
  with ConventionalBehaviourManager
  with EventHandler {

  // Interpretation Part
  import Case._
  private[dialogueManager] def processEvent(e: ExternalEvent): Unit = {
    e match {
      case evt: ExtraDialogicEvent =>
        computeExtraDialogicEvent(evt)
      case evt: DialogicEvent =>
        cases(evt) match {
          case FORBIDDEN =>
            logger.info(s"Forbidden act $evt")
            computeForbidden(evt)
          case UNEXPECTED =>
            logger.info(s"Unexpected act $evt")
            computeUnexpected(evt)
          case NONPRIORITY =>
            logger.info(s"Non-priority act $evt")
            computeNonPriority(evt)
          case STANDARD =>
            logger.info(s"Regular act $evt")
            computeStandard(evt)
        }
    }
  }

  /**
   * Interpretative process in case of the occurrence of an extra-dialogic
   * event.
   */
  protected def computeExtraDialogicEvent(evt: ExtraDialogicEvent): Unit
  /**
   * Interpretative process in case of the occurrence of a '''forbidden'''
   * dialogic event.
   */
  protected def computeForbidden(da: DialogicEvent): Unit
  /**
   * Interpretative process in case of the occurrence of an '''unexpected'''
   * dialogic event.
   */
  protected def computeUnexpected(da: DialogicEvent): Unit
  /**
   * Interpretative process in case of the occurrence of a '''non-priority'''
   * dialogic event.
   */
  protected def computeNonPriority(da: DialogicEvent): Unit
  /**
   * Interpretative process in case of the occurrence of an '''allowed''',
   * '''expected''' and '''priority''' dialogic event.
   */
  protected def computeStandard(da: DialogicEvent): Unit
}
