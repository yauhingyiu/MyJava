package com.db;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.util.SqlUtil;

public class CUPISInsertStaff {

	public static void main(String[] args)
	{
		new CUPISInsertStaff();
	}
	
	public CUPISInsertStaff()
	{
		run();
	}
	
	public void run()
	{
		Connection conn = null;
		try
		{
			conn = SqlUtil.getOracleConn("dev", "oim_cupis", "oracle123");
			create(conn, "8888831");
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
	
	public void create(Connection conn, String staffId)
	{
		//77100000106
		
		PreparedStatement ps = null;
		try
		{
			String sql = "insert into OIM_PS_CU_ARM_SI1PS_VW ( " +
					"EMPLID, NATIONAL_ID, PASSPORT_NBR, NAME_PREFIX, LAST_NAME, " +
					"FIRST_NAME, CU_CUST_NAME_E, CU_NAME_CHI, CU_CUST_NAME_C, SEX, " + 
					"BIRTHDATE, CU_DT_JOIN_U, CU_LAST_REJOIN_U, CU_CUR_ANT_HIS, CU_STAFF_STATUS, " + 
					"CU_ORG_CNTCT_ENDDT, CU_ACT_CNTCT_ENDDT, CU_MAX_CNTCT_ENDDT, CU_EXP_RET_DT, CU_ACT_RET_DT, " + 
					"CU_APPT_MODE, CU_TIME_BASIS, CU_TERM_OF_SERVICE, CU_APPT_STAT_TYPE, CU_PER_ORG_DESC,  " +
					"CU_POI_TYPE_DESC, JOB_FUNCTION, JOB_FAMILY, JOBCODE, CU_ACAD_BUS_TITLE,  " +
					"DEPTID, CU_COLLEGE, CU_CREATION_DATE, CU_DECORATION1_ENG " +
					") values ( " +
					
					"'"+staffId+"', ' ', ' ', 'MR', 'TAM ', " + 
					"'Kei Man', 'Mr Tam Kei Man', '譚啟文', '譚啟文', 'M', " + 
					"'08-JUL-84', '12-MAY-17', '12-MAY-17', 'C', 'A',  " +
					"'', '', '', '31-JUL-44', '',  " +
					"'C', 'F', 'B', 'NOR', 'Employee', " + 
					"' ', 'ADM', 'EO', 'EO2', ' ',  " +
					"'EMO', 'CN', '12-MAY-17', ' ' " +
					
					") ";

			
			ps = conn.prepareStatement(sql);
			int c = ps.executeUpdate();
			SqlUtil.closeStmt(ps);
			System.out.println(c+" rows inserted");
			
			ps = conn.prepareStatement("call CUPIS_TO_OIM_PKG.assign_computing_id ()");
			ps.executeUpdate();
			SqlUtil.closeStmt(ps);
			
			System.out.println("login self service, run > STAFF_CUPIS_ALL_GTC");
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
