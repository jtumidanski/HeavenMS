package net.server.channel.handlers;

import client.MapleClient;
import client.autoban.AutoBanFactory;
import config.YamlConfig;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.pet.PetChatPacket;
import net.server.channel.packet.reader.PetChatReader;
import tools.LogHelper;
import tools.LoggerOriginator;
import tools.LoggerUtil;
import tools.MasterBroadcaster;
import tools.packet.pet.PetChat;

public final class PetChatHandler extends AbstractPacketHandler<PetChatPacket> {
   @Override
   public Class<PetChatReader> getReaderClass() {
      return PetChatReader.class;
   }

   @Override
   public void handlePacket(PetChatPacket packet, MapleClient client) {
      byte pet = client.getPlayer().getPetIndex(packet.petId());
      if ((pet < 0 || pet > 3) || (packet.act() < 0 || packet.act() > 9)) {
         return;
      }
      if (packet.text().length() > Byte.MAX_VALUE) {
         AutoBanFactory.PACKET_EDIT.alert(client.getPlayer(), client.getPlayer().getName() + " tried to packet edit with pets.");
         LoggerUtil.printError(LoggerOriginator.EXPLOITS, client.getPlayer().getName() + " tried to send text with length of " + packet.text().length());
         client.disconnect(true, false);
         return;
      }
      MasterBroadcaster.getInstance().sendToAllInMap(client.getPlayer().getMap(), new PetChat(client.getPlayer().getId(), pet, packet.act(), packet.text()), true, client.getPlayer());
      if (YamlConfig.config.server.USE_ENABLE_CHAT_LOG) {
         LogHelper.logChat(client, "Pet", packet.text());
      }
   }
}
