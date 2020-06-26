package it.polito.tdp.newufosightings.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.newufosightings.db.NewUfoSightingsDAO;

public class Model {
	
	private NewUfoSightingsDAO dao;
	private Graph<State, DefaultWeightedEdge> graph;
	private Map<String, State> idMapStates;
	private List<State> listStates;
	private List<StatePair> listStatePairs;
	private Simulator simulator;
	private Integer year;
	private String shape;
	
	public Model() {
		this.dao = new NewUfoSightingsDAO();
		this.idMapStates = new HashMap<>();
	}
	
	public void createGraph(Integer year, String shape) {
		this.year = year;
		this.shape = shape;
		this.graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		this.listStates = this.dao.getAllStates(idMapStates);
		Graphs.addAllVertices(this.graph, this.listStates);
		this.listStatePairs = this.dao.getAllStatePairs(this.year, this.shape, idMapStates);
		for(StatePair sp : this.listStatePairs) {
			if(! this.graph.containsEdge(sp.getS1(), sp.getS2()))
				Graphs.addEdge(this.graph, sp.getS1(), sp.getS2(), sp.getWeight());
		}
	}
	
	public List<State> getAllStates() {
		return this.listStates;
	}
	
	public Integer getWeightNeighbors(State s) {
		Integer weight = 0;
		for(State neighbor : Graphs.neighborListOf(this.graph, s)) {
			weight += (int) this.graph.getEdgeWeight(this.graph.getEdge(s, neighbor));
		}
		return weight;
	}
	
	public List<String> getShapes() {
		return this.dao.getShapes();
	}

	public void simulate(Integer T1, Integer alfa) {
		this.simulator = new Simulator();
		this.simulator.init(this.graph, this.dao.getSightingsByYearAndShape(this.year, this.shape),
				T1, alfa, this.idMapStates, year);
		this.simulator.run();
	}
	
	public Map<State, Double> getAlertLevel() {
		return this.simulator.getAlertLevel();
	}
}
