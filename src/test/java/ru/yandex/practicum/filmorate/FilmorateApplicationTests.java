package ru.yandex.practicum.filmorate;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = "server.port=8080"
)
class FilmorateApplicationTests {

    private static final String BASE = "http://localhost:8080";
    private static final Gson GSON = new Gson();
    private final HttpClient client = HttpClient.newHttpClient();

    @Test
    void postFilms_whenValid_returns201() throws Exception {

        JsonObject json = new JsonObject();
        json.addProperty("name", "John Wick");
        json.addProperty("description", "Хороший фильм");
        json.addProperty("releaseDate", "2010-01-01");
        json.addProperty("duration", 120);
        String body = json.toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject obj = GSON.fromJson(response.body(), JsonObject.class);

        assertEquals(201, response.statusCode());
        assertTrue(obj.has("id"));
        assertTrue(obj.get("id").getAsInt() > 0);
        assertEquals("John Wick", obj.get("name").getAsString());
        assertEquals("Хороший фильм", obj.get("description").getAsString());
        assertEquals("2010-01-01", obj.get("releaseDate").getAsString());
        assertEquals(120, obj.get("duration").getAsInt());

    }

    @Test
    void postFilms_whenNameBlank_returns400() throws Exception {

        JsonObject json = new JsonObject();
        json.addProperty("name", "");
        json.addProperty("description", "Хороший фильм");
        json.addProperty("releaseDate", "2000-01-01");
        json.addProperty("duration", 120);
        String body = json.toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject obj = GSON.fromJson(response.body(), JsonObject.class);

        assertEquals(400, response.statusCode());
        assertEquals("Название не может быть пустым.",obj.get("name").getAsString());
    }

    @Test
    void postFilms_whenDescriptionLengthMore200_returns400() throws Exception {
        String longDescription = "a".repeat(201);

        JsonObject json = new JsonObject();
        json.addProperty("name", "Some Name");
        json.addProperty("description", longDescription);
        json.addProperty("releaseDate", "2000-01-01");
        json.addProperty("duration", 120);
        String body = json.toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject obj = GSON.fromJson(response.body(), JsonObject.class);

        assertEquals(400, response.statusCode());
        assertEquals("Максимальная длина описания — 200 символов.",obj.get("description").getAsString());
    }

    @Test
    void postFilms_whenDescriptionLength200_returns201() throws Exception {
        String longDescription = "a".repeat(200);

        JsonObject json = new JsonObject();
        json.addProperty("name", "Some Name");
        json.addProperty("description", longDescription);
        json.addProperty("releaseDate", "2000-01-01");
        json.addProperty("duration", 120);
        String body = json.toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject obj = GSON.fromJson(response.body(), JsonObject.class);

        assertEquals(201, response.statusCode());
        assertTrue(obj.has("id"));
        assertTrue(obj.get("id").getAsInt() > 0);
        assertEquals("Some Name", obj.get("name").getAsString());
        assertEquals("2000-01-01", obj.get("releaseDate").getAsString());
        assertEquals(120, obj.get("duration").getAsInt());
    }

    @Test
    void postFilms_whenEmptyDescription_returns201() throws Exception {

        JsonObject json = new JsonObject();
        json.addProperty("name", "Some Name");
        json.addProperty("description", "");
        json.addProperty("releaseDate", "2000-01-01");
        json.addProperty("duration", 120);
        String body = json.toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject obj = GSON.fromJson(response.body(), JsonObject.class);

        assertEquals(201, response.statusCode());
        assertTrue(obj.has("id"));
        assertTrue(obj.get("id").getAsInt() > 0);
        assertEquals("Some Name", obj.get("name").getAsString());
        assertEquals("", obj.get("description").getAsString());
        assertEquals("2000-01-01", obj.get("releaseDate").getAsString());
        assertEquals(120, obj.get("duration").getAsInt());
    }

    @Test
    void postFilms_whenReleaseDateBefore28_12_1895_returns400() throws Exception {

        JsonObject json = new JsonObject();
        json.addProperty("name", "Some Name");
        json.addProperty("description", "Some description");
        json.addProperty("releaseDate", "1895-12-27");
        json.addProperty("duration", 120);
        String body = json.toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject obj = GSON.fromJson(response.body(), JsonObject.class);

        assertEquals(400, response.statusCode());
        assertEquals("Дата не может быть раньше 28.12.1895 г.",obj.get("releaseDate").getAsString());
    }

    @Test
    void postFilms_whenEmptyReleaseDate_returns201() throws Exception {

        JsonObject json = new JsonObject();
        json.addProperty("name", "Some Name");
        json.addProperty("description", "Some description");
        json.addProperty("releaseDate", " ");
        json.addProperty("duration", 120);
        String body = json.toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject obj = GSON.fromJson(response.body(), JsonObject.class);

        assertEquals(201, response.statusCode());
        assertTrue(obj.has("id"));
        assertTrue(obj.get("id").getAsInt() > 0);
        assertEquals("Some Name", obj.get("name").getAsString());
        assertEquals("Some description", obj.get("description").getAsString());
        assertTrue(obj.get("releaseDate").isJsonNull());
        assertEquals(120, obj.get("duration").getAsInt());
    }

    @Test
    void postFilms_whenDurationBelow1_returns400() throws Exception {

        JsonObject json = new JsonObject();
        json.addProperty("name", "Some Name");
        json.addProperty("description", "Some description");
        json.addProperty("releaseDate", "2000-10-01");
        json.addProperty("duration", 0);
        String body = json.toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject obj = GSON.fromJson(response.body(), JsonObject.class);

        assertEquals(400, response.statusCode());
        assertEquals("Продолжительность фильма должна быть больше 0",obj.get("duration").getAsString());
    }

    @Test
    void postFilms_whenEmptyDuration_returns400() throws Exception {

        JsonObject json = new JsonObject();
        json.addProperty("name", "Some Name");
        json.addProperty("description", "Some description");
        json.addProperty("releaseDate", "2000-10-01");
        json.addProperty("duration", 0);
        String body = json.toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject obj = GSON.fromJson(response.body(), JsonObject.class);

        assertEquals(400, response.statusCode());
        assertEquals("Продолжительность фильма должна быть больше 0",obj.get("duration").getAsString());
    }

    @Test
    void postUsers_whenValid_returns201() throws Exception {

        JsonObject json = new JsonObject();
        json.addProperty("email", "mail@mail.ru");
        json.addProperty("login", "dolore");
        json.addProperty("name", "Nick Name");
        json.addProperty("birthday", "1946-08-20");
        String body = json.toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject obj = GSON.fromJson(response.body(), JsonObject.class);

        assertEquals(201, response.statusCode());
        assertTrue(obj.has("id"));
        assertTrue(obj.get("id").getAsInt() > 0);
        assertEquals("mail@mail.ru", obj.get("email").getAsString());
        assertEquals("dolore", obj.get("login").getAsString());
        assertEquals("Nick Name", obj.get("name").getAsString());
        assertEquals("1946-08-20", obj.get("birthday").getAsString());
    }

    @Test
    void postUsers_whenEmptyEmail_returns400() throws Exception {

        JsonObject json = new JsonObject();
        json.addProperty("email", "");
        json.addProperty("login", "dolore");
        json.addProperty("name", "Nick Name");
        json.addProperty("birthday", "1946-08-20");
        String body = json.toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject obj = GSON.fromJson(response.body(), JsonObject.class);

        assertEquals(400, response.statusCode());
        assertEquals("Email не может быть пустым",obj.get("email").getAsString());
    }

    @Test
    void postUsers_whenIncorrectEmail_returns400() throws Exception {

        JsonObject json = new JsonObject();
        json.addProperty("email", "qweqwe@");
        json.addProperty("login", "dolore");
        json.addProperty("name", "Nick Name");
        json.addProperty("birthday", "1946-08-20");
        String body = json.toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject obj = GSON.fromJson(response.body(), JsonObject.class);

        assertEquals(400, response.statusCode());
        assertEquals("Некорректный формат email",obj.get("email").getAsString());
    }

    @Test
    void postUsers_whenEmptyLogin_returns400() throws Exception {

        JsonObject json = new JsonObject();
        json.addProperty("email", "mail@mail.ru");
        json.addProperty("login", "");
        json.addProperty("name", "Nick Name");
        json.addProperty("birthday", "1946-08-20");
        String body = json.toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject obj = GSON.fromJson(response.body(), JsonObject.class);

        assertEquals(400, response.statusCode());
        assertEquals("логин не может быть пустым и содержать пробелы",obj.get("login").getAsString());
    }

    @Test
    void postUsers_whenLoginHasSpaces_returns400() throws Exception {

        JsonObject json = new JsonObject();
        json.addProperty("email", "mail@mail.ru");
        json.addProperty("login", "Log in");
        json.addProperty("name", "Nick Name");
        json.addProperty("birthday", "1946-08-20");
        String body = json.toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject obj = GSON.fromJson(response.body(), JsonObject.class);

        assertEquals(400, response.statusCode());
        assertEquals("логин не может быть пустым и содержать пробелы",obj.get("login").getAsString());
    }

    @Test
    void postUsers_whenEmptyName_returns201() throws Exception {

        JsonObject json = new JsonObject();
        json.addProperty("email", "mail@mail.ru");
        json.addProperty("login", "Dolore");
        json.addProperty("name", "");
        json.addProperty("birthday", "1946-08-20");
        String body = json.toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject obj = GSON.fromJson(response.body(), JsonObject.class);

        assertEquals(201, response.statusCode());
        assertTrue(obj.has("id"));
        assertTrue(obj.get("id").getAsInt() > 0);
        assertEquals("mail@mail.ru", obj.get("email").getAsString());
        assertEquals("Dolore", obj.get("login").getAsString());
        assertEquals("Dolore", obj.get("name").getAsString());
        assertEquals("1946-08-20", obj.get("birthday").getAsString());
    }

    @Test
    void postUsers_whenBirthDayAfterToDay_returns400() throws Exception {

        JsonObject json = new JsonObject();
        json.addProperty("email", "mail@mail.ru");
        json.addProperty("login", "Dolore");
        json.addProperty("name", "qweqwe");
        json.addProperty("birthday", "2045-08-20");
        String body = json.toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject obj = GSON.fromJson(response.body(), JsonObject.class);

        assertEquals(400, response.statusCode());
        assertEquals("Дата рождения не может быть в будущем",obj.get("birthday").getAsString());
    }

}