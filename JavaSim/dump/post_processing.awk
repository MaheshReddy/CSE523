BEGIN {} 
{
	if ($5 == "CRTDPRDA") {
	
		pit_hit_satisfied_at_node[$9] = pit_hit_satisfied_at_node[$9] + 1;
		pit_hit_satisfied_per_object[$3] = pit_hit_satisfied_per_object[$3] + 1;	
	}

	if ($2 == "d" && $12 == 6) {

		data_packet_received_count[$14] = data_packet_received_count[$14] + 1;
	}
	
	if ($2 == "i") {
	
		if ($5 == "CRTEDTO") {
			interestTO_at_node[$9] = interestTO_at_node[$9] + 1;
			interestPKTTO[$14] = interestPKTTO[$14] + 1;
		}
		
		if ($5 == "CREATED") {
			interest_at_node[$9] = interest_at_node[$9] + 1;
		}
	}
	
	if($5 == "DESTROY") {
	
		if ($12 == 1) {
	
			pit_suppression_at_node[$7] = pit_suppression_at_node[$7] + 1;
			pit_suppression_per_object[$3] = pit_suppression_per_object[$3] + 1;	
			print $1 "\t" $6 "\t" $5 "\t" $3 "\t" $7 "\t" $14 > "suppression_no_pit"
		}
	
		if ($12 == 12) {
		
			pit_expiration_at_node[$7] = pit_expiration_at_node[$7] + 1;
			pit_expiration_per_object[$6] = pit_expiration_per_object[$6] + 1;	
			print $1 "\t" $3 "\t" $5 "\t" $6 "\t" $7 "\t" $14 > "pit_expiration"
		}
	
		if ($12 == 8) {
		
			pit_hit_at_node[$7] = pit_hit_at_node[$7] + 1;
			pit_hit_per_object[$6] = pit_hit_per_object[$6] + 1;	
		}
		
		if ($12 == 13) {
		
			redund_pit_hit_at_node[$7] = redund_pit_hit_at_node[$7] + 1;
			redund_pit_hit_per_object[$6] = redund_pit_hit_per_object[$6] + 1;	
		}
	}
} 
END {
	
	total = 0;
	
	for (x = 0; x <= 250000; x++) {
		if (x in interestPKTTO) {	
			print "InterestID " x "\tExpired: " interestPKTTO[x] > "interestPKTTOCount"
			total = total + interestPKTTO[x]
		}		
	}
	
	print "Total (Column 2): " total > "interestPKTTOCount"	

	
	total = 0;

	for (x = 0; x <= 250000; x++) {
		if (x in data_packet_received_count) {	
			if (data_packet_received_count[x] >= 2) {
				print "InterestID " x "\tData Received Count: " data_packet_received_count[x] > "data_packet_received_count"
				total = total + data_packet_received_count[x]
			}
		}		
	}

	print "Total (Column 2): " total > "data_packet_received_count"


	total1 = 0;
	total2 = 0;
	for (x = 0; x <= 55000; x++) {
		if (x in pit_hit_per_object) {	
			print "ObjectID " x "\tPIT hits: " pit_hit_per_object[x] "\tPIT hits satisfied: " pit_hit_satisfied_per_object[x] > "pit_hit-satisfied_per_object"
			total1 = total1 + pit_hit_per_object[x]
			total2 = total2 + pit_hit_satisfied_per_object[x]
		}	
	}
	
	print "Total (Column 2): " total1 > "pit_hit-satisfied_per_object"
	print "Total (Column 3): " total2 > "pit_hit-satisfied_per_object"
	
	total1 = 0;
	total2 = 0;
	total3 = 0;
	for (x = 0; x < nodes; x++) {
		print "NodeID " x "\tPIT hits: " pit_hit_at_node[x] "\tPIT hits satisfied: " pit_hit_satisfied_at_node[x] "\tRedundant PIT hits: " redund_pit_hit_at_node[x] > "pit_hit-satisfied_at_node"
		total1 = total1 + pit_hit_at_node[x]
		total2 = total2 + pit_hit_satisfied_at_node[x]	
		total3 = total3 + redund_pit_hit_at_node[x]
	}
	
	print "Total (Column 2): " total1 > "pit_hit-satisfied_at_node"
	print "Total (Column 3): " total2 > "pit_hit-satisfied_at_node"
	print "Total (Column 4): " total3 > "pit_hit-satisfied_at_node"
	
	total1 = 0;
	total2 = 0;
	for (x = 0; x < nodes; x++) {
		print "NodeID: " x "\tInterests Created: " interest_at_node[x] "\tInterests Recreated: " interestTO_at_node[x] > "interest_created-recreated_at_node"
		total1 = total1 + interest_at_node[x]
		total2 = total2 + interestTO_at_node[x]			
	}
	print "Total (Column 2): " total1 > "interest_created-recreated_at_node"
	print "Total (Column 3): " total2 > "interest_created-recreated_at_node"
}