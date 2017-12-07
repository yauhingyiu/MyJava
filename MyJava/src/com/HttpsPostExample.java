package com;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpsPostExample
{
	
	
	public static void main(String[] args)
	{
		new HttpsPostExample();
		
	}
	
	public HttpsPostExample()
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
				//SSLContext sc = SSLContext.getInstance("SSL");
				sc.init(null, trustAllCerts, new java.security.SecureRandom());
				HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			} catch (Exception e) {
			}
			//*/
			
			
			//URL url = new URL("https://identity.itsc.cuhk.edu.hk:7778/oimservice/updateApproveByAppNo");
			//URL url = new URL("https://uidms1.itsc.cuhk.edu.hk:15001/oimservice/updateApproveByAppNo");
			URL url = new URL("http://didms1.itsc.cuhk.edu.hk:15000/oimservice/updateApproveByAppNo");
			
			//HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			{
				String[] approve = {
					"yes", "yes", "yes", "yes", "yes",
					"yes", "yes", "yes", "yes", "yes"
					, "yes", "yes"
				};
				String appNoDate = "170918";
				String[] appNo = {
					appNoDate+"-000",
					/*
					"170413-015",
//*/
				};
				StringBuffer sbParam = new StringBuffer("a=");
				for(int i = 0;i<appNo.length;i++)
				{
					//sbParam.append("&approve[]="+approve[i]+"&appNo[]="+appNo[i]);
					sbParam.append("&approve[]=no&appNo[]="+appNo[i]);
				}
				DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			    wr.writeBytes(sbParam.toString());
			    wr.flush();
			    wr.close();
			}
			
			
			
			InputStream is = connection.getInputStream();
			int responseCode = connection.getResponseCode();
			System.out.println(responseCode);
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
