package br.com.rest.refact;

import br.com.rest.core.BaseTest;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;

public class AuthTest extends BaseTest {

    //será executado apenas uma vez para classe inteira

    /**
     * @BeforeClass
     *     public static void login(){
     *         Map<String, String> login = new HashMap<>();
     *         login.put("email", "renato@teste.com.br");
     *         login.put("senha", "teste123");
     *         String TOKEN = given()
     *                 .body(login)
     *                 .when()
     *                 .post("/signin")
     *                 .then()
     *                 .statusCode(200)
     *                 .extract().path("token");
     *
     *         //toda requisição que for feita já vai está incluido o token
     *         requestSpecification.header("Authorization", "JWT " + TOKEN);
     *
     *         //resetando o banco com dados default
     *         RestAssured.get("/reset").then().statusCode(200);
     *     }
     *
     */


    @Test
    public void naoDeveAcessarAPISemToken() {
        //remover token da especificação estatica que estamos mandando
        FilterableRequestSpecification req = (FilterableRequestSpecification) requestSpecification;
        req.removeHeader("Authorization");

        given()
        .when()
            .get("/contas")
        .then()
         //   .log().all()
            .statusCode(401);
    }

}
