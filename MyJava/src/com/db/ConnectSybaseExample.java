package com.db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

public class ConnectSybaseExample {
	
	public static void main(String[] args)
	{
		/*
		 String driver, String url, String charset, String user, String pass, 
			String univeristyId, String computingId, String initialPassword, String caaSync, String adSync, 
			Date expDate, String updateBy
		 * */
		/*
		SyBaseUtil s = new SyBaseUtil();
		s.createOrUpdateInfoARMS(
				"com.sybase.jdbc3.jdbc.SybDriver",
				"jdbc:sybase:Tds:virgo.itsc.cuhk.edu.hk:2000/newaims_db",
				"cp850", "oim_user", "oim@cuhk", 
				"YS028", "zzzB140082", "aaaa123$", "Y", "Y", 
				java.sql.Date.valueOf("2020-07-14"), "oim_user"
				);
		System.out.println("ConnectSybaseExample c");
		//*/
		new ConnectSybaseExample();
	}
	
	public ConnectSybaseExample()
	{
		Connection conn = null;
		try
		{
			conn = getConn("virgo.itsc.cuhk.edu.hk", 2000,
				"oim_user", "oim@cuhk");
			selectSql(conn);
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
	
	public void selectSql(Connection conn)
	{
		String sql = "select * from INFO_FROM_OIM_T where univ_id = 'YS028'";
		//where univ_id like 'U%' 
		PreparedStatement ps = null;
		ResultSet rs = null;
		try
		{
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next())
			{
				System.out.println(
					rs.getString("univ_id")+" "+
					rs.getString("comp_id")+" "+
					rs.getString("cwem_pw")+" "+
					rs.getString("caa_password_sync")+" "+
					rs.getString("update_on")
						);
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
				if(rs!=null)
				{
					rs.close();
				}
			}
			catch(Exception e)
			{
				
			}
			try
			{
				if(ps!=null)
				{
					ps.close();
				}
			}
			catch(Exception e)
			{
				
			}
		}
	}
	
	public Connection getConn(String host, int port, String username, String password)
	{
		Connection conn = null;
		try
		{
			Properties prop = new Properties();
	        prop.put("user", username);
	        prop.put("password", password);
	        prop.setProperty("charset", "cp850");
			
			Class.forName("com.sybase.jdbc3.jdbc.SybDriver");
			//conn = DriverManager.getConnection("jdbc:oracle:thin:@udbs1.itsc.cuhk.edu.hk:1521/UOIM" );
			conn = DriverManager.getConnection("jdbc:sybase:Tds:"+host+":"+port+"/newaims_db", 
				prop );
			
			System.out.println("conn==null? "+(conn==null));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return conn;
	}
}
