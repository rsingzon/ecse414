/**
 * ECSE 414
 * Assignment 3
 */
import java.io.*;
import java.net.InetAddress;
import java.util.*;

/**
 * This is the "main" class for this problem. It loads a forwarding table from file, converts the
 * forwarding table entries to longest prefix matching rules, loads a list of IP addresses to
 * forward, performs longest prefix matching for each IP address, and then prints the results to
 * file or outputs them to the console.
 * 
 * The program is executed from the command line and takes up to four arguments: 1. The name of the
 * file containing the forwarding table information; 2. The name of the file containing the list of
 * IP addresses to test (map to interfaces); 3. (optional) A file in which to write the prefix
 * table; 4. (optional) A file in which to write the IP-to-interface mappings for test IP addresses.
 * The last two arguments are optional, and if they are not specified then the output will be
 * displayed in the console.
 * 
 * @author michaelrabbat
 * 
 */
public class Hw3Main {
	public static void main(String args[]) {
		// Parse command line arguments
		if ((args.length < 2) || (args.length > 4)) {
			String errorMessage = "Usage: java Hw3Main forwardingTableFile testIPInputFile [prefixOutputFile] [testIPOutputFile]";
			System.out.println("Error using Hw3Main");
			System.out.println(errorMessage);
			System.exit(0);
		}

		String forwardingTableFile = args[0];
		String testIPInputFile = args[1];

		String prefixOutputFile = null;
		String testIPOutputFile = null;
		if (args.length >= 3) {
			prefixOutputFile = args[2];
		}
		if (args.length >= 4) {
			testIPOutputFile = args[3];
		}

		try {
			// Load the ForwardingTable from file
			ForwardingTable forwardingTable = new ForwardingTable(forwardingTableFile);

			// Make a set of longest prefix matching rules from the table
			long startTime = System.nanoTime();
			LongestPrefixMatcher longestPrefixMatcher = new LongestPrefixMatcher(forwardingTable);
			long finishTime = System.nanoTime();
			Double runTime = new Double(((double)finishTime - (double)startTime) / (double)1000000);
			System.out.println("");
			System.out.println("Computed prefix matching rules in " + runTime + " ms");

			// Output the longest prefix rules
			if (prefixOutputFile == null) {
				System.out.println(longestPrefixMatcher.toString());
				System.out.println("");
			} else {
				BufferedWriter writer = new BufferedWriter(new FileWriter(prefixOutputFile));
				writer.write(longestPrefixMatcher.toString());
				writer.flush();
				writer.close();
			}

			// Load the set of test IP addresses
			List<TestPair> testAddresses = loadTestData(testIPInputFile);

			// Map all IP addresses to interfaces
			startTime = System.nanoTime();
			for (int i = 0; i < testAddresses.size(); i++) {
				TestPair testPair = testAddresses.get(i);
				testPair.setInterface(longestPrefixMatcher.getInterfaceFor(testPair.getAddress()));
			}
			finishTime = System.nanoTime();
			runTime = new Double(((double)finishTime - (double)startTime) / (double)1000000);
			Double avgTime = new Double(runTime.doubleValue() / (double)testAddresses.size());
			System.out.println("Matched " + testAddresses.size() + " prefixes in " + runTime
					+ " ms (" + avgTime + " ms per lookup)");
			System.out.println("");

			// Output the results to file or display them on the screen
			if (testIPOutputFile == null) {
				for (int i = 0; i < testAddresses.size(); i++) {
					System.out.println(testAddresses.get(i));
				}
			} else {
				BufferedWriter writer = new BufferedWriter(new FileWriter(testIPOutputFile));
				for (int i = 0; i < testAddresses.size(); i++) {
					writer.write(testAddresses.get(i) + "\n");
				}
				writer.flush();
				writer.close();
			}

			System.out.println("All done!");
			System.out.println("");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println(e.getStackTrace());
			System.exit(-1);
		}
	}

	/**
	 * Helper method to load the list of Test IP addresses from a file
	 * 
	 * @param testFileName
	 *            name of the file containing the test IP addresses
	 * @return a list of TestPairs
	 * @throws Exception
	 *             on file IO errors
	 */
	static List<TestPair> loadTestData(String testFileName) throws Exception {
		List<TestPair> testList = new ArrayList<TestPair>();

		// Open the test file
		BufferedReader reader = new BufferedReader(new FileReader(testFileName));

		// Iterate through each line until reaching the end of file
		String nextLine;
		while ((nextLine = reader.readLine()) != null) {
			// Skip empty lines and lines that start with #
			if (nextLine.equals("") || nextLine.startsWith("#")) {
				continue;
			}

			testList.add(new TestPair(InetAddress.getByName(nextLine.trim())));
		}
		reader.close();

		return testList;
	}

	/**
	 * This is a simple class to hold (IP Address, interface) pairs for testing the
	 * LongestPrefixMatcher.
	 * 
	 * @author michaelrabbat
	 * 
	 */
	static class TestPair {
		int address;
		int interfaceID;

		/**
		 * The constructor initializes the address field to that specified in the argument
		 * 
		 * @param inetAddress
		 *            used to initialize the address
		 */
		public TestPair(InetAddress inetAddress) {
			address = bytesToInt(inetAddress.getAddress());
			interfaceID = -1;
		}

		/**
		 * Utility method to convert the byte-array representation of an IPv4 address to a 32-bit
		 * integer with the left-most bits representing the most significant byte of the IP address
		 * 
		 * @param addressBytes
		 *            array of bytes, with the byte in position 0 corresponding to the most
		 *            significant byte, as returned by InetAddress.getAddress()
		 * @return integer representation of the IP address
		 */
		private int bytesToInt(byte[] addressBytes) {
			int address = (((int) addressBytes[0] << 24) & 0xFF000000)
					^ (((int) addressBytes[1] << 16) & 0x00FF0000)
					^ (((int) addressBytes[2] << 8) & 0x0000FF00)
					^ (((int) addressBytes[3]) & 0x000000FF);
			return address;
		}

		int getAddress() {
			return address;
		}

		void setInterface(int value) {
			interfaceID = value;
		}

		@Override
		public String toString() {
			return ipIntToString(address) + ", " + interfaceID;
		}

		private String ipIntToString(int address) {
			int a = (address & 0xFF000000) >>> 24;
			int b = (address & 0x00FF0000) >>> 16;
			int c = (address & 0x0000FF00) >>> 8;
			int d = (address & 0x000000FF);
			return a + "." + b + "." + c + "." + d;
		}
	}
}
