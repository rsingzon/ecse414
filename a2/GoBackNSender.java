/**
 * Singzon, Ryan
 * 260397455
 */

/** 
 * GoBackNSender - Assignment 2 for ECSE 414
 *
 * Sender implementation of the Go-back-N protocol
 * 
 * Michael Rabbat
 * McGill University
 * michael.rabbat@mcgill.ca
 */
 
import java.io.*;
import java.net.*;

class GoBackNSender {

	// UDP socket for communications
	private DatagramSocket senderSocket;
	
	// Receiver's IP Address and port number
	private InetAddress receiverAddress;
	private int receiverPort;
	
	// Go-Back-N Window size and nextseqnum
	private static final int N = 5;
	private byte nextseqnum;
	private byte base;


	/**
	 * Creates a GoBackNSender and connects to the specified port number on 
	 * the named host.
	 */
	public GoBackNSender(String host, int port) {
  		// STEP 4: Fill in the GoBackNSender constructor
        // Initialize senderSocket
        // Lookup the specified hostname using InetAddress.getByName()
        // Store the receiverAddress and receiverPort
        // Construct a Hello packet and send it to the receiver
        // Wait for an ACK
        // Initialize base and nextseqnum to zero
	}
	
	
	/**
	 * Helper method to send a single Go-back-N packet
	 */
	private void sendData(byte seqnum, char c) {
		GoBackNPacket gbnPacket = new GoBackNPacket(GoBackNPacket.TYPE_DATA, seqnum, c);
		DatagramPacket p = gbnPacket.toDatagramPacket();
		p.setAddress(receiverAddress);
		p.setPort(receiverPort);
		try {
			senderSocket.send(p);
		} catch (Exception e) {
			System.out.println("Sender error sending data packet (" + seqnum + ", " + c + ")");
			System.out.print(e);
			System.out.println("");
			System.exit(1);
		}
	}
	
	
	/**
	 * Helper method to receive an ACK.  
	 * When an ACK is received, return the ACKed sequence number
	 * After timeout milliseconds, this method times out and throws an exception
	 */
	private byte receiveAck(int timeout) throws SocketTimeoutException {
		byte[] data = new byte[3];
		DatagramPacket dgPacket = new DatagramPacket(data, 3);
		try {
			senderSocket.setSoTimeout(timeout);
			senderSocket.receive(dgPacket);
		} catch (Exception e) {
			if (e instanceof SocketTimeoutException) {
				throw (SocketTimeoutException)e;
			} else {
				System.out.println("Sender error while receiving ACK");
				System.out.print(e);
				System.out.println("");
				System.exit(1);
			}
		}

		GoBackNPacket gbnPacket = new GoBackNPacket(dgPacket);
		if (!gbnPacket.isAck()) {
			// Print an error message because this isn't an ACK
			System.out.println("Sender error: Expecting an ACK but received something else");
			System.exit(1);
		}
		return gbnPacket.getSequenceNumber();
	}
	
	
	/**
	 * Send a message one character at a time using the Go-back-N reliable
	 * data transfer protocol.
	 */
	public void send(String message) {
		// STEP 5: Implement the main part of the Go-Back-N sender
        // First send one window's worth of packets
        // Then start a loop that iterates until the entire message is received
        // Within the loop, first wait for an ACK using receiveAck(timeout)
        // After the ACK, update base and send new packets as appropriate
        // If receiveAck() times out, catch the exception and retransmit
	}
	
	
	/**
	 * Send a goodbye packet
	 */
	public void close() {
		GoBackNPacket gbnPacket = new GoBackNPacket(GoBackNPacket.TYPE_GOODBYE, (byte)0, 'g');
		DatagramPacket p = gbnPacket.toDatagramPacket();
		p.setAddress(receiverAddress);
		p.setPort(receiverPort);
		try {
			senderSocket.send(p);
		} catch (Exception e) {
			System.out.println("Sender error sending goodbye packet");
			System.out.print(e);
			System.out.println("");
			System.exit(1);
		}
		senderSocket.close();
	}
	

	/**
	 * Main method
	 * 
	 * Open a connection to the receiver, both running on this machine, and
	 * transmit a very long String using the GoBackN protocol
	 */
	public static void main(String args[]) {
		String message = "A long time ago, in a galaxy far, far away...\n\nEpisode IV, A NEW HOPE\n\nIt is a period of civil war. Rebel spaceships, striking from a hidden base, have won their first victory against the evil Galactic Empire. During the battle, Rebel spies managed to steal secret plans to the Empire's ultimate weapon, the DEATH STAR, an armored space station with enough power to destroy an entire planet. Pursued by the Empire's sinister agents, Princess Leia races home aboard her starship, custodian of the stolen plans that can save her people and restore freedom to the galaxy....";
	
		// Instantiate a new GoBackNSender which connects to address
		// 'localhost' on port 9876
		GoBackNSender gbnSender = new GoBackNSender("localhost", 9876);
		
		// Send the message
		gbnSender.send(message);
		
		// Close the connection
		gbnSender.close();
		
		System.out.println("");
	}
}