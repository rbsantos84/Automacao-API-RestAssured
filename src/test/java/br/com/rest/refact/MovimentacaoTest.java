package br.com.rest.refact;

import br.com.rest.Movimentacao;
import br.com.rest.core.BaseTest;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;
import utils.BarrigaUtils;
import utils.DataUtils;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;

public class MovimentacaoTest extends BaseTest {

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
     */


    @Test
    public void deveInserirMovimentacaoSucesso() {
        Movimentacao mov = getMovimentacaoValida();

          given()
             .body(mov)
         .when()
             .post("/transacoes")
         .then()
             .statusCode(201);
    }

    @Test
    public void deveValidarCamposObrigatoriosMovimentacao() {
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
    public void naoDeveInserirMovimentacaoComDataFutura(){
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
    public void naoDeveRemoverContaComMovimentacao(){
        Integer CONTA_ID = BarrigaUtils.getIdContaPeloNome("Conta com movimentacao");

        given()
           .pathParam("id", CONTA_ID)
        .when()
           .delete("/contas/{id}")
        .then()
           .statusCode(500)
           .body("constraint", Matchers.is("transacoes_conta_id_foreign"));
    }

    @Test
    public void deveRemoverMovimentacao(){
        Integer MOV_ID = BarrigaUtils.getIdMovPelaDescricao("Movimentacao para exclusao");
        given()
            .pathParam("id", MOV_ID)
        .when()
            .delete("/transacoes/{id}")
        .then()
            .statusCode(204);
    }

    //extraindo a primeira ocorrencia do id
    /**
     * public Integer getIdContaPeloNome(String nome){
     *         return RestAssured.get("/contas?nome="+nome).then().extract().path("id[0]");
     *     }
     *
     */


    /**
     *  public Integer getIdMovPelaDescricao(String desc){
     *         return RestAssured.get("/transacoes?descricao="+desc).then().extract().path("id[0]");
     *     }
     *
     */


    private Movimentacao getMovimentacaoValida(){

        Movimentacao mov = new Movimentacao();
        //    mov.setUsuario_id();
        mov.setConta_id(BarrigaUtils.getIdContaPeloNome("Conta para movimentacoes"));
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
