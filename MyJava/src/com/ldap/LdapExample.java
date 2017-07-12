package com.ldap;

import java.util.Hashtable;

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

public class LdapExample {

	public static final String JAVA_HOME = "C:\\Program Files\\Java\\jdk1.8.0_131";
	
	public static void main(String[] args)
	{
		final String ldapAdServer = "dstest.itsc.cuhk.edu.hk:6368";
		final String ldapSearchBase = "dc=cuhk,dc=edu,dc=hk";

		final String ldapUsername = "uid=37b7fe15-dd1d-42c5-b91e-a7eae9297115, dc=cuhk, dc=edu, dc=hk";
		final String ldapPassword = "Ymy2ms5W";
		
		//final String ldapAccountToLookup = "(|(universityid=ddz044071)(universityid=ddz044072)(universityid=ddz044073)(universityid=ddz044074))";
		final String ldapAccountToLookup = "(|(universityid=ddz044071))";
		
		LdapContext ctx = null;
		try
		{
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

			env.put(Context.SECURITY_PROTOCOL, "ssl");
			env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
			
			if( "ssl".equals(env.get(Context.SECURITY_PROTOCOL)) )
			{
				env.put(Context.PROVIDER_URL, "ldaps://"+ldapAdServer);
			}
			else
			{
				env.put(Context.PROVIDER_URL, "ldap://"+ldapAdServer);
			}

			System.out.println("java home: "+System.getProperty("java.home"));
			System.setProperty("javax.net.ssl.keyStore", JAVA_HOME+"\\jre\\lib\\security\\cacerts");
			System.setProperty("javax.net.ssl.trustStore", JAVA_HOME+"\\jre\\lib\\security\\cacerts");
			
			
			//ensures that objectSID attribute values
			//will be returned as a byte[] instead of a String
			env.put("java.naming.ldap.attributes.binary", "objectSID");
			
			// the following is helpful in debugging errors
			//env.put("com.sun.jndi.ldap.trace.ber", System.err);
			
			SearchControls ctls = new SearchControls();
			ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			ctls.setReturningAttributes(
				new String[] {
					"ds-cfg-listen-port",
					"objectclass"
				} );
			
			ctx = new InitialLdapContext( env, null );

			LdapExample ldap = new LdapExample();
			
			NamingEnumeration<SearchResult> list = ldap.findAccount(ctx, 
					ldapSearchBase, ldapAccountToLookup);
			
			listResult( list );
			
			
			// modify attributes
			ModificationItem[] mods = new ModificationItem[1];

		    Attribute mod0 = new BasicAttribute("visible", "yes");
		    //Attribute mod1 = new BasicAttribute("number2", "AAA");

		    mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, mod0);
		    //mods[1] = new ModificationItem(DirContext.ADD_ATTRIBUTE, mod1);

		    ctx.modifyAttributes("uid=304f8d81-5bce11e7-80babd7d-ae3f753a,dc=cuhk,dc=edu,dc=hk", mods);
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
}
