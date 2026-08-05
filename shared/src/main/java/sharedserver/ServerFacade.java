package sharedserver;

import com.google.gson.Gson;
import exception.ResponseException;
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
    private String authToken;

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public LoginResult login(LoginRequest request) throws ResponseException {
        LoginResult result = this.makeRequest("POST", "/session", request, LoginResult.class);
        authToken = result.authToken();
        return result;
    }

    public RegisterResult register(RegisterRequest request) throws ResponseException {
        RegisterResult result = this.makeRequest("POST", "/user", request, RegisterResult.class);
        authToken = result.authToken();
        return result;
    }

    public void logout() throws ResponseException {
        this.makeRequest("DELETE", "/session", null, null);
        authToken = null;
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

    public void clear() throws ResponseException {
        this.makeRequest("DELETE", "/db", null, null);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseType) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            //Write body
            if (authToken != null) {
                http.setRequestProperty("Authorization", authToken);
            }
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
                String message = "Unknown error";

                try (InputStream errorStream = http.getErrorStream()) {
                    if (errorStream != null) {
                        InputStreamReader reader = new InputStreamReader(errorStream);

                        ErrorResult error = new Gson().fromJson(reader, ErrorResult.class);
                        message = error.getMessage();
                    }
                }

                throw new ResponseException(message, status);
            }

            //read Body
            try (InputStream reqBody = http.getInputStream()) {
                if (responseType != null){
                    InputStreamReader reader = new InputStreamReader(reqBody);
                    return new Gson().fromJson(reader, responseType);
                }
            }

            return null;
        } catch (ResponseException ex) {
            throw ex;
        }
        catch(Exception ex) {
            throw new ResponseException(ex.getMessage(), 500);
        }
    }

}
