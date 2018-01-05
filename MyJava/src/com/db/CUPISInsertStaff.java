package com.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.MessageFormat;

import com.util.SqlUtil;

public class CUPISInsertStaff {

	public static void main(String[] args)
	{
		new CUPISInsertStaff();
	}
	
	public CUPISInsertStaff()
	{
		run();
		//runAudit();
	}
	
	public void runAudit()
	{
		
		Connection conn = null;
		try
		{
			conn = SqlUtil.getOracleConn("prd", "oim_cupis", "RF3y9up#");
			oimAuditLog(conn, "415988");
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
	
	public void run()
	{
		Connection conn = null;
		try
		{
			conn = SqlUtil.getOracleConn("dev", "oim_cupis", "oracle123");
			create(conn, "8888841", false);
			check(conn);
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
	
	public void check(Connection conn)
	{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try
		{
			String sql = "select * from OIM_PS_CU_ARM_SI1PS_VW";
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
	
	public void create(Connection conn, String staffId, boolean clearOldRecord)
	{
		//77100000106
		
		PreparedStatement ps = null;
		try
		{
			if(clearOldRecord)
			{
				ps = conn.prepareStatement("truncate table OIM_PS_CU_ARM_SI1PS_VW");
				ps.executeUpdate();
				SqlUtil.closeStmt(ps);
			}

			String firstName = "Tai Man";
			String lastName = "Chan";
			String chiName = "陳大文";
			String deptCode = "BUR";
			Object[] arr = {
				firstName,
				lastName,
				chiName,
				deptCode
			};
			String sql = "insert into OIM_PS_CU_ARM_SI1PS_VW ( " +
					"EMPLID, NATIONAL_ID, PASSPORT_NBR, NAME_PREFIX, LAST_NAME, " +
					"FIRST_NAME, CU_CUST_NAME_E, CU_NAME_CHI, CU_CUST_NAME_C, SEX, " + 
					"BIRTHDATE, CU_DT_JOIN_U, CU_LAST_REJOIN_U, CU_CUR_ANT_HIS, CU_STAFF_STATUS, " + 
					"CU_ORG_CNTCT_ENDDT, CU_ACT_CNTCT_ENDDT, CU_MAX_CNTCT_ENDDT, CU_EXP_RET_DT, CU_ACT_RET_DT, " + 
					"CU_APPT_MODE, CU_TIME_BASIS, CU_TERM_OF_SERVICE, CU_APPT_STAT_TYPE, CU_PER_ORG_DESC,  " +
					"CU_POI_TYPE_DESC, JOB_FUNCTION, JOB_FAMILY, JOBCODE, CU_ACAD_BUS_TITLE,  " +
					"DEPTID, CU_COLLEGE, CU_CREATION_DATE, CU_DECORATION1_ENG " +
					") values ( " +
					
					"''"+staffId+"'', '' '', '' '', ''MR'', ''{1}'', " + 
					"''{0}'', ''Mr {1} {0}'', ''{2}'', ''{2}'', ''M'', " + 
					"''08-JUL-84'', ''12-MAY-17'', ''12-MAY-17'', ''C'', ''A'', " +
					"'''', '''', '''', ''31-JUL-44'', '''', " +
					"''C'', ''F'', ''B'', ''NOR'', ''Employee'', " + 
					"'' '', ''ADM'', ''EO'', ''EO2'', '' '',  " +
					"''{3}'', ''CN'', ''12-MAY-17'', '' '' " +
					
					") ";
			sql = MessageFormat.format(sql, arr);
			System.out.println(sql);
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
	
	public void oimAuditLog(Connection conn, String usrLogin)
	{
		String sql = "select " +
		"u.upa_usr_key, d.field_name, d.FIELD_old_VALUE, d.FIELD_NEW_VALUE, upa_usr_eff_from_date " +
		"from  " +
		"idm_oim.upa_usr u, idm_oim.upa_fields d " + 
		"where  " +
		"u.upa_usr_key = d.upa_usr_key " + 
		"and u.usr_login in ( " +
		"'"+usrLogin+"' " +
		") " +
		"order by " + 
		"	upa_usr_eff_from_date desc ";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try
		{
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next())
			{
				for(int i = 1;i<6;i++)
				{
					System.out.print(rs.getString(i)+"\t");
				}
				System.out.println();
			}
			
			
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
	
}
