package server;

import java.util.LinkedList;

/**
 * Добавляет комнаты в список
 *
 * Created by SurfinBirb on 27.04.2017.
 */
public class RoomRunnable implements Runnable {
    private static volatile RoomRunnable instance;

    public static RoomRunnable getInstance() {
        RoomRunnable localInstance = instance;
        if (localInstance == null) {
            synchronized (RoomRunnable.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new RoomRunnable();
                }
            }
        }
        return localInstance;
    }

    public void run() {
        Storage storage = Storage.getInstance();
        WannabeSQLbd bd = WannabeSQLbd.getInstance();
        try {
            while (ServerRunnable.getInstance().isLive()) {                               //Пока сервер жив
                Room room = storage.getRoomCreateRequests().poll();
                while (room != null) {
                    for (Long id : bd.getRoomTreeMap().keySet()) {                      //Для каждого идентификатора комнаты из мапы комнат
                        if (!bd.getRoomTreeMap().get(id).getRoomName().equals(room.getRoomName())) {    //Если нет совпадений с названием существующей комнаты
                            Long roomid = (long) bd.getRoomTreeMap().size() + 1;
                            bd.getRoomTreeMap().put(                                    //Закинуть комнату в бд
                                    roomid,
                                    new Room(
                                            room.getCreatorId(),
                                            roomid,
                                            room.getRoomName(),
                                            new LinkedList<>(room.getIdList())
                                    )
                            );
                            room = storage.getRoomCreateRequests().poll();
                        } else {                                                            //Ежели комната с таким названием существует
                            storage.getOutputQueue().add(                                      //Послать создателю комнаты сообщение
                                    new Packet(
                                            "servicemessage",
                                            null,
                                            null,
                                            room.getCreatorId(),
                                            new ServiceMessage("Room already exists"),
                                            null
                                    )
                            );
                        }
                    }
                    this.wait(500);
                }
            }
        } catch (Exception e) {
            if (e != null) {
                storage.getErrorMessages().offerLast(e.getMessage());
            }
        }
    }
}
