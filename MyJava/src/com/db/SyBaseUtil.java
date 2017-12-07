package com.db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Properties;
import java.util.Random;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.sql.DataSource;

public class SyBaseUtil {
	
	private final static String TABLE_NAME = "INFO_FROM_OIM_T";	
//	private static SyBaseConnectionPool connPool;
	
	private final static String SQL_CREATE_INFORM_ARMS_RECORD = 
			"INSERT INTO " + TABLE_NAME
			+ " (univ_id, comp_id, cwem_pw, "
			+ " caa_create_date, caa_password_sync, "
			+ " ad_create_date, ad_password_sync, "
			+ " exp_date, create_date, update_on, update_by )"
			+ "  VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";	
	
	private final static String SQL_UPDATE_INFORM_ARMS_RECORD = 
			"UPDATE " + TABLE_NAME			
			+ " set comp_id = ?, "
			+ " cwem_pw = ?, "
			+ " caa_create_date = ?, "
			+ " caa_password_sync = ?, "
			+ " ad_create_date = ?, "
			+ " ad_password_sync = ?, "			
			+ " exp_date = ?, "
			+ " update_on = ?, "
			+ " update_by = ? "
			+ " where univ_id = ? ";	

	private final static String SQL_COUNT_INFORM_ARMS_RECORD = 
			"SELECT COUNT(1) FROM " + TABLE_NAME
			+ " where univ_id = ? ";
	
	private final static String SQL_INFORM_ARMS_RECORD_EXISTS = 
			"SELECT univ_id FROM " + TABLE_NAME
			+ " where univ_id = ? ";		
	
	private final static String SQL_INSERT_OR_UPDATE_ARMS_RECORD =
			"IF EXISTS (" + SQL_INFORM_ARMS_RECORD_EXISTS + ") "
			+ " BEGIN "
			+ SQL_UPDATE_INFORM_ARMS_RECORD
			+ " END "
			+ " ELSE "
			+ " BEGIN "
			+ SQL_CREATE_INFORM_ARMS_RECORD
			+ " END ";

	public static void main(String[] args) throws Exception{
		String driver		= "com.sybase.jdbc3.jdbc.SybDriver";
		String url			= "jdbc:sybase:Tds:virgo.itsc.cuhk.edu.hk:2000/newaims_db";
		String charset		= "cp850";
		String admin		= "oim_user";
		String pass			= "oim@cuhk";
		String universityId	= "179040";
		String comp_id		= "b790449";
		String cwem_pw		= "Abcd456!";
		
		//new SyBaseUtil().insertOrUpdateINFO_FROM_OIM_T(driver, url, charset, admin, pass, universityId, comp_id, cwem_pw);
		
	}
	
	private Connection connect(String driver, String url, String charset, String user, String pass)throws Exception {
		Properties prop = new Properties();
        prop.put("user", user);
        prop.put("password", pass);
        prop.setProperty("charset", charset);
		
		Class.forName(driver);
		Connection con = DriverManager.getConnection(url, prop);
		return con;
	}

	/*
	 * called by design console task "ASL Update SyBase Program"
	 * 
	 * */
	public  String insertOrUpdateINFO_FROM_OIM_T(String driver , String url, String charset, String user, String pass, String universityId,
												String comp_id, String cwem_pw){

		debug("UniversityId = "+ universityId);
		ResultSet resultset = null;
		String result = "";
		
		
		try{
			Connection conn = connect(driver, url, charset, user, pass);
			String query = "select count(1) as result from INFO_FROM_OIM_T where univ_id = '"+universityId+"'";
			//debug(query);
			
			Statement stmt = conn.createStatement();
			resultset = stmt.executeQuery(query);
			
		    int total = 0;
		    if (resultset.next()){
		    	total = resultset.getInt(1);

		    	if (total > 0){
		    		debug("Type : Insert record");
		    		result = updateINFO_FROM_OIM_T(driver, url, charset, user, pass, universityId, comp_id, cwem_pw);
		    	}
		    	else{
		    		debug("Type : Update record");
		    		result = insertINFO_FROM_OIM_T(driver, url, charset, user, pass, universityId, comp_id, cwem_pw);
		    	}
		    	
		    }  
			
		    debug("Number of records : "+total);
		    
			conn.close();
			
			if(!result.equalsIgnoreCase("C")){
				return "R";
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return "R";
		}
		return "C";
	}
	
	public  String insertINFO_FROM_OIM_T(String driver , String url, String charset, String user, String pass, String universityId, 
										String comp_id, String cwem_pw){
		
		try{
			Connection conn = connect(driver, url, charset, user, pass);
			String query = "INSERT INTO INFO_FROM_OIM_T ";
			query += "(univ_id, comp_id, cwem_pw, create_date, exp_date, update_on, update_by) VALUES ";
			query += "('"+universityId+"', '"+comp_id+"', '"+cwem_pw+"', getdate() , dateadd(yy, 4, getdate()), getdate() , 'oim_user')";
			
			//debug(query);
			
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(query);

			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
			return "R";
		}
		return "C";
	}
	
	public  String updateINFO_FROM_OIM_T(String driver , String url, String charset, String user, String pass, String universityId, 
										String comp_id, String cwem_pw){
		
		try{
			Connection conn = connect(driver, url, charset, user, pass);
			String query = "update INFO_FROM_OIM_T set comp_id='"+comp_id+"', cwem_pw='"+cwem_pw+"', update_by='oim_user', update_on=getdate() where univ_id = '"+universityId+"'";
			//debug(query);
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(query);
		    
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
			return "R";
		}
		return "C";
	}
	
	public void debug(String message){
		System.out.println(new java.util.Date()+ " "+message);
	}
	
	/*
	 * called by design console task "CUHK Staff Inform ARMS"
	 * 
	 * */
	public String createOrUpdateInfoARMS(String driver, String url, String charset, String user, String pass, 
			String univeristyId, String computingId, String initialPassword, String caaSync, String adSync, 
			Date expDate, String updateBy){		
		
		String result = "R";
		
		Timestamp currentTimestamp = new Timestamp(Calendar.getInstance().getTime().getTime());		
		Timestamp caaCreateTimestamp = currentTimestamp;
		Timestamp adCreateTimestamp = currentTimestamp;					
		
		if(caaSync != null && caaSync.length() > 0 
				&& adSync != null && adSync.length() > 0)
		{		
			Connection conn = null;
				log("***** Start update ARMS DB updated for " + univeristyId);				
			try {		
				long now = System.currentTimeMillis();
				conn =  connect(driver, url, charset, user, pass);
				if(checkInfoArmsRecordExists(conn, univeristyId)){	
					result = updateInfoArmsRecord(conn, computingId, initialPassword, caaCreateTimestamp, caaSync, 
							adCreateTimestamp, adSync, expDate, updateBy, univeristyId, currentTimestamp);
				}else{					
					result = createInfoArmsRecord(conn, univeristyId, computingId, initialPassword, 
							caaCreateTimestamp, caaSync, adCreateTimestamp, adSync, expDate, updateBy, currentTimestamp);
				}
				log("***** End update ARMS DB updated for " + univeristyId + " - duration: " + (System.currentTimeMillis() - now)/1000 + " seconds");					
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(conn != null){
					closeConnection(conn);						
				}
			}
		}else{
			result = "C";
		}
		
		return result;		
	}	
	
	private String insertOrUpdateInfoArmsRecord(Connection conn, String univeristyId, String computingId, 
			String initialPassword, Timestamp caaCreateTimestamp, String caaSync, 
			Timestamp adCreateTimestamp, String adSync, Date expDate, String updateBy, Timestamp currentTimestamp) throws Exception{
		String result = "R";
		PreparedStatement ps = null;
		try {		
			ps = conn.prepareStatement(SQL_INSERT_OR_UPDATE_ARMS_RECORD);
			ps.setString(1, univeristyId);			
			ps.setString(2, computingId);
			ps.setString(3, initialPassword);
			ps.setTimestamp(4, caaCreateTimestamp);
			ps.setString(5, caaSync);	
			ps.setTimestamp(6, adCreateTimestamp);			
			ps.setString(7, adSync);			
			ps.setDate(8, expDate);		
			ps.setTimestamp(9, currentTimestamp);			
			ps.setString(10, updateBy);
			ps.setString(11, univeristyId);					
			ps.setString(12, univeristyId);
			ps.setString(13, computingId);
			ps.setString(14, initialPassword);
			ps.setTimestamp(15, caaCreateTimestamp);
			ps.setString(16, caaSync);			
			ps.setTimestamp(17, adCreateTimestamp);			
			ps.setString(18, adSync);
			ps.setDate(19, expDate);		
			ps.setTimestamp(20, currentTimestamp);	
			ps.setTimestamp(21, currentTimestamp);				
			ps.setString(22, updateBy);
			ps.executeUpdate();			
			result = "C";
		} catch (Exception e) {
			throw e;
		} finally {
			ps.close();
		}				
		return result;
	}
		
	
	private String createInfoArmsRecord(Connection conn, String univeristyId, String computingId, 
			String initialPassword, Timestamp caaCreateTimestamp, String caaSync, 
			Timestamp adCreateTimestamp, String adSync, Date expDate, String updateBy, Timestamp currentTimestamp) throws Exception{
		String result = "R";
		PreparedStatement ps = null;
		try {		
			ps = conn.prepareStatement(SQL_CREATE_INFORM_ARMS_RECORD);
			ps.setString(1, univeristyId);
			ps.setString(2, computingId);
			ps.setString(3, initialPassword);
			ps.setTimestamp(4, caaCreateTimestamp);
			ps.setString(5, caaSync);			
			ps.setTimestamp(6, adCreateTimestamp);			
			ps.setString(7, adSync);
			ps.setDate(8, expDate);		
			ps.setTimestamp(9, currentTimestamp);	
			ps.setTimestamp(10, currentTimestamp);				
			ps.setString(11, updateBy);
			ps.executeUpdate();			
			result = "C";
		} catch (Exception e) {
			throw e;
		} finally {
			ps.close();
		}				
		return result;
	}	
	
	private String updateInfoArmsRecord(Connection conn, String computingId, 
			String initialPassword, Timestamp caaCreateTimestamp, String caaSync,
			Timestamp adCreateTimestamp, String adSync,
			Date expDate, String updateBy, String univeristyId, Timestamp currentTimestamp) throws Exception{
		String result = "R";
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(SQL_UPDATE_INFORM_ARMS_RECORD);
			ps.setString(1, computingId);
			ps.setString(2, initialPassword);
			ps.setTimestamp(3, caaCreateTimestamp);
			ps.setString(4, caaSync);	
			ps.setTimestamp(5, adCreateTimestamp);			
			ps.setString(6, adSync);			
			ps.setDate(7, expDate);		
			ps.setTimestamp(8, currentTimestamp);			
			ps.setString(9, updateBy);
			ps.setString(10, univeristyId);			
			ps.executeUpdate();
			result = "C";
		} catch (Exception e) {
			throw e;
		} finally {
			ps.close();
		}				
		return result;
	}	
	
	private boolean checkInfoArmsRecordExists(Connection conn, String univeristyId) throws Exception{
		boolean result = false;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(SQL_COUNT_INFORM_ARMS_RECORD);
			ps.setString(1, univeristyId);			
			rs = ps.executeQuery();
			if(rs.next() && rs.getInt(1) > 0){
				result = true;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			rs.close();			
			ps.close();
		}			
		return result;
	}
	
	private void closeConnection(Connection conn){
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void log(Object o){
		 System.out.println(Calendar.getInstance().getTime().toString() + " v20160224v1 - " +Thread.currentThread().getId() + ":" + o);
	}
}
