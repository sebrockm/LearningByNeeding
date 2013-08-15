package model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class SQLManager {
	
	private static String sqliteDriver = "org.sqlite.JDBC";
	private static String pathPraefix = "jdbc:sqlite:";
	
	private String databasePath;
	private Connection connection = null;
	
	private void createTable(Connection c)
	{
		String create = "CREATE TABLE IF NOT EXISTS EnglishGerman " +
				"(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
				"english	VARCHAR(250)," +
				"german		VARCHAR(250)," +
				"type		VARCHAR(20))";
		
		PreparedStatement stmt = null;
		try
		{
			stmt = c.prepareStatement(create);
			stmt.executeUpdate();
			stmt.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			throw new RuntimeException("SQLException occured:\n" + e.getMessage());
		}
	}
	
	public SQLManager(String databasePath)
	{		
		try {
			Class.forName(sqliteDriver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("ClassNotFoundException occured:\n" + e.getMessage());
		}

		switchDatabase(databasePath);
	}
	
	public String getDatabasePath()
	{
		return databasePath;
	}
	
	public void switchDatabase(String databasePath)
	{
		try {
			this.databasePath = databasePath;
			if(connection != null)
				connection.close();
			
			connection = DriverManager.getConnection(pathPraefix + databasePath);
			createTable(connection);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQLExeption occured:\n" + e.getMessage());
		}
	}
	
	public void truncate()
	{
		try {
			String truncate = "DROP TABLE EnglishGerman";
			Statement stmt = connection.createStatement();
			stmt.executeUpdate(truncate);
			stmt.close();
			createTable(connection);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQLExeption occured:\n" + e.getMessage());
		}
	}
	
	public boolean insertEntry(String english, String german, String type)
	{
		boolean res = false;
		try {
			String insert = "INSERT INTO EnglishGerman (english,german,type) VALUES(?,?,?);";
			PreparedStatement stmt = connection.prepareStatement(insert);
			stmt.setString(1, english);
			stmt.setString(2, german);
			stmt.setString(3, type);
			res = stmt.executeUpdate() == 1;
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQLExeption occured:\n" + e.getMessage());
		}
		
		return res;
	}
	
	public int insertEntriesFromFile(String path) throws FileNotFoundException
	{
		BufferedReader r = new BufferedReader(new FileReader(path));

		int counter = 0;
		try {		
			connection.setAutoCommit(false);
	
			String insert = "INSERT INTO EnglishGerman (english,german,type) VALUES(?,?,?);";
			PreparedStatement stmt = connection.prepareStatement(insert);
			String line = null;
			while((line = r.readLine()) != null)
			{
				if(line.length() == 0 || line.charAt(0) == '#')
					continue;
				
				StringTokenizer tok = new StringTokenizer(line, "\t");
				if(!tok.hasMoreTokens())
					continue;			
				String english = tok.nextToken();
				
				if(!tok.hasMoreTokens())
					continue;		
				String german = tok.nextToken();
				
				String type = "";
				if(tok.hasMoreTokens())
					type = tok.nextToken();

				stmt.setString(1, english);
				stmt.setString(2, german);
				stmt.setString(3, type);
				
				counter += stmt.executeUpdate();
				stmt.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("IOException occured:\n" + e.getMessage());
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQLException occured:\n" + e.getMessage());
		}
		finally
		{
			try {
				r.close();
				connection.commit();
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException("SQLException occured:\n" + e.getMessage());
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("IOException occured:\n" + e.getMessage());
			}
		}
		
		return counter;
	}
	
	public List<String[]> searchForEnglish(String english, boolean like)
	{
		String select = "SELECT * FROM EnglishGerman WHERE english ";
		select += like ? "LIKE '%' || ? || '%'" : "= ?";
		
		LinkedList<String[]> res = null;
		try {
			PreparedStatement stmt = connection.prepareStatement(select);
			stmt.setString(1, english);
			
			ResultSet rs = stmt.executeQuery();
			res = new LinkedList<String[]>();
			while(rs.next())
			{
				res.addLast(new String[]{rs.getString("english"), rs.getString("german"), rs.getString("type")});
			}
			
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQLException occured:\n" + e.getMessage());
		}
		
		Collections.sort(res, new Comparator<String[]>(){

			@Override
			public int compare(String[] arg0, String[] arg1) {
				int len0 = arg0[0].length();
				if(arg0[2].equals("verb") && arg0[0].startsWith("to "))
					len0 -= 3;
				int len1 = arg1[0].length();
				if(arg1[2].equals("verb") && arg1[0].startsWith("to "))
					len1 -= 3;
				
				if(len0 != len1)
					return len0 - len1;
				
				return arg0[1].length() - arg1[1].length();
			}
			
		});
		
		return res;
	}
	
	public List<String[]> searchForGerman(String german, boolean like)
	{
		String select = "SELECT * FROM EnglishGerman WHERE german ";
		select += like ? "LIKE '%' || ? || '%'" : "= ?";
		
		LinkedList<String[]> res = null;
		try {
			PreparedStatement stmt = connection.prepareStatement(select);
			stmt.setString(1, german);
			
			ResultSet rs = stmt.executeQuery();
			res = new LinkedList<String[]>();
			while(rs.next())
			{
				res.addLast(new String[]{rs.getString("german"), rs.getString("english"), rs.getString("type")});
			}
			
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQLException occured:\n" + e.getMessage());
		}
		
		Collections.sort(res, new Comparator<String[]>(){

			@Override
			public int compare(String[] arg0, String[] arg1) {
				int diff = arg0[0].length() - arg1[0].length();
				if(diff != 0)
					return diff;
				
				return arg0[1].length() - arg1[1].length();
			}
			
		});
		
		return res;
	}
	
	public void close()
	{
		if(connection != null)
		{
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException("SQLException occured:\n" + e.getMessage());
			}
		}
	}
}
