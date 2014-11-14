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
	}
}
