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
package fr.dubuissonduplessis.dogma.examples

/**
 * Provides a complete and rich example of configuration of $DOGMA for a specific interaction system.
 *
 * ==Overview==
 *  This package provides the implementation of the interaction model defined in the PhD thesis of
 *  Guillaume DUBUISSON DUPLESSIS ([[http://www.dubuissonduplessis.fr website]]). It shows a
 *  stereotypical example of usage of $DOGMA.
 *
 *  The interaction system is defined by:
 *   - a library of dialogic event (DIT++ dialogue acts defined in $DOGMA)
 *   - a library of communications games: see package [[fr.dubuissonduplessis.dogma.examples.gdd.communicationGame gdd.communicationGame]]
 *   - a library of dialogue games: see package [[fr.dubuissonduplessis.dogma.examples.gdd.dialogueGame gdd.dialogueGame]]
 *
 *  Dialogue games are defined in [[fr.dubuissonduplessis.dogma.examples.gdd.dialogueGame modules]] that should be mixed-in with the dialogue manager
 *  that is going to use the system.
 *
 *  Similarly, communication games are defined in [[fr.dubuissonduplessis.dogma.examples.gdd.communicationGame modules]]
 *  that should be mixed-in with the dialogue manager that is going to use the system.
 *
 *  A [[fr.dubuissonduplessis.dogma.examples.gdd.dialogueGame.GDDBundle helper bundle]] is provided to easily
 *  import all the dialogue games defined in this project.
 *
 *  Package [[fr.dubuissonduplessis.dogma.examples.gdd.app gdd.app]] shows how to use the defined games in an
 *  application.
 *
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 * @define DOGMA Dogma
 */
package object gdd {

}
