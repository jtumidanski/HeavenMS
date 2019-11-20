package tools;

import client.MapleClient;
import tools.packet.PacketInput;

public interface PacketFactory {
   void announce(MapleClient client, PacketInput packetInput);

   byte[] create(PacketInput packetInput);
}
