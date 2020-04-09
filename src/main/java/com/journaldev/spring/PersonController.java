package com.journaldev.spring;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;



@RestController
public class PersonController {

	public String myString = "";
	@Autowired
	private Person person;
	
//	@RequestMapping("/")
//	public String healthCheck() {
//		return "OK";
//	}
	
	@RequestMapping("/person/get")
	public Person getPerson(@RequestParam(name="name", required=false, defaultValue="Unknown") String name) {
//		person.setName(name);
		return person;
	}
//	@PostMapping(value="/process")
//	public void process(@RequestBody String payload) {
//		myString = myString+payload;
//		JSONObject jsonObject = new JSONObject(payload);
//		String projectkey = jsonObject.getJSONObject("fields").getJSONObject("project").getString("key");
//		System.out.println(projectkey);
//
////		System.out.println(payload);
//	}

//	@RequestMapping(value = "/createbacklog")
//	@RequestMapping(value = "/")
	@PostMapping(value="/process")
//	public String naughtyFunction(@RequestParam(name="issue") String issue,@RequestParam(name="desc") String desc,@RequestParam(name="type") String type)
//	public String talkToJira(@RequestBody String payload)
	public String talkToJira(@RequestBody String payload)
	{
//		JSONObject js = new JSONObject(payload);
//		String projectkey = js.getJSONObject("fields").getString("summary");
//		System.out.println(projectkey);
		payload = payload.replace("bug","Bug");
		payload = payload.replace("backlog","Backlog Item");
		/*******************************************
		payload = payload.replace("bug","Bug");
		payload = payload.replace("backlog","Backlog Item");
		********************************************/


//		myString = myString+payload;
//		myString = myString+payload.toString();
//		System.out.println(myString);

		String responseString="Success!";
		try{

			SSLSocketFactory sf = new SSLSocketFactory(new TrustStrategy(){
				@Override
				public boolean isTrusted(X509Certificate[] chain,
										 String authType) throws CertificateException {
					return true;
				}
			}, new AllowAllHostnameVerifier());

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("https",8444, sf));
			ClientConnectionManager ccm = new ThreadSafeClientConnManager(registry);

			HttpClient httpclient = new DefaultHttpClient(ccm);
			HttpPost httppost = new HttpPost("https://sapjira-test.wdf.sap.corp/rest/api/2/issue/");

			String issueType="Backlog Item";
			String issue = "This issue is created using Java Rest API";
			String desc = "This is a sample description for our test backlog";

//			switch (type){
//				case "Backlog Item":issueType="Backlog Item";
//				break;
//				case "Epic":issueType="Epic";
//				break;
//				case "User Story":issueType="User Story";
//					break;
//				case "Impediment":issueType="Impediment";
//					break;
//				default:
//					issueType="Bug";
//
//			}

			StringEntity requestEntity = new StringEntity(
					"{\n" +
							"    \"fields\": {\n" +
							"       \"project\":\n" +
							"       {\n" +
							"          \"key\": \"JIVAPRODUCTTHREE\"\n" +
							"       },\n" +
							"       \"summary\": \""+issue+"\",\n" +
							"       \"description\": \""+desc+"\",\n" +
							"       \"issuetype\": {\n" +
							"          \"name\": \""+issueType+"\"\n" +
							"       }\n" +
							"   }\n" +
							"}",
					ContentType.APPLICATION_JSON);


//			String FinalMyString = myString.replaceAll('"',)
			StringEntity requestEntity1 = new StringEntity(payload, ContentType.APPLICATION_JSON);


			httppost.setEntity(requestEntity1);
//			httppost.setEntity(requestEntity);

//String st = "{\"issue\" : \"My issue\",\"name\" :\"Divyam\"}";


			String encoding= Base64.getEncoder().encodeToString("UNAME:PASSWD".getBytes());

			httppost.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoding);

			System.out.println("executing request " + httppost.getRequestLine());
			HttpResponse response = httpclient.execute(httppost);
			System.out.println(response.getStatusLine().getStatusCode());
			System.out.println(response.toString());
//			responseString=response.toString();
//			responseString = "You just created a backlog using java rest api under JIVAPROJECTFOUR ";

			HttpEntity entity = response.getEntity();
			int str123 = response.getStatusLine().getStatusCode();
//			String str = "{\"status\":\""+str123+"\"}";
			String str = "";
			if (str123==201){
				str = str+"You have successfully created an issue.";
			}else {
				str = str+"Could not communicate with server.";
			}
//			str = str+str123;

			BufferedReader br=new BufferedReader(new InputStreamReader(entity.getContent()));
			String line;

			while((line=br.readLine())!=null)
			{
				System.out.println(line);
//				str = str+line;
			}
			responseString = str;

		}catch (Exception e){
			e.printStackTrace();
		}

		return responseString;
	}
	
//	@RequestMapping(value="/person/update", method=RequestMethod.POST)
//	public Person updatePerson(@RequestParam(name="name", required=true) String name) {
//		person.setName(name);
//		return person;
//	}
	
	@RequestMapping(value="/person/update", method=RequestMethod.POST, consumes = "application/json")
	public Person updatePerson(@RequestBody Person p) {
		person.setName(p.getName());
		return person;
	}
}
