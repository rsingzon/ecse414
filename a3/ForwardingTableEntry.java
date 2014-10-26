/**
 * ECSE 414
 * Assignment 3
 */
import java.net.InetAddress;

/**
 * A class to hold individual entries in the forwarding table.
 * 
 * @author michaelrabbat
 *
 */
public class ForwardingTableEntry {
	private InetAddress startAddress;
	private InetAddress endAddress;
	private Integer outInterface;
	
	/**
	 * Construct a new ForwwardingTableEntry representing that the IP address range from startString
	 * to endString should be forwarded on the interface specified in interfaceString.
	 * 
	 * @param startString
	 *            String holding the start IP address (lower end of the address range)
	 * @param endString
	 *            String holding the end IP address (upper end of the address range)
	 * @param interfaceString
	 *            String holding the integer interface on which packets with a destination in the
	 *            given range should be forwarded
	 */
	public ForwardingTableEntry(String startString, String endString, String interfaceString) {
		try {
			startAddress = InetAddress.getByName(startString.trim());
			endAddress = InetAddress.getByName(endString.trim());
			outInterface = new Integer(interfaceString.trim());
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public int getStartAddress() {
		return bytesToInt(startAddress.getAddress());
	}
	
	public int getEndAddress() {
		return bytesToInt(endAddress.getAddress());
	}
	
	public int getInterface() {
		return outInterface.intValue();
	}
	
	private int bytesToInt(byte[] addressBytes) {
		int address = (((int) addressBytes[0] << 24) & 0xFF000000) ^ 
				(((int) addressBytes[1] << 16) & 0x00FF0000) ^
				(((int) addressBytes[2] << 8) & 0x0000FF00) ^
				(((int) addressBytes[3]) & 0x000000FF);
		return address;		
	}
}
