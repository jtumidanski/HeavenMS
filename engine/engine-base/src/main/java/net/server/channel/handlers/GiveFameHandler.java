package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleCharacter.FameStatus;
import client.MapleClient;
import client.autoban.AutoBanFactory;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.GiveFamePacket;
import net.server.channel.packet.reader.GiveFameReader;
import tools.I18nMessage;
import tools.LogType;
import tools.LoggerOriginator;
import tools.LoggerUtil;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.packet.message.GiveFameErrorResponse;

public final class GiveFameHandler extends AbstractPacketHandler<GiveFamePacket> {
   @Override
   public Class<GiveFameReader> getReaderClass() {
      return GiveFameReader.class;
   }

   @Override
   public void handlePacket(GiveFamePacket packet, MapleClient client) {
      MapleCharacter target = (MapleCharacter) client.getPlayer().getMap().getMapObject(packet.characterId());
      int mode = packet.mode();
      int fameChange = 2 * mode - 1;
      MapleCharacter player = client.getPlayer();
      if (target == null || target.getId() == player.getId() || player.getLevel() < 15) {
         return;
      } else if (fameChange != 1 && fameChange != -1) {
         AutoBanFactory.PACKET_EDIT.alert(client.getPlayer(), client.getPlayer().getName() + " tried to packet edit fame.");
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXPLOITS, client.getPlayer().getName() + " tried to fame hack with fame change " + fameChange);
         client.disconnect(true, false);
         return;
      }

      FameStatus status = player.canGiveFame(target);
      if (status == FameStatus.OK) {
         if (target.gainFame(fameChange, player, mode)) {
            if (!player.isGM()) {
               player.hasGivenFame(target);
            }
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("FAME_GIVE_ERROR_MINIMUM_LEVEL"));
         }
      } else {
         PacketCreator.announce(client, new GiveFameErrorResponse(status == FameStatus.NOT_TODAY ? 3 : 4));
      }
   }
}