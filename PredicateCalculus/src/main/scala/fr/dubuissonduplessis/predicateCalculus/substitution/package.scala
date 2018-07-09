/*******************************************************************************
 * Copyright (c) 2014 Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr> - initial API and implementation
 ******************************************************************************/
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
package fr.dubuissonduplessis.predicateCalculus

/**
 * Provides traits and default implementation for dealing with
 * substitution and substitution set.
 *
 * == Overview ==
 * This package comes with the following traits that may be mixed in:
 *  - [[fr.dubuissonduplessis.predicateCalculus.substitution.SubstitutionSet]] that represents
 *  a set of [[fr.dubuissonduplessis.predicateCalculus.substitution.Substitution]].
 *  - [[fr.dubuissonduplessis.predicateCalculus.substitution.Substitution]] that represents
 *  a substitution from a [[fr.dubuissonduplessis.predicateCalculus.Variable]] to an
 *  [[fr.dubuissonduplessis.predicateCalculus.Individual]] or a
 *  [[fr.dubuissonduplessis.predicateCalculus.PredN]].
 *  - [[fr.dubuissonduplessis.predicateCalculus.substitution.VariableSubstitution]] that represents
 *  a substitution from a [[fr.dubuissonduplessis.predicateCalculus.Variable]] to a
 *  [[fr.dubuissonduplessis.predicateCalculus.Variable]].
 *
 */
package object substitution
