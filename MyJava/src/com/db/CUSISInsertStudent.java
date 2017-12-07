package com.db;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.util.SqlUtil;

public class CUSISInsertStudent {

	public static void main(String[] args)
	{
		new CUSISInsertStudent();
	}
	
	public CUSISInsertStudent()
	{
		run();
	}
	
	public void run()
	{
		Connection conn = null;
		try
		{
			conn = SqlUtil.getOracleConn("uat", "oim_cusis", "oracle123");
			create(conn, "77100000114");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			SqlUtil.closeConn(conn);
		}
	}
	
	public void create(Connection conn, String studentId)
	{
		//77100000106
		
		PreparedStatement ps = null;
		try
		{
			String sql = "insert into OIM_PS_CU_ARMS_D_VW values ( " +
					"'"+studentId+"', 'CUHK1', 'RPG', 'Postgraduate - Research', 'IEG', " + 
					"'PhD Information Erg_Post', 'Doctor of Philosophy in Information Engineering', 'POF', '60037', 'MPhil-PhD Information Erg', " +
					"'IEGD-MPHD', 'AC', 'RLOA', '20-OCT-17', 'F',  " +
					"'2017', '20-OCT-17', '20-OCT-17', '', 'PHD', 'ENF', 'Faculty of Engineering', 'ENF', 'Y' " +
					") ";

			String sql2 = "insert into OIM_PS_CU_ARMS_M_VW values ( " +
					"'"+studentId+"', 'Chan Hong Tai', 'Chan', '', 'Hong Tai', '陳康泰', '28-DEC-83', 'F', 'yzh1, b710,', 'jhf qhwxflf kxwvfmlwjy hf hhxz bhxz', 'lhdjwx', ' ', 'HKG', 'NT', ' ', ' ', ' ', '', '', '' " +
					") ";
			
			ps = conn.prepareStatement(sql);
			int c = ps.executeUpdate();
			SqlUtil.closeStmt(ps);
			System.out.println(c+" rows inserted");
			
			ps = conn.prepareStatement(sql2);
			int c2 = ps.executeUpdate();
			SqlUtil.closeStmt(ps);
			System.out.println(c2+" rows inserted");
			
			System.out.println("login self service > run STUDENT_CUSIS_ACTIVE_GTC");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			
		}
	}
	
}
