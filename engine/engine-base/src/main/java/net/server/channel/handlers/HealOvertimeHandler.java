package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import client.autoban.AutoBanFactory;
import client.autoban.AutoBanManager;
import net.server.AbstractPacketHandler;
import net.server.Server;
import net.server.channel.packet.HealOvertimePacket;
import net.server.channel.packet.reader.HealOvertimeReader;
import server.maps.MapleMap;
import tools.MasterBroadcaster;
import tools.packet.foreigneffect.ShowRecovery;

public final class HealOvertimeHandler extends AbstractPacketHandler<HealOvertimePacket> {
   @Override
   public Class<HealOvertimeReader> getReaderClass() {
      return HealOvertimeReader.class;
   }

   @Override
   public boolean successfulProcess(MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      return chr.isLoggedInWorld();
   }

   @Override
   public void handlePacket(HealOvertimePacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      AutoBanManager abm = chr.getAutoBanManager();
      int timestamp = Server.getInstance().getCurrentTimestamp();

      if (packet.healHP() != 0) {
         abm.setTimestamp(8, timestamp, 28);
         if ((abm.getLastSpam(0) + 1500) > timestamp) {
            AutoBanFactory.FAST_HP_HEALING.addPoint(abm, "Fast hp healing");
         }

         MapleMap map = chr.getMap();
         int abHeal = (int) (77 * map.getRecovery() * 1.5);
         if (packet.healHP() > abHeal) {
            AutoBanFactory.HIGH_HP_HEALING.autoBan(chr, "Healing: " + packet.healHP() + "; Max is " + abHeal + ".");
            return;
         }

         chr.addHP(packet.healHP());
         MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), new ShowRecovery(chr.getId(), (byte) packet.healHP()), false, chr);
         abm.spam(0, timestamp);
      }

      if (packet.healMP() != 0 && packet.healMP() < 1000) {
         abm.setTimestamp(9, timestamp, 28);
         if ((abm.getLastSpam(1) + 1500) > timestamp) {
            AutoBanFactory.FAST_MP_HEALING.addPoint(abm, "Fast mp healing");
            return;
         }
         chr.addMP(packet.healMP());
         abm.spam(1, timestamp);
      }
   }
}
