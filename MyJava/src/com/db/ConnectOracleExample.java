package com.db;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectOracleExample {
	public static void main(String[] args)
	{
		new ConnectOracleExample();
	}
	
	public ConnectOracleExample()
	{
		try
		{
			Connection conn = getConn("udbs1.itsc.cuhk.edu.hk", 1521,
				"oim_proj", "oracle123");
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public Connection getConn(String host, int port, String username, String password)
	{
		Connection conn = null;
		try
		{
			Class.forName("oracle.jdbc.driver.OracleDriver");
			//conn = DriverManager.getConnection("jdbc:oracle:thin:@udbs1.itsc.cuhk.edu.hk:1521/UOIM" );
			conn = DriverManager.getConnection("jdbc:oracle:thin:@"+host+":"+port+"/UOIM", 
				username, password );
			
			System.out.println("conn==null? "+(conn==null));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return conn;
	}
}
