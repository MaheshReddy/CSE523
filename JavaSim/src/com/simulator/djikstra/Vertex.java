package com.simulator.djikstra;

import java.util.List;

public class Vertex implements Comparable<Vertex> {
	public final Integer node;
	public List<Edge> adjacencies;
	public double minDistance = Double.POSITIVE_INFINITY;
	public Vertex previous;

	public Vertex(Integer argNode) {
		node = argNode;
	}

	public String toString() {
		return Integer.toString(node);
	}

	public int compareTo(Vertex other) {
		return Double.compare(minDistance, other.minDistance);
	}

}
