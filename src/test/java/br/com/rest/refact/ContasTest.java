package br.com.rest.refact;

import br.com.rest.core.BaseTest;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;

public class ContasTest extends BaseTest {

    //será executado apenas uma vez para classe inteira
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

    @Test
    public void deveIncluirContaComSucesso() {
         given()
             .body("{\"nome\": \"Conta inserida\" }")
          .when()
             .post("/contas")
          .then()
             .statusCode(201);
    }

    @Test
    public void deveAlterarContaComSucesso() {
        Integer CONTA_ID = getIdContaPeloNome("Conta para alterar");
        given()
                .body("{\"nome\": \"Conta alterada\" }")
                .pathParam("id", CONTA_ID)
                .when()
                .put("/contas/{id}")
                .then()
                .log().all()
                .statusCode(200)
                .body("nome", Matchers.is("Conta alterada"));
    }

    @Test
    public void naoDeveInserirContaMesmoNome(){
        given()
                .body("{\"nome\": \"Conta mesmo nome\" }")
                .when()
                .post("/contas")
                .then()
                //   .log().all()
                .statusCode(400)
                .body("error", Matchers.is("Já existe uma conta com esse nome!"));

    }


    //extraindo a primeira ocorrencia do id
    public Integer getIdContaPeloNome(String nome){
        return RestAssured.get("/contas?nome="+nome).then().extract().path("id[0]");
    }

}
