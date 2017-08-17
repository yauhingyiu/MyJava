package com;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpsExample
{
	
	
	public static void main(String[] args)
	{
		new HttpsExample();
		
	}
	
	public HttpsExample()
	{
		run();
	}
	
	public void run()
	{
		try
		{
			// Create a new trust manager that trust all certificates
			//*
			TrustManager[] trustAllCerts = new TrustManager[]{
				new X509TrustManager() {
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return null;
					}
					public void checkClientTrusted(
						java.security.cert.X509Certificate[] certs, String authType) {
					}
					public void checkServerTrusted(
						java.security.cert.X509Certificate[] certs, String authType) {
					}
				}
			};

			// Activate the new trust manager
			
			try {
				SSLContext sc = SSLContext.getInstance("TLS");
				sc.init(null, trustAllCerts, new java.security.SecureRandom());
				HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			} catch (Exception e) {
			}
			//*/
			
			
			URL url = new URL("https://portal.cuhk.edu.hk/psp/epprd/?cmd=login&languageCd=ENG&");
			//System.out.println(url.openConnection().getClass());
			URLConnection connection = url.openConnection();
			
			//HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			InputStream is = connection.getInputStream();
			BufferedReader br = new BufferedReader( new InputStreamReader( new BufferedInputStream( is ) ) );
			String tmp;
			while(true)
			{
				tmp = br.readLine();
				if(tmp==null)
				{
					break;
				}
				System.out.println(tmp);
			}
			br.close();
			is.close();

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
