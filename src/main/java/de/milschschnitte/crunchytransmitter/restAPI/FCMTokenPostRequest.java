package de.milschschnitte.crunchytransmitter.restAPI;

/**
 * PostRequest class for posting to fcm-token by the client
 */
public class FCMTokenPostRequest {
    private String password;
    private String token;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
