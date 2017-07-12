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

public class LdapSearch {

	public static final String JAVA_HOME = "C:\\Program Files\\Java\\jdk1.8.0_131";
	public static final ResourceBundle res = ResourceBundle.getBundle(LdapSearch.class.getName());

	public static final SimpleDateFormat SDF_1 = new SimpleDateFormat("yyyyMMdd_hhmmss");
	private String ldapAdServer = "";
	private String ldapSearchBase = "";

	private String ldapUsername = "";
	private String ldapPassword = "";
	
	private boolean useSsl;
	private LdapContext ctx;
	
	public static void main(String[] args)
	{
		//final String ldapAccountToLookup = "(|(universityid=ddz044071)(universityid=ddz044072)(universityid=ddz044073)(universityid=ddz044074))";
		String ldapSettingSet = "dstest";
		String searchKeyword = "(|(universityid=ddz044071))";
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
		
		new LdapSearch( ldapSettingSet, searchKeyword );
	}
	
	public LdapSearch(String ldapSettingSet, String searchKeyword)
	{
		System.out.println("host="+res.getString(ldapSettingSet+".host"));
		System.out.println("searchbase="+res.getString(ldapSettingSet+".searchbase"));
		System.out.println("username="+res.getString(ldapSettingSet+".username"));
		System.out.println("password="+res.getString(ldapSettingSet+".password"));
		System.out.println("ssl="+res.getString(ldapSettingSet+".ssl"));
		
		ldapAdServer = res.getString(ldapSettingSet+".host");
		ldapSearchBase = res.getString(ldapSettingSet+".searchbase");
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
		
		run( env, searchKeyword );
	}
	
	public void run(Hashtable<String, Object> env, String searchKeyword)
	{

		
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
			
			LdapContext ctx = new InitialLdapContext( env, null );

			NamingEnumeration<SearchResult> list = findAccount(ctx, 
					ldapSearchBase, searchKeyword);
			
			listResult( list );
			
			
			// modify attributes
			//*
		    {
		    	List<String> attributeNames = new ArrayList<String>(); 
		    	List<String> attributeValues = new ArrayList<String>();
		    	attributeNames.add("mailalias");
		    	attributeValues.add("ddz044071@cuhk.edu.hk");
		    	//attributeNames.add("userPassword");
		    	//attributeValues.add("99999999");
		    	modifyAttr( ctx, 
			    		"uid=304f8d81-5bce11e7-80babd7d-ae3f753a,dc=cuhk,dc=edu,dc=hk", 
			    		attributeNames, attributeValues);
		    }
		    //*/
		    
		}
		catch(Exception e)
		{
			e.printStackTrace();
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
				
				System.out.print( "name: "+attrObj.getID()+", " );
				System.out.println( "value: "+attrObj.get() );
				//System.out.println( "attr: "+(attrObj instanceof javax.naming.directory.Attribute) );
				
				//break;
			}
			i++;
		}
	}
	
	public static void listResultCsv(NamingEnumeration<SearchResult> results) throws NamingException, IOException
	{
		Calendar cal = Calendar.getInstance();
		
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(SDF_1.format(cal.getTime())+"_ldapSearch.csv"), "utf8"));
		String csvCaption = "nameInNameSpace";
		int i = 1;
		SearchResult searchResult = null;
		StringBuffer sbValue;
		
		int captionSeq = 0;
		
		
		// calculate caption
		Map<String, Integer> captionMap = new TreeMap<String, Integer>();
		List<Map<String, Object>> captionValueList = new ArrayList<Map<String, Object>>();
		Map<String, Object> captionValue;
		while(results.hasMoreElements())
		{
			searchResult = (SearchResult) results.nextElement();
			System.out.println("============ record "+i+" ============");
			System.out.println( searchResult.getNameInNamespace() );
			Attributes attrs = searchResult.getAttributes();
			NamingEnumeration enumer = attrs.getAll();
			Attribute attrObj;
			//sbValue = new StringBuffer( "\""+searchResult.getNameInNamespace()+"\"" );
			captionValue = new TreeMap<String, Object>();
			captionValue.put("nameInNameSpace", searchResult.getNameInNamespace() );
			while( enumer.hasMore() )
			{
				attrObj = (Attribute)enumer.next();
				
				if(!captionMap.containsKey(attrObj.getID()))
				{
					captionMap.put(attrObj.getID(), captionSeq++);
				}
				
				captionValue.put(attrObj.getID(), attrObj.get());
				System.out.print( "name: "+attrObj.getID()+", " );
				System.out.println( "value: "+attrObj.get() );
				
			}
			captionValueList.add(captionValue);
			i++;
		}
		
		// write caption
		{
			Set<Entry<String, Integer>> entrySet = captionMap.entrySet();
			Iterator<Entry<String, Integer>> iter = entrySet.iterator();
			bw.write( "nameInNameSpace" );
			while(iter.hasNext())
			{
				bw.write( ","+iter.next().getKey() );
			}
			bw.newLine();
		}
		
		// write data
		for(Map<String, Object> captionValue2: captionValueList)
		{
			bw.write("\""+captionValue2.get("nameInNameSpace").toString()+"\"");
			
			Set<Entry<String, Integer>> entrySet = captionMap.entrySet();
			Iterator<Entry<String, Integer>> iter = entrySet.iterator();
			Object o;
			while(iter.hasNext())
			{
				o = captionValue2.get( iter.next().getKey() );
				if(o==null)
				{
					bw.write( ",");
				}
				else
				{
					bw.write( ",\""+o.toString()+"\"");
				}
			}
			bw.newLine();
			
		}

		bw.close();
	}
}
