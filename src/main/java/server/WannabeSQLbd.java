package server;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by SurfinBirb on 26.04.2017.
 *
 * Сейчас 11 вечера, мне влом вспоминать, как работать с бд. Поэтому тут пока полежат
 * логины, пароли, и прочий хлам
 */
public class WannabeSQLbd {
    private static volatile WannabeSQLbd instance;

    private volatile TreeMap<String, Long> loginToLong;
    private volatile TreeMap<Long, String> idPasswordHash;
    private volatile TreeMap<Long, Room> roomTreeMap;
    private volatile TreeMap<Long, List<Long>> avialableRoomsForClient;

    private WannabeSQLbd() {
        this.loginToLong = new TreeMap<>();
        loginToLong.put("tester", 1L); //login: tester, id: 1
        loginToLong.put("tester2", 2L);//login: tester2, id: 2
        this.idPasswordHash = new TreeMap<>();
        idPasswordHash.put(1L, "d8578edf8458ce06fbc5bb76a58c5ca4"); // password: qwerty
        idPasswordHash.put(2L, "d8578edf8458ce06fbc5bb76a58c5ca4");
        List<Long> idList = new LinkedList<>();
        idList.add(1L);
        idList.add(2L);
        this.roomTreeMap = new TreeMap<>();
        roomTreeMap.put(1L, new Room(1L,1L, "TestRoom", idList, new LinkedList<>()));
        this.avialableRoomsForClient = new TreeMap<>();
        List<Long> longList = new LinkedList<>();
        longList.add(1L);
        longList.add(2L);
        avialableRoomsForClient.put(1L,longList);
        avialableRoomsForClient.put(2L,longList);
    }

    public static WannabeSQLbd getInstance() {
        WannabeSQLbd localInstance = instance;
        if (localInstance == null) {
            synchronized (WannabeSQLbd.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new WannabeSQLbd();
                }
            }
        }
        return localInstance;
    }

    public TreeMap<Long, Room> getRoomTreeMap() {
        return roomTreeMap;
    }

    public String getIdPasswordHash(Long clientId) {
        return idPasswordHash.get(clientId);
    }

    public Long getClientId(String login) {
        return loginToLong.get(login);
    }

    public List<Long> getRoomIds(Long clientId) {
        return avialableRoomsForClient.get(clientId);
    }

    public Room getRoomById(Long clientId){
        return roomTreeMap.get(clientId);
    }

}
