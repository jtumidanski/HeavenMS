package net.server.channel.handlers;

import client.MapleClient;
import client.MapleJob;
import net.server.AbstractPacketHandler;
import net.server.packet.NoOpPacket;
import net.server.packet.reader.NoOpReader;
import scripting.npc.NPCScriptManager;

public class ClickGuideHandler extends AbstractPacketHandler<NoOpPacket> {
   @Override
   public Class<NoOpReader> getReaderClass() {
      return NoOpReader.class;
   }

   @Override
   public void handlePacket(NoOpPacket packet, MapleClient client) {
      if (client.getPlayer().getJob().equals(MapleJob.NOBLESSE)) {
         NPCScriptManager.getInstance().start(client, 1101008, null);
      } else {
         NPCScriptManager.getInstance().start(client, 1202000, null);
      }
   }

}
