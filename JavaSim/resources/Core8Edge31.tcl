# Export from BRITE topology
# Generator Model Used: Model (3 - ASWaxman):  100 1000 100 1  2  0.15000000596046448 0.20000000298023224 1 1 10.0 1024.0 

#Create a simulator object
set ns [new Simulator]

#Open the nam trace file
set nf [open out.nam w]
$ns namtrace-all $nf

#Define a 'finish' procedure
proc finish {} {
        global ns nf
        $ns flush-trace
	#Close the trace file
        close $nf
	#Execute nam on the trace file
        exec ../nam-1.10/nam out.nam &
        exit 0
}

	#nodes:
	set num_node 39
	for {set i 0} {$i < $num_node} {incr i} {
	   set n($i) [$ns node]
	}

	 #links:
	set qtype DropTail

	$ns duplex-link $n(0) $n(1) 10.0Mb 1.1013728785521348ms $qtype
	$ns duplex-link $n(1) $n(2) 10.0Mb 1.1013728785521348ms $qtype
	$ns duplex-link $n(2) $n(3) 10.0Mb 1.1013728785521348ms $qtype
	$ns duplex-link $n(3) $n(4) 10.0Mb 1.1013728785521348ms $qtype
	$ns duplex-link $n(4) $n(5) 10.0Mb 1.1013728785521348ms $qtype
	$ns duplex-link $n(5) $n(6) 10.0Mb 1.1013728785521348ms $qtype
	$ns duplex-link $n(6) $n(0) 10.0Mb 1.1013728785521348ms $qtype
	
	$ns duplex-link $n(7) $n(1) 10.0Mb 1.1013728785521348ms $qtype
	$ns duplex-link $n(7) $n(3) 10.0Mb 1.1013728785521348ms $qtype
	$ns duplex-link $n(7) $n(5) 10.0Mb 1.1013728785521348ms $qtype
	
	
	$ns duplex-link $n(0) $n(8) 10.0Mb 1.1013728785521348ms $qtype
	$ns duplex-link $n(0) $n(9) 10.0Mb 1.1013728785521348ms $qtype
	$ns duplex-link $n(0) $n(10) 10.0Mb 1.1013728785521348ms $qtype
	$ns duplex-link $n(0) $n(11) 10.0Mb 1.1013728785521348ms $qtype
	$ns duplex-link $n(0) $n(12) 10.0Mb 1.1013728785521348ms $qtype
	$ns duplex-link $n(0) $n(13) 10.0Mb 1.1013728785521348ms $qtype
	
	$ns duplex-link $n(1) $n(14) 10.0Mb 1.1013728785521348ms $qtype
	$ns duplex-link $n(1) $n(15) 10.0Mb 1.1013728785521348ms $qtype
		
	$ns duplex-link $n(2) $n(16) 10.0Mb 1.1013728785521348ms $qtype
	$ns duplex-link $n(2) $n(17) 10.0Mb 1.1013728785521348ms $qtype
	$ns duplex-link $n(2) $n(18) 10.0Mb 1.1013728785521348ms $qtype
	$ns duplex-link $n(2) $n(19) 10.0Mb 1.1013728785521348ms $qtype
	$ns duplex-link $n(2) $n(20) 10.0Mb 1.1013728785521348ms $qtype	
	
	$ns duplex-link $n(3) $n(21) 10.0Mb 1.1013728785521348ms $qtype
	$ns duplex-link $n(3) $n(22) 10.0Mb 1.1013728785521348ms $qtype
	$ns duplex-link $n(3) $n(23) 10.0Mb 1.1013728785521348ms $qtype
	$ns duplex-link $n(3) $n(24) 10.0Mb 1.1013728785521348ms $qtype
	$ns duplex-link $n(3) $n(25) 10.0Mb 1.1013728785521348ms $qtype
	
	$ns duplex-link $n(4) $n(26) 10.0Mb 1.1013728785521348ms $qtype
	$ns duplex-link $n(4) $n(27) 10.0Mb 1.1013728785521348ms $qtype
	$ns duplex-link $n(4) $n(28) 10.0Mb 1.1013728785521348ms $qtype	
	
	$ns duplex-link $n(5) $n(29) 10.0Mb 1.1013728785521348ms $qtype
	$ns duplex-link $n(5) $n(30) 10.0Mb 1.1013728785521348ms $qtype
	$ns duplex-link $n(5) $n(31) 10.0Mb 1.1013728785521348ms $qtype
	$ns duplex-link $n(5) $n(32) 10.0Mb 1.1013728785521348ms $qtype
	$ns duplex-link $n(5) $n(33) 10.0Mb 1.1013728785521348ms $qtype
	$ns duplex-link $n(5) $n(34) 10.0Mb 1.1013728785521348ms $qtype
	$ns duplex-link $n(5) $n(35) 10.0Mb 1.1013728785521348ms $qtype	
	
	$ns duplex-link $n(6) $n(36) 10.0Mb 1.1013728785521348ms $qtype
	$ns duplex-link $n(6) $n(37) 10.0Mb 1.1013728785521348ms $qtype
	$ns duplex-link $n(6) $n(38) 10.0Mb 1.1013728785521348ms $qtype	

#Call the finish procedure after 5 seconds of simulation time
$ns at 5.0 "finish"

#Run the simulation
$ns run
