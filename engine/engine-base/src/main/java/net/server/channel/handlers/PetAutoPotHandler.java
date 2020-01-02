package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import client.processor.action.PetAutoPotProcessor;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.pet.PetAutoPotPacket;
import net.server.channel.packet.reader.PetAutoPotReader;
import server.MapleItemInformationProvider;
import server.MapleStatEffect;
import tools.PacketCreator;
import tools.packet.stat.EnableActions;

public final class PetAutoPotHandler extends AbstractPacketHandler<PetAutoPotPacket> {
   @Override
   public Class<PetAutoPotReader> getReaderClass() {
      return PetAutoPotReader.class;
   }

   @Override
   public boolean successfulProcess(MapleClient client) {
      if (!client.getPlayer().isAlive()) {
         PacketCreator.announce(client, new EnableActions());
         return false;
      }
      return true;
   }

   @Override
   public void handlePacket(PetAutoPotPacket packet, MapleClient client) {
      MapleCharacter character = client.getPlayer();
      MapleStatEffect stat = MapleItemInformationProvider.getInstance().getItemEffect(packet.itemId());
      if (stat.getHp() > 0 || stat.getHpRate() > 0.0) {
         float estimatedHp = ((float) character.getHp()) / character.getMaxHp();
         character.setAutoPotHpAlert(estimatedHp + 0.05f);
      }

      if (stat.getMp() > 0 || stat.getMpRate() > 0.0) {
         float estimatedMp = ((float) character.getMp()) / character.getMaxMp();
         character.setAutoPotMpAlert(estimatedMp + 0.05f);
      }

      PetAutoPotProcessor.getInstance().runAutoPotAction(client, packet.slot(), packet.itemId());
   }
}
