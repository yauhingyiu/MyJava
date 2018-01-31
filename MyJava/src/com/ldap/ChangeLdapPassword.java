package com.ldap;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

/*
 * for active directory, ldap user name = userPrincipalName
 * 
 * */
public class ChangeLdapPassword {

	public static final String JAVA_HOME = "C:\\Program Files\\Java\\jdk1.8.0_131";
	public static final ResourceBundle res = ResourceBundle.getBundle(LdapSearch.class.getName());

	public static final SimpleDateFormat SDF_1 = new SimpleDateFormat("yyyyMMdd_hhmmss");
	private String ldapAdServer = "";
	private String ldapSearchBase = "";

	private String ldapUsername = "";
	private String ldapPassword = "";
	
	private boolean useSsl;
	
	public static void main(String[] args)
	{
		String ldapSettingSet = "dstest";
		String universityId = "77100000126";
		
		String newPassword = "bbbb1234!";
		if(args.length>0)
		{
			if(args[0]!=null)
			{
				ldapSettingSet = args[0].trim();
			}
			if(args[1]!=null)
			{
				universityId = args[1].trim();
			}
			if(args[2]!=null)
			{
				newPassword = args[2].trim();
			}
		}
		
		new ChangeLdapPassword( ldapSettingSet, universityId, newPassword );
	}
	
	public ChangeLdapPassword(String ldapSettingSet, 
			String universityId, String newPassword)
	{
		System.out.println("host="+res.getString(ldapSettingSet+".host"));
		System.out.println("searchbase="+res.getString(ldapSettingSet+".searchbase"));
		//System.out.println("username="+res.getString(ldapSettingSet+".username"));
		//System.out.println("password="+res.getString(ldapSettingSet+".password"));
		System.out.println("ssl="+res.getString(ldapSettingSet+".ssl"));
		
		ldapAdServer = res.getString(ldapSettingSet+".host");
		ldapSearchBase = res.getString(ldapSettingSet+".searchbase");
		ldapUsername = res.getString(ldapSettingSet+".username");
		ldapPassword = res.getString(ldapSettingSet+".password");
		useSsl = "yes".equals(res.getString(ldapSettingSet+".ssl"));
		
		
		
		//ldapUsername = "77100000126@connect.itsc.cuhk.edu.hk";
		//ldapPassword = "bbbb1234!";
		
		//ldapUsername = "77100000126@con.itsc.cuhk.edu.hk";
		//ldapPassword = "bbbb1234!";
		
		Hashtable<String, Object> env = new Hashtable<String, Object>();
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		if(ldapUsername != null)
		{
			env.put(Context.SECURITY_PRINCIPAL, ldapUsername);
		}
		if(ldapPassword != null)
		{
			env.put(Context.SECURITY_CREDENTIALS, ldapPassword);
		}

		if(useSsl)
		{
			env.put(Context.SECURITY_PROTOCOL, "ssl");
			env.put(Context.PROVIDER_URL, "ldaps://"+ldapAdServer);
		}
		else
		{
			env.put(Context.PROVIDER_URL, "ldap://"+ldapAdServer);
		}
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		
		//ensures that objectSID attribute values
		//will be returned as a byte[] instead of a String
		env.put("java.naming.ldap.attributes.binary", "objectSID");
		
		// the following is helpful in debugging errors
		//env.put("com.sun.jndi.ldap.trace.ber", System.err);
		
		run( env, ldapSettingSet, universityId, newPassword );
	}

	public String updatePassword(
			DirContext ctx,
			String searchBase, String universityId,
			String newPassword)
	{
		try
		{
		    String newQuotedPassword = "\"" + newPassword + "\"";
		    byte[] newUnicodePassword = newQuotedPassword.getBytes("UTF-16LE");
		    
		    ModificationItem[] mods = new ModificationItem[1];
		    mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
		                   new BasicAttribute("userPassword", newPassword));

		    String userDn = getUserDN(searchBase, universityId, ctx);
		    System.out.println("userDn "+userDn);
		    // Perform the update
		    ctx.modifyAttributes(userDn, mods);
		    System.out.println("Changed Password for " + universityId + " successfully");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	private String getUserDN(String searchBase, String universityId, DirContext dirContext)
			throws NamingException {
		String userDn = null;
		
		String MY_FILTER = "(mailalias="+universityId+"@link.cuhk.edu.hk)";

		SearchControls constraints = new SearchControls();
		constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
		NamingEnumeration results = dirContext.search(searchBase, MY_FILTER, constraints);
		while (results != null && results.hasMore()) {
			SearchResult sr = (SearchResult) results.next();
			//userDn = sr.getName() + "," + searchBase;
			return sr.getNameInNamespace();
		}
		return userDn;
	}
	
	public void run(Hashtable<String, Object> env, String ldapSettingSet,
			String universityId, String newPassword)
	{

		LdapContext ctx = null;
		try
		{
			System.out.println("java home: "+System.getProperty("java.home"));
			System.setProperty("javax.net.ssl.keyStore", JAVA_HOME+"\\jre\\lib\\security\\cacerts");
			System.setProperty("javax.net.ssl.trustStore", JAVA_HOME+"\\jre\\lib\\security\\cacerts");
			
			ctx = new InitialLdapContext( env, null );

			updatePassword(
				ctx,
				ldapSearchBase,
				universityId,
				newPassword);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(ctx!=null)
				{
					ctx.close();
				}
			}
			catch(Exception e)
			{
			}
		}
		System.out.println("LdapExample exit");
	}
	
	private void modifyAttr(LdapContext ctx, String nameInNamespace, 
			List<String> attributeNames, 
			List<String> attributeValues) throws NamingException
	{
		if(attributeNames==null || attributeNames.size()==0)
		{
			return;
		}
	
		ModificationItem[] mods = new ModificationItem[attributeNames.size()];

	    //Attribute mod0 = new BasicAttribute("visible", "yes");

		int i = 0;
	    for(String attrName: attributeNames)
	    {
	    	mods[i] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute(attrName, attributeValues.get(i) ));
	    	i++;
	    }

	    //ctx.modifyAttributes("uid=304f8d81-5bce11e7-80babd7d-ae3f753a,dc=cuhk,dc=edu,dc=hk", mods);
	    ctx.modifyAttributes(nameInNamespace, mods);
	}
	

}
