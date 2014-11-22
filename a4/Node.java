/**
 * ECSE 414 - Homework Assignment 4
 * Michael Rabbat
 * McGill University
 * michael.rabbat@mcgillca
 */

/**
 * Singzon, Ryan
 * 260397455
 */

import java.util.*;

/**
 * This class represents one node in the network and stores all information
 * available to that node, including it's name (String), its distance vector,
 * its neighbors and the costs to get to each neighbor, and a list of the most
 * recently received message from each neighbor. In addition, the class has a
 * method to implement distance vector routing updates.
 * 
 * @author michaelrabbat
 * 
 */
public class Node implements Comparable<Node> {
	// Field to hold this node's name
	private String name;

	// Data structure mapping this nodes neighbors to the costs of getting to
	// each neighbor.
	private HashMap<Node,Float> costToNeighborMap;
	
	// Data structure to hold the most recently received message from each neighbor
	private HashMap<Node,Message> messages;
	private boolean newMessages = false;
	private Vector<Message> messageQueue;
	
	// Data structures representing this nodes local distance vector information
	// HashMap distanceVector holds the cost to each destination from this node
	// HashMap forwardingTable holds the next hop to each destination from this node
	private HashMap<Node,Float> distanceVector;
	private HashMap<Node,Node> forwardingTable;

	/**
	 * Constructor for Node
	 * 
	 * @param name is this node's name
	 */
	public Node(String name) {
		// Initialize this node's private fields
		this.name = name;
		costToNeighborMap = new HashMap<Node,Float>();
		messages = new HashMap<Node,Message>();
		newMessages = false;
		messageQueue = new Vector<Message>();
		distanceVector = new HashMap<Node,Float>();
		distanceVector.put(this, new Float(0));
		forwardingTable = new HashMap<Node,Node>();
		forwardingTable.put(this, this);
	}

	/**
	 * Method used by the Network to tell this node that the list of possible
	 * destinations has changed.
	 * 
	 * @param destinations
	 */
	public void updateDestinations(Collection<Node> destinations) {
		for (Node n : destinations) {
			// Only add if don't already know about n
			if (!distanceVector.containsKey(n)) {
				distanceVector.put(n, Float.POSITIVE_INFINITY);
				forwardingTable.put(n, null);
			}
		}
	}

	/**
	 * Overriding method toString() so that printing a Node prints its name
	 */
	@Override
	public String toString() {
		return name;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Node o) {
		return name.compareTo(o.toString());
	}

	/**
	 * Add a new neighbor to this node and specifies the cost to this neighbor
	 * 
	 * @param neighbor
	 *            is the neighboring Node
	 * @param cost
	 *            is the non-negative integer cost to get to this neighbor
	 * @throws Exception
	 *             when
	 */
	public void addNeighbor(Node neighbor, float cost) throws Exception {
		if ((costToNeighborMap.containsKey(neighbor)) || (cost < 0)) {
			String message = "Error adding neighbor to node" + this + "("
					+ neighbor + ", " + cost + ")"
					+ "\nCan't have duplicate links or negative costs";
			throw new Exception(message);
		}
		
		// Add an entry for the new neighbor in the local data structures
		costToNeighborMap.put(neighbor, cost);
		messages.put(neighbor, null);
		if (!distanceVector.containsKey(neighbor)) {
			distanceVector.put(neighbor, cost);
			forwardingTable.put(neighbor, neighbor);
		}
		
		// Send a message to all neighbors with this new cost info
		notifyNeighbors();
	}
	
	public void changeCostToNeighbor(Node neighbor, float cost) throws Exception {
		if (!costToNeighborMap.containsKey(neighbor)) {
			throw new Exception("Trying to change cost to a node that isn't already a neighbor");
		}
		
		// Change the cost to this neighbor
		costToNeighborMap.put(neighbor, cost);
	
		// Update the local routing info (distance vector and forwarding table) with the new cost
		doDistanceVectorUpdate();
		clearNewMessagesFlag();
		
		// Notify neighbors of the change
		notifyNeighbors();
	}

	/**
	 * Send a distance vector message to this node. Adds the message to this
	 * node's message queue.
	 * 
	 * @param m
	 */
	public void sendMessage(Message m) {
		messageQueue.add(m);
		newMessages = true;
	}
	
	public void deliverMessageQueue() {
		for (int i=0; i<messageQueue.size(); i++) {
			Message m = messageQueue.get(i);
			messages.put(m.getFrom(), m);
		}
		messageQueue.clear();
		newMessages = true;
	}

	/**
	 * Check if this Node has received a new distance vector message from one of
	 * its neighbors
	 * 
	 * @return true if it has received a new message, false otherwise
	 */
	public boolean hasNewMessages() {
		return newMessages;
	}
	
	/**
	 * Set this node's newMessages flag to false
	 */
	public void clearNewMessagesFlag() {
		newMessages = false;
	}
	
	/**
	 * @return a Collection of this node's neighbors
	 */
	protected Collection<Node> getNeighbors() {
		return new TreeSet<Node>(costToNeighborMap.keySet());
	}

	/**
	 * Get a collection of all destinations in the network from this node
	 * 
	 * @return a Collection of all possible destinations from this node
	 */
	protected Collection<Node> getDestinations() {
		return new TreeSet<Node>(distanceVector.keySet());
	}

	/**
	 * Get the cost of the link to go directly from this node to a neighbor
	 * 
	 * @param neighbor
	 *            is a node directly connected to this one
	 * @return the link cost to go directly to this neighbor, or infinity if the
	 *         specified node isn't a neighbor
	 */
	private float getCostToNeighbor(Node neighbor) {
		if (costToNeighborMap.containsKey(neighbor)) {
			return costToNeighborMap.get(neighbor);
		} else {
			return Float.POSITIVE_INFINITY;
		}
	}
	
	/**
	 * Get the cost from a neighbor to a destination, as advertised in the
	 * latest message received from that neighbor
	 * 
	 * @param neighbor
	 *            is the potential next hop
	 * @param destination
	 *            a potential node we are trying to get to
	 * @return the cost from neighbor to destination as advertised in the most
	 *         recent message received from neighbor
	 */
	private float getCostFromNeighborTo(Node neighbor, Node destination) {
		Message m = messages.get(neighbor);
		if (m != null) {
			return m.getCostTo(destination);
		} else {
			return Float.POSITIVE_INFINITY;
		}
	}

	/**
	 * Get the next hop listed in this nodes forwardingTable to get to a
	 * specified destination
	 * 
	 * @param destination
	 *            is another Node in the Network
	 * @return the Node that is the next hop to get from this node to the
	 *         specified destination
	 */
	protected Node getNextHopTo(Node destination) {
		return forwardingTable.get(destination);
	}

	/**
	 * Get the current distanceVector entry from this node to a specified
	 * destination
	 * 
	 * @param destination
	 *            is another Node in the Network
	 * @return the cost to get from this Node to destination in our current
	 *         distanceVector
	 */
	protected float getCostToDestination(Node destination) {
		return distanceVector.get(destination);
	}
	
	private void updateDistanceVector(Node destination, float cost) throws Exception {
		if (cost > 0) {
			distanceVector.put(destination, cost);
		} else {
			throw new Exception("Costs can't be negative");
		}
	}
	
	private void updateForwardingTable(Node destination, Node nextHop) throws Exception {
		if ((!costToNeighborMap.containsKey(nextHop)) && (nextHop != this)) {
			throw new Exception("Trying to add a forwarding table entry to a node that isn't a neighbor");
		}
		forwardingTable.put(destination, nextHop);
	}
	
	public void printLatestMessages() {
		System.out.println("Latest messages received by node " + this + ":");
		System.out.println("      \t| Neighbors");
		System.out.print("Dest. \t|");
		Collection<Node> neighbors = getNeighbors(); 
		for (Node n : neighbors) {
			System.out.print("\t" + n);
		}
		System.out.print("\n"); System.out.print("---------------");
		for (int i=0; i<neighbors.size(); i++) {
			System.out.print("--------");
		}
		System.out.print("\n");
		for (Node dest : getDestinations()) {
			System.out.print(dest + "\t|");
			for (Node n : neighbors) {
				String costFromNeighborTo = "";
				if (getCostFromNeighborTo(n,dest) == Float.POSITIVE_INFINITY) {
					costFromNeighborTo = "Inf";
				} else {
					//costFromNeighborTo = Float.toString(getCostFromNeighborTo(n,dest));
					costFromNeighborTo = Integer.toString((int)getCostFromNeighborTo(n,dest));
				}
				System.out.print("\t" + costFromNeighborTo);
			}
			System.out.print("\n");
		}
		System.out.println(" ");
	}
	
	public void printDistanceVector() {
		System.out.println("Distance vector and forwarding table for node "
				+ this + ":");
		System.out.println("Dest.\tCost (Next Hop)");
		System.out.println("-------------------------");
		for (Node dest : getDestinations()) {
			String costToDestination = "";
			if (getCostToDestination(dest) == Float.POSITIVE_INFINITY) {
				costToDestination = "Inf";
			} else {
				costToDestination = Integer.toString((int)getCostToDestination(dest));
			}
			System.out.println(dest + "\t" + costToDestination + " ("
					+ getNextHopTo(dest) + ")");
		}
		System.out.println("");
	}
	
	/**
	 * Implements the Bellman-Ford equation to update the distanceVector costs
	 * and forwardingTable of this node
	 */
	public void doDistanceVectorUpdate() {
		// STEP 1: Fill in this method
	
		// Loop over all possible destinations
		// Do Bellman-Ford updates using this node's local info
		// If something changed, notify this node's neighbors by calling notifyNeighbors()
		for(Node destination : getDestinations()){

			float costToDestination = getCostToNeighbor(destination);

			//System.out.println("Cost from " +this.name+" to "+destination.name+": "+costToDestination);
			

			// Find the cost of the neighbors of the current node
			Collection<Node> neighbors = getNeighbors();
			for(Node neighbor: neighbors){

				float newCost = getCostFromNeighborTo(neighbor, destination) + getCostToNeighbor(neighbor);
				float currentCost = getCostToDestination(destination);
				
				
				// Update the forwarding table if the cost from the neighbor is less
				if(newCost < currentCost){
					System.out.println("Something changed at node " + this.name + ".  The cost to " + destination.name + " changed. Notifying neighbours...");
					//notifyNeighbors();
				}	
			}	
		}
	}
	
	/**
	 * Send distance vector messages to all neighbors of this node
	 */
	protected void notifyNeighbors() {
		// STEP 2: Fill in this method
		
		// Create a message containing the contents of this node's distance vector
		HashMap<Node, Float> distances = new HashMap<Node,Float>(); 
		
		// Iterate through each neighbour and record the distance to each neighbour
		for(Node neighbor : getNeighbors()){
			distances.put(neighbor, getCostToNeighbor(neighbor));
		}
		
		Message message = new Message(this, distances);
		
		// (Not doing poisoned reverse in this implementation)
		// Send the message to every neighbor
		for(Node neighbor : getNeighbors()){
			neighbor.sendMessage(message);
		}
		
	}
}
