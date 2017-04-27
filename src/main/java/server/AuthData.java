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

    public AuthData(String login, String hash) {
        this.login = login;
        this.hash = hash;
    }

}
