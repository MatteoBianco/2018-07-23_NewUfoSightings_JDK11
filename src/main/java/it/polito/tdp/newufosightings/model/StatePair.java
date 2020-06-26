package it.polito.tdp.newufosightings.model;

public class StatePair {
	
	private State s1;
	private State s2;
	private Integer weight;
	
	public StatePair(State s1, State s2, Integer weight) {
		super();
		this.s1 = s1;
		this.s2 = s2;
		this.weight = weight;
	}

	public State getS1() {
		return s1;
	}

	public State getS2() {
		return s2;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setS1(State s1) {
		this.s1 = s1;
	}

	public void setS2(State s2) {
		this.s2 = s2;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}
	
	

}
