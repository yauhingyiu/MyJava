package com.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import java.sql.ResultSet;

public class SqlUtil {

	private static final Map<String, String> DEV_DB_PROFILE = new HashMap<String, String>();
	private static final Map<String, String> UAT_DB_PROFILE = new HashMap<String, String>();
	private static final Map<String, String> PRD_DB_PROFILE = new HashMap<String, String>();
	
	static
	{
		DEV_DB_PROFILE.put("driver", "oracle.jdbc.driver.OracleDriver");
		DEV_DB_PROFILE.put("url", "jdbc:oracle:thin:@ddbs1.itsc.cuhk.edu.hk:1521/DOIM");
		DEV_DB_PROFILE.put("username", "idm_oim");
		DEV_DB_PROFILE.put("password", "oracle123");
		
		UAT_DB_PROFILE.put("driver", "oracle.jdbc.driver.OracleDriver");
		UAT_DB_PROFILE.put("url", "jdbc:oracle:thin:@udbs1.itsc.cuhk.edu.hk:1521/UOIM");
		UAT_DB_PROFILE.put("username", "idm_oim");
		UAT_DB_PROFILE.put("password", "oracle123");
		
		PRD_DB_PROFILE.put("driver", "oracle.jdbc.driver.OracleDriver");
		PRD_DB_PROFILE.put("url", "jdbc:oracle:thin:@pdbs1.itsc.cuhk.edu.hk:1521/POIM");
		PRD_DB_PROFILE.put("username", "sdt_oim");
		PRD_DB_PROFILE.put("password", "1dm@7ws!");
	}
	private static String driver;
	private static String url;
	private static String username;
	private static String password;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static final Connection getOracleConn(String profileName)
	{
		
		if("uat".equalsIgnoreCase(profileName))
		{
			driver = UAT_DB_PROFILE.get("driver");
			url = UAT_DB_PROFILE.get("url");
			username = UAT_DB_PROFILE.get("username");
			password = UAT_DB_PROFILE.get("password");
		}
		else
		{
			driver = DEV_DB_PROFILE.get("driver");
			url = DEV_DB_PROFILE.get("url");
			username = DEV_DB_PROFILE.get("username");
			password = DEV_DB_PROFILE.get("password");
		}
		Connection conn = null;
		try
		{
			System.out.println(url+", "+username+", "+password);
			Class.forName(driver);
			conn = DriverManager.getConnection(url, username, password);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return conn;
	}
	
	public static final void closeRs(ResultSet s)
	{
		try
		{
			if(s!=null)
			{
				s.close();
			}
		}
		catch(Exception e)
		{
			
		}
	}
	
	public static final void closeStmt(Statement s)
	{
		try
		{
			if(s!=null)
			{
				s.close();
			}
		}
		catch(Exception e)
		{
			
		}
	}
	
	public static final void closeConn(Connection s)
	{
		try
		{
			if(s!=null)
			{
				s.close();
			}
		}
		catch(Exception e)
		{
			
		}
	}
}
