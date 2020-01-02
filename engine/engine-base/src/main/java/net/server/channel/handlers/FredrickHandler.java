package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import client.processor.npc.FredrickProcessor;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.fredrick.BaseFrederickPacket;
import net.server.channel.packet.reader.FrederickReader;

public class FredrickHandler extends AbstractPacketHandler<BaseFrederickPacket> {
   @Override
   public Class<FrederickReader> getReaderClass() {
      return FrederickReader.class;
   }

   @Override
   public void handlePacket(BaseFrederickPacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();

      switch (packet.operation()) {
         case 0x19: //Will never come...
            //c.announce(MaplePacketCreator.getFredrick((byte) 0x24));
            break;
         case 0x1A:
            FredrickProcessor.fredrickRetrieveItems(client);
            break;
         case 0x1C: //Exit
            break;
         default:
      }
   }
}
