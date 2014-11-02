ECSE 414
Assignment 3
Additional Test Sets

This folder contains four additional test sets you can use to test your longest prefix matching program. They have the following characteristics (increasing in size):

TestSet		#Interfaces		#Prefix Entries		#Test Addresses
2			10				10					100
3			32				64					5000
4			256				512					100000
5			128				8192				2000000

Note that the number of interfaces mentioned above does not count the default ("otherwise") interfaces. Also, in test sets 2-5, the same interface may be used for multiple prefix table entries.

The TestSet#.txt, TestSet#Truth.txt, and TestTable#.txt files all need to be used together, for the same number. (If you try the TestSet3.txt addresses with TestTable4.txt, the interface numbers in TestSet3Truth.txt won't match your results.)

The Hw3Main.java code provided includes a few lines that time how long it takes your implementation to build the prefix table and to find interfaces (prefix match) for all of the test addresses. This is purely for your interest; you will only be graded on the correctness of your code and its output, and not on its efficiency (as long as it runs in a reasonable amount of time on our test machines).

For example, running TestSet 5 on my simple (non-optimized) implementation on a MacBook Air with 2GHz Intel Core i7 processor, I get the following output:


michaelrabbat ecse414hw3 solution$ java Hw3Main TestTable5.txt TestSet5
TestSet5.txt       TestSet5Truth.txt  
michaelrabbat ecse414hw3 solution$ java Hw3Main TestTable5.txt TestSet5.txt PrefixOutput5.txt TestOutput5.txt 
Successfully created ForwardingTable from file TestTable5.txt
The table contains 8192 entries, and all other traffic is forwarded on interface 128

Computed prefix matching rules in 12.282143 ms
Matched 2000000 prefixes in 23100.196801 ms (0.0115500984005 ms per lookup)

All done!

michaelrabbat ecse414hw3 solution$ 
