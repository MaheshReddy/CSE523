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
#my $n = $ARGV[1];

my $src;
my $dest;
my $src_ts;
my $dest_ts;
my $seg_no;
my $new_tot;

my $total_int;
my $useful_int;
my $total_data;
my $useful_data;

#
# Input file name taken as argument. This file needs to be in the same directory.
#
open INPUT, $ARGV[0];
@trace = <INPUT>;
close INPUT;

chomp(@trace);

$length_of_trace = $#trace;
$timestamp = 1;

#Represents the source node for the interest packet
$src = 0;

#Represents the  seg no
$seg_no = 1;

#Timestamp field for interst packet
$src_ts = 2;

#Timestamp field for data packet
$dest_ts = 3;

#Represents the node from which the data packet was first received at the source
$dest = 4;

$total_int = 5;
$useful_int = 6;
$total_data = 7;
$useful_data = 8;

for($i = 0; $i <= $length_of_trace; $i++) 
{
	@row = split(' ', $trace[$i]);

	if($row[4] eq "CREATED") 
	{
		my @node;		

		#initializing hash row

		$node[$src] = $row[8]; 		
		$node[$seg_no] = $row[3]; 

		$node[$src_ts] = $timestamp;
		$timestamp = $timestamp + 1;

		$node[$dest_ts] = $timestamp;
		$timestamp = $timestamp + 1;
		
		$node[$dest] = "X";

		$node[$total_int] = 0;
		$node[$useful_int] = 0;
		$node[$total_data] = 0;
		$node[$useful_data] = 0;

		my $temp = "";
		
		for($j = 0; $j <= 7; $j++)
		{
			$temp .= $node[$j] . " ";
		}

		$temp .= $node[$useful_data];

		$packets{$row[2].$row[3]} = $temp;
			
		#print "New interest packet\n";
		#print $packets{$row[2].$row[3]}."\n\n";
	}

	elsif(($row[4] eq "ENQUEUE") && ($row[1] eq "i") && ($row[9] != 0)) 
	{
	
		#
		#Interest Packet Enqueued
		#

		@node = split(' ', $packets{$row[2].$row[3]});
		$node[$total_int] += 1;

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
					
		#print "Interest packet enqueued\n";
		#print $packets{$row[2].$row[3]}."\n\n"; 
	}

	elsif ($row[1] eq "d") 
	{
		if ($row[4] eq "ENQUEUE") {
		
			@node = split(' ', $packets{$row[5].$row[3]});			
					
			$node[$total_data] += 1;		
				
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
		}
		
		elsif (($row[11] == 6) && ($row[4] eq "DESTROY")) {
				
		#
		#	Data packet when it has reached the destination
		
			@node = split(' ', $packets{$row[5].$row[3]});

			#print "D-". $row[2].$row[3].":: At node ".$row[6]." DataSource = ". $row[8]."\n";

			$node[$useful_int] = $row[9];
			$node[$useful_data] = $row[9];					
			
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
		}
		
		#print "Data packet has reached the requesting source node\n";
		#print $packets{$row[5].$row[3]}."\n\n";				
	}	
}

while (($key, $value) =each(%packets))
{
		@node = split(' ', $value);

		my $segment = $node[$seg_no];
		my $interest_packet_id = 0;
		my $time_interest = $node[$src_ts];
		my $time_data = $node[$dest_ts];
		
		if(($time_interest == -1) || ($time_data == -1)) {
			next;
		}

		$interest_packet_id = int(($key-$segment)/(10**length($segment)));
		# print format ------->   interest(i)/data(d)  interest_packet_number  segment_number  total_packets  useful_packets		

		print $time_interest." i ".$interest_packet_id." ".$segment." ".$node[$total_int]." ".$node[$useful_int]."\n";
		print $time_data." d ".$interest_packet_id." ".$segment." ".$node[$total_data]." ".$node[$useful_data]."\n";
}



