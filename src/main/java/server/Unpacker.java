package server;

import com.thoughtworks.xstream.XStream;


/**
 * Created by SurfinBirb on 21.04.2017.
 */
public class Unpacker {

    public String type;

    /**
     *
     * @param xmlString - String wiyh XML structure
     * @return Packet
     */
    public Packet unpack(String xmlString){
        XStream xstream = new XStream();
        xstream.alias("pack", Packet.class);
        xstream.alias("message", Message.class);
        xstream.alias("room", Room.class);
        xstream.alias("servicemessage", ServiceMessage.class);
        xstream.alias("auth", AuthData.class);
        Packet temPacket = (Packet) xstream.fromXML(xmlString);
        this.type = temPacket.getType();
        return temPacket;
    }


}


