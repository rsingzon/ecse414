/**
 * ECSE 414
 * Assignment 3
 */
import java.io.*;
import java.util.*;

/**
 * A table holding ForwardingTableEntries which contain IP address-to-interface mappings for forwarding. 
 * 
 * @author michaelrabbat
 *
 */
public class ForwardingTable implements Enumeration<ForwardingTableEntry> {
	private List<ForwardingTableEntry> table;
	private Integer defaultInterface = null;
	private int enumerationIndex = 0;
	
	/**
	 * Load a ForwardingTable from file. The file should contain one line for each forwarding table
	 * entry. The lines should follow one of two formats. The first format is of the form
	 * "[startAddress], [endAddress], [interfaceNumber]" where startAddress and endAddress specify
	 * the IP address range and interfaceNumber is the interface on which to forward those packets.
	 * The second format is the catch-all of the form "otherwise, [interfaceNumber]"
	 * 
	 * @param csvFilename
	 *            comma separated values file containing the fowarding table entries
	 * @throws Exception
	 *            on I/O errors (opening the file) or when encountering badly formatted lines
	 */
	public ForwardingTable(String csvFilename) throws Exception {
		// Initialize the ArrayList used for internal storage of the ForwardingTable
		table = new ArrayList<ForwardingTableEntry>();
		
		// Declare and initialize variables used for parsing the csv file
		String nextLine = null;
		StringTokenizer tokenizer;
		String startString = null;
		String endString = null;
		String interfaceString = null;
		
		// Open the file and parse it line-by-line 
		BufferedReader reader = new BufferedReader(new FileReader(csvFilename));
		for (int lineNumber = 1; (nextLine = reader.readLine()) != null; lineNumber++) {
			// Skip empty lines and lines that start with #
			if (nextLine.equals("") || nextLine.startsWith("#")) {
				continue;
			}
			
			// Break the line into tokens at each comma
			tokenizer = new StringTokenizer(nextLine, ",");
			
			// Check if this line is an "otherwise" line
			if ((tokenizer.countTokens() == 2) && (tokenizer.nextToken().equalsIgnoreCase("otherwise"))) {
				if (defaultInterface == null) {
					defaultInterface = new Integer(tokenizer.nextToken().trim());
				} else {
					reader.close();
					throw new Exception("Error in " + csvFilename + " \n" + "Declaring 'otherwise' interface for a second time at line " + lineNumber);
				}
			} else if (tokenizer.countTokens() == 3) {
				startString = tokenizer.nextToken();
				endString = tokenizer.nextToken();
				interfaceString = tokenizer.nextToken();
				table.add(new ForwardingTableEntry(startString, endString, interfaceString));
			} else {
				reader.close();
				throw new Exception("Improperly formatted input in " + csvFilename + " at line " + lineNumber);
			}
		}
		reader.close();
		
		System.out.println("Successfully created ForwardingTable from file " + csvFilename);
		System.out.println("The table contains " + table.size() + " entries, and all other traffic is forwarded on interface " + defaultInterface);
	}
	
	/**
	 * Part of the Enumeration interface, for easily processing all ForwardingTableEntries.
	 * Returns true if there are more elements to enumerate.
	 */
	public boolean hasMoreElements() {
		return (enumerationIndex < table.size());
	}
	
	/**
	 * Also part of the Enumeration interface, for easily processing all ForwardingTableEntries.
	 * Returns the next ForwardingTableEntry in the list 
	 */
	public ForwardingTableEntry nextElement() throws NoSuchElementException {
		if (hasMoreElements()) {
			ForwardingTableEntry nextEntry = table.get(enumerationIndex);
			enumerationIndex++;
			return nextEntry;
		} else {
			throw new NoSuchElementException();
		}
	}
	
	/**
	 * Returns the default interface on which to forward packets which do not match any
	 * ForwardingTableEntry.
	 */
	public int getDefaultInterface() {
		return defaultInterface.intValue();
	}
}
