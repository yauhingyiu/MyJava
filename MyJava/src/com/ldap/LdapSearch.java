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
public class LdapSearch {

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
		String ldapUsername = null;
		String ldapPassword = null;
		//final String ldapAccountToLookup = "(|(universityid=ddz044071)(universityid=ddz044072)(universityid=ddz044073)(universityid=ddz044074))";
		String ldapSettingSet = "prdad";
		//ldapUsername = "77100000127@connect.itsc.cuhk.edu.hk";
		//ldapUsername = "uid=496e5181-059611e8-80babd7d-ae3f753a,dc=cuhk, dc=edu, dc=hk";
		//ldapUsername = "1007008630@link.cuhk.edu.hk";
		//ldapUsername = "uid=ece20781-4c5411e8-80babd7d-ae3f753a,dc=cuhk,dc=edu,dc=hk";
		//ldapPassword = "pjacct#test1";
		//ldapPassword = "8wikw7&&";
		String searchKeyword = "(|(mail=wilson.ng@link.cuhk.edu.hk)(mail=sphpc_ug@cuhk.edu.hk))";
		//searchKeyword = "(|(mail=dda1000090@cuhk.edu.hk)(mail=dda1000100@cuhk.edu.hk))";
		searchKeyword = "(|(universityid=8888844))";
		searchKeyword = "(|(universityid=a484700))";
		//searchKeyword = "(|(universityid=1155117722))";

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
		
		new LdapSearch( ldapSettingSet, searchKeyword, true, ldapUsername, ldapPassword );
	}
	
	public LdapSearch(String ldapSettingSet, String searchKeyword, boolean writeFile,
			String overrideLdapUsername, String overrideLdapPassword)
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
		
		if(overrideLdapUsername!=null)
		{
			ldapUsername = overrideLdapUsername;
		}
		if(overrideLdapPassword!=null)
		{
			ldapPassword = overrideLdapPassword;
		}
		
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
		env.put("java.naming.ldap.attributes.binary", "objectGUID");
		//Security.addProvider(new BouncyCastleProvider());
		
		// the following is helpful in debugging errors
		//env.put("com.sun.jndi.ldap.trace.ber", System.err);
		
		run( env, searchKeyword, ldapSettingSet, writeFile );
	}
	
	public void run(Hashtable<String, Object> env, 
			String searchKeyword, String ldapSettingSet, boolean writeFile)
	{

		LdapContext ctx = null;
		try
		{
			System.out.println("java home: "+System.getProperty("java.home"));
			System.setProperty("javax.net.ssl.keyStore", JAVA_HOME+"\\jre\\lib\\security\\cacerts");
			System.setProperty("javax.net.ssl.trustStore", JAVA_HOME+"\\jre\\lib\\security\\cacerts");
			//System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
			
			SearchControls ctls = new SearchControls();
			ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			ctls.setReturningAttributes(
				new String[] {
					"ds-cfg-listen-port",
					"objectclass"
				} );
			
			ctx = new InitialLdapContext( env, null );

			//GetADGUID(ctx, ldapSearchBase, "1008013550");
			//GetADGUID(ctx, ldapSearchBase, "603149");
			/*
			removeProxyAddress(ctx,
					"CN=1a0eff81-4e7911e8-80babd7d-ae3f753a,OU=OtherAccounts,OU=People,DC=uad,DC=cuhk,DC=edu,DC=hk",
					"smtp:dda1000700@mailserv.cuhk.edu.hk"
			);
			removeProxyAddress(ctx,
					"CN=1a0eff81-4e7911e8-80babd7d-ae3f753a,OU=OtherAccounts,OU=People,DC=uad,DC=cuhk,DC=edu,DC=hk",
					"SMTP:dda1000700@cuhk.edu.hk"
			);
			//*/
			
			//*
			NamingEnumeration<SearchResult> list = findAccount(ctx, 
					ldapSearchBase, searchKeyword);
			
			listResultCsv( list, ldapSettingSet, true, writeFile );
			//*/
			
			//ModificationItem[] mods = new ModificationItem[1];
			//mods[0]=new ModificationItem(DirContext.ADD_ATTRIBUTE,new BasicAttribute("proxyAddresses", "SMTP:zbb040045@cuhk.edu.hk"));
			//ctx.modifyAttributes("CN=Tai Man Chan - 045,OU=BUR,OU=BurAdm,OU=Staff,OU=People,DC=uad,DC=cuhk,DC=edu,DC=hk", mods);
			
			// modify attributes
			/*
		    {
		    	List<String> attributeNames = new ArrayList<String>(); 
		    	List<String> attributeValues = new ArrayList<String>();
		    	//attributeNames.add("displayName");
		    	//attributeValues.add("Kei Man Tam  (EMO)44");
		    	attributeNames.add("userPassword");
		    	attributeValues.add("99999999");
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
	
	public static void listResultCsv(NamingEnumeration<SearchResult> results, 
			String ldapSettingSet, boolean printProperties, boolean writeFile) throws NamingException, IOException
	{
		Calendar cal = Calendar.getInstance();
		
		
		//String csvCaption = "nameInNameSpace";
		int i = 1;
		SearchResult searchResult = null;
		//StringBuffer sbValue;
		
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
				if(printProperties && (
						"memberOf".equals(attrObj.getID()) ||
						"proxyAddresses".equals(attrObj.getID())
						))
				{
					listAttrs( attrObj.getAll(), attrObj.getID() );
				}
				else if(printProperties && (
						"objectGUID".equals(attrObj.getID()) 
						
					)
				)
				{
					byte[] guid = (byte[]) attrObj.get(0);
					//System.out.println( attr.get(0).getClass() );
					System.out.println( ""+attrObj.getID()+" = "+toHexString(guid) );
				}
				else if( printProperties )
				{
					System.out.println( ""+attrObj.getID()+" = "+attrObj.get() );
				}
				
			}
			captionValueList.add(captionValue);
			i++;
		}
		
		if(writeFile)
		{
			//captionMap.clear();
			//captionMap.put("telephoneNumber", 1);
			
			BufferedWriter bw = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream("D:\\Documents\\temp\\"+SDF_1.format(cal.getTime())
							+"_"+ldapSettingSet+"_search.csv"), "utf8"));
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
		// end if
	}
	
	
	public static void removeProxyAddress(DirContext dirContext, 
			String userDn, String value) throws NamingException
	{
		ModificationItem[] mods = new ModificationItem[1];
		mods[0]=new ModificationItem(DirContext.REMOVE_ATTRIBUTE,new BasicAttribute("proxyAddresses",value));
		dirContext.modifyAttributes(userDn, mods);
	}
	
	public String GetADGUID(DirContext dirContext, String searchBase, String universityId){

		System.out.println("GetADGUID = "+ universityId);
		String result="";
		
		
		try{
			String MY_FILTER = "(universityid="+universityId+")";

			SearchControls constraints = new SearchControls();
			constraints.setReturningAttributes(new String []{"objectGUID"});
			constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration results = dirContext.search(searchBase, MY_FILTER, constraints);

			while (results != null && results.hasMore()) {
				SearchResult sr = (SearchResult) results.next();
				
				Attributes attributes = sr.getAttributes();
				Attribute attr = attributes.get("objectGUID");

			    byte[] GUID = (byte[])attr.get();
//System.out.print(Arrays.toString(GUID));			    
				String objectGUID = toHexString(GUID);
				System.out.println("objectGUID = "+objectGUID);
				result = objectGUID;
			}
			return result;

		} catch (Exception e) {
			e.printStackTrace();
			return "R";
		}finally{

		}
	}
	
	private static String toHexString(byte[] inArr){
	    StringBuffer guid = new StringBuffer();
	    for (int i = 0; i < inArr.length; i++) {
	      guid.append(Integer.toHexString(256 + (inArr[i] & 0xFF)).substring(1));
	    }
	    return guid.toString();
	}
}
