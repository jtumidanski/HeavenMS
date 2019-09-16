/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation version 3 as published by
the Free Software Foundation. You may not use, modify or distribute
this program under any other version of the GNU Affero General Public
License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import client.autoban.AutobanFactory;
import client.autoban.AutobanManager;
import net.server.AbstractPacketHandler;
import net.server.Server;
import net.server.channel.packet.HealOvertimePacket;
import net.server.channel.packet.reader.HealOvertimeReader;
import server.maps.MapleMap;
import tools.MaplePacketCreator;

public final class HealOvertimeHandler extends AbstractPacketHandler<HealOvertimePacket> {
   @Override
   public Class<HealOvertimeReader> getReaderClass() {
      return HealOvertimeReader.class;
   }

   @Override
   public boolean successfulProcess(MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      return chr.isLoggedinWorld();
   }

   @Override
   public void handlePacket(HealOvertimePacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      AutobanManager abm = chr.getAutobanManager();
      int timestamp = Server.getInstance().getCurrentTimestamp();

      if (packet.healHP() != 0) {
         abm.setTimestamp(8, timestamp, 28);  // thanks Vcoc & Thora for pointing out d/c happening here
         if ((abm.getLastSpam(0) + 1500) > timestamp) {
            AutobanFactory.FAST_HP_HEALING.addPoint(abm, "Fast hp healing");
         }

         MapleMap map = chr.getMap();
         int abHeal = (int) (77 * map.getRecovery() * 1.5); // thanks Ari for noticing players not getting healed in sauna in certain cases
         if (packet.healHP() > abHeal) {
            AutobanFactory.HIGH_HP_HEALING.autoban(chr, "Healing: " + packet.healHP() + "; Max is " + abHeal + ".");
            return;
         }

         chr.addHP(packet.healHP());
         chr.getMap().broadcastMessage(chr, MaplePacketCreator.showHpHealed(chr.getId(), packet.healHP()), false);
         abm.spam(0, timestamp);
      }

      if (packet.healMP() != 0 && packet.healMP() < 1000) {
         abm.setTimestamp(9, timestamp, 28);
         if ((abm.getLastSpam(1) + 1500) > timestamp) {
            AutobanFactory.FAST_MP_HEALING.addPoint(abm, "Fast mp healing");
            return;
         }
         chr.addMP(packet.healMP());
         abm.spam(1, timestamp);
      }
   }
}
