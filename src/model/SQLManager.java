package model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.StringTokenizer;

public class SQLManager {
	static boolean fill = false;
	public static void main(String[] args)
	{
		BufferedReader r = null;
		try {
			r = new BufferedReader(new FileReader("/home/poppes/Downloads/conmkfoffm-8415581224-e7eeu8.txt"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			System.exit(0);
		}
		
		
		Connection c = null;
		PreparedStatement stmt = null;
		try
		{
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:test.db");
			c.setAutoCommit(false);
			
			if(fill)
			{
				String drop = "DROP TABLE IF EXISTS EnglishGerman";
				stmt = c.prepareStatement(drop);
				stmt.executeUpdate();
				
				String create = "CREATE TABLE IF NOT EXISTS EnglishGerman " +
								"(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
								"english	VARCHAR(250)," +
								"german		VARCHAR(250)," +
								"type		VARCHAR(20))";
				stmt = c.prepareStatement(create);
				stmt.executeUpdate();
				
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
					
					String type = "other";
					if(tok.hasMoreTokens())
						type = tok.nextToken();
					
					String insert = "INSERT INTO EnglishGerman (english,german,type) VALUES(?,?,?);";
					
					stmt = c.prepareStatement(insert);
					stmt.setString(1, english);
					stmt.setString(2, german);
					stmt.setString(3, type);
					
					System.out.println(english + "\t" + german + "\t" + type);
					
					stmt.executeUpdate();
				}
				c.commit();
			}
			else // not fill
			{
				/*stmt = c.prepareStatement("CREATE INDEX indexOnEnglishSide ON EnglishGerman (english)");
				stmt.execute();
				stmt = c.prepareStatement("CREATE INDEX indexOnGermanSide ON EnglishGerman (german)");
				stmt.execute();
				c.commit();*/
				stmt = c.prepareStatement("SELECT * FROM EnglishGerman WHERE german LIKE '%schwimmen%'");
				ResultSet set = stmt.executeQuery();
				
				while(set.next())
				{
					System.out.println(set.getString("german") + " - " + set.getString("english") + "\t\t" + set.getString("type"));
					
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(0);
		}
		finally
		{
			if(r != null)
			{
				try {
					r.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			try {
				stmt.close();
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			System.out.println("Läuft");
		}
		
		
	}
}
