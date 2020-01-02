package net;

import client.MapleClient;
import tools.data.input.SeekableLittleEndianAccessor;

public interface MaplePacketHandler {
   void handlePacket(SeekableLittleEndianAccessor accessor, MapleClient c);

   boolean validateState(MapleClient c);
}
