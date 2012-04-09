# Perl code to parse the trace file and check the bandwidth consumption by interest and data packets of CCN
#
# Authors :: Mandar Paingankar 
#		Sumit Bagga
#
# Last Modified :: 03/17/2012

#
# The following code directly gives an estimation of the bandwidth consumption. to test the validity of the code all the #prints have to be uncommented.
#

#!/usr/bin/perl -w

use strict;
use warnings;
use IO::File;

my @trace;
my $i;
my $j;
my $key;
my $value;
my $timestamp;
my @row;
my @node;
my $length_of_trace;
my %packets;

#
# $n represents the no of nodes in the topolgoy. It is the second command line argument to the perl script.
#
my $n = $ARGV[1];

my $src;
my $dest;
my $src_ts;
my $dest_ts;
my $seg_no;
my $new_tot;
my $useful_data;

#
# Input file name taken as arguement. This file needs to be in the same directory.
#
open INPUT, $ARGV[0];
@trace = <INPUT>;
close INPUT;

chomp(@trace);

$length_of_trace = $#trace;
$timestamp = 1;

$j = $n;

#Represents the source node for the interest packet
$src = $j;

#Represents the  seg no
$j++;
$seg_no = $j;

#Timestamp field for interst packet
$j++;
$src_ts = $j;

#Timestamp field for data packet
$j++;
$dest_ts = $j;

#Represents the node from which the data packet was first received at the source
$j++;
$dest = $j;

$j++;
$useful_data = $j;

$new_tot = $j+1;

for($i = 0; $i <= $length_of_trace; $i++) 
{
		@row = split(' ', $trace[$i]);

		if($row[4] eq "CREATED") 
		{
				my @node;

				for($j = 0; $j < $n; $j++)
				{
					$node[$j] = 0;
				}
	
				#initializing hash row
				$node[$src] = $row[8]; 
				$node[$seg_no] = $row[3]; 

				$node[$src_ts] = $timestamp;
				$timestamp = $timestamp + 1;

				$node[$dest_ts] = -1; 
				$node[$dest] = "X";

				$node[$useful_data] = 0;

				my $temp = "";
				for($j = 0; $j <= $dest; $j++)
				{
					$temp .= $node[$j] . " ";
				}
				$temp .= $node[$useful_data];

				$packets{$row[2].$row[3]} = $temp;
			
				#print "New interest packet\n";
				#print $packets{$row[2].$row[3]}."\n";
		}

		elsif($row[4] eq "ENQUEUE") 
		{
			if(($row[1] eq "i") && ($row[8] ne $row[6])) 
			{

					#
					#	Interest Packet Enqueued
					#

					@node = split(' ', $packets{$row[2].$row[3]});
					$node[$row[6]] += 1;

					#print "I-". $row[2] ." Segment " .$node[$seg_no].":: At node ".$row[6]."\n";

					$packets{$row[2].$row[3]} = "";
					for(my $j = 0; $j <= $#node; $j++)
					{
							if($j != $#node)
							{
									$packets{$row[2].$row[3]} = $packets{$row[2].$row[3]}.$node[$j]." ";
							}
							else
							{
									$packets{$row[2].$row[3]} = $packets{$row[2].$row[3]}.$node[$j];
							}
					}

					#print $packets{$row[2].$row[3]}."\n";

			}

			elsif($row[1] eq "d")
			{
					#
					#	Data Packet Enqueued
					#

					@node = split(' ', $packets{$row[5].$row[3]});

					#print "D-". $row[2].$row[3].":: At node ".$row[6]." DataSource = ". $row[8]."\n";

					
					my $j = $useful_data + 1;
					for(; $j <= $#node && $node[$j] != $row[8]; $j += 2){}
					$node[$j + 1]++;

					if(($node[$src] == $row[6]) && ($node[$dest] eq "X"))
					{
							#print "Data Packet reached Destination\n";
							$node[$dest] = $row[8];
							$node[$useful_data] = $node[$j+1];
					}


					$packets{$row[5].$row[3]} = "";

					for($j = 0; $j <= $#node; $j++)
					{
							if($j != $#node)
							{
									$packets{$row[5].$row[3]} = $packets{$row[5].$row[3]}.$node[$j]." ";
							}
							else
							{
									$packets{$row[5].$row[3]} = $packets{$row[5].$row[3]}.$node[$j];
							}
					}

					#print $packets{$row[5].$row[3]}."\n";

			}

		}

		elsif(($row[4] eq "CRTDPRD") || ($row[4] eq "CRTDPRDA"))
		{
			@node = split(' ', $packets{$row[5].$row[3]});
			if($node[$dest_ts] == -1) {
				$node[$dest_ts] = $timestamp;
				$timestamp = $timestamp + 1;

				#print "First Data Packet Found for ".$row[5]. " Segment " .$row[3]." at ". $row[8]. " node_timestamp = ".$node[$dest_ts]."\n";

				$packets{$row[5].$row[3]} = "";
				for(my $j = 0; $j <= $#node; $j++)
				{
						if($j != $#node)
						{
							$packets{$row[5].$row[3]} = $packets{$row[5].$row[3]}.$node[$j]." ";
						}
						else
						{
							$packets{$row[5].$row[3]} = $packets{$row[5].$row[3]}.$node[$j];
						}
				}
			}

			else {
				#print "Another Data Packet Found for ".$row[5]. " Segment " .$row[3]." at ". $row[8]. " node_timestamp = ".$timestamp."\n";
			}

			$packets{$row[5].$row[3]} = $packets{$row[5].$row[3]}." ".$row[8]." 0";
			#print $packets{$row[5].$row[3]}."\n";
		}

}


while (($key, $value) =each(%packets))
{
		@node = split(' ', $value);

		my $total_interest_pkt = 0; 

		for($i = 0; $i  < $n; $i++)
		{
			$total_interest_pkt += $node[$i];
		}

		my $useful_interest_pkt = 0;
		my $useful_data_pkt = 0;
		my $total_data_pkt = 0;
		my $i = $useful_data + 1;
		my $segment = $node[$seg_no];
		my $interest_packet_id = 0;
		my $time_interest = $node[$src_ts];
		my $time_data = $node[$dest_ts];
		
		if(($time_interest == -1) || ($time_data == -1)) {
			next;
		}

		while($i <= $#node)
		{
			$total_data_pkt += $node[$i + 1]; 
			$i += 2;
		}		


		$useful_data_pkt = $node[$useful_data];
		$useful_interest_pkt = $useful_data_pkt;	
		$interest_packet_id = int(($key-$segment)/(10**length($segment)));
		# print format ------->   interest(i)/data(d)  interest_packet_number  segment_number  total_packets  useful_packets		

		print $time_interest." i ".$interest_packet_id." ".$segment." ".$total_interest_pkt." ".$useful_interest_pkt."\n";
		print $time_data." d ".$interest_packet_id." ".$segment." ".$total_data_pkt." ".$useful_data_pkt."\n";

}



