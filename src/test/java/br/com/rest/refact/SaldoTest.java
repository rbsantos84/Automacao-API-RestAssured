package br.com.rest.refact;

import br.com.rest.core.BaseTest;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;
import utils.BarrigaUtils;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;

public class SaldoTest extends BaseTest {

    //será executado apenas uma vez para classe inteira

    /**
     *  @BeforeClass
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
     */


    @Test
    public void deveCalcularSaldoConta(){
        Integer CONTA_ID = BarrigaUtils.getIdContaPeloNome("Conta para saldo");
        given()
        .when()
            .get("/saldo")
        .then()
            .statusCode(200)
            .body("find{it.conta_id == "+CONTA_ID+"}.saldo", Matchers.is("534.00"));
    }

    //extraindo a primeira ocorrencia do id

    /**
     * public Integer getIdContaPeloNome(String nome){
     *         return RestAssured.get("/contas?nome="+nome).then().extract().path("id[0]");
     *     }
     *
     */

}
