package server;

import java.util.HashMap;

/**
 * Created by SurfinBirb on 26.04.2017.
 *
 * Сейчас 11 вечера, мне влом вспоминать, как работать с бд. Поэтому тут пока полежат
 * логины, пароли, и прочий хлам
 */
public class WannabeSQLbd {
    private static volatile WannabeSQLbd instance;

    private WannabeSQLbd() {
        this.loginToLong = new HashMap<>();
        loginToLong.put("tester", 1L); //login: tester, id: 1
        this.idPasswordHash = new HashMap<>();
        idPasswordHash.put(1L, "d8578edf8458ce06fbc5bb76a58c5ca4"); // password: qwerty
    }

    public static WannabeSQLbd getInstance() {
        WannabeSQLbd localInstance = instance;
        if (localInstance == null) {
            synchronized (Storage.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new WannabeSQLbd();
                }
            }
        }
        return localInstance;
    }

    private volatile HashMap<String, Long> loginToLong;

    private volatile HashMap<Long, String> idPasswordHash;

    public HashMap<Long, String> getIdPasswordHash() {
        return idPasswordHash;
    }

    public HashMap<String, Long> getLoginToId() {
        return loginToLong;
    }
}
