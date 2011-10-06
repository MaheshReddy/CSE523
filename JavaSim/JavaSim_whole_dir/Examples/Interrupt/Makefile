MAKE = make

JAVA  = javac

JAVA_FLAGS = 

SOURCES	= Job.java Arrivals.java Processor.java \
	  MachineShop.java Queue.java Main.java Signaller.java

all: Job.class Arrivals.class Processor.class \
     MachineShop.class Queue.class Main.class Signaller.class

Job.class: Job.java
	$(JAVA) $(JAVA_FLAGS) Job.java

Arrivals.class: Arrivals.java
	$(JAVA) $(JAVA_FLAGS) Arrivals.java

Processor.class: Processor.java
	$(JAVA) $(JAVA_FLAGS) Processor.java

MachineShop.class: MachineShop.java
	$(JAVA) $(JAVA_FLAGS) MachineShop.java

Queue.class: Queue.java
	$(JAVA) $(JAVA_FLAGS) Queue.java

Main.class: Main.java
	$(JAVA) $(JAVA_FLAGS) Main.java

Signaller.class: Signaller.java
	$(JAVA) $(JAVA_FLAGS) Signaller.java

clean:
	rm -rf *.class *~*

purge: clean
