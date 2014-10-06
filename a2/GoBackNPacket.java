/** 
 * GoBackNPacket - Assignment 2 for ECSE 414
 *
 * Simple Go-back-N packet data structure.
 *
 * Michael Rabbat
 * McGill University
 * michael.rabbat@mcgill.ca
 */
 
import java.net.*;
 
class GoBackNPacket {
	// Types of packets
	static final byte TYPE_HELLO = 1;
	static final byte TYPE_GOODBYE = 2;
	static final byte TYPE_DATA = 3;
	static final byte TYPE_ACK = 4;

	private byte type;
	private byte sequenceNumber;
	private byte value;
	
	/**
	 * Constructor that also initializes the values
	 */
	public GoBackNPacket(byte type, byte sequenceNumber, char value) {
		this.type = type;
		this.sequenceNumber = sequenceNumber;
		this.value = (byte)value;
	}
	
	/**
	 * Constructor that initializes values from a DatagramPacket
	 */
	public GoBackNPacket(DatagramPacket packet) {
		byte[] data = packet.getData();
		this.type = data[0];
		this.sequenceNumber = data[1];
		this.value = data[2];
	}
	
	/**
	 * Test if this is a Hello-type packet
	 */
	public boolean isHello() {
		return (type == TYPE_HELLO);
	}
	
	/**
	 * Test if this is a Goodbye-type packet
	 */
	public boolean isGoodbye() {
		return (type == TYPE_GOODBYE);
	}
	
	/**
	 * Test if this is a Data-type packet
	 */
	public boolean isData() {
		return (type == TYPE_DATA);
	}
	
	/**
	 * Test if this is an ACK-type packet
	 */
	public boolean isAck() {
		return (type == TYPE_ACK);
	}
	
	/**
	 * Get the sequence number of this packet
	 */
	public byte getSequenceNumber() {
		return sequenceNumber;
	}
	
	/**
	 * Get the data value from this packet
	 */
	public char getValue() {
		return (char)value;
	}
	
	/**
	 * Convert this GoBackNPacket to a DatagramPacket
	 */
	public DatagramPacket toDatagramPacket() {
		byte[] data = new byte[3];
		data[0] = type;
		data[1] = sequenceNumber;
		data[2] = value;
		return new DatagramPacket(data, 3);
	}
}