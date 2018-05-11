package com.daily;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TaskStudentOptIn {
	public static void main(String[] args)
	{
		new TaskStudentOptIn();
	}
	
	public TaskStudentOptIn()
	{
		Connection conn = null;
		try
		{
			System.out.println(getClass().getSimpleName()+" start");
			conn = getConn("bee-sql.itsc.cuhk.edu.hk", 1433,
				"hyyau_itsc", "qgaxQk4tOig65yOO", "ARMS_PRD_DB");
			query(conn);
			System.out.println(getClass().getSimpleName()+" end");
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
	
	public Connection getConn(String host, int port, String username, String password, String db)
	{
		Connection conn = null;
		try
		{
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			conn = DriverManager.getConnection("jdbc:sqlserver://"+host+":"+port+";databaseName="+db, 
				username, password );
			
			System.out.println("conn==null? "+(conn==null));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return conn;
	}
	
	private void query(Connection conn)
	{
		String sql = "";
		sql += "SELECT stu.* " +
				"FROM [dbo].[OIM_STUDENT_T] stu " +
				"where  " +
				"getdate() > stu.actual_disable_date " +
				"and stu.usr_end_date > getdate() " +
				"and exists( " +
				"select 1 from O365_ALUMNI_OPTIN_T opt " +
				"where opt.university_id = stu.university_id " +
				") ";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try
		{
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next())
			{
				System.out.print( rs.getString("university_id"));
				System.out.println( );
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(rs==null) {
					rs.close();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			try
			{
				if(ps==null) {
					ps.close();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

	}
}
