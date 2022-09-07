package tests;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.testng.TestNGUtils;
import org.testng.annotations.Test;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class Wso2_Apim {
	
	Response  res1, res2, res3, res4, res5, res6, res7;

	FileInputStream input;
	Properties p;
	byte[] authplj;
	byte[] apicreationplj;
	String authpls;
	String apicreationpls;

	String accessToken;

	
	@Test
	public void oauth2() {

		try {
			String path =  "./src/test/resources/config.properties";
			authplj = Files.readAllBytes(Paths.get("./src/test/payloads/payload.json"));
			authpls = new String(authplj);
			apicreationplj = Files.readAllBytes(Paths.get("./src/test/payloads/apicretioon_payload.json"));
			apicreationpls = new String(apicreationplj);
			p = new Properties();
			input = new FileInputStream(path);
			p.load(input);

		} catch (Exception e) {
			System.out.println(e);
		}
		
		res1 = RestAssured.given()
				.relaxedHTTPSValidation()
				.auth()
				.preemptive()
				.basic(p.getProperty("adminusername"),p.getProperty("adminpassword"))
				.body(authpls)
				.contentType("application/json")
				.post(p.getProperty("hosturi")+"9443/client-registration/v0.17/register");
		
		///System.out.println(res1.jsonPath().prettify());
		
		res2 = RestAssured.given()
				.relaxedHTTPSValidation()
				.auth()
				.basic(res1.jsonPath().get("clientId").toString(), res1.jsonPath().get("clientSecret").toString())  
				.queryParam("grant_type","password")
				.queryParam("username",p.getProperty("adminusername"))
				.queryParam("password","admin")
				.queryParam("scope","apim:api_view apim:api_create")
				.post(p.getProperty("hosturi")+"8243/token");
	
		accessToken = res2.jsonPath().get("access_token").toString();

		res3 = RestAssured.given()
				.relaxedHTTPSValidation()
				.auth()
				.oauth2(accessToken)
				.body(apicreationpls)
				.contentType("application/json")
				.post(p.getProperty("publisher_url"));
		
		res4 = RestAssured.given()
				.relaxedHTTPSValidation()
				.auth()
				.oauth2(accessToken)
				.get(p.getProperty("publisher_url"));
		
		//System.out.println(res4.jsonPath().prettyPrint());
			
	}
	
	@Test
	public void oauth2_test2() {
		
		res5 = RestAssured.given()
				.relaxedHTTPSValidation()
				.auth()
				.oauth2(accessToken)
				.get(p.getProperty("publisher_url")+"/"+res4.jsonPath().get("list[0]['id']"));
		
		//System.out.println(res5.jsonPath().prettyPrint());
	}
	
	@Test
	public void oauth2_test3() {
		
		res6 = RestAssured.given()
				.relaxedHTTPSValidation()
				.auth()
				.oauth2(accessToken)
				.get(p.getProperty("publisher_url")+"/"+res4.jsonPath().get("list[0]['id']"));
		
		//System.out.println(res5.jsonPath().prettyPrint());

		res7 = RestAssured.given()
				.relaxedHTTPSValidation()
				.auth()
				.oauth2(accessToken)
				.multiPart(new File("./src/test/payloads/thumbnail.jpg"))
				//.multiPart("./src/test/payloads/thumbnail.jpg","file","application/json")
				.put(p.getProperty("publisher_url")+"/"+res4.jsonPath().get("list[0]['id']")+"/thumbnail");

		System.out.println(res7.jsonPath().prettyPrint());
	}
	// @Test 
	// public void add_thumbnail_app(){

	// 	res7 = RestAssured.given()
	// 			.relaxedHTTPSValidation()
	// 			.auth()
	// 			.oauth2(accessToken)
	// 			.multiPart("./src/test/payloads/thumbnail.jpg","application/json")
	// 			.get(p.getProperty("publisher_url")+"/"+res4.jsonPath().get("list[0]['id']/thumbnail"));

	// 	System.out.println(res6.jsonPath().prettyPrint());

	// }

}
