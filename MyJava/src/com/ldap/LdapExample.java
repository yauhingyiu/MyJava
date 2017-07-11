package com.ldap;

/*
 * how to setup:
 * 
 * 1. import cert first: (default password is changeit)
 * keytool -importcert -file C:\Users\a578700\Desktop\dstest.der -alias dstest.der -keystore "C:\Program Files\Java\jdk1.8.0_131\jre\lib\security\cacerts" -storepass changeit
 * 
 * 
 * */

import java.io.File;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
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
		
		final String ldapAccountToLookup = "(|(universityid=ddz044071)(universityid=ddz044072)(universityid=ddz044073)(universityid=ddz044074))";
		
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
			
			LdapContext ctx = new InitialLdapContext( env, null );

			LdapExample ldap = new LdapExample();
			
			NamingEnumeration<SearchResult> list = ldap.findAccount(ctx, 
					ldapSearchBase, ldapAccountToLookup);
			
			listResult( list );
		}
		catch(Exception e)
		{
			e.printStackTrace();
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
