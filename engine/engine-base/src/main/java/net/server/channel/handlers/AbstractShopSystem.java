package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.MaplePacket;
import net.server.Server;
import server.maps.MapleMiniDungeonInfo;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.packet.stat.EnableActions;

public abstract class AbstractShopSystem<T extends MaplePacket> extends AbstractPacketHandler<T> {

   /**
    * Determines if a user can enter the specified shop.
    *
    * @param client the client representing the user
    * @return false if the user can enter the shop
    */
   protected abstract boolean featureDisabled(MapleClient client);

   /**
    * Determines if a user can enter the specified shop. Any failures must return user to functional state.
    *
    * @param client the client representing the user
    * @return false if the user can enter the shop
    */
   protected abstract boolean failsShopSpecificValidation(MapleClient client);

   /**
    * Generic handler for entering either the cash shop, or maple trade system.
    *
    * @param client the client representing the user
    */
   protected void genericHandle(MapleClient client) {
      if (featureDisabled(client)) {
         PacketCreator.announce(client, new EnableActions());
         return;
      }

      if (client.getPlayer().getEventInstance() != null) {
         MessageBroadcaster.getInstance().sendServerNotice(client.getPlayer(), ServerNoticeType.PINK_TEXT, "Entering Cash Shop or MTS are disabled when registered on an event.");
         PacketCreator.announce(client, new EnableActions());
         return;
      }

      if (MapleMiniDungeonInfo.isDungeonMap(client.getPlayer().getMapId())) {
         MessageBroadcaster.getInstance().sendServerNotice(client.getPlayer(), ServerNoticeType.PINK_TEXT, "Changing channels or entering Cash Shop or MTS are disabled when inside a Mini-Dungeon.");
         PacketCreator.announce(client, new EnableActions());
         return;
      }

      if (failsShopSpecificValidation(client)) {
         return;
      }

      prepareCharacter(client);
      openShop(client);
   }

   /**
    * Prepares the character for entering the shop
    *
    * @param client the client representing the user
    */
   protected void prepareCharacter(MapleClient client) {
      MapleCharacter character = client.getPlayer();
      character.closePlayerInteractions();
      character.closePartySearchInteractions();
      character.unregisterChairBuff();
      Server.getInstance().getPlayerBuffStorage().addBuffsToStorage(character.getId(), character.getAllBuffs());
      Server.getInstance().getPlayerBuffStorage().addDiseasesToStorage(character.getId(), character.getAlAbnormalStatuses());
      character.setAwayFromChannelWorld();
      character.notifyMapTransferToPartner(-1);
      character.removeIncomingInvites();
      character.cancelAllBuffs(true);
      character.cancelAllAbnormalStatuses();
      character.cancelBuffExpireTask();
      character.cancelDiseaseExpireTask();
      character.cancelSkillCoolDownTask();
      character.cancelExpirationTask();

      character.forfeitExpirableQuests();
      character.cancelQuestExpirationTask();

      character.saveCharToDB();
      client.getChannelServer().removePlayer(character);
      character.getMap().removePlayer(client.getPlayer());
   }

   /**
    * Opens the specified shop for the user
    *
    * @param client the client representing the user
    */
   protected abstract void openShop(MapleClient client);
}
