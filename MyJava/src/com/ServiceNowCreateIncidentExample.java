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

public class ServiceNowCreateIncidentExample
{
	
	
	public static void main(String[] args)
	{
		new ServiceNowCreateIncidentExample();
		
	}
	
	public ServiceNowCreateIncidentExample()
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
			
			URL url = new URL("https://dev22433.service-now.com/api/now/table/incident");
			url = new URL("https://cuhkdev.service-now.com/api/now/table/incident");
			
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			//HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestProperty("Content-Type", "application/json");
			// dev22433
			//connection.setRequestProperty("Authorization", "Basic YWRtaW46MnR4dmZ4Z1hXUkJZ");
			// cuhkdev hingyiuyau@itsc
			connection.setRequestProperty("Authorization", "Basic aGluZ3lpdXlhdUBpdHNjLmN1aGsuZWR1LmhrOnNlcnZpY2Vub3c=");
			
			// jenny law
			String callerId = "8e56ebf70fa6b2005e5bc19ce1050e45";
			String category = "Account, Email & Access";
			String assignmentGroup = "USS - System Development Support";
			String closeCode = "Enquiry";
			String closeNotes = "hello";
			
			//1	New
			//2	In Progress
			//3	On Hold
			//6	Resolved
			//7	Closed
			//8	Canceled
			
			String incidentState = "7";
			// dev22433
			String requestBody = "{\"short_description\":\"cannot catch a bus\",\"caller_id\":\"62826bf03710200044e0bfc8bcbe5df1\",\"category\":\"Software\"}";
			requestBody = "{\"short_description\":\"cannot catch a bus\","
					+ "\"caller_id\":\""+callerId+"\","
					+ "\"state\":\""+incidentState+"\","
					+ "\"assignment_group\":\""+assignmentGroup+"\","
					+ "\"close_code\":\""+closeCode+"\","
					+ "\"close_notes\":\""+closeNotes+"\","
					+ "\"category\":\""+category+"\""
					+ "}";
			
			connection.setDoOutput(true);
			{
				DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
				wr.writeBytes(requestBody);
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
