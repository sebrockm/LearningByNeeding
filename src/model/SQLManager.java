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

/**
 * This class is used to get access to a vocabulary database using sqlite.
 * @author Sebastian Brockmeyer
 *
 */
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
	
	/**
	 * Creates a new SQLManager with a connection to the database given by <tt>databasePath</tt>.
	 * If the given file does not exist, it will be created.
	 * @param databasePath path to the database
	 */
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
	
	/**
	 * Retrieves the path to the database.
	 * @return path to database
	 */
	public String getDatabasePath()
	{
		return databasePath;
	}
	
	/**
	 * Closes the connection to the old database and connects to the database given by
	 * <tt>databasePath</tt> instead. If the given file does not exist, it will be created.
	 * @param databasePath path to the new database
	 */
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
	
	/**
	 * Truncates the database, i.e. the table isn't dropped but it's contents are deleted.
	 */
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
	
	/**
	 * Inserts a new entry into the database.
	 * @param english English meaning of the vocabulary
	 * @param german German meaning of the vocabulary
	 * @param type the type of the word, e.g. noun
	 * @return true if the new entry could be inserted, false otherwise
	 */
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
	
	/**
	 * Inserts entries from a text file given by <tt>path</tt>. The text file must have the
	 * following scheme: 
	 * 1) a line beginning with '#' will be ignored
	 * 2) any other line must begin with an English word, followed by a '\t' and the German meaning and then (optionally)
	 * followed by another '\t' and the word type.
	 * @param path the path to the text file
	 * @return the number of inserted entries
	 * @throws FileNotFoundException if the file given by <tt>path</tt> was not found
	 */
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
	
	/**
	 * Searches for an English word and returns a list of entries matching that word.
	 * @param english the word to be searched for
	 * @param like if true, the SQL query uses 'LIKE'
	 * @return a list of String triples {english, german, type} where english matches the parameter <tt>english</tt>
	 */
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
	
	/**
	 * Searches for an German word and returns a list of entries matching that word.
	 * @param german the word to be searched for
	 * @param like if true, the SQL query uses 'LIKE'
	 * @return a list of String triples {german, english, type} where german matches the parameter <tt>german</tt>
	 */
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
	
	/**
	 * Closes the connection to the database.
	 */
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
