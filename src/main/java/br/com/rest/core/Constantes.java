package br.com.rest.core;

import io.restassured.http.ContentType;

public interface Constantes {

    //incluir todas as constantes do projeto, quando alterar alterar somente dentro dessa interface
    //site da interface web -> https://seubarriga.wcaquino.me/login
    //https://barrigarest.wcaquino.me

        String APP_BASE_URL = "https://barrigarest.wcaquino.me";
        Integer APP_PORT = 443; //http -> 80
        String APP_BASE_PATH = "";

        ContentType App_CONTENT_TYPE = ContentType.JSON;

        Long MAX_TIMEOUT = 5000L; // Maximo de tempo que uma requisição vai responder 5segundo
}
