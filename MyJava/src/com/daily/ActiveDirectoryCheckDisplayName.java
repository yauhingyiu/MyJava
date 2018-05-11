package com.daily;

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
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.PagedResultsControl;
import javax.naming.ldap.PagedResultsResponseControl;

import com.ldap.LdapSearch;

public class ActiveDirectoryCheckDisplayName {

	public static final String JAVA_HOME = "C:\\Program Files\\Java\\jdk1.8.0_131";
	public static final ResourceBundle res = ResourceBundle.getBundle(LdapSearch.class.getName());

	public static final SimpleDateFormat SDF_1 = new SimpleDateFormat("yyyyMMdd_hhmmss");
	private String ldapAdServer = "";
	private String ldapSearchBase = "";

	private String ldapUsername = "";
	private String ldapPassword = "";
	
	private boolean useSsl;
	//private LdapContext ctx;
	
	public static void main(String[] args)
	{
		//final String ldapAccountToLookup = "(|(universityid=ddz044071)(universityid=ddz044072)(universityid=ddz044073)(universityid=ddz044074))";
		String ldapSettingSet = "prdad";
		String searchKeyword = "(|(mail=wilson.ng@link.cuhk.edu.hk)(mail=sphpc_ug@cuhk.edu.hk))";
		//searchKeyword = "(|(mail=dda1000090@cuhk.edu.hk)(mail=dda1000100@cuhk.edu.hk))";
		searchKeyword = "(|(universityid=77100000107))";
		//searchKeyword = "(|(universityid=1155055188))";
		//searchKeyword = "(|(universityid=1155097778))";
		//searchKeyword = "displayName=aa,-() _'./";
		searchKeyword = "(&(objectCategory=CN=Person,CN=Schema,CN=Configuration,DC=cuhk,DC=edu,DC=hk)(deptCode=ITSC))";
		searchKeyword = "(universityid=*)";
		//searchKeyword = "(|(universityid=77100000114))";
		if(args.length>0)
		{
			if(args[0]!=null)
			{
				ldapSettingSet = args[0].trim();
			}
			if(args[1]!=null)
			{
				searchKeyword = args[1].trim();
			}
		}
		
		new ActiveDirectoryCheckDisplayName( ldapSettingSet, searchKeyword );
	}
	
	public ActiveDirectoryCheckDisplayName(String ldapSettingSet, String searchKeyword)
	{
		System.out.println("host="+res.getString(ldapSettingSet+".host"));
		System.out.println("searchbase="+res.getString(ldapSettingSet+".searchbase"));
		//System.out.println("username="+res.getString(ldapSettingSet+".username"));
		//System.out.println("password="+res.getString(ldapSettingSet+".password"));
		System.out.println("ssl="+res.getString(ldapSettingSet+".ssl"));
		
		ldapAdServer = res.getString(ldapSettingSet+".host");
		ldapSearchBase = "OU=ArtsAdm,OU=Staff,OU=People,DC=cuhk,DC=edu,DC=hk";//res.getString(ldapSettingSet+".searchbase");
		ldapUsername = res.getString(ldapSettingSet+".username");
		ldapPassword = res.getString(ldapSettingSet+".password");
		useSsl = "yes".equals(res.getString(ldapSettingSet+".ssl"));
		
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
		
		run( env, searchKeyword, ldapSettingSet );
	}
	
	public void run(Hashtable<String, Object> env, String searchKeyword, String ldapSettingSet)
	{

		LdapContext ctx = null;
		try
		{
			System.out.println("java home: "+System.getProperty("java.home"));
			System.setProperty("javax.net.ssl.keyStore", JAVA_HOME+"\\jre\\lib\\security\\cacerts");
			System.setProperty("javax.net.ssl.trustStore", JAVA_HOME+"\\jre\\lib\\security\\cacerts");
			
			SearchControls ctls = new SearchControls();
			ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			ctls.setReturningAttributes(
				new String[] {
					"ds-cfg-listen-port",
					"objectclass"
				} );
			
			ctx = new InitialLdapContext( env, null );

			NamingEnumeration<SearchResult> list = findAccountWithPaging(ctx, 
					ldapSearchBase, searchKeyword);
			
			//listResult( list );
			
			
			// modify attributes
			/*
		    {
		    	List<String> attributeNames = new ArrayList<String>(); 
		    	List<String> attributeValues = new ArrayList<String>();
		    	attributeNames.add("displayName");
		    	attributeValues.add("Kei Man Tam  (EMO)44");
		    	//attributeNames.add("userPassword");
		    	//attributeValues.add("99999999");
		    	modifyAttr( ctx, 
			    		"CN=Tam Kei Man - 084,OU=EMO,OU=EmoAdm,OU=Staff,OU=People,DC=adtest,DC=cuhk,DC=edu,DC=hk", 
			    		attributeNames, attributeValues);
		    }
		    //*/
		    
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
	
	private void modifyAttr(LdapContext ctx, String nameInNamespace, List<String> attributeNames, List<String> attributeValues) throws NamingException
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
	
	public NamingEnumeration<SearchResult> findAccount(DirContext ctx, 
			String ldapSearchBase, String searchFilter) throws NamingException {

		SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

		NamingEnumeration<SearchResult> results = ctx.search(ldapSearchBase, searchFilter, searchControls);

		return results;
	}
	
	public NamingEnumeration<SearchResult> findAccountWithPaging(LdapContext ctx, 
			String ldapSearchBase, String searchFilter) throws NamingException, IOException
	{
		SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		
		int pageSize = 1000;
		byte[] cookie = null;
		ctx.setRequestControls(new Control[] { new PagedResultsControl(pageSize,
				Control.NONCRITICAL) });
		int total = 0;
		
		do {
			/* perform the search */
			NamingEnumeration<SearchResult> results = ctx.search(ldapSearchBase, searchFilter, searchControls);

			/* for each entry print out name + all attrs and values */
			while (results != null && results.hasMore()) {
				SearchResult entry = (SearchResult) results.next();
				//System.out.println(entry.getName());
				listAttrs(entry);
			}

			// Examine the paged results control response
			Control[] controls = ctx.getResponseControls();
			if (controls != null) {
				for (int i = 0; i < controls.length; i++) {
					if (controls[i] instanceof PagedResultsResponseControl) {
						PagedResultsResponseControl prrc = (PagedResultsResponseControl) controls[i];
						total = prrc.getResultSize();
						if (total != 0) {
							System.out.println(
									"***************** END-OF-PAGE " + "(total : " + total + ") *****************\n");
						} else {
							System.out.println("***************** END-OF-PAGE " + "(total: unknown) ***************\n");
						}
						cookie = prrc.getCookie();
					}
				}
			} else {
				System.out.println("No controls were sent from the server");
			}
			// Re-activate paged results
			ctx.setRequestControls(new Control[] { new PagedResultsControl(pageSize, cookie, Control.CRITICAL) });

		} while (cookie != null);
		
		return null;
	}
	
	public static void listResult(NamingEnumeration<SearchResult> results) throws NamingException
	{
		int i = 1;
		SearchResult searchResult = null;
		while(results.hasMoreElements())
		{
			searchResult = (SearchResult) results.nextElement();
			System.out.println("============ record "+i+" ============");
			System.out.println( searchResult.getNameInNamespace() );
			Attributes attrs = searchResult.getAttributes();
			NamingEnumeration enumer = attrs.getAll();
			Attribute attrObj;
			while( enumer.hasMore() )
			{
				attrObj = (Attribute)enumer.next();
				//System.out.println( "attr: "+attrObj.getClass().getName() );
				//System.out.println( "attr: "+attrObj );
				
				//System.out.print( "name: "+attrObj.getID()+", " );
				//System.out.println( "value: "+attrObj.get() );
				
				//break;
			}
			i++;
		}
	}
	
	public static void listAttrs(NamingEnumeration enumer, String attrName) throws NamingException
	{
		//System.out.println("listAttrs ");
		Attribute attrObj;
		String s;
		while( enumer.hasMore() )
		{
			//attrObj = (Attribute)enumer.next();
			s = (String)enumer.next();
			//System.out.print( "name: "+attrObj.getID()+", " );
			//System.out.println( "value: "+attrObj.get() );
			System.out.println( attrName + " = " + s );
			
			
		}
	}
	
	public static void listAttrs(SearchResult searchResult) throws NamingException
	{
		System.out.println( searchResult.getNameInNamespace() );
		Attributes attrs = searchResult.getAttributes();
		NamingEnumeration enumer = attrs.getAll();
		Attribute attrObj;
		//sbValue = new StringBuffer( "\""+searchResult.getNameInNamespace()+"\"" );
		while( enumer.hasMore() )
		{
			attrObj = (Attribute)enumer.next();
			
			if("memberOf".equals(attrObj.getID()))
			{
				//listAttrs( attrObj.getAll(), attrObj.getID() );
			}
			else if("displayName".equals(attrObj.getID()))
			{
				System.out.print( ""+attrObj.getID()+" = " );
				System.out.println( ""+attrObj.get() );
			}
			else
			{
				
				//System.out.print( ""+attrObj.getID()+" = " );
				//System.out.println( ""+attrObj.get() );
			}
			
		}
	}
}
