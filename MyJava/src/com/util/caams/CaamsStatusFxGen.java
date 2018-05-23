package com.util.caams;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Set;
import java.util.TreeSet;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFColor;

public class CaamsStatusFxGen {

	private static final String PATH = "C:\\Users\\a578700\\Downloads\\";
	
	public static void main(String[] args)
	{
		new CaamsStatusFxGen();
		try
		{
			run();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void run() throws Exception
	{
		Workbook wb = WorkbookFactory.create(new FileInputStream(PATH + "Application Status & Data Mapping Overview.xlsx"));
		Sheet sheet = wb.getSheetAt(0);
		Row row;
		Cell cell;
		BufferedWriter bw = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(PATH+"uf_caams_ac_status2.sql"), "utf-8"));
		
		String prj_ac_status;
		String leader_endorse_status;
		String itsc_endorse_status;
		String oim_ac_status;
		String prj_ac_confirmdel_date;
		String prj_ac_original_expiry_date;
		
		String status;
		String applicationType;
		
		
		bw.write("SET ANSI_NULLS ON");bw.newLine();
		bw.write("GO");bw.newLine();

		bw.write("SET QUOTED_IDENTIFIER ON");bw.newLine();
		bw.write("GO");bw.newLine();


		bw.write("alter FUNCTION [dbo].[uf_caams_ac_status2]");bw.newLine(); 
		bw.write("(");bw.newLine();
		bw.write("	@prj_ac_status varchar(15),");bw.newLine();
		bw.write("	@leader_endorse_status varchar(15),");bw.newLine();
		bw.write("	@itsc_endorse_status varchar(15),");bw.newLine();
		bw.write("	@oim_ac_status varchar(15),");bw.newLine();
		bw.write("	@prj_ac_confirmdel_date datetime,");bw.newLine();
		bw.write("	@prj_ac_original_expiry_date datetime,");bw.newLine();
		bw.write("	@prj_ac_expiry_date datetime,");bw.newLine();
		
		bw.write("	@return_index integer");bw.newLine();
		bw.write(")");bw.newLine();
		bw.write("RETURNS varchar(100)");bw.newLine();
		bw.write("AS");bw.newLine();
		bw.write("BEGIN");bw.newLine();
		
		
		bw.write("declare @status as varchar(50);");
		bw.newLine();
		bw.write("declare @applicationType as varchar(50);");
		bw.newLine();
		bw.write("set @status = 'Default';");
		bw.newLine();
		bw.write("set @applicationType = 'Default';");
		bw.newLine();
		
		Set<String> setStatus = new TreeSet<String>();
		Set<String> setApplicationType = new TreeSet<String>();
		
		StringBuffer sbIfClause;
		int caseNumber = 1;
		for(int r = 2;r<19;r++)
		//for(int r = 7;r<8;r++)
		{
			row = sheet.getRow(r);

				
			status = row.getCell(0).getStringCellValue();
			applicationType = row.getCell(1).getStringCellValue();
			
			setStatus.add(status);
			setApplicationType.add(applicationType);
			
			leader_endorse_status = "";
			itsc_endorse_status = "";
			prj_ac_status = "";
			oim_ac_status = "";
			prj_ac_confirmdel_date = "";
			prj_ac_original_expiry_date = "";

			int idx = 3;
			{
				XSSFColor color = XSSFColor.toXSSFColor(row.getCell(idx).getCellStyle().getFillForegroundColorColor());
				if(color!=null && "FFFFFF00".equals(color.getARGBHex()))// yellow
				{
					leader_endorse_status = row.getCell(idx).getStringCellValue();
				}
				idx++;
			}
			
			{
				XSSFColor color = XSSFColor.toXSSFColor(row.getCell(idx).getCellStyle().getFillForegroundColorColor());
				if(color!=null && "FFFFFF00".equals(color.getARGBHex()))// yellow
				{
					itsc_endorse_status = row.getCell(idx).getStringCellValue();
				}
				idx++;
			}
			
			{
				XSSFColor color = XSSFColor.toXSSFColor(row.getCell(idx).getCellStyle().getFillForegroundColorColor());
				if(color!=null && "FFFFFF00".equals(color.getARGBHex()))// yellow
				{
					prj_ac_status = row.getCell(idx).getStringCellValue();
				}
				idx++;
			}
			
			{
				XSSFColor color = XSSFColor.toXSSFColor(row.getCell(idx).getCellStyle().getFillForegroundColorColor());
				if(color!=null && "FFFFFF00".equals(color.getARGBHex()))// yellow
				{
					oim_ac_status = row.getCell(idx).getStringCellValue();
				}
				idx++;
			}
			
			{
				XSSFColor color = XSSFColor.toXSSFColor(row.getCell(idx).getCellStyle().getFillForegroundColorColor());
				if(color!=null && "FFFFFF00".equals(color.getARGBHex()))// yellow
				{
					prj_ac_confirmdel_date = row.getCell(idx).getStringCellValue();
				}
				idx++;
			}
			
			{
				XSSFColor color = XSSFColor.toXSSFColor(row.getCell(idx).getCellStyle().getFillForegroundColorColor());
				if(color!=null && "FFFFFF00".equals(color.getARGBHex()))// yellow
				{
					prj_ac_original_expiry_date = row.getCell(idx).getStringCellValue();
				}
				idx++;
			}
			//System.out.println(color.getARGBHex());
			
			
			
			
			bw.write("--"+caseNumber);bw.newLine();
			caseNumber++;
			if(r>2)
			{
				bw.write("else ");
			}
			
			sbIfClause = new StringBuffer();
			boolean bbIf = false;
			
			{
				String tmp = getIf("leader_endorse_status", leader_endorse_status, bbIf);
				bbIf = bbIf | tmp.length()>0;
				sbIfClause.append( tmp );
			}
			
			{
				String tmp = getIf("itsc_endorse_status", itsc_endorse_status, bbIf);
				bbIf = bbIf | tmp.length()>0;
				sbIfClause.append( tmp );
			}
			
			{
				String tmp = getIf("prj_ac_status", prj_ac_status, bbIf);
				bbIf = bbIf | tmp.length()>0;
				sbIfClause.append( tmp );
			}
			
			{
				String tmp = getIf("oim_ac_status", oim_ac_status, bbIf);
				bbIf = bbIf | tmp.length()>0;
				sbIfClause.append( tmp );
			}
			
			{
				String tmp = getIf("prj_ac_confirmdel_date", prj_ac_confirmdel_date, bbIf);
				bbIf = bbIf | tmp.length()>0;
				sbIfClause.append( tmp );
			}
			
			{
				String tmp = getIf("prj_ac_original_expiry_date", prj_ac_original_expiry_date, bbIf);
				bbIf = bbIf | tmp.length()>0;
				sbIfClause.append( tmp );
			}
			
			
			
			
			bw.write("if " + sbIfClause.toString());
			bw.newLine();
			bw.write("begin");
			bw.newLine();
			bw.write("\tset @status = '"+status+"';");
			bw.newLine();
			bw.write("\tset @applicationType = '"+applicationType+"';");
			bw.newLine();
			bw.write("end");
			bw.newLine();
			
			//System.out.println();
		}
		
		
		bw.write("if @return_index = 1");bw.newLine(); 
		bw.write("	Return @status;");bw.newLine();
		bw.write("if @return_index = 2 ");bw.newLine();
		bw.write("	Return @applicationType;");bw.newLine();
		
		bw.write("Return '{\"status\":\"'+@status+'\",\"applicationType\":\"'+@applicationType+'\"}';");bw.newLine();
		bw.write("end");
		bw.newLine();
		bw.close();
		
		setStatus.add("Default");
		setApplicationType.add("Default");
		System.out.println("--- Status ---");
		for(String s: setStatus)
		{
			System.out.println("_caamsStatusStatus.Add(new SelectListItem() {");
            System.out.println("\tText = \""+s+"\",");
            System.out.println("\tValue = \""+s+"\" });");
		}
		System.out.println("--- Application Type ---");
		for(String s: setApplicationType)
		{
			System.out.println("_caamsStatusApplicationTypes.Add(new SelectListItem() {");
            System.out.println("\tText = \""+s+"\",");
            System.out.println("\tValue = \""+s+"\" });");
		}
		
	}
	
	private static String getIf(String fieldName, String value, boolean appendAnd)
	{
		String aaa = "";
		if(appendAnd)
		{
			aaa = "and ";
		}
		
		if("".equals(value))
		{
			//return aaa + "1=1 ";
			return "";
		}
		
		if("NOT EQUAL to PRJ_AC_APP_T.prj_ac_expiry_date".equals(value))
		{
			return aaa + "@"+ fieldName + " <> @prj_ac_expiry_date ";
		}

		
		if(value.toUpperCase().startsWith("NOT NULL"))
		{
			return aaa + "@"+fieldName+" is not null "; 
		}
		if(value.toUpperCase().startsWith("NULL"))
		{
			return aaa + "@"+fieldName+" is null "; 
		}
		
		if(value.indexOf('/')>=0)
		{
			String[] arr = value.split("/");
			String ss = "'"+arr[0]+"'";
			for(int i = 1;i<arr.length;i++)
			{
				ss += ", '"+arr[i]+"'";
			}
			return aaa + "@"+fieldName+" in ("+ss+") ";
		}
		return aaa + "@"+fieldName+"='"+value+"' ";
	}
}
