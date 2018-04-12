package com.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectSqlServerExample {
	public static void main(String[] args)
	{
		new ConnectSqlServerExample();
	}
	
	public ConnectSqlServerExample()
	{
		Connection conn = null;
		try
		{
			//conn = getConn("logkeeper.itsc.cuhk.edu.hk", 5000, "hingyiu_itsc", "hing#0518yiu", "IAM_DEV_DB");
			
			conn = getConn("bee-sql.itsc.cuhk.edu.hk", 1433, "hyyau_itsc", "qgaxQk4tOig65yOO", "ARMS_PRD_DB");
			//assignComputingID(conn);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(conn!=null)
				{
					conn.close();
				}
			}
			catch(Exception e)
			{
				
			}
		}
	}
	
	public Connection getConn(String host, int port, String username, String password, String database)
	{
		Connection conn = null;
		try
		{
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			conn = DriverManager.getConnection("jdbc:sqlserver://"+host+":"+port+";databaseName="+database, 
				username, password );
			
			System.out.println("conn==null? "+(conn==null));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return conn;
	}
	
	private void assignComputingID(Connection dbconnection) throws SQLException {
		CallableStatement cstmt = 
				dbconnection.prepareCall ("{call PROJECT_TO_OIM_PKG.assign_computing_id()}");	
		cstmt.setQueryTimeout(600);		
		cstmt.execute();
		cstmt.close();
	}
}
