/**
 * ECSE 414 - Homework Assignment 4
 * Michael Rabbat
 * McGill University
 * michael.rabbat@mcgillca
 */

import java.util.HashMap;

/**
 * @author michaelrabbat
 *
 */
public class PoisonedReverseNode extends Node {
	public PoisonedReverseNode(String name) {
		super(name);
	}

	@Override
	protected void notifyNeighbors() {
		// Step 3: Fill in this method

		// Construct messages according to the poisoned reverse rule
		// and send these messages to each neighbor

		// Create a message containing the contents of this node's distance vector
		HashMap<Node, Float> distances = new HashMap<Node, Float>();

		// Iterate through each destination and record the distance to each
		for (Node destination : getDestinations()) {

			// If the destination routes through another node, the distance to that node is infinity
			if(getNextHopTo(destination) != destination){
				distances.put(destination, Float.POSITIVE_INFINITY);
			}
			else{
				distances.put(destination, getCostToDestination(destination));
			}
		}

		// Send the message to every neighbor
		Message message = new Message(this, distances);
		for (Node neighbor : getNeighbors()) {
			neighbor.sendMessage(message);
		}
	}
}
