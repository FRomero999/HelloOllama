package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        var ollama = new OllamaService("llama3.2:1b","http://localhost:11434");
        ollama.sendPrompt("Hola");
        ollama.sendPrompt("Responde únicamente con JSON válido, sin texto adicional, sin explicaciones, sin markdown. Array con los nombres de las provincias de Andalucia:");

    }


}