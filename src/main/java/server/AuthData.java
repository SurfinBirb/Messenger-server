package server;

/**
 * Created by SurfinBirb on 26.04.2017.
 */
public class AuthData {

    public String getLogin() {
        return login;
    }

    public String getHash() {
        return hash;
    }

    private String login;
    private String hash;
    private Long clientId;

    private boolean logged = false;

    public AuthData(String login, String hash, Long clientId, boolean logged) {
        this.login = login;
        this.hash = hash;
        this.clientId = clientId;
        this.logged = logged;
    }

}
