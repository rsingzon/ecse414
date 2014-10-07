/**
 * Singzon, Ryan
 * 260397455
 */

/** 
 * GoBackNReceiver - Assignment 2 for ECSE 414
 *
 * Receiver implementation of the Go-back-N protocol
 *
 * Michael Rabbat
 * McGill University
 * michael.rabbat@mcgill.ca
 */
 
import java.io.*;
import java.net.*;

class GoBackNReceiver {
	
	private DatagramSocket receiverSocket;
	private byte expectedseqnum;
	private InetAddress senderIPAddress;
	private int senderPort;
	
	/**
	 * Create a new receiver socket and wait for an incoming connection
	 */ 
	public GoBackNReceiver(int port) {
		// STEP 1: Fill in this constructor method
        // Initialize receiverSocket as a DatagramSocket on the specified port
		try{
			receiverSocket = new DatagramSocket(senderPort);			
		} catch(SocketException e){
			System.out.println("Error creating receiverSocket" + e.getStackTrace());
		}
		
        // Then call waitForConnection()
		waitForConnection();
		
	}
	
	/**
	 * Wait for a sender to contact us with the Hello message
	 */
	private void waitForConnection() {
		// STEP 2: Block until a Hello packet is received, then initialize the receiver
        // First block until receiving an incoming message
		while(true){
			GoBackNPacket packet = receivePacket();

	        // Make sure it's a Hello (otherwise, ignore it and continue waiting)
			if(packet.isHello()){
				break;
			}
		}
		
        // If it's a Hello, initialize expectedseqnum to 0
		expectedseqnum = 0;
		
        // Send an ACK back to the sender using the sendAck() method
		sendAck(expectedseqnum);
		
        // Call the receiveMessage() method to receive the message
		receiveMessage();
	}
	
	/**
	 * Receive message from the sender
	 */
	private void receiveMessage() {
		
		byte seqnum;
		
		// STEP 3: Implement the main portion of the Go-back-N protocol
        // Contine processing Data packets until the Goodbye packet is received
		while(true){
			GoBackNPacket packet = receivePacket();
			
			// When Goodbye is received, close the socket and leave
			if(packet.isGoodbye()){
				break;
			}
			
	        // For each data packet received, check the sequence number
			seqnum = packet.getSequenceNumber();
			
	        // If it was the expected one, print the data to the command line
			if(seqnum == expectedseqnum){
				System.out.println(packet.getValue());
			}
			
	        // Send the appropriate ACK to the sender
			sendAck(seqnum);
		}
	}
	
	/**
	 * Helper method to receive a single packet from the sender
	 */
	private GoBackNPacket receivePacket() {
		byte[] data = new byte[3];
		DatagramPacket dgPacket = new DatagramPacket(data,3);
		try {
			receiverSocket.receive(dgPacket);
		} catch (Exception e) {
			System.out.println("Receiver error when receiving data packet");
			System.out.print(e);
			System.out.println("");
			System.exit(1);
		}
		return new GoBackNPacket(dgPacket);
	}
	
	/**
	 * Helper method to send an ACK for a given sequence number
	 */
	private void sendAck(byte sequenceNumber) {
		GoBackNPacket gbnPacket = new GoBackNPacket(GoBackNPacket.TYPE_ACK, sequenceNumber, 'a');
		DatagramPacket p = gbnPacket.toDatagramPacket();
		p.setAddress(senderIPAddress);
		p.setPort(senderPort);
		try {
			receiverSocket.send(p);
		} catch (Exception e) {
			System.out.println("Receiver error when sending ACK");
			System.out.print(e);
			System.out.println("");
			System.exit(1);
		}
	}
	

	/**
	 * Main method
	 */
	public static void main(String args[]) {
		while (true) {
			GoBackNReceiver receiver = new GoBackNReceiver(9876);
		}
	}
}