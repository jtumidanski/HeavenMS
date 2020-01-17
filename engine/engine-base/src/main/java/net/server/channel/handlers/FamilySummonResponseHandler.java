package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleFamilyEntitlement;
import client.MapleFamilyEntry;
import config.YamlConfig;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.family.FamilySummonResponsePacket;
import net.server.channel.packet.reader.FamilySummonResponseReader;
import net.server.coordinator.world.MapleInviteCoordinator;
import net.server.coordinator.world.MapleInviteCoordinator.InviteResult;
import net.server.coordinator.world.MapleInviteCoordinator.InviteType;
import net.server.coordinator.world.MapleInviteCoordinator.MapleInviteResult;
import server.maps.MapleMap;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.I18nMessage;
import tools.packet.family.GetFamilyInfo;

public class FamilySummonResponseHandler extends AbstractPacketHandler<FamilySummonResponsePacket> {
   @Override
   public Class<FamilySummonResponseReader> getReaderClass() {
      return FamilySummonResponseReader.class;
   }

   @Override
   public boolean successfulProcess(MapleClient client) {
      return YamlConfig.config.server.USE_FAMILY_SYSTEM;
   }

   @Override
   public void handlePacket(FamilySummonResponsePacket packet, MapleClient client) {
      MapleInviteResult inviteResult = MapleInviteCoordinator.answerInvite(InviteType.FAMILY_SUMMON, client.getPlayer().getId(), client.getPlayer(), packet.accept());
      if (inviteResult.result == InviteResult.NOT_FOUND) {
         return;
      }
      MapleCharacter inviter = inviteResult.from;
      MapleFamilyEntry inviterEntry = inviter.getFamilyEntry();
      if (inviterEntry == null) {
         return;
      }
      MapleMap map = (MapleMap) inviteResult.params[0];
      if (packet.accept() && inviter.getMap() == map) { //cancel if inviter has changed maps
         client.getPlayer().changeMap(map, map.getPortal(0));
      } else {
         inviterEntry.refundEntitlement(MapleFamilyEntitlement.SUMMON_FAMILY);
         inviterEntry.gainReputation(MapleFamilyEntitlement.SUMMON_FAMILY.getRepCost(), false); //refund rep cost if declined
         PacketCreator.announce(inviter, new GetFamilyInfo(inviterEntry));
         MessageBroadcaster.getInstance().sendServerNotice(inviter, ServerNoticeType.PINK_TEXT, I18nMessage.from("FAMILY_SUMMON_DENIED").with(client.getPlayer().getName()));
      }
   }

}
