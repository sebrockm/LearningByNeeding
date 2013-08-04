package model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
				
				String type = "";
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
	
	public void close() throws SQLException
	{
		if(connection != null)
			connection.close();
	}
	

	public static void main(String[] args)
	{
		SQLManager myMgr = null;
		BufferedReader buf = null;
		try
		{			
			myMgr = new SQLManager("test.db");
			//myMgr.truncate();
			//myMgr.insertEntriesFromFile("../Downloads/conmkfoffm-8415581224-e7eeu8.txt");
			
			VocabularyBox box = new VocabularyBox();
			buf = new BufferedReader(new InputStreamReader(System.in));
			String line = null;
			System.out.println("Have fun!");
			while(!(line = buf.readLine()).equals("q"))
			{
				if(line.equals("n"))
				{
					System.out.println("new vocabulary:");
					box.insert(buf.readLine());
				}
				else
				{
					int c = Integer.parseInt(line);
					System.out.println(box.getNextVocabInCase(c));
					buf.readLine();
					List<String[]> germans = myMgr.searchForEnglish(box.getNextVocabInCase(c), true);
					int count = 0;
					for(String[] g : germans)
					{
						System.out.println(g[0] + " - " + g[1] + "\t" + g[2]);
						if(++count >= 20)
							break;
					}
					System.out.println("your answer was correct (j)?");
					box.answerVocabInCase(c, buf.readLine().equals("j"));	
				}
				int[] amount = box.getCaseVolumes();
				for(int i=0; i<amount.length; i++)
				{
					System.out.println("case " + i + ": " + amount[i]);
				}
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
			if(buf != null)
			{
				try {
					buf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			System.exit(0);
		}
	}
}
