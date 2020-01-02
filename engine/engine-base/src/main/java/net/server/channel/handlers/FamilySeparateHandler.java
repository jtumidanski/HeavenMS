package net.server.channel.handlers;

import java.util.Collection;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleFamily;
import client.MapleFamilyEntry;
import config.YamlConfig;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.family.FamilySeparatePacket;
import net.server.channel.packet.reader.FamilySeparateReader;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.packet.family.FamilyMessage;
import tools.packet.family.GetFamilyInfo;

public class FamilySeparateHandler extends AbstractPacketHandler<FamilySeparatePacket> {
   @Override
   public Class<FamilySeparateReader> getReaderClass() {
      return FamilySeparateReader.class;
   }

   @Override
   public boolean successfulProcess(MapleClient client) {
      if (!YamlConfig.config.server.USE_FAMILY_SYSTEM) {
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
         PacketCreator.announce(client, new FamilyMessage(isSenior ? 81 : 80, cost));
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
      PacketCreator.announce(client, new GetFamilyInfo(forkOn)); //pedigree info will be requested by the client if the window is open
      forkOn.updateSeniorFamilyInfo(true);
      PacketCreator.announce(client, new FamilyMessage(1, 0));
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
