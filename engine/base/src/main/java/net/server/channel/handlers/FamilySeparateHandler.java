/*
    This file is part of the HeavenMS MapleStory Server
    Copyleft (L) 2016 - 2018 RonanLana

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

import java.util.Collection;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleFamily;
import client.MapleFamilyEntry;
import constants.ServerConstants;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.family.FamilySeparatePacket;
import net.server.channel.packet.reader.FamilySeparateReader;
import tools.MaplePacketCreator;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class FamilySeparateHandler extends AbstractPacketHandler<FamilySeparatePacket> {
   @Override
   public Class<FamilySeparateReader> getReaderClass() {
      return FamilySeparateReader.class;
   }

   @Override
   public boolean successfulProcess(MapleClient client) {
      if (!ServerConstants.USE_FAMILY_SYSTEM) {
         return false;
      }
      MapleFamily oldFamily = client.getPlayer().getFamily();
      return oldFamily != null;
   }

   @Override
   public void handlePacket(FamilySeparatePacket packet, MapleClient client) {
      MapleFamilyEntry forkOn;
      boolean isSenior;
      if (packet.available()) { //packet 0x95 doesn't send id, since there is only one senior
         forkOn = client.getPlayer().getFamily().getEntryByID(packet.characterId());
         if (!client.getPlayer().getFamilyEntry().isJunior(forkOn)) {
            return; //packet editing?
         }
         isSenior = true;
      } else {
         forkOn = client.getPlayer().getFamilyEntry();
         isSenior = false;
      }
      if (forkOn == null) {
         return;
      }

      MapleFamilyEntry senior = forkOn.getSenior();
      if (senior == null) {
         return;
      }
      int levelDiff = Math.abs(client.getPlayer().getLevel() - senior.getLevel());
      int cost = 2500 * levelDiff;
      cost += levelDiff * levelDiff;
      if (client.getPlayer().getMeso() < cost) {
         client.announce(MaplePacketCreator.sendFamilyMessage(isSenior ? 81 : 80, cost));
         return;
      }
      client.getPlayer().gainMeso(-cost);
      int repCost = separateRepCost(forkOn);
      senior.gainReputation(-repCost, false);
      if (senior.getSenior() != null) {
         senior.getSenior().gainReputation(-(repCost / 2), false);
      }

      Collection<MapleCharacter> recipients = forkOn.getSeniors(true);
      MessageBroadcaster.getInstance().sendServerNotice(recipients, ServerNoticeType.PINK_TEXT, forkOn.getName() + " has left the family.");
      forkOn.fork();
      client.announce(MaplePacketCreator.getFamilyInfo(forkOn)); //pedigree info will be requested by the client if the window is open
      forkOn.updateSeniorFamilyInfo(true);
      client.announce(MaplePacketCreator.sendFamilyMessage(1, 0));
   }


   private int separateRepCost(MapleFamilyEntry junior) {
      int level = junior.getLevel();
      int ret = level / 20;
      ret += 10;
      ret *= level;
      ret *= 2;
      return ret;
   }
}
