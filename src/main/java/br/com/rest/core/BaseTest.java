package br.com.rest.core;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;



public class BaseTest implements Constantes  {

/**
 * Antes de todos os testes
 */
@BeforeClass
public static void setup(){
    //System.out.println("passou aqui");
    RestAssured.baseURI = APP_BASE_URL;
    RestAssured.port = APP_PORT;
    RestAssured.basePath = APP_BASE_PATH;

    RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
    requestSpecBuilder.setContentType(App_CONTENT_TYPE);
    RestAssured.requestSpecification = requestSpecBuilder.build();

    ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
    responseSpecBuilder.expectResponseTime(Matchers.lessThan(MAX_TIMEOUT));
    RestAssured.responseSpecification = responseSpecBuilder.build();

    //habilitar log da requisição e da resposta apenas se tiver problema no teste
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

}
}
