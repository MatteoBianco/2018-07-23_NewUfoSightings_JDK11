package it.polito.tdp.newufosightings.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.newufosightings.model.Sighting;
import it.polito.tdp.newufosightings.model.State;
import it.polito.tdp.newufosightings.model.StatePair;

public class NewUfoSightingsDAO {
	
	public List<String> getShapes() {
		String sql = "SELECT DISTINCT shape " + 
				"FROM sighting " + 
				"ORDER BY shape";
		List<String> list = new ArrayList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);	
			ResultSet res = st.executeQuery();

			while (res.next()) {
				list.add(res.getString("shape"));
				}

			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		
		return list;

	}

	public List<Sighting> getSightingsByYearAndShape(Integer year, String shape) {
		String sql = "SELECT * " + 
				"FROM sighting " + 
				"WHERE YEAR(`datetime`) = ? AND shape = ? " + 
				"ORDER BY `datetime`";
		List<Sighting> list = new ArrayList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);	
			st.setInt(1, year);
			st.setString(2, shape);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				list.add(new Sighting(res.getInt("id"), res.getTimestamp("datetime").toLocalDateTime(),
						res.getString("city"), res.getString("state"), res.getString("country"), res.getString("shape"),
						res.getInt("duration"), res.getString("duration_hm"), res.getString("comments"),
						res.getDate("date_posted").toLocalDate(), res.getDouble("latitude"),
						res.getDouble("longitude")));
			}

			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}

		return list;
	}

	public List<State> getAllStates(Map<String, State> idMapStates) {
		String sql = "SELECT * FROM state order by id";
		List<State> result = new ArrayList<>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				if(! idMapStates.containsKey(rs.getString("id"))) {
					State state = new State(rs.getString("id"), rs.getString("Name"), rs.getString("Capital"),
							rs.getDouble("Lat"), rs.getDouble("Lng"), rs.getInt("Area"), rs.getInt("Population"),
							rs.getString("Neighbors"));
					result.add(state);
					idMapStates.put(state.getId(), state);
				}
				else result.add(idMapStates.get(rs.getString("id")));
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<StatePair> getAllStatePairs(Integer year, String shape, Map<String, State> idMapStates) {
		String sql = "SELECT n.state1, n.state2, COUNT(*) AS sightings " + 
				"FROM sighting AS s1, sighting AS s2, neighbor AS n " + 
				"WHERE n.state1 = s1.state AND n.state2 = s2.state AND " + 
				"s1.id > s2.id AND s1.shape = ? AND s1.shape = s2.shape " + 
				"AND YEAR(s1.datetime) = ? AND YEAR(s1.datetime) = YEAR(s2.datetime) " + 
				"GROUP BY n.state1, n.state2";
		List<StatePair> result = new ArrayList<>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, shape);
			st.setInt(2, year);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				if(idMapStates.containsKey(rs.getString("n.state1")) && idMapStates.containsKey(rs.getString("n.state2"))) {
					result.add(new StatePair(idMapStates.get(rs.getString("n.state1")), 
							idMapStates.get(rs.getString("n.state2")), rs.getInt("sightings")));
				}
				else {
					throw new RuntimeException("Errore: lo stato presente nella coppia non è stato aggiunto"
							+ "al grafo.\n");
				}
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

}

