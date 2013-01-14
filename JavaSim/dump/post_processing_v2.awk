BEGIN {} 

NR==FNR {
		if ($5 == "CREATED") {	
			CREATED = CREATED + 1;		
			IPKT_NODEWISE[$9] = IPKT_NODEWISE[$9] + 1;
			IPKT_OBJECTWISE[$6] = IPKT_OBJECTWISE[$6] + 1;	
		}
		
		if ($5 == "CRTEDTO") {	
			CRTEDTO	= CRTEDTO + 1;
			
			IPKTTO_NODEWISE[$9] = IPKTTO_NODEWISE[$9] + 1;
			IPKTTO_OBJECTWISE[$6] = IPKTTO_OBJECTWISE[$6] + 1;
			interestPKTTO[$14] = interestPKTTO[$14] + 1;
		}
		
		#I might want to use previous node, and not current. I want to know what action the node performed
		
		if ($5 == "CRTDPRD") {	
			CRTDPRD = CRTDPRD + 1;	
			if ($20 == 1) {
			
				TOTAL_REQUEST_RECEIVED_BY_NODE[$9] = TOTAL_REQUEST_RECEIVED_BY_NODE[$9] + 1;
				TOTAL_REQUEST_RECEIVED_BY_OBJECT[$3] = TOTAL_REQUEST_RECEIVED_BY_OBJECT[$3] + 1;
				
				SOURCE_HIT_BY_NODE[$9] = SOURCE_HIT_BY_NODE[$9] + 1;	
				SOURCE_HIT_BY_OBJECT[$3] = SOURCE_HIT_BY_OBJECT[$3] + 1;					
				SOURCE_HIT = SOURCE_HIT + 1;
			}
			
			if ($20 == 0) {
			
				TOTAL_REQUEST_RECEIVED_BY_NODE[$9] = TOTAL_REQUEST_RECEIVED_BY_NODE[$9] + 1;
				TOTAL_REQUEST_RECEIVED_BY_OBJECT[$3] = TOTAL_REQUEST_RECEIVED_BY_OBJECT[$3] + 1;
				
				CACHE_HIT_BY_NODE[$9] = CACHE_HIT_BY_NODE[$9] + 1;
				CACHE_HIT_BY_OBJECT[$3] = CACHE_HIT_BY_OBJECT[$3] + 1;							
				CACHE_HIT = CACHE_HIT + 1;
			}
		}
		
		if ($5 == "CRTDPRDA") {	
			
			CRTDPRDA = CRTDPRDA + 1;
			
			PIT_HIT_BY_NODE[$9] = PIT_HIT_BY_NODE[$9] + 1;
			PIT_HIT_BY_OBJECT[$3] = PIT_HIT_BY_OBJECT[$3] + 1;	
			PIT_HIT = PIT_HIT + 1;	
		
			TOTAL_REQUEST_RECEIVED_BY_NODE[$9] = TOTAL_REQUEST_RECEIVED_BY_NODE[$9] + 1;
			TOTAL_REQUEST_RECEIVED_BY_OBJECT[$3] = TOTAL_REQUEST_RECEIVED_BY_OBJECT[$3] + 1;
				
			#if ($23 == 1) {
			
			#	SOURCE_HIT_BY_NODE[$9] = SOURCE_HIT_BY_NODE[$9] + 1;	
			#	SOURCE_HIT_BY_OBJECT[$3] = SOURCE_HIT_BY_OBJECT[$3] + 1;					
			#	SOURCE_HIT = SOURCE_HIT + 1;
			#}
			
			#if ($23 == 0) {
			#	
			# 	CACHE_HIT_BY_NODE[$9] = CACHE_HIT_BY_NODE[$9] + 1;
			#	CACHE_HIT_BY_OBJECT[$3] = CACHE_HIT_BY_OBJECT[$3] + 1;							
			#	CACHE_HIT = CACHE_HIT + 1;
			#}	
		}
		
		if($2 == "i") {
			
			#This will provide PITs activity hop-wise
			for (i = 0; i <= depth; i++) {
				if ($5 == "DESTROY" && $10 == i) {				
					#Destroyed at birth.
					if ($12 == 8) {
						#PIT HIT
						PIT_HIT_NODE[$7] = PIT_HIT_NODE[$7] + 1;
						PIT_HIT_OBJECT[$6] = PIT_HIT_OBJECT[$6] + 1; 
						PITENTRY_HIT[i] = PITENTRY_HIT[i] + 1; 
					}
					if ($12 == 13) {
						#REDUNDANT_PIT_HIT
						R_PIT_HIT_NODE[$7] = R_PIT_HIT_NODE[$7] + 1;
						R_PIT_HIT_OBJECT[$6] = R_PIT_HIT_OBJECT[$6] + 1; 
						R_PITENTRY_HIT[i] = R_PITENTRY_HIT[i] + 1;
					}			
				}
			}
			
			depthEnd = depth + 1;		
			for (i = depth + 1; i <= diameter; i++) {
				if ($5 == "DESTROY" && $10 == i) {		
					if ($12 == 8) {
						#PIT HIT
						PIT_HIT_NODE[$7] = PIT_HIT_NODE[$7] + 1;
						PIT_HIT_OBJECT[$6] = PIT_HIT_OBJECT[$6] + 1; 
						PITENTRY_HIT[depthEnd] = PITENTRY_HIT[depthEnd] + 1; 
					}
					if ($12 == 13) {
						#REDUNDANT_PIT_HIT
						R_PIT_HIT_NODE[$7] = R_PIT_HIT_NODE[$7] + 1;
						R_PIT_HIT_OBJECT[$6] = R_PIT_HIT_OBJECT[$6] + 1; 
						R_PITENTRY_HIT[depthEnd] = R_PITENTRY_HIT[depthEnd] + 1;
					}
				}
			}
			
			if ($5 == "DESTROY" && $12 == 9) {		
				PIT_EXP = PIT_EXP + 1;
			}								
		}
		
		if ($5 == "ENQUEUE" && $2 == "i" && $10 != 0) {	
		
			checktotalinthops = checktotalinthops + 1		
			
			if ($12 == 7) {
				
				#I might want to use previous node, and not current. I want to know what action the node performed
				FIB_HIT_BY_NODE[$8] = FIB_HIT_BY_NODE[$8] + 1;
				FIB_HIT_BY_INTID[$3] = FIB_HIT_BY_INTID[$3] + 1;
				FIB_HIT_BY_OBJECT[$6] = FIB_HIT_BY_OBJECT[$6] + 1;			
				
				FIB_HIT = FIB_HIT + 1;
				TOTAL_INTEREST_HOPS = TOTAL_INTEREST_HOPS + 1;
			}
			
			if ($12 == 0) {
			
				#I might want to use previous node, and not current. I want to know what action the node performed
				FLOODING_BY_NODE[$8] = FLOODING_BY_NODE[$8] + 1;
				FLOODING_BY_INTID[$3] = FLOODING_BY_INTID[$3] + 1;
				FLOODING_BY_OBJECT[$6] = FLOODING_BY_OBJECT[$6] + 1;
				
				FLOODING = FLOODING + 1;
				TOTAL_INTEREST_HOPS = TOTAL_INTEREST_HOPS + 1;
			}
					
		}
		
		if($5 == "ENQUEUE" && $2 == "d") {	
			# We do not count CRTPRDA, which is counted in PIT consumption count.
			TOTAL_DATA_HOPS = TOTAL_DATA_HOPS + 1;	
		}
		
		if($5 == "DESTROY" && $2 == "d") {
		
			if ($12 == 1) {	
				SUP_NO_PIT_INDEX_NODE[$7] = SUP_NO_PIT_INDEX_NODE[$7] + 1;
				SUP_NO_PIT_INDEX_OBJECT[$3] = SUP_NO_PIT_INDEX_OBJECT[$3] + 1;
				SUPRESSION_NO_PIT_INDEX = SUPRESSION_NO_PIT_INDEX + 1;	
				TOTAL_DATA_DESTROY = TOTAL_DATA_DESTROY + 1; 		
			}
		
			if ($12 == 2) {
				SUP_ALREADY_NODE[$7] = SUP_ALREADY_NODE[$7] + 1;
				SUP_ALREADY_OBJECT[$3] = SUP_ALREADY_OBJECT[$3] + 1;
				SUPRESSION_ALREADY_SERVED = SUPRESSION_ALREADY_SERVED + 1;	
				TOTAL_DATA_DESTROY = TOTAL_DATA_DESTROY + 1; 
			}
		
			if ($12 == 6) {
			
				SUPRESSION_DEST_NODE = SUPRESSION_DEST_NODE + 1;			
				USEFUL_DATA_HOPS = USEFUL_DATA_HOPS + $18;			
				
				if ($20 == 1 && $27 == 0) {
				
					TOTAL_REQUEST_ISSUED_BY_NODE[$7] = TOTAL_REQUEST_ISSUED_BY_NODE[$7] + 1;
					#TOTAL_REQUEST_ISSUED_BY_OBJECT[$3] = TOTAL_REQUEST_ISSUED_BY_OBJECT[$3] + 1;
				
					SOURCE_HIT_BY_NODE_AT_DEST[$7] = SOURCE_HIT_BY_NODE_AT_DEST[$7] + 1;	
					#SOURCE_HIT_BY_OBJECT_AT_DEST[$3] = SOURCE_HIT_BY_OBJECT_AT_DEST[$3] + 1;						
					SOURCE_HIT_AT_DEST = SOURCE_HIT_AT_DEST + 1;
				}
			
				if ($20 == 0 && $27 == 0) {
				
					TOTAL_REQUEST_ISSUED_BY_NODE[$7] = TOTAL_REQUEST_ISSUED_BY_NODE[$7] + 1;
					#TOTAL_REQUEST_ISSUED_BY_OBJECT[$3] = TOTAL_REQUEST_ISSUED_BY_OBJECT[$3] + 1;
					
					CACHE_HIT_BY_NODE_AT_DEST[$7] = CACHE_HIT_BY_NODE_AT_DEST[$7] + 1;
					#CACHE_HIT_BY_OBJECT_AT_DEST[$3] = CACHE_HIT_BY_OBJECT_AT_DEST[$3] + 1;			
					CACHE_HIT_AT_DEST = CACHE_HIT_AT_DEST + 1;				
				}
				
				if (($20 == 0 || $20 == 1) && $27 == 1) {
				
					TOTAL_REQUEST_ISSUED_BY_NODE[$7] = TOTAL_REQUEST_ISSUED_BY_NODE[$7] + 1;
					#TOTAL_REQUEST_ISSUED_BY_OBJECT[$3] = TOTAL_REQUEST_ISSUED_BY_OBJECT[$3] + 1;
				
					PIT_HIT_BY_NODE_AT_DEST[$7] = PIT_HIT_BY_NODE_AT_DEST[$7] + 1;
					#PIT_HIT_BY_OBJECT_AT_DEST[$3] = PIT_HIT_BY_OBJECT_AT_DEST[$3] + 1;			
					PIT_HIT_AT_DEST = PIT_HIT_AT_DEST + 1;			
				}
			}
			
			if ($12 == 12) {	
			
				SUP_NO_PIT_EXP_NODE[$7] = SUP_NO_PIT_EXP_NODE[$7] + 1;
				SUP_NO_PIT_EXP_OBJECT[$3] = SUP_NO_PIT_EXP_OBJECT[$3] + 1;	
				SUPRESSION_NO_PIT_INDEX_DUE_PIT_EXPIRATION = SUPRESSION_NO_PIT_INDEX_DUE_PIT_EXPIRATION + 1;	
				TOTAL_DATA_DESTROY = TOTAL_DATA_DESTROY + 1;
			}
			
			if ($12 == 14) {	
				
				SUP_NO_PIT_ORIG_NODE[$7] = SUP_NO_PIT_ORIG_NODE[$7] + 1;
				SUP_NO_PIT_ORIG_OBJECT[$3] = SUP_NO_PIT_ORIG_OBJECT[$3] + 1;		
				SUPRESSION_NO_PIT_ENTRY_ORIGDPKT = SUPRESSION_NO_PIT_ENTRY_ORIGDPKT + 1	
				TOTAL_DATA_DESTROY = TOTAL_DATA_DESTROY + 1;
			}		
		}
	}
	
NR>FNR {
	
		#Values from this file "*_simulation-level_values"
		NPITC = $1;
		UPITC = $2;
		DPITC = $3;
		APITCON = $4;
		PITCONU = $5;
		EXPATNODES = $6;
		EXPINSIM = $7;
		APITEXP = $8;	
}
 
END {
	
	print "1. Interest Packets:" > "Summary.txt"
	
	print "\n1A. Original Interest Packets (CREATED)\t\t\t	: " CREATED >> "Summary.txt"
	print "1B. Retransmitted Interest Packets (CRTEDTO)\t	: " CRTEDTO >> "Summary.txt"
	print "1C. Total (1A + 1B): \t\t\t\t\t\t\t\t: " CRTEDTO + CREATED >> "Summary.txt"
	print "1D. Ratio of TO (1B/1A x 100): \t\t\t\t\t\t: " (CRTEDTO/CREATED * 100.0)"%" >> "Summary.txt"	
	print "Note: For a detailed node-wise, object-wise breakdown, interest-wise of 1A and 1B could be seen '1A-1B_node.txt', '1A-1B_object.txt', and '1B_interest.txt', respectively." >> "Summary.txt"
	print "\n" >> "Summary.txt"	
	
	
	print "2. Data Packets:" > "Summary.txt"
	
	print "\n2A. Useful number of hops (USEFUL HOPS)\t	: " USEFUL_DATA_HOPS  >> "Summary.txt"
	print "2B. Total number of hops (TOTAL_DATA_HOPS)\t: " TOTAL_DATA_HOPS >> "Summary.txt"
	print "2C. Ratio of Useful hops to total \t\t\t: " (USEFUL_DATA_HOPS/TOTAL_DATA_HOPS * 100.0)"%">> "Summary.txt"		
	
	print "\n2D. Hit at a cache\t\t\t\t\t\t\t\t\t		: " CACHE_HIT >> "Summary.txt"
	print "2E. Hit at a source\t\t\t\t\t\t\t\t\t\t	: " SOURCE_HIT >> "Summary.txt"
	print "2F. Data packets created at Source or Cache (CRTDPRD))\t\t: " CRTDPRD >> "Summary.txt"
	
	print "\n2G. Data Packet satisfied at PIT (PIT hit)\t	: " PIT_HIT >> "Summary.txt"	
	print "2H. Data packets created at PIT (CRTDPRDA))\t	: " CRTDPRDA >> "Summary.txt"
	
	print "\n2I. Total Data Packets Created (2F + 2H): " CRTDPRD + CRTDPRDA >> "Summary.txt"
	print "Note: For a detailed node and object breakdown of 2D, 2E, and 2G see '2D_2E_2G_node.txt' and '2D_2E_2G_2J-2L_object.txt', respectively" >> "Summary.txt"
	
	print "\n2J. Data Packets satisfied from Source\t\t\t\t	: "  SOURCE_HIT_AT_DEST >> "Summary.txt"
	print "2K. Data Packets satisfied from Cache\t\t\t\t	: "  CACHE_HIT_AT_DEST >> "Summary.txt"
	print "2L. Data Packets satisfied from PIT\t\t\t\t		: "  PIT_HIT_AT_DEST >> "Summary.txt"
	print "2M. Total data packets satisfied (SUPRESSION_DEST_NODE)	: "  SUPRESSION_DEST_NODE >> "Summary.txt"
	print "Note: For a detailed node and object breakdown of 2J, 2K, and 2L see '2J-2L_node.txt' and '2D_2E_2G_2J-2L_object.txt', respectively" >> "Summary.txt"
	
	print "\n2N. No pit Entry for data packet (SUPRESSION_NO_PIT_INDEX)\t						: " SUPRESSION_NO_PIT_INDEX >> "Summary.txt"
	print "2O. Expired pit Entry for data packet (SUPRESSION_NO_PIT_INDEX_DUE_PIT_EXPIRATION)	: " SUPRESSION_NO_PIT_INDEX_DUE_PIT_EXPIRATION >> "Summary.txt"
	print "2P. Complicated (SUPRESSION_NO_PIT_ENTRY_ORIGDPKT)\t\t\t\t\t				: "  SUPRESSION_NO_PIT_ENTRY_ORIGDPKT >> "Summary.txt"
	print "2Q. Duplicate arrival of data packet at destination (SUPRESSION_ALREADY_SERVED)		: " SUPRESSION_ALREADY_SERVED >> "Summary.txt"	
	print "2R. Total number of data packets destroyed (TOTAL_DATA_DESTROY)						: " TOTAL_DATA_DESTROY >> "Summary.txt"
	
	print "\n2S. Accounting for data Packets created (2I) is (2M + 2R): " SUPRESSION_DEST_NODE + TOTAL_DATA_DESTROY>> "Summary.txt"
	print "Note: For a detailed node and object breakdown of 2N-2Q see '2N-2Q_node.txt' and '2N-2Q_object.txt', respectively" >> "Summary.txt"
	print "\n" >> "Summary.txt"
	
	
	print "3. Forwarding vs. Flooding per hop:" > "Summary.txt"
	
	print "\n3A. Hops using FIBS (FIB_HIT)\t\t\t\t	: " FIB_HIT " or " (FIB_HIT/(TOTAL_INTEREST_HOPS)*100)"%"  >> "Summary.txt"
	print "3B. Hops using flooding (FLOODING)\t\t\t	: " FLOODING " or " (FLOODING/(TOTAL_INTEREST_HOPS)*100)"%" >> "Summary.txt"
	print "3C. Total number of hops for Interest packets\t: " TOTAL_INTEREST_HOPS >> "Summary.txt"
	print "Note: For a detailed node-wise and object-wise breakdown of 3A-3C, see '3A-3C_node.txt' and '3A-3C_object.txt', respectively" >> "Summary.txt"
	print "\n" >> "Summary.txt"
	
	
	print "4. PIT Entries:" > "Summary.txt"
	
	print "\n4A. New PIT Entries created: " NPITC " or " (NPITC/(NPITC + UPITC)*100)"%" >> "Summary.txt"
	print "4B. PIT Hits (Entry created, but suppressed): " UPITC " or " (UPITC/(NPITC + UPITC)*100)"%"  >> "Summary.txt"
	for (x = 0; x <= depth; x++) {
		print "\t\tPIT Hits at Depth " x " is: " PITENTRY_HIT[x] >> "Summary.txt"		
	}
	print "\t\tPIT Hits at Depth > " x " is: " PITENTRY_HIT[depth+1] >> "Summary.txt"
	
	print "\n4C. Total number of PIT entries created (4A + 4B): " NPITC + UPITC >> "Summary.txt"
	
	print "\n4D. PIT entries consumed to forward data packets: " PITCONU  " or " (PITCONU/(APITCON + APITEXP)*100)"%" >> "Summary.txt"
	print "4E. PIT entries expired or unused: " APITEXP + (APITCON - PITCONU)   " or " ((APITEXP + (APITCON - PITCONU))/(APITCON + APITEXP)*100)"%">> "Summary.txt"
	print "4F. Accounting for PIT entries that were created (4D + 4E): " (APITCON + APITEXP) >> "Summary.txt"
	
	print "\n4G. Redundant PIT Hits (Interest from duplicate interface, hence suppressed): " DPITC >> "Summary.txt"
	for (x = 0; x <= depth; x++) {
		print "\t\tRedundant PIT Hits at Depth " x " is: " R_PITENTRY_HIT[x] >> "Summary.txt"		
	}
	print "\t\tRedundant PIT Hits at Depth > " x " is: " R_PITENTRY_HIT[depth+1] >> "Summary.txt"
	
	print "\n4H. Suppressed PIT entries ((4B + 4G) / 4C): " ((UPITC + DPITC)/(NPITC + UPITC)) * 100"%" >> "Summary.txt"
	print "Note: For a detailed node and object breakdown of 4B and 4G see '4B_4G_node.txt' and '4B_4G_object.txt', respectively" >> "Summary.txt"
	
	print "\n4I. Utilized PIT hit entries (2H/4B): " (CRTDPRDA/(UPITC)) * 100"%" >> "Summary.txt"
	print "4J. PIT hit entries that satisfied a request (2L/4B): " (PIT_HIT_AT_DEST/(UPITC)) * 100"%" >> "Summary.txt"
	
	total1 = 0;
	total2 = 0;
	for (x = 0; x < nodes; x++) {
		print "NodeID: " x "\tInterests Created: " IPKT_NODEWISE[x] "\tInterests Recreated: " IPKTTO_NODEWISE[x] > "1A_1B_node.txt"
		total1 = total1 + IPKT_NODEWISE[x]
		total2 = total2 + IPKTTO_NODEWISE[x]			
	}
	print "Total (Column 2): " total1 > "1A_1B_node.txt"
	print "Total (Column 3): " total2 > "1A_1B_node.txt"
	
	total1 = 0;
	total2 = 0;
	for (x = 0; x < objects; x++) {
		print "ObjectID: " x "\tObjects Created: " IPKT_OBJECTWISE[x] "\tObjects Re-requested: " IPKTTO_OBJECTWISE[x] > "1A_1B_object.txt"
		total1 = total1 + IPKT_OBJECTWISE[x]
		total2 = total2 + IPKTTO_OBJECTWISE[x]			
	}
	print "Total (Column 2): " total1 > "1A_1B_object.txt"
	print "Total (Column 3): " total2 > "1A_1B_object.txt"
	
	
	total = 0;
	
	for (x = 0; x <= interests; x++) {
		if (x in interestPKTTO) {	
			print "InterestID " x "\tExpired: " interestPKTTO[x] > "1B_interest.txt"
			total = total + interestPKTTO[x]
		}		
	}
	
	print "Total (Column 2): " total > "1B_interest.txt"
	
	total1 = 0;
	total2 = 0;
	total3 = 0;
	total4 = 0;
	total5 = 0;
	total6 = 0;
	total7 = 0;
	total8 = 0;
	for (x = -1; x < nodes; x++) {
		
		print "Total requests received at Node " x ": " TOTAL_REQUEST_RECEIVED_BY_NODE[x] "\tSatisfied using Source: " SOURCE_HIT_BY_NODE[x] "\tSatisfied using Cache: " CACHE_HIT_BY_NODE[x] "\tSatisfied using PIT: " PIT_HIT_BY_NODE[x] >> "2D_2E_2G_node.txt";
		print "Total requests issued at Node " x ": " TOTAL_REQUEST_ISSUED_BY_NODE[x] "\tSatisfied by Source: " SOURCE_HIT_BY_NODE_AT_DEST[x] "\tSatisfied by Cache: " CACHE_HIT_BY_NODE_AT_DEST[x] "\tSatisfied by PIT: " PIT_HIT_BY_NODE_AT_DEST[x] >> "2J-2L_node.txt";
		
		total1 = total1 + TOTAL_REQUEST_RECEIVED_BY_NODE[x];
		total2 = total2 + SOURCE_HIT_BY_NODE[x];
		total3 = total3 + CACHE_HIT_BY_NODE[x];	
		total4 = total4 + PIT_HIT_BY_NODE[x];
		total5 = total5 + TOTAL_REQUEST_ISSUED_BY_NODE[x];
		total6 = total6 + SOURCE_HIT_BY_NODE_AT_DEST[x];
		total7 = total7 + CACHE_HIT_BY_NODE_AT_DEST[x];	
		total8 = total8 + PIT_HIT_BY_NODE_AT_DEST[x];
		
	}
	
	print "Total (Column 1): " total1 > "2D_2E_2G_node.txt"
	print "Total (Column 2): " total2 > "2D_2E_2G_node.txt"
	print "Total (Column 3): " total3 > "2D_2E_2G_node.txt"
	print "Total (Column 4): " total4 > "2D_2E_2G_node.txt"
	
	print "Total (Column 1): " total5 > "2J-2L_node.txt"
	print "Total (Column 2): " total6 > "2J-2L_node.txt"
	print "Total (Column 3): " total7 > "2J-2L_node.txt"
	print "Total (Column 4): " total8 > "2J-2L_node.txt"
	
	total1 = 0;
	total2 = 0;
	total3 = 0;
	total4 = 0;
	for (x = 0; x <= objects; x++) {
		print "Total requests for Object " x ": " TOTAL_REQUEST_RECEIVED_BY_OBJECT[x] "\t\tSatisfied by Source: " SOURCE_HIT_BY_OBJECT[x] "\t\tSatisfied by Cache: " CACHE_HIT_BY_OBJECT[x] "\tSatisfied by PIT: " PIT_HIT_BY_OBJECT[x] >> "2D_2E_2G_2J-2L_object.txt"
		total1 = total1 + TOTAL_REQUEST_RECEIVED_BY_OBJECT[x];
		total2 = total2 + SOURCE_HIT_BY_OBJECT[x];
		total3 = total3 + CACHE_HIT_BY_OBJECT[x];	
		total4 = total4 + PIT_HIT_BY_OBJECT[x];					
	}
	print "Total (Column 1): " total1 > "2D_2E_2G_2J-2L_object.txt"
	print "Total (Column 2): " total2 > "2D_2E_2G_2J-2L_object.txt"
	print "Total (Column 3): " total3 > "2D_2E_2G_2J-2L_object.txt"
	print "Total (Column 4): " total4 > "2D_2E_2G_2J-2L_object.txt"
	
	total1 = 0;
	total2 = 0;
	total3 = 0;
	total4 = 0;
	for (x = 0; x < nodes; x++) {
		print "NodeID: " x "\tSUP_NO_PIT: " SUP_NO_PIT_INDEX_NODE[x] "\tSUP_NO_PIT_EXP: " SUP_NO_PIT_EXP_NODE[x] "\tSUP_ALREADY_SERVED: " SUP_ALREADY_NODE[x] "\tSUP_NO_PIT_ORIG: " SUP_NO_PIT_ORIG_NODE[x] > "2N-2Q_node.txt"
		total1 = total1 + SUP_NO_PIT_INDEX_NODE[x];
		total2 = total2 + SUP_NO_PIT_EXP_NODE[x];
		total3 = total3 + SUP_ALREADY_NODE[x];	
		total4 = total4 + SUP_NO_PIT_ORIG_NODE[x];				
	}
	print "Total (Column 2): " total1 > "2N-2Q_node.txt"
	print "Total (Column 3): " total2 > "2N-2Q_node.txt"
	print "Total (Column 4): " total3 > "2N-2Q_node.txt"
	print "Total (Column 5): " total4 > "2N-2Q_node.txt"
	
	total1 = 0;
	total2 = 0;
	total3 = 0;
	total4 = 0;
	for (x = 0; x < objects; x++) {
		print "ObjectID: " x "\tSUP_NO_PIT: " SUP_NO_PIT_INDEX_OBJECT[x] "\tSUP_NO_PIT_EXP: " SUP_NO_PIT_EXP_OBJECT[x] "\tSUP_ALREADY_SERVED: " SUP_ALREADY_OBJECT[x] "\tSUP_NO_PIT_ORIG: " SUP_NO_PIT_ORIG_OBJECT[x] > "2N-2Q_object.txt"
		total1 = total1 + SUP_NO_PIT_INDEX_OBJECT[x];
		total2 = total2 + SUP_NO_PIT_EXP_OBJECT[x];
		total3 = total3 + SUP_ALREADY_OBJECT[x];	
		total4 = total4 + SUP_NO_PIT_ORIG_OBJECT[x];				
	}
	print "Total (Column 2): " total1 > "2N-2Q_object.txt"
	print "Total (Column 3): " total2 > "2N-2Q_object.txt"
	print "Total (Column 4): " total3 > "2N-2Q_object.txt"
	print "Total (Column 5): " total4 > "2N-2Q_object.txt"
	
	for (x = 0; x <= interests; x++) {
		#if (x in FIB_HIT_BY_INTID) {	
			print "InterestID " x "\tFIB_HIT: " FIB_HIT_BY_INTID[x] "\tFLOOD_HIT: " FLOODING_BY_INTID[x] >> "3A-3C_intID.txt"			
		#}		
	}
	
	print "\nHops using FIBS (FIB_HIT)\t\t\t\t	: " FIB_HIT " or " (FIB_HIT/(TOTAL_INTEREST_HOPS)*100)"%"  >> "3A-3C_intID.txt"
	print "Hops using flooding (FLOODING)\t\t\t	: " FLOODING " or " (FLOODING/(TOTAL_INTEREST_HOPS)*100)"%" >> "3A-3C_intID.txt"
	print "Total number of hops for Interest packets\t: " TOTAL_INTEREST_HOPS >> "3A-3C_intID.txt.txt"
	
	total1 = 0;
	total2 = 0;
	for (x = 0; x <= objects; x++) {
		#if (x in FIB_HIT_BY_OBJECT) {	
			print "ObjectID " x "\tFIB_HIT: " FIB_HIT_BY_OBJECT[x] "\tFLOOD_HIT: " FLOODING_BY_OBJECT[x] >> "3A-3C_object.txt"
			total1 = total1 + FIB_HIT_BY_OBJECT[x];
			total2 = total2 + FLOODING_BY_OBJECT[x];			
		#}		
	}
	
	print "Total (Column 2): " total1 > "3A-3C_object.txt"
	print "Total (Column 3): " total2 > "3A-3C_object.txt"
	
	print "\n Hops using FIBS (FIB_HIT)\t\t\t\t	: " FIB_HIT " or " (FIB_HIT/(TOTAL_INTEREST_HOPS)*100)"%"  >> "3A-3C_object.txt"
	print "Hops using flooding (FLOODING)\t\t\t	: " FLOODING " or " (FLOODING/(TOTAL_INTEREST_HOPS)*100)"%" >> "3A-3C_object.txt"
	print "Total number of hops for Interest packets\t: " TOTAL_INTEREST_HOPS >> "3A-3C_object.txt"
	
	total1 = 0;
	total2 = 0;
	for (x = 0; x < nodes; x++) {
		print "NodeID " x "\tFIB_HIT: " FIB_HIT_BY_NODE[x] "\tFLOOD_HIT: " FLOODING_BY_NODE[x] >> "3A-3C_node.txt"	
		total1 = total1 + FIB_HIT_BY_NODE[x];
		total2 = total2 + FLOODING_BY_NODE[x];	
	}
	
	print "Total (Column 2): " total1 > "3A-3C_node.txt"
	print "Total (Column 3): " total2 > "3A-3C_node.txt"
	
	print "\n Hops using FIBS (FIB_HIT)\t\t\t\t	: " FIB_HIT " or " (FIB_HIT/(TOTAL_INTEREST_HOPS)*100)"%"  >> "3A-3C_node.txt"
	print "Hops using flooding (FLOODING)\t\t\t	: " FLOODING " or " (FLOODING/(TOTAL_INTEREST_HOPS)*100)"%" >> "3A-3C_node.txt"
	print "Total number of hops for Interest packets\t: " TOTAL_INTEREST_HOPS >> "3A-3C_node.txt"
	
	total1 = 0;
	total2 = 0;
	for (x = 0; x < nodes; x++) {
		print "NodeID: " x "\tPIT Hits (suppressed): " PIT_HIT_NODE[x] "\tDuplicate PIT hits (not entertained): " R_PIT_HIT_NODE[x] > "4B_4G_node.txt"			
		total1 = total1 + PIT_HIT_NODE[x];
		total2 = total2 + R_PIT_HIT_NODE[x];			
	}
	print "Total (Column 2): " total1 > "4B_4G_node.txt"
	print "Total (Column 3): " total2 > "4B_4G_node.txt"
	
	total1 = 0;
	total2 = 0;
	for (x = 0; x < objects; x++) {
		print "ObjectID: " x "\tPIT Hits (suppressed): " PIT_HIT_OBJECT[x] "\tDuplicate PIT hits (not entertained): " R_PIT_HIT_OBJECT[x] > "4B_4G_object.txt"
		total1 = total1 + PIT_HIT_OBJECT[x]
		total2 = total2 + R_PIT_HIT_OBJECT[x]				
	}
	print "Total (Column 2): " total1 > "4B_4G_object.txt"
	print "Total (Column 3): " total2 > "4B_4G_object.txt"

}