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

        System.out.println("=== Test Simple Ollama ===\n");

        Gson gson = new Gson();
        HttpClient client = HttpClient.newHttpClient();

        System.out.println("Enviando prompt...");
        try {
            // Construir JSON
            JsonObject body = new JsonObject();
            body.addProperty("model", "llama3.2:1b");  // Cambia según tu modelo
            body.addProperty("prompt", "Cuanto son pi+e. Dame solo el numero del resultado final.");
            body.addProperty("stream", false);

            String jsonBody = gson.toJson(body);
            System.out.println("Request: " + jsonBody + "\n");

            // Enviar request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:11434/api/generate"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            System.out.println("Status: " + response.statusCode());
            System.out.println("Response: " + response.body() + "\n");

            if (response.statusCode() == 200) {
                JsonObject responseJson = gson.fromJson(response.body(), JsonObject.class);
                String answer = responseJson.get("response").getAsString();
                System.out.println("✅ Respuesta de Ollama: " + answer);
            } else {
                System.out.println("❌ Error en la petición");
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }

    }


}