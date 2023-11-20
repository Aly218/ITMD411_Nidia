package javaapplication1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Dao {
	// instance fields
	static Connection connect = null;
	Statement statement = null;

	// constructor
	public Dao() {
	  
	}

	public Connection getConnection() {
		// Setup the connection with the DB
		try {
			connect = DriverManager
					.getConnection("jdbc:mysql://www.papademas.net:3307/tickets?autoReconnect=true&useSSL=false"
							+ "&user=fp411&password=411");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connect;
	}

	// CRUD implementation

	public void createTables() {
		// variables for SQL Query table creations
		final String createTicketsTable = "CREATE TABLE n_guadTickets(ticket_id INT AUTO_INCREMENT PRIMARY KEY, ticket_issuer VARCHAR(30), ticket_description VARCHAR(200), start_date DATETIME, end_date DATETIME, status VARCHAR(5))";
		final String createUsersTable = "CREATE TABLE nguad_users(uid INT AUTO_INCREMENT PRIMARY KEY, uname VARCHAR(30), upass VARCHAR(30), admin int)";

		try {
			// execute queries to create tables

			statement = getConnection().createStatement();

			statement.executeUpdate(createTicketsTable);
			statement.executeUpdate(createUsersTable);
			System.out.println("Created tables in given database...");

			// end create table
			// close connection/statement object
			statement.close();
			connect.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		// add users to user table
		addUsers();
	}

	public void addUsers() {
		// add list of users from userlist.csv file to users table

		// variables for SQL Query inserts
		String sql;

		Statement statement;
		BufferedReader br;
		List<List<String>> array = new ArrayList<>(); // list to hold (rows & cols)

		// read data from file
		try {
			br = new BufferedReader(new FileReader(new File("./userlist.csv")));

			String line;
			while ((line = br.readLine()) != null) {
				array.add(Arrays.asList(line.split(",")));
			}
		} catch (Exception e) {
			System.out.println("There was a problem loading the file");
		}

		try {

			// Setup the connection with the DB

			statement = getConnection().createStatement();

			// create loop to grab each array index containing a list of values
			// and PASS (insert) that data into your User table
			for (List<String> rowData : array) {

				sql = "insert into nguad_users(uname,upass,admin) " + "values('" + rowData.get(0) + "'," + " '"
						+ rowData.get(1) + "','" + rowData.get(2) + "');";
				statement.executeUpdate(sql);
			}
			System.out.println("Inserts completed in the given database...");

			// close statement object
			statement.close();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/*
	public int insertRecords(String ticketName, String ticketDesc) {
		int id = 0;
		
		try {
			statement = getConnection().createStatement();
			
			statement.executeUpdate("Insert into jpapa_tickets" + "(ticket_issuer, ticket_description) values(" + " '"
					+ ticketName + "','" + ticketDesc + "')", Statement.RETURN_GENERATED_KEYS);

			// retrieve ticket id number newly auto generated upon record insertion
			ResultSet resultSet = null;
			resultSet = statement.getGeneratedKeys();
			if (resultSet.next()) {
				// retrieve first field in table
				id = resultSet.getInt(1);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id;
	}
	*/
	

	public ResultSet readRecords() {
		ResultSet results = null;
		try {
			statement = connect.createStatement();
			results = statement.executeQuery("SELECT * FROM n_guadTickets");
			//connect.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return results;
	}
	
	// continue coding for updateRecords implementation
	public int updateRecords(int ticketId, String ticketDesc) {
		int row = 0;
		
		try {
			// execute queries to update table
			
			System.out.println("Connecting to a selected database for updates...");
			statement = getConnection().createStatement();
			System.out.println("Connected to database successfully...");

			String sql = "UPDATE n_guadTickets SET ticket_description = '" + ticketDesc + "' WHERE ticket_id = " + ticketId;
			
			row = statement.executeUpdate(sql);
		
			// end update table
			// close connection/statement object
			statement.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return row;
	}
	
	// continue coding for deleteRecords implementation
	public int deleteRecords(int ticketId) {
		int row = 0;
		try {
			// execute queries to delete records
			
			System.out.println("Connecting to a selected database for deletes...");
			statement = getConnection().createStatement();
			System.out.println("Connected to database successfully...");

			String sql = "DELETE FROM n_guadTickets WHERE ticket_id = " + ticketId;

			row = statement.executeUpdate(sql);
			
			
			// close connection/statement object
			statement.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return row;
	}
	
	
	public void insertRecords(String ticketName, String ticketDesc) throws SQLException {
		
		PreparedStatement ps = (PreparedStatement) getConnection().prepareStatement("Insert into n_guadTickets(ticket_issuer, ticket_description, start_date, status) values(?,?,?,?)");
		java.sql.Timestamp date = new java.sql.Timestamp(new java.util.Date().getTime());
		ps.setString(1, ticketName);
		ps.setString(2, ticketDesc);
		ps.setTimestamp(3, date);
		ps.setString(4, "open");
		ps.executeUpdate();
	}
	
	public void closeTicket(int ticketId) throws SQLException {
		PreparedStatement ps = (PreparedStatement) getConnection().prepareStatement("UPDATE n_guadTickets SET end_date = ?, status = ? WHERE ticket_id = ?");
		java.sql.Timestamp date = new java.sql.Timestamp(new java.util.Date().getTime());
		ps.setTimestamp(1, date);
		ps.setString(2, "close");
		ps.setInt(3, ticketId);
		ps.executeUpdate();
		
		System.out.println("" + date);
	}
	
	public ResultSet viewTicket2(String ticketName) {
		ResultSet results = null;
		try {
			statement = connect.createStatement();
			results = statement.executeQuery("SELECT * FROM n_guadTickets WHERE ticket_issuer = '" + ticketName + "'" );
			
			
			//connect.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return results;
	}
	
	public ResultSet viewTicket3(int ticketId) {
		ResultSet results = null;
		try {
			statement = connect.createStatement();
			results = statement.executeQuery("SELECT * FROM n_guadTickets WHERE ticket_id = '" + ticketId + "'" );
			
			
			//connect.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return results;
	}
	
	
	
}
