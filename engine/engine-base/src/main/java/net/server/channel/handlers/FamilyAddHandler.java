package net.server.channel.handlers;

import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import config.YamlConfig;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.family.FamilyAddPacket;
import net.server.channel.packet.reader.FamilyAddReader;
import net.server.coordinator.world.MapleInviteCoordinator;
import net.server.coordinator.world.MapleInviteCoordinator.InviteType;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.I18nMessage;
import tools.packet.family.FamilyMessage;
import tools.packet.family.SendFamilyInvite;
import tools.packet.stat.EnableActions;

public final class FamilyAddHandler extends AbstractPacketHandler<FamilyAddPacket> {
   @Override
   public Class<FamilyAddReader> getReaderClass() {
      return FamilyAddReader.class;
   }

   @Override
   public boolean successfulProcess(MapleClient client) {
      return YamlConfig.config.server.USE_FAMILY_SYSTEM;
   }

   @Override
   public void handlePacket(FamilyAddPacket packet, MapleClient client) {
      Optional<MapleCharacter> addChr = client.getChannelServer().getPlayerStorage().getCharacterByName(packet.toAdd());
      MapleCharacter chr = client.getPlayer();
      if (addChr.isEmpty()) {
         PacketCreator.announce(client, new FamilyMessage(65, 0));
      } else if (addChr.get() == chr) { //only possible through packet editing/client editing i think?
         PacketCreator.announce(client, new EnableActions());
      } else if (addChr.get().getMap() != chr.getMap() || (addChr.get().isHidden()) && chr.gmLevel() < addChr.get().gmLevel()) {
         PacketCreator.announce(client, new FamilyMessage(69, 0));
      } else if (addChr.get().getLevel() <= 10) {
         PacketCreator.announce(client, new FamilyMessage(77, 0));
      } else if (Math.abs(addChr.get().getLevel() - chr.getLevel()) > 20) {
         PacketCreator.announce(client, new FamilyMessage(72, 0));
      } else if (addChr.get().getFamily() != null && addChr.get().getFamily() == chr.getFamily()) { //same family
         PacketCreator.announce(client, new EnableActions());
      } else if (MapleInviteCoordinator.hasInvite(InviteType.FAMILY, addChr.get().getId())) {
         PacketCreator.announce(client, new FamilyMessage(73, 0));
      } else if (chr.getFamily() != null && addChr.get().getFamily() != null && addChr.get().getFamily().getTotalGenerations() + chr.getFamily().getTotalGenerations() > YamlConfig.config.server.FAMILY_MAX_GENERATIONS) {
         PacketCreator.announce(client, new FamilyMessage(76, 0));
      } else {
         MapleInviteCoordinator.createInvite(InviteType.FAMILY, chr, addChr.get(), addChr.get().getId());
         PacketCreator.announce(addChr.get(), new SendFamilyInvite(chr.getId(), chr.getName()));
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.NOTICE, I18nMessage.from("FAMILY_INVITE_SUCCESS"));
         PacketCreator.announce(client, new EnableActions());
      }
   }
}
