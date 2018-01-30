package com.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import com.util.SqlUtil;


/*
Advance	PROG_STATUS=AD
Active	PROG_STATUS = AC
Leave of Absence	PROG_STATUS = LA
Suspension	PROG_STATUS = SP
Completed Program	PROG_STATUS = CM
Discontinued	PROG_STATUS = DC
Cancelled	PROG_STATUS = CN
*/
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
			//conn = SqlUtil.getOracleConn("dev", "oim_cusis", "oracle123");
			create(conn, "77100000126", true, true);
			
			checkView( conn );
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
	
	
	public void checkView(Connection conn)
	{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try
		{
			String sql = "select * from CUSIS_TO_OIM_ACTIVE";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			ResultSetMetaData metaData = rs.getMetaData();
			int columnCount = metaData.getColumnCount();
			for(int c = 1;c<=columnCount;c++)
			{
				System.out.print(metaData.getColumnName(c)+",");
			}
			System.out.println();
			int cnt = 0;
			while(rs.next())
			{
				for(int c = 1;c<=columnCount;c++)
				{
					System.out.print(rs.getString(metaData.getColumnName(c))+",");
				}
				System.out.println();
				cnt++;
			}
			System.out.println(cnt+" rows returned");
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			SqlUtil.closeRs(rs);
			SqlUtil.closeStmt(ps);
		}
	}
	
	public void create(Connection conn, String studentId, 
			boolean generateCompletedProgram, boolean optin)
	{
		// after provision, update [active program] will generate [Active Program for AD]
		// execute STUDENT_CUSIS_ACTIVE_PROF_GTC in OIM
		// [active program] will be updated
		// go to design console > odsee student > Active Program Updated > Get AD Program List > Active Program Fpr AD Updated  
		// task "Active Program Updated" calls LdapUtil2.updateUserProgramCompareData()
		// task "Active Program for AD Updated" updates the [active program for ad] in OIM usr
		
		
		// active program = Doctor~JUD-P-JD~SLAW~Faculty of Law~RPG~2016;LLM Common Law~LCL-P-LLM~SLAW~Faculty of Law~PGDE~2018
		
		/*
		1000000420	CUHK1	RPG	Postgraduate - Research	SWL	PhD Social Welfare	Doctor of Philosophy in Social Welfare	PGP	60029	MPhil-PhD Social Welfare	SOWF-MPHD	CM	COMP	30-APR-05	P	2000	31-DEC-07	31-DEC-07	31-DEC-07	PHD	SSF	Faculty of Social Science	SSF	Y	SOWKV
		1000000420	CUHK1	TPG	Postgraduate - Taught	SWK	Master of Social Work	Master of Social Work	PGP	40184	Master of Social Work	SOWK-MSW	CM	COMP	31-JUL-97	P	1994	31-JUL-97	31-JUL-97	31-JUL-97	MSW	SSF	Faculty of Social Science	SSF	Y	SOWKV 
		 * */
		//boolean generateCompletedProgram = true; 
		
		PreparedStatement ps = null;
		try
		{
			String effdt = "20-OCT-17";
			String expectGradDt = "20-OCT-18";
			String idCardExpiryDt = "20-OCT-18";
			String progStatus1 = "AC";
			String progStatus2 = "CM";
			String sql = "insert into OIM_PS_CU_ARMS_D_VW values ( " +
					"'"+studentId+"', 'CUHK1', 'RPG', 'Postgraduate - Research', 'IEG', " + 
					"'PhD Information Erg_Post', 'Doctor of Philosophy in Information Engineering', 'POF', '60037', 'MPhil-PhD Information Erg', " +
					"'IEGD-MPHD', '"+progStatus1+"', 'RLOA', '"+effdt+"', 'F',  " +
					"'2018', '"+expectGradDt+"', '"+idCardExpiryDt+"', '', 'PHD', 'ENF', 'Faculty of Engineering', 'ENF', 'Y' " +
					") ";
			
			String sql2 = "insert into OIM_PS_CU_ARMS_D_VW values ( " +
					"'"+studentId+"', 'CUHK1', 'TPG', 'Postgraduate - Taught', 'SWK', " + 
					"'Master of Social Work', 'Master of Social Work', 'PGP', '40184', 'Master of Social Work', " +
					"'SOWK-MSW', '"+progStatus2+"', 'COMP', '"+effdt+"', 'F',  " +
					"'2018', '"+expectGradDt+"', '"+idCardExpiryDt+"', '', 'MSW', 'SSF', 'Faculty of Social Science', 'SSF', 'Y' " +
					") ";

			String sql3 = "insert into OIM_PS_CU_ARMS_M_VW values ( "
					 		+ "'"+studentId+"', 'Chan Hong Tai', 'Chan', '', 'Hong Tai', "
							+ "'陳康泰', '28-DEC-83', 'F', 'yzh1, b710,', 'jhf qhwxflf kxwvfmlwjy hf hhxz bhxz', "
							+ "'lhdjwx', ' ', 'HKG', 'NT', ' ', ' ', ' ', '', '', '' " +
					") ";
			
			String sqlOptin = "insert into PS_CU_LIFELNG_DE_V values('"+studentId+"', sysdate, 'Y')";
			
			
			{
				ps = conn.prepareStatement(sql);
				int c = ps.executeUpdate();
				SqlUtil.closeStmt(ps);
				System.out.println("sql "+c+" rows inserted");
			}
			
			if(generateCompletedProgram)
			{
				ps = conn.prepareStatement(sql2);
				int c = ps.executeUpdate();
				SqlUtil.closeStmt(ps);
				System.out.println("sql2 "+c+" rows inserted");
			}
			
			if(optin)
			{
				ps = conn.prepareStatement(sqlOptin);
				int c = ps.executeUpdate();
				SqlUtil.closeStmt(ps);
				System.out.println("sqlOptin "+c+" rows inserted");
			}
			
			ps = conn.prepareStatement(sql3);
			int c2 = ps.executeUpdate();
			SqlUtil.closeStmt(ps);
			System.out.println("sql3 "+c2+" rows inserted");
			
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
