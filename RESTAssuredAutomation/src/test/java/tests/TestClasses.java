package tests;

import org.testng.annotations.Test;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import restapi.Authentication;
import restapi.AuthenticationObject;
import restapi.ContentTypes;
import restapi.GrantTypes;
import restapi.Scopes;
import restapi.devportal.DevportalApis;
import restapi.publisher.PublisherApiLifecycle;
import restapi.publisher.PublisherApiProducts;
import restapi.publisher.PublisherApis;

public class TestClasses {
	String accessToken;
	@Test
	public void dataGeneration() {

        AuthenticationObject authenticationObject = new AuthenticationObject();
        authenticationObject.setUsername("admin");
        authenticationObject.setUserpassword("admin");
        authenticationObject.setEndpoint("https://localhost:9443/client-registration/v0.17/register");
        authenticationObject.setTokenUrl("https://localhost:8243/token");
        authenticationObject.setPayloadPath("./src/test/payloads/payload.json");
        authenticationObject.setScopes(Scopes.API_PUBLISH, Scopes.API_CREATE, Scopes.API_VIEW, Scopes.API_IMPORT_EXPORT);
        authenticationObject.setContentType(ContentTypes.APPLICATION_JSON);
        authenticationObject.setGrantType(GrantTypes.PASSSWORD);



        Authentication authentication = new Authentication(authenticationObject);
        accessToken = authentication.getAccessToken();

        PublisherApis api = new PublisherApis(accessToken);
        Response createApiRes = api.createApi(ContentTypes.APPLICATION_JSON, "./src/test/payloads/apicretion_payload.json");
        String apiId = api.searchApis().jsonPath().get("list[0]['id']");
        Response uploadApiThumbnailRes = api.uploadThumbnailImage("./src/test/payloads/thumbnail.jpg", apiId);
        Response changeApiStatusRes = api.changeApiStatus(apiId, "Publish");
        Response createNewApiVersioRes = api.createNewApiVersion(apiId, "2.0.1", false);
        Response getComplexityRelatedDetailsOfApiRes = api.getComplexityRelatedDetailsOfApi(apiId);
        
        System.out.println(createApiRes.statusCode());
        System.out.println(apiId);
        System.out.println(changeApiStatusRes.statusCode());
        System.out.println(createNewApiVersioRes.statusCode());
        System.out.println(uploadApiThumbnailRes.statusCode());
        System.out.println(getComplexityRelatedDetailsOfApiRes.statusCode());
        
        PublisherApiProducts apiProd = new PublisherApiProducts(accessToken);
        Response searchApiProductRes = apiProd.searchApiProduct();
        String apiProductId = searchApiProductRes.jsonPath().get("list[0]['id']");
        Response createApiProductRes = apiProd.createApiProduct(ContentTypes.APPLICATION_JSON, "./src/test/payloads/apiproduct_creation.json");
        Response updateApiProductRes = apiProd.updateApiProduct(apiProductId, ContentTypes.APPLICATION_JSON, "./src/test/payloads/updateApiProductPayload.json");
        Response uploadProductThumbnailRes = apiProd.uploadProductThumbnail("./src/test/payloads/thumbnail.jpg", apiProductId);
        
        System.out.println(createApiProductRes.statusCode());
        System.out.println(updateApiProductRes.statusCode());
        System.out.println(uploadProductThumbnailRes.statusCode());
}
    
}
