package br.com.rest.refact.suite;

import br.com.rest.core.BaseTest;
import br.com.rest.refact.AuthTest;
import br.com.rest.refact.ContasTest;
import br.com.rest.refact.MovimentacaoTest;
import br.com.rest.refact.SaldoTest;
import io.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;

//identificando a classe como suite de teste(Conjunto de classes de teste)
//a execução da suite segue a ordem definida dentro do SuiteClasses
@RunWith(org.junit.runners.Suite.class)
@org.junit.runners.Suite.SuiteClasses({
        ContasTest.class,
        MovimentacaoTest.class,
        SaldoTest.class,
        AuthTest.class
})
public class Suite extends BaseTest {

    @BeforeClass
    public static void login(){
        Map<String, String> login = new HashMap<>();
        login.put("email", "renato@teste.com.br");
        login.put("senha", "teste123");
        String TOKEN = given()
                .body(login)
                .when()
                .post("/signin")
                .then()
                .statusCode(200)
                .extract().path("token");

        //toda requisição que for feita já vai está incluido o token
        requestSpecification.header("Authorization", "JWT " + TOKEN);

        //resetando o banco com dados default
        RestAssured.get("/reset").then().statusCode(200);
    }

}
