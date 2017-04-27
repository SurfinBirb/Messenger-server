package server;

import com.thoughtworks.xstream.XStream;

/**
 * Created by SurfinBirb on 22.04.2017.
 */
public class Packer {


    public String pack(Packet b){
        XStream xstream = new XStream();
        xstream.alias("pack", Packet.class);
        xstream.alias("message", Message.class);
        xstream.alias("room", Room.class);
        xstream.alias("servicemessage", ServiceMessage.class);
        return xstream.toXML(b);
    }
}
