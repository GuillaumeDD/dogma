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
package fr.dubuissonduplessis

/**
 * Provides tools to build up a dialogue manager based on $DOGMA.
 *
 *
 * '''Keywords''': dialogue management, dialogue act, information-state approach, common ground, convention,
 *  dialogue game, social commitment
 *
 *
 * ==Introduction to $DOGMA==
 *
 * $DOGMA (which stands for « DialOgue Game MAnager ») is a normative module to manage conventional
 * interaction based on the notion of dialogue games. $DOGMA is a tool to help you design
 * the conventional dialogic behavior of an agent that interacts with humans (or other agents). As
 * such, it is conceived as a part of a dialogue manager '''currently limited to two-party dialogue'''.
 *
 * $DOGMA stands on several theoretical notions that are worth understanding to take full advantage of
 * it. It includes:
 * dialogue act, dialogue context, information-state approach, common ground, convention,
 * social commitment, and dialogue game used to explain human dialogue and to generate artificial dialogues
 * dedicated to humans. Good places to start include:
 *  - (in French) PhD thesis of Guillaume DUBUISSON DUPLESSIS ([[http://www.dubuissonduplessis.fr website]])
 *  - (in English) Dubuisson Duplessis, G., Chaignaud, N., Kotowicz, J.-P., Pauchet, A. & Pécuchet, J.-P., Demazeau, Y.; Ishida, T.; Corchado, J. & Bajo, J. (Eds.), '''Empirical Specification of Dialogue Games for an Interactive Agent''', Proceedings of the 11th International Conference on Practical Applications of Agents and Multi-Agent Systems (PAAMS), 2013, LNAI 7879, pp.49-60
 *
 * In a nutshell, dialogue games for human interaction are conventional devices used by dialogue
 * participants to coordinate their contribution during dialogue, viewed as a shared and dynamic
 * activity. The metaphor of dialogue game is essentially normative. It is a structure that describes
 * the rights and obligations as well as the goals and possible dialogic actions of dialogue participants
 * according to the progress of an activity of some sort.
 * A dialogue game is a bounded joint activity with an entry, a body and an exit where participants
 * play a role (initiator or partner). It is a joint project that must be jointly established.
 * Rules of the game specify the expected dialogue moves for each participant. Participants are
 * expected to play their roles by making the moves expected by the current stage of the game.
 *
 * $DOGMA sees dialogue games (and communication games) as structures capturing public commitments
 * created during dialogue. Games are given a '''public semantic''' which stands on social commitments.
 * Game specifications are then independent of the private states of the agents (e.g., belief, desire, intention)
 * taking part in the interaction. Dialogue games are dialogic contexts in which
 * dialogue participants contract commitments that in turns guide their communicative behaviour.
 * In $DOGMA, social commitments aims at defining '''conventional sequence of dialogue acts''' as well
 * as '''the effects of the occurrence of these acts on the information state'''.
 *
 * ==Overview of the Architecture of $DOGMA==
 * A dialogue manager can base its management of conventional interaction on $DOGMA.
 * The architecture of $DOGMA is depicted on the following figure:<br/>
 * <img src="$ROOTPATH/doc-resources/dogma.svg" width="550px" style="text-align:center;display:block;"/>
 *
 * $DOGMA adopts the Information-State Update (ISU-)approach to dialogue management.
 * The public part of the information state is represented by a
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard conversational gameboard]] which includes:
 *  - the dialogue history in terms of external events;
 *  - dialogue participants' public positions on propositions;
 *  - dialogue participants' public commitments on the fulfillment of future actions (dialogic or not);
 *  - dialogue intermediary structure in the form of suggested, open or closed dialogue games.
 *
 * $DOGMA is responsible for the '''update''' and the '''exploitation''' of the
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore commitment store]],
 * part of the conversational gameboard.
 * The commitment store is the central component of $DOGMA.
 * It contains the [[fr.dubuissonduplessis.dogma.commitment commitments]] of each dialogue participant, subdivided into
 * propositional commitments and action commitments.
 * It also includes the sets of dialogue games that are currently being played or that have been played.
 * The commitment store evolves through the occurrence of [[fr.dubuissonduplessis.dogma.event external events]]
 * such as [[fr.dubuissonduplessis.dogma.event.dialogueAct dialogue acts]].
 * Modification of the state of the commitment store may trigger [[fr.dubuissonduplessis.dogma.event.InternalEvent internal events]].
 * The [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.ExplicitCommitmentStore commitment manager]] module keeps
 * the commitment store up to date when an external or an internal event occurs.
 * It has a read/write access to the commitment store.
 * This component should be called by the dialogue manager.
 * $DOGMA provides two modules taking advantage of the commitment store: the
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.ExplicitReferee referee]] (on an interpretative perspective) and the
 * [[fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.ConventionalBehaviourManager conventional behaviour manager]] (on a generative perspective).
 * These modules have a read access to the commitment store.
 * The referee module aims at determining the '''legality of a dialogue move''' (in terms of expectation, priority and interdiction)
 * in relation to the commitment store.
 * The conventional behaviour manager exploits the commitment store to identify the communicative
 * actions that are expected (or forbidden) for each dialogue participant. As such, it
 * limits the attention of the agent to a set of legal dialogue moves, and
 * provides conventional motivations to the production of dialogue acts.
 *
 * $DOGMA provides the conversational gameboard as well as primitives to update and to take advantage of the gameboard
 * for interpretative and generative purposes inside a [[fr.dubuissonduplessis.dogma.dialogueManager dialogue manager]].
 * $DOGMA also provides a
 * [[fr.dubuissonduplessis.dogma.dialogueManager.ExplicitDialogueManager predefined dialogue manager adjustable to your needs]].
 *
 * To use $DOGMA, the following elements must be defined:
 *  - a library of dialogic event (which defines the dialogue acts in use; see, e.g., package [[fr.dubuissonduplessis.dogma.ditpp ditpp]]);
 *  - a library of [[fr.dubuissonduplessis.dogma.game.CommunicationGames communication games]];
 *  - a library of [[fr.dubuissonduplessis.dogma.game.DialogueGames dialogue games]].
 *
 * These resources portray a normative model of conventional interaction adaptable to a number of
 * application domains. In other words, these resources define the dialogue moves usable by the
 * dialogue participant in the interaction as well as the conventional devices the participants
 * can use to coordinate their interaction (i.e. the [[fr.dubuissonduplessis.dogma.game games]]).
 *
 * Then, the application domain must be specified via:
 *  - the utterance semantics;
 *  - a library of [[fr.dubuissonduplessis.dogma.event.ExtraDialogicEvent extra-dialogic events]] and a
 *  [[fr.dubuissonduplessis.dogma.dialogueManager.ExplicitDialogueManager#computeExtraDialogicEvent specification of how they relate to extra-dialogic action commitments]];
 *  - a [[fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.contextualisation.ExplicitReferee specification of the allowed combinations of dialogue games]].
 *
 * Examples of usage of Dogma can be found in the [[fr.dubuissonduplessis.dogma.examples dogma.examples]] package.
 * 
 * ==Notes about the Technical Architecture==
 * ===Modular Architecture of $DOGMA===
 * $DOGMA is based on a modular architecture. Modules are implemented as Scala traits, meaning that:
 *  - a module is a trait
 *  - a module can specify abstract members (function, type, value, etc.)
 *  - a module can have multiple implementations
 *  - a module may have a default implementation
 *  - a module can specialize another one via inheritance
 *
 * Dependency requirements between modules are specified via Scala self-type.
 *
 * Packages group modules and classes that logically go together (e.g., package [[fr.dubuissonduplessis.dogma.game game]] group
 * modules dedicated to dialogue games and communication games). Subpackages named 'impl' contain default implementations.
 *
 * Visibility rules should be carefully examined in the modular architecture. The following table
 * describes the semantic of visibility rules used in $DOGMA:
 * <table style="border-collapse: collapse; border: 1px solid black"border="2">
 * <thead>
 * <tr>
 * 	<th scope="col" style="background-color: #CCCCCC; border-width: 1px; padding: 3px; padding-top: 7px; border: 1px solid black; text-align: left">
 *  	<strong>Visibility</strong>
 *  </th>
 * 	<th scope="col" style="background-color: #CCCCCC; border-width: 1px; padding: 3px; padding-top: 7px; border: 1px solid black; text-align: left">
 *  	<strong>Description</strong>
 *  </th>
 * 	<th scope="col" style="background-color: #CCCCCC; border-width: 1px; padding: 3px; padding-top: 7px; border: 1px solid black; text-align: left">
 *  	<strong>Usage Example</strong>
 *  </th>
 * </tr>
 * </thead>
 * <tbody>
 * <tr>
 * 	<td style="border-width: 1px; padding: 3px; border: 1px solid black; text-align: left">
 *  	private
 *  </td>
 *  <td style="border-width: 1px; padding: 3px; border: 1px solid black; text-align: left">
 * 		Element strictly restricted to a module. Invisible from the outside.
 *  </td>
 *  <td style="border-width: 1px; padding: 3px; border: 1px solid black; text-align: left">
 *  	Internal module mechanisms (e.g., helper methods)
 *  </td>
 * </tr>
 * <tr>
 * 	<td style="border-width: 1px; padding: 3px; border: 1px solid black; text-align: left">
 *  	private[X]<br/>
 *   (X is a package)
 *  </td>
 *  <td style="border-width: 1px; padding: 3px; border: 1px solid black; text-align: left">
 * 		Element strictly restricted to a module. Accessible inside the scope X.
 *  </td>
 *  <td style="border-width: 1px; padding: 3px; border: 1px solid black; text-align: left">
 *  	Internal module mechanisms which access is limited to modules belonging to a common package.
 *  </td>
 * </tr>
 * <tr>
 * 	<td style="border-width: 1px; padding: 3px; border: 1px solid black; text-align: left">
 *  	protected
 *  </td>
 *  <td style="border-width: 1px; padding: 3px; border: 1px solid black; text-align: left">
 * 		Element restricted to the whole dialogue manager. Invisible from the outside of the dialogue manager.
 *  </td>
 *  <td style="border-width: 1px; padding: 3px; border: 1px solid black; text-align: left">
 *  	Dialogue management mechanisms.
 *  </td>
 * </tr>
 * <tr>
 * 	<td style="border-width: 1px; padding: 3px; border: 1px solid black; text-align: left">
 *  	protected[X]<br/>
 *   (X is a package)
 *  </td>
 *  <td style="border-width: 1px; padding: 3px; border: 1px solid black; text-align: left">
 * 		Element restricted to the whole dialogue manager. Accessible inside the scope X.
 *  </td>
 *  <td style="border-width: 1px; padding: 3px; border: 1px solid black; text-align: left">
 *  	Should not be used in general (dependency requirements should be preferred).
 *  </td>
 * </tr>
 * <tr>
 * 	<td style="border-width: 1px; padding: 3px; border: 1px solid black; text-align: left">
 *  	public
 *  </td>
 *  <td style="border-width: 1px; padding: 3px; border: 1px solid black; text-align: left">
 * 		Element accessible outside the dialogue manager.
 *  </td>
 *  <td style="border-width: 1px; padding: 3px; border: 1px solid black; text-align: left">
 *  	Should not be used in general.
 *  </td>
 * </tr>
 * </tbody>
 * </table>
 * ===Packages and Important Modules===
 *  - package [[fr.dubuissonduplessis.dogma.dialogue dialogue]]: [[fr.dubuissonduplessis.dogma.dialogue.Dialogue Dialogue]] module
 *  - package [[fr.dubuissonduplessis.dogma.commitment commitment]]: [[fr.dubuissonduplessis.dogma.commitment.SemanticTypes SemanticTypes]], [[fr.dubuissonduplessis.dogma.commitment.Commitments Commitments]] modules
 *  - package [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit conversationalGameBoard.commitmentStore]]:
 *  [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.CommitmentStore CommitmentStore]],
 *  [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.explicit.CommitmentStoreOperations CommitmentStoreOperations]],
 *  [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.GameManager GameManager]],
 *  [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.gameManager.GameManagerOperations GameManagerOperations]],
 *  [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.internalEvent.InternalEventGenerator InternalEventGenerator]]
 *  - package [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation conversationalGameBoard.contextualisation]]:
 *  [[fr.dubuissonduplessis.dogma.conversationalGameBoard.commitmentStore.contextualisation.ContextualisationGame ContextualisationGame]]
 *  - package [[fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma conversationalGameBoard.dogma]]:
 *  [[fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.ExplicitReferee ExplicitReferee]],
 *  [[fr.dubuissonduplessis.dogma.conversationalGameBoard.dogma.ConventionalBehaviourManager ConventionalBehaviourManager]]
 *  - package [[fr.dubuissonduplessis.dogma.conversationalGameBoard conversationalGameBoard]]:
 *  [[fr.dubuissonduplessis.dogma.conversationalGameBoard.ConversationalGameBoard ConversationalGameBoard]]
 *  - package [[fr.dubuissonduplessis.dogma.game game]]: [[fr.dubuissonduplessis.dogma.game.DialogueGames DialogueGames]], [[fr.dubuissonduplessis.dogma.game.CommunicationGames CommunicationGames]], [[fr.dubuissonduplessis.dogma.game.factory.GameRepositories GameRepositories]]
 *  - package [[fr.dubuissonduplessis.dogma.gameInstance gameInstance]]: [[fr.dubuissonduplessis.dogma.gameInstance.DialogueGameInstances DialogueGameInstances]]
 *
 * === Other Independent Packages===
 * $DOGMA includes a [[fr.dubuissonduplessis.dogma.instantiable generic instantiation mechanism]] to describe events that may occur.
 *
 * $DOGMA stands on a specific [[fr.dubuissonduplessis.dogma.event event hierarchy]] which is extended to implement some of the
 * [[fr.dubuissonduplessis.dogma.ditpp DIT++ dialogue acts]].
 *
 * $DOGMA includes a library to define [[fr.dubuissonduplessis.dogma.description event descriptions]].
 *
 * @define DOGMA Dogma
 * @define ROOTPATH ../../../
 * @author Guillaume DUBUISSON DUPLESSIS <guillaume.dubuisson_duplessis@insa-rouen.fr>
 *
 */
package object dogma {

}
