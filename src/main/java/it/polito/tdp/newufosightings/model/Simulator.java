package it.polito.tdp.newufosightings.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.newufosightings.model.Event.EventType;

public class Simulator {
	
	//CODA DEGLI EVENTI
	private PriorityQueue<Event> queue;
	
	//MODELLO DEL MONDO
	private Graph<State, DefaultWeightedEdge> graph;
	private Map<State, Double> alertLevel;
	
	//PARAMETRI DI SIMULAZIONE
	private Integer T1;
	private Integer alfa;
	private LocalDateTime lastEvent;
	
	//OUTPUT
	//La mappa degli stati che fa parte del modello del mondo.
	

	public void init(Graph<State, DefaultWeightedEdge> graph, List<Sighting> sightingsByYearAndShape, Integer T1,
			Integer alfa, Map<String, State> stateIdMap, Integer year) {
		this.queue = new PriorityQueue<>();
		this.graph = graph;
		
		this.T1 = T1;
		this.alfa = alfa;
		this.lastEvent = LocalDateTime.MIN;
		
		this.alertLevel = new HashMap<>();
		
		for(State s : this.graph.vertexSet()) {
			this.alertLevel.put(s, 5.0);
		}
		
		for(Sighting sighting : sightingsByYearAndShape) {
			if(sighting.getDatetime().isAfter(this.lastEvent))
				this.lastEvent = sighting.getDatetime();
			this.queue.add(new Event(EventType.SIGHTING, sighting.getDatetime(), stateIdMap.get(sighting.getState()),
					1.0));
		}
	}
	
	public void run() {
		while(! this.queue.isEmpty()) {
			Event e = this.queue.poll();
			if(e.getDate().isBefore(this.lastEvent) || e.getDate().equals(this.lastEvent))
				this.processEvent(e);
		}
	}

	private void processEvent(Event e) {
		System.out.println(e.getType() + " " + e.getState() + " " + this.alertLevel.get(e.getState()));
		switch(e.getType()) {
		case SIGHTING:
			this.queue.add(new Event(EventType.START_ALERT, e.getDate(), e.getState(), 1.0));
			for(State neighbor : Graphs.neighborListOf(this.graph, e.getState())) {
				Double chance = Math.random();
				if(chance < (alfa/100.0)) {
					this.queue.add(new Event(EventType.START_ALERT, e.getDate(), neighbor, 0.5));
				}
			}
			break;
		case START_ALERT:
			Double newAlertLevel = this.alertLevel.get(e.getState()) - e.getAlertLevel();
			if(newAlertLevel < 1) {
				this.alertLevel.replace(e.getState(), 1.0);
			}
			else this.alertLevel.replace(e.getState(), newAlertLevel);
			this.queue.add(new Event(EventType.END_ALERT, e.getDate().plusDays(this.T1), e.getState(), e.getAlertLevel()));
			break;
		case END_ALERT:
			Double backToAlertLevel = this.alertLevel.get(e.getState()) + e.getAlertLevel();
			if(backToAlertLevel > 5) {
				this.alertLevel.replace(e.getState(), 5.0);
			}
			else this.alertLevel.replace(e.getState(), backToAlertLevel);
			break;
		}
	}

	public Map<State, Double> getAlertLevel() {
		return alertLevel;
	}

	public void setT1(Integer t1) {
		T1 = t1;
	}

	public void setAlfa(Integer alfa) {
		this.alfa = alfa;
	}
	
	
	
	

}
