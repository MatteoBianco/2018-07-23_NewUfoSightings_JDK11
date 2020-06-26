package it.polito.tdp.newufosightings.db;

import java.sql.Connection;

public class TestDAO {

	public static void main(String[] args) {
		
		try {
			Connection connection = DBConnect.getConnection();
			connection.close();
			System.out.println("Test PASSED");

		} catch (Exception e) {
			System.err.println("Test FAILED");
		}

		NewUfoSightingsDAO dao = new NewUfoSightingsDAO();
		System.out.println(dao.getSightingsByYearAndShape(2010, "").size());
	}

}
