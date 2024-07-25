package br.com.rest;

import br.com.rest.core.BaseTest;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import utils.DataUtils;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;

@FixMethodOrder(MethodSorters.NAME_ASCENDING) //executa os metodos de teste em ordem alfabética
public class BarrigaTest extends BaseTest {
    //private String TOKEN; //definido de forma global
    private static String CONTA_NAME = "Conta " + System.nanoTime();// garantir que a cada execução o numero gerado é diferente
    private static Integer CONTA_ID;
    private static Integer MOV_ID;

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

        requestSpecification.header("Authorization", "JWT " + TOKEN);
    }

    /**
     *  @Test
     *     public void naoDeveAcessarAPISemToken() {
     *         given()
     *                 .when()
     *                 .get("/contas")
     *                 .then()
     *                 .log().all()
     *                 .statusCode(401);
     *     }
     *
     */


    /**
     * faz login primeiro para extrair o token da requisição
     */
    @Test
    public void t01_deveIncluirContaComSucesso() {
    CONTA_ID = given()
     //         .header("Authorization", "JWT " + TOKEN)
     //            .body("{\"nome\": \"conta qualquer\"}")
                   .body("{\"nome\": \""+CONTA_NAME+"\" }")
                .when()
                   .post("/contas")
                .then()
                   .statusCode(201)
                   .extract().path("id");
    }

   // {"nome": "conta qualquer"}

    @Test
    public void t02_deveAlterarContaComSucesso() {
        given()
   //       .header("Authorization", "JWT " + TOKEN)
        //  .body("{\"nome\": \"conta alterada\"}")
            .body("{\"nome\": \""+CONTA_NAME+" alterada\" }")
            .pathParam("id", CONTA_ID)
         .when()
      //    .put("/contas/2211788")
            .put("/contas/{id}")
         .then()
            .log().all()
            .statusCode(200)
        .body("nome", Matchers.is(CONTA_NAME+" alterada"));
    }

    @Test
    public void t03_naoDeveInserirContaMesmoNome() {
        given()
    //   .header("Authorization", "JWT " + TOKEN)
       //   .body("{\"nome\": \"conta alterada\"}")
            .body("{\"nome\": \""+CONTA_NAME+" alterada\" }")
         .when()
            .post("/contas")
         .then()
            .statusCode(400)
            .body("error", Matchers.is("Já existe uma conta com esse nome!"));
    }

    @Test
    public void t04_deveInserirMovimentacaoSucesso() {
        Movimentacao mov = getMovimentacaoValida();

       MOV_ID =  given()
        //        .header("Authorization", "JWT " + TOKEN)
                      .body(mov)
                  .when()
                      .post("/transacoes")
                  .then()
                      .statusCode(201)
                      .extract().path("id");
    }

    @Test
    public void t05_DeveValidarCamposObrigatoriosMovimentacao() {
        given()
       //   .header("Authorization", "JWT " + TOKEN)
            .body("{}")
         .when()
            .post("/transacoes")
         .then()
            .statusCode(400)
            .body("$", Matchers.hasSize(8))
            .body("msg", Matchers.hasItems(
                        "Data da Movimentação é obrigatório",
                        "Data do pagamento é obrigatório",
                        "Descrição é obrigatório",
                        "Interessado é obrigatório",
                        "Valor é obrigatório",
                        "Valor deve ser um número",
                        "Conta é obrigatório",
                        "Situação é obrigatório"
                ));
    }

    @Test
    public void t06_naoDeveInserirMovimentacaoComDataFutura(){
        Movimentacao mov = getMovimentacaoValida();
        mov.setData_transacao(DataUtils.getDataComDiferencaDias(2)); // retorna a data depois de amanha formatada em string

        given()
        //      .header("Authorization", "JWT " + TOKEN)
                .body(mov)
                .when()
                .post("/transacoes")
                .then()
                .statusCode(400)
                .body("$", Matchers.hasSize(1))
                .body("msg", Matchers.hasItem("Data da Movimentação deve ser menor ou igual à data atual"));


    }

    @Test
    public void t07_naoDeveRemoverContaComMovimentacao(){
      given()
      //    .header("Authorization", "JWT " + TOKEN)
            .pathParam("id", CONTA_ID)
        .when()
   //       .delete("/contas/2211788")
            .delete("/contas/{id}")
        .then()
            .statusCode(500)
            .body("constraint", Matchers.is("transacoes_conta_id_foreign"));
 }

    @Test
    public void t08_deveCalcularSaldoConta(){
        given()
     //     .header("Authorization", "JWT " + TOKEN)
         .when()
            .get("/saldo")
         .then()
            .statusCode(200)
   //       .body("find{it.conta_id == 2211788}.saldo", Matchers.is("200.00"));
            .body("find{it.conta_id == "+CONTA_ID+"}.saldo", Matchers.is("100.00"));
    }



    @Test
    public void t09_deveRemoverMovimentacao(){
        given()
    //      .header("Authorization", "JWT " + TOKEN)
            .pathParam("id", MOV_ID)
         .when()
    //      .delete("/transacoes/2075237")
            .delete("/transacoes/{id}")
         .then()
            .statusCode(204);
    }

    @Test
    public void t10_naoDeveAcessarAPISemToken() {
        //remover token da especificação estatica que estamos mandando
        FilterableRequestSpecification req = (FilterableRequestSpecification) requestSpecification;
        req.removeHeader("Authorization");

        given()
                .when()
                .get("/contas")
                .then()
                .log().all()
                .statusCode(401);
    }


    private Movimentacao getMovimentacaoValida(){

        Movimentacao mov = new Movimentacao();
       // mov.setConta_id(CONTA_ID);
       //   mov.setConta_id(2211788);
        //    mov.setUsuario_id();
        mov.setConta_id(CONTA_ID);
        mov.setDescricao("Descricao da movimentacao");
        mov.setEnvolvido("Envolvido na mov");
        mov.setTipo("REC");
        mov.setData_transacao(DataUtils.getDataComDiferencaDias(-1)); //retorna a data do dia anterior
        mov.setData_pagamento(DataUtils.getDataComDiferencaDias(5));
        mov.setValor(100f);
        mov.setStatus(true);
        return mov;
    }


}