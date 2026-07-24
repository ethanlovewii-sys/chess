package server;

import com.google.gson.Gson;
import request.*;
import result.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public LoginResult login(LoginRequest request) throws ResponseException {
        return this.makeRequest("POST", "/session", request, LoginResult.class);
    }

    public RegisterResult register(LoginRequest request) throws ResponseException {
        return this.makeRequest("POST", "/user", request, RegisterResult.class);
    }

    public CreateGameResult createGame(CreateGameRequest request) throws ResponseException {
        return this.makeRequest("POST", "/game", request, CreateGameResult.class);
    }

    public ListGamesResult listGames() throws ResponseException {
        return this.makeRequest("GET", "/game", null, ListGamesResult.class);
    }

    public void joinGame(JoinGameRequest request) throws ResponseException {
        this.makeRequest("PUT", "/game", request, null);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseType) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            //Write body
            if(request != null) {
                http.setRequestProperty("Content-Type", "application/json");
                String reqData = new Gson().toJson(request);
                try (OutputStream reqBody = http.getOutputStream()) {
                    reqBody.write(reqData.getBytes());
                }
            }

            //Check for success
            var status = http.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                throw new ResponseException("failure: " + status, status);
            }

            //read Body
            if (http.getContentLength() < 0) {
                try (InputStream reqBody = http.getInputStream()) {
                    InputStreamReader reader = new InputStreamReader(reqBody);
                    if (responseType != null){
                        return new Gson().fromJson(reader, responseType);
                    }
                }
            }
            throw new ResponseException("failure: " + status, status);
        } catch (Exception ex) {
            throw new ResponseException(ex.getMessage(), 500);
        }
    }

}
