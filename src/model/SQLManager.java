package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class SQLManager {
	
	private static String sqliteDriver = "org.sqlite.JDBC";
	private static String pathPraefix = "jdbc:sqlite:";
	
	private String databasePath;
	private Connection connection = null;
	
	private void createTable(Connection c) throws SQLException
	{
		String create = "CREATE TABLE IF NOT EXISTS EnglishGerman " +
				"(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
				"english	VARCHAR(250)," +
				"german		VARCHAR(250)," +
				"type		VARCHAR(20))";
		PreparedStatement stmt = c.prepareStatement(create);
		stmt.executeUpdate();
		stmt.close();
	}
	
	public SQLManager(String databasePath) throws SQLException, ClassNotFoundException
	{		
		Class.forName(sqliteDriver);

		switchDatabase(databasePath);
	}
	
	public String getDatabasePath()
	{
		return databasePath;
	}
	
	public void switchDatabase(String databasePath) throws SQLException
	{
		this.databasePath = databasePath;
		if(connection != null)
			connection.close();
		
		connection = DriverManager.getConnection(pathPraefix + databasePath);
		createTable(connection);
	}
	
	public void truncate() throws SQLException
	{
		String truncate = "DROP TABLE EnglishGerman";
		Statement stmt = connection.createStatement();
		stmt.executeUpdate(truncate);
		stmt.close();
		createTable(connection);
	}
	
	public boolean insertEntry(String english, String german, String type) throws SQLException
	{
		String insert = "INSERT INTO EnglishGerman (english,german,type) VALUES(?,?,?);";
		PreparedStatement stmt = connection.prepareStatement(insert);
		stmt.setString(1, english);
		stmt.setString(2, german);
		stmt.setString(3, type);
		boolean res = stmt.executeUpdate() == 1;
		stmt.close();
		
		return res;
	}
	
	public int insertEntriesFromFile(String path) throws FileNotFoundException, SQLException
	{
		BufferedReader r = new BufferedReader(new FileReader(path));
		
		connection.setAutoCommit(false);

		String insert = "INSERT INTO EnglishGerman (english,german,type) VALUES(?,?,?);";
		PreparedStatement stmt = connection.prepareStatement(insert);
		int counter = 0;
		String line = null;
		try {
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
				
				String type = "other";
				if(tok.hasMoreTokens())
					type = tok.nextToken();

				stmt.setString(1, english);
				stmt.setString(2, german);
				stmt.setString(3, type);
				
				counter += stmt.executeUpdate();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		stmt.close();
		connection.commit();
		connection.setAutoCommit(true);
		
		return counter;
	}
	
	public List<String[]> searchForEnglish(String english, boolean like) throws SQLException
	{
		String select = "SELECT * FROM EnglishGerman WHERE english ";
		select += like ? "LIKE '%' || ? || '%'" : "= ?";
		
		PreparedStatement stmt = connection.prepareStatement(select);
		stmt.setString(1, english);
		
		ResultSet rs = stmt.executeQuery();
		LinkedList<String[]> res = new LinkedList<String[]>();
		while(rs.next())
		{
			res.addLast(new String[]{rs.getString("english"), rs.getString("german"), rs.getString("type")});
		}
		
		stmt.close();
		rs.close();
		
		return res;
	}
	
	public List<String[]> searchForGerman(String german, boolean like) throws SQLException
	{
		String select = "SELECT * FROM EnglishGerman WHERE german ";
		select += like ? "LIKE '%' || ? || '%'" : "= ?";
		
		PreparedStatement stmt = connection.prepareStatement(select);
		stmt.setString(1, german);
		
		ResultSet rs = stmt.executeQuery();
		LinkedList<String[]> res = new LinkedList<String[]>();
		while(rs.next())
		{
			res.addLast(new String[]{rs.getString("german"), rs.getString("english"), rs.getString("type")});
		}
		
		stmt.close();
		rs.close();
		
		return res;
	}
	
	public void close() throws SQLException
	{
		if(connection != null)
			connection.close();
	}
	

	public static void main(String[] args)
	{
		SQLManager myMgr = null;
		try
		{			
			myMgr = new SQLManager("test.db");
			//myMgr.truncate();
			//myMgr.insertEntriesFromFile("../Downloads/conmkfoffm-8415581224-e7eeu8.txt");
			List<String[]> list = myMgr.searchForEnglish("took", false);
			for(String[] ary : list)
			{
				System.out.println(ary[0] + " - " + ary[1] + "\t\t" + ary[2]);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(myMgr != null)
			{
				try {
					myMgr.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			System.exit(0);
		}
	}
}
