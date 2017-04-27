package server;

import java.util.LinkedList;

/**
 * Добавляет комнаты в список
 *
 * Created by SurfinBirb on 27.04.2017.
 */
public class RoomThread implements Runnable {
    private static volatile RoomThread instance;

    public static RoomThread getInstance() {
        RoomThread localInstance = instance;
        if (localInstance == null) {
            synchronized (Storage.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new RoomThread();
                }
            }
        }
        return localInstance;
    }

    public void run() {
        Storage storage = Storage.getInstance();
        WannabeSQLbd bd = WannabeSQLbd.getInstance();
        Sender sender = Sender.getInstance();
        while (ServerThread.getInstance().isLive()) {                               //Пока сервер жив
            for (Room room : storage.getRoomCreateRequests()) {                     //Для каждой комнаты из очереди запросов на создание комнаты
                int hashMapSize = bd.getRoomHashMap().size();                       //Узнаем номер последней комнаты
                for (Long id : bd.getRoomHashMap().keySet()) {                      //Для каждого идентификатора комнаты из мапы комнат
                    if (!bd.getRoomHashMap().get(id).getRoomName().equals("")) {    //Если нет совпадений с названием существующей комнаты
                        bd.getRoomHashMap().put(                                    //Закинуть комнату в бд
                                        (long) hashMapSize + 1,
                                        new Room(
                                                room.getCreatorId(),
                                                (long) hashMapSize + 1,
                                                room.getRoomName(),
                                                new LinkedList<>(room.getIdList())
                                        )
                        );
                    } else try {                                                            //Ежели комната с таким названием существует
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
                    } catch (Exception e){storage.getErrorMessages().add(e.getMessage());}
                }
                storage.getRoomCreateRequests().poll();                     //Убрать комнату из очереди
                }
            }
        }
    }
