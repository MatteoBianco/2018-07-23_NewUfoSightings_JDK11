package it.polito.tdp.newufosightings.model;

import java.time.LocalDateTime;

public class Event implements Comparable<Event>{
	
	public enum EventType {
		SIGHTING, START_ALERT, END_ALERT
	}
	
	private EventType type;
	private LocalDateTime date;
	private State state;
	private Double alertLevel;
	
	public Event(EventType type, LocalDateTime date, State state, Double alertLevel) {
		super();
		this.type = type;
		this.date = date;
		this.state = state;
		this.alertLevel = alertLevel;
	}

	public EventType getType() {
		return type;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public State getState() {
		return state;
	}

	public Double getAlertLevel() {
		return alertLevel;
	}

	@Override
	public int compareTo(Event o) {
		return this.date.compareTo(o.date);
	}
	
	

}
