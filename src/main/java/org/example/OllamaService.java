package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OllamaService {

    private final Integer TIMEOUT = 2;
    private static final Logger logger = Logger.getLogger("OllamaService");

    private String ollamaChatUrl= "http://localhost:11434/api/generate";
    private String ollamaTagsUrl = "http://localhost:11434/api/tags";
    private String model = "llama3.2:1b";
    private final HttpClient httpClient;
    private final Gson gson;

    public OllamaService(String model, String ollamaUrl) {
        logger.info("Creating OllamaService... ");
        this.httpClient = HttpClient.newHttpClient();
        this.model = model;
        this.ollamaChatUrl = ollamaUrl+"/api/generate";
        this.ollamaTagsUrl = ollamaUrl+"/api/tags";
        this.gson = new Gson();
        logger.info("... OllamaService created!");
    }

    /**
     * Envía un prompt a Ollama y devuelve la respuesta
     */
    public String sendPrompt(String prompt) {
        try {
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("model", model);  // Cambia según tu modelo
            requestBody.addProperty("prompt", escapeJson(prompt));
            requestBody.addProperty("stream", false);

            String jsonRequest = gson.toJson(requestBody);
            logger.log(Level.INFO,"JsonRequest: {0}",jsonRequest.toString());

            // Crear petición HTTP
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ollamaChatUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                    .build();

            // Enviar petición
            logger.info("Making request...");
            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );


            // Verificar status
            if (response.statusCode() != 200) {
                logger.severe("Error in response");
                System.err.println("Error: " + response.statusCode());
                System.err.println("Body: " + response.body());
                return null;
            }

            // Parsear respuesta
            logger.log(Level.INFO,"response.body() {0} ",response.body().toString());

            JsonObject responseJson = gson.fromJson(response.body(), JsonObject.class);
            String answer = responseJson.get("response").getAsString();

            answer = answer.replaceAll("```json\\s*", "")
                    .replaceAll("```\\s*", "")
                    .trim();

            logger.log(Level.INFO,"Ollama answer: {0}",answer);
            return answer;

        } catch (Exception e) {
            System.err.println("Error al comunicarse con Ollama: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Verifica si Ollama está disponible
     */
    public boolean isAvailable() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ollamaTagsUrl))
                    .GET()
                    .timeout(Duration.ofSeconds(5))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            return response.statusCode() == 200;

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Escapa caracteres especiales para JSON
     */
    private String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}