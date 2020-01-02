package net.server.channel.handlers;

import client.MapleClient;
import client.processor.npc.DueyProcessor;
import config.YamlConfig;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.duey.BaseDueyPacket;
import net.server.channel.packet.duey.DueyClaimPackagePacket;
import net.server.channel.packet.duey.DueyReceiveItemPacket;
import net.server.channel.packet.duey.DueyRemovePackagePacket;
import net.server.channel.packet.duey.DueySendItemPacket;
import net.server.channel.packet.reader.DueyReader;
import tools.PacketCreator;
import tools.packet.stat.EnableActions;

public final class DueyHandler extends AbstractPacketHandler<BaseDueyPacket> {
   @Override
   public Class<DueyReader> getReaderClass() {
      return DueyReader.class;
   }

   @Override
   public boolean successfulProcess(MapleClient client) {
      if (!YamlConfig.config.server.USE_DUEY) {
         PacketCreator.announce(client, new EnableActions());
         return false;
      }
      return true;
   }

   @Override
   public void handlePacket(BaseDueyPacket packet, MapleClient client) {
      if (packet instanceof DueyReceiveItemPacket) {
         receiveItem(client);
      } else if (packet instanceof DueySendItemPacket) {
         sendItem(client, (DueySendItemPacket) packet);
      } else if (packet instanceof DueyClaimPackagePacket) {
         claimPackage(client, ((DueyClaimPackagePacket) packet).packageId());
      } else if (packet instanceof DueyRemovePackagePacket) {
         removePackage(client, ((DueyRemovePackagePacket) packet).packageId());
      }
   }

   private void sendItem(MapleClient client, DueySendItemPacket packet) {
      DueyProcessor.dueySendItem(client, packet.inventoryId(),
            packet.itemPosition(), packet.amount(),
            packet.mesos(), packet.message(),
            packet.recipient(), packet.quick());
   }

   private void claimPackage(MapleClient c, int packageId) {
      DueyProcessor.dueyClaimPackage(c, packageId);
   }

   private void removePackage(MapleClient c, int packageId) {
      DueyProcessor.dueyRemovePackage(c, packageId, true);
   }

   private void receiveItem(MapleClient c) {
      DueyProcessor.dueySendTalk(c, false);
   }
}
