#!/bin/bash
java -Xmx6g -classpath lib/log4j-1.2.16.jar:lib/slf4j-log4j12-1.6.1.jar:resources:CCNSimulator.jar com.simulator.controller.SimulationController > processed&

