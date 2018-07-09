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

import fr.dubuissonduplessis.dogma.instantiable.Variable

/**
 * Provides classes to deal with a simple instantiation process.
 *
 * ==Overview==
 * The instantiation involves four concepts: instantiable, generator, variable and instantiation set.
 *
 * Instantiable and generator are defined in two main traits:
 *  - the [[fr.dubuissonduplessis.dogma.instantiable.Instantiable]] trait defines something that is
 *    instantiable.
 *  - the [[fr.dubuissonduplessis.dogma.instantiable.Generator]] trait defines something that generates a
 *    binding for the instantiation process.
 *
 * The instantiation process also involves variables (see [[fr.dubuissonduplessis.dogma.instantiable.Variable]])
 * and instantiation sets (see [[fr.dubuissonduplessis.dogma.instantiable.InstantiationSet]]).
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 */
package object instantiable {
  /**
   * Type of a generic variable
   */
  type InstantiationVariable = Variable[_]
}
