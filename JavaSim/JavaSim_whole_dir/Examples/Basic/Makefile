MAKE = make

JAVA  = javac

JAVA_FLAGS = 

SOURCES	= Job.java Arrivals.java Breaks.java Machine.java \
	  MachineShop.java Queue.java Main.java

all: Job.class Arrivals.class Breaks.class Machine.class \
     MachineShop.class Queue.class Main.class

Job.class: Job.java
	$(JAVA) $(JAVA_FLAGS) Job.java

Arrivals.class: Arrivals.java
	$(JAVA) $(JAVA_FLAGS) Arrivals.java

Breaks.class: Breaks.java
	$(JAVA) $(JAVA_FLAGS) Breaks.java

Machine.class: Machine.java
	$(JAVA) $(JAVA_FLAGS) Machine.java

MachineShop.class: MachineShop.java
	$(JAVA) $(JAVA_FLAGS) MachineShop.java

Queue.class: Queue.java
	$(JAVA) $(JAVA_FLAGS) Queue.java

Main.class: Main.java
	$(JAVA) $(JAVA_FLAGS) Main.java

clean:
	rm -rf *.class *~*

purge: clean
