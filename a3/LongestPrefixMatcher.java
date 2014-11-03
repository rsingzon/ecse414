/**
 * ECSE 414
 * Assignment 3
 * 
 * Singzon, Ryan
 * 260397455
 */
import java.util.*;

/**
 * This class implements longest prefix matching. It constructs longest prefix matching rules given
 * a forwarding table. After being instantiated, it can be used to map the destination address of an
 * incoming packet to the appropriate interface by applying the longest prefix matching rules.
 * 
 * @author michaelrabbat
 * 
 */
public class LongestPrefixMatcher {
	List<LongestPrefixEntry> prefixTable;
	int defaultInterface;
	
	/**
	 * Construct a LongestPrefixMatcher from a given ForwardingTable.
	 * 
	 * @param forwardingTable
	 */
	public LongestPrefixMatcher(ForwardingTable forwardingTable) {
		// Instantiate the list in which to store prefix entries
		prefixTable = new ArrayList<LongestPrefixEntry>();
		
		// Enumerate through the entries in the forwarding table
		while (forwardingTable.hasMoreElements()) {
			addEntry(forwardingTable.nextElement());
		}
		defaultInterface = forwardingTable.getDefaultInterface();
	}
	
	/**
	 * This method is called by the constructor to make longest prefix rules for each each
	 * ForwardingTableEntry.
	 * 
	 * @param entry
	 */
	private void addEntry(ForwardingTableEntry entry) {
		int prefix = 0;
		int mask = 0;
		int outInterface = 0;
		
		// STEP 1: 
		// Determine the values of prefix, mask, and outInterface to put in the table for this entry 
		outInterface = entry.getInterface();
		
		// The prefix is the same as the first bits in the starting address
		prefix = entry.getEndAddress() & entry.getStartAddress();
		
		// Find the bits that change between the start and end addresses by XOR'ing them
		int changedBits = entry.getEndAddress() ^ entry.getStartAddress();
		
		// Get the position of the largest bit that has been changed
		int largestChangedBitPosition = (int) Math.ceil(Math.log(changedBits)/Math.log(2));
		
		// Get an integer that is all 1's after the largest bit that was changed
		int maskInverse = (int)(Math.pow(2, largestChangedBitPosition)-1);
		
		// Manually set the mask for IP ranges that vary by only 1 bit due to rounding 
		if(changedBits == 1){
			maskInverse = 1;
		}
		
		// NOT the mask inverse 
		mask = (0x7fffffff - maskInverse) | 0x80000000;
		// (Put your code above here)
		
		// Store this entry in prefixTable
		prefixTable.add(new LongestPrefixEntry(prefix, mask, outInterface));
	}
	
	/**
	 * Lookup the interface corresponding to a given address
	 * @param address the 32-bit integer version of the address to lookup
	 * @return the integer interface ID on which to forward this packet
	 */
	public int getInterfaceFor(int address) {
		// STEP 2:
		// Determine which interface a packet with the given destination address should be forwarded on.
		
		int outInterface = -1;
		// Iterate through the prefix table until the proper interface is found
		for(LongestPrefixEntry prefixEntry : prefixTable){
			outInterface = prefixEntry.getOutInterface();
			
			// First get the start and end addresses
			int startAddress = prefixEntry.getPrefix();
			
			// Get the end address by taking the NOT of the mask and AND'ing it with the prefix
			int maskInverse = ~prefixEntry.getMask();
			int endAddress = maskInverse | prefixEntry.getPrefix();
			
			// Set the MSB of the input IP and the start and end addresses to zero so they 
			// are always positive integers
			boolean startMSB =   (0x80000000 & startAddress) == 0x80000000;
			boolean addressMSB = (0x80000000 & address)      == 0x80000000;
			
			startAddress = startAddress & 0x7fffffff;
			endAddress = endAddress & 0x7fffffff;
			
			int maskedAddress = address & 0x7fffffff;

			// Check if the address lies between the start and end addresses
			if(startMSB == addressMSB){
				if(maskedAddress <= endAddress && maskedAddress >= startAddress){
					return outInterface;
				}
				else if(maskedAddress == startAddress){
					return outInterface;
				}
				else if(maskedAddress == endAddress){
					return outInterface;
				}
			}
		}
		return defaultInterface;
	}
	
	
	/**
	 * Overriding the toString() method to display the longest prefix matching rules.
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		for (int i=0; i < prefixTable.size(); i++) {
			sb.append(prefixTable.get(i).toString() + "\n");
		}
		sb.append("otherwise, " + defaultInterface);
		
		return sb.toString();
	}


	/**
	 * An internal class used to represent each entry in the longest prefix table. It is essentially
	 * a integer prefix, a bit mask indicating which bits in the prefix are relevant (1 = relevant,
	 * 0 = not relevant), the interface to forward on, and some getter methods.
	 * 
	 * @author michaelrabbat
	 * 
	 */
	private class LongestPrefixEntry {
		private int prefix;
		private int mask;
		private int outInterface;
		
		LongestPrefixEntry(int prefix, int mask, int outInterface) {
			this.prefix = prefix;
			this.mask = mask;
			this.outInterface = outInterface;
		}
		
		int getPrefix() {
			return prefix;
		}
		
		int getMask() {
			return mask;
		}
		
		int getOutInterface() {
			return outInterface;
		}
		
		/**
		 * Print the relevant part of the prefix (those bits for which mask==1), followed by the
		 * interface number
		 */
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			
			int bitmask = 0x80000000;
			for (int i=0; i<32; i++) {
				if ((mask & bitmask) == 0) {
					break;
				}

				if ((prefix & bitmask) != 0) {
					sb.append("1");
				} else {
					sb.append("0");
				}
				
				bitmask = bitmask >>> 1;
			}
			
			return sb.toString() + ", " + outInterface;
		}
	}
}
