#
# Script to find the Path trace given the number of nodes and the Shortest Path from every node to every other node (output of Djikstra's Algo)
# To run the script type the command "perl path_strech.pl <no of nodes>"
#

#! /usr/bin/perl

use strict;
use warnings;

#Output of djikstra's Algo
open DJIKSTRA, $ARGV[2];


#Parse the Shortest Route info into a hash
my %route; my $i = 0; 
while(my $line = <DJIKSTRA>) {
	#Consider only the shortestPathTable[x][y] = z; lines in the Djikstra file
	my @entry = ();
	if($i % 2 == 0)
	{		
		@entry = split(' ', $line);
		my $key = substr($entry[0], index($entry[0], "["));
		my $value = substr($entry[2], 0, length($entry[2]) - 1);
		
		$route{$key} = $value;
	}
	$i++;
}
close DJIKSTRA;

#Trace file
open TRACE, $ARGV[1] or die "could not open file";

my %packets;
my @entry = ();
my $no_of_nodes = $ARGV[0];
my $old_topology = -1;
if($ARGV[3]) {
	$old_topology = 1;
}

#print "Old topology = ".$old_topology."\n";
my @node_array_100 = (11,30,32,44,45,49,72,70,61,59,54,51,75,78,79,80,82,83,84,87,88,89,90,91,92,93,94,95,96,97,98,99);

while(my $line = <TRACE>) {
	@entry = split(' ', $line);
	
	if($entry[1] eq 'i' && $entry[4] eq 'CREATED')
	{
		my $interest_packet_id = $entry[13];
		my $requesting_node = $entry[8];
		my $requested_data_id = $entry[5];
		my $source_node = $requested_data_id % $no_of_nodes;

		# calculate this stuff from hardcoded array for old topology
		if($old_topology == 1) {
			if($no_of_nodes == 39) {
				$source_node = ($requested_data_id % 31)+8;
			}
			elsif($no_of_nodes == 100) {
				my $temp = ($requested_data_id % 32);
				$source_node = $node_array_100[$temp];
			}
		}

		my $key_into_route = "[" . $requesting_node . "][" . $source_node . "]";
#		print "CREATED Found: ".$key_into_route." \n";
		my $shortest_path = $route{$key_into_route};
		
		$packets{$interest_packet_id} = "D " . $shortest_path;
	}
	if($entry[1] eq 'd' && $entry[4] eq 'DESTROY' && $entry[11] == 6)
	{
		my $actual_hops = $entry[9];		
		my $interest_packet_id = $entry[13];
		
		$packets{$interest_packet_id} = $packets{$interest_packet_id} . " N " .$actual_hops;
#		print "DESTROY found:".$packets{$interest_packet_id}." N=".$actual_hops."\n";
	}
}
close TRACE;

my $key = "";
my $value = "";
#print "Id Path-Strech\n";
while(($key, $value) = each(%packets))
{
	if(index($value, 'N') != -1 && index($value, 'D') != -1)
	{
		my $Nr;
		my $Dr;
		my @entry = split(' ', $value);
		if($entry[0] eq 'N')
		{
			$Nr = $entry[1];
			$Dr = $entry[3];
		}
		else
		{
			$Nr = $entry[3];
			$Dr = $entry[1];
		}
		if($Dr == 0) {
			print $key." ".$Nr." ".$Dr." Denominator 0. N=".$Nr." D=".$Dr."\n";
		}
		else {
			print $key." ".$Nr." ".$Dr." ".($Nr / $Dr) . "\n"; 
		}
	}
}

