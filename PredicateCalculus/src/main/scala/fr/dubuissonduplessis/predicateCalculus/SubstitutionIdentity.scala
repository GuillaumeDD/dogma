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
package fr.dubuissonduplessis.predicateCalculus

import fr.dubuissonduplessis.predicateCalculus.substitution.SubstitutionSet

trait SubstitutionIdentity[+T <: Unifiable[_]] extends Unifiable[T] {
  this: T =>
  def substitute(subSet: SubstitutionSet): T =
    this
}
