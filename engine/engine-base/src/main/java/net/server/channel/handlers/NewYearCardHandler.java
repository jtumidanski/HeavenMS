package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import database.provider.CharacterProvider;
import client.inventory.Item;
import client.newyear.NewYearCardRecord;
import client.processor.NewYearCardProcessor;
import constants.inventory.ItemConstants;
import net.server.AbstractPacketHandler;
import net.server.Server;
import net.server.channel.packet.newyear.BaseNewYearCardPacket;
import net.server.channel.packet.newyear.CardAcceptedPacket;
import net.server.channel.packet.newyear.CardHasBeenSentPacket;
import net.server.channel.packet.reader.NewYearCardReader;
import database.DatabaseConnection;
import tools.MasterBroadcaster;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.I18nMessage;
import tools.packet.NewYearCardResolution;

public final class NewYearCardHandler extends AbstractPacketHandler<BaseNewYearCardPacket> {
   @Override
   public Class<NewYearCardReader> getReaderClass() {
      return NewYearCardReader.class;
   }

   @Override
   public void handlePacket(BaseNewYearCardPacket packet, MapleClient client) {
      final MapleCharacter player = client.getPlayer();
      if (packet instanceof CardHasBeenSentPacket) {
         cardHasBeenSent(client, player, ((CardHasBeenSentPacket) packet).slot(),
               ((CardHasBeenSentPacket) packet).itemId(), ((CardHasBeenSentPacket) packet).receiver(),
               ((CardHasBeenSentPacket) packet).message());
      } else if (packet instanceof CardAcceptedPacket) {
         cardAccepted(client, player, ((CardAcceptedPacket) packet).cardId());
      }
   }

   private int getReceiverId(String receiver, int world) {
      return DatabaseConnection.getInstance().withConnectionResultOpt(connection ->
            CharacterProvider.getInstance().getCharacterForNameAndWorld(connection, receiver, world))
            .orElse(-1);
   }

   private int getValidNewYearCardStatus(int itemId, MapleCharacter player, short slot) {
      if (!ItemConstants.isNewYearCardUse(itemId)) {
         return 0x14;
      }

      Item it = player.getInventory(ItemConstants.getInventoryType(itemId)).getItem(slot);
      return (it != null && it.id() == itemId) ? 0 : 0x12;
   }

   private void cardAccepted(MapleClient c, MapleCharacter player, int cardId) {
      NewYearCardRecord newYear = NewYearCardProcessor.getInstance().loadNewYearCard(cardId);

      if (newYear != null && newYear.receiverId() == player.getId() && !newYear.receiverReceivedCard()) {
         if (!newYear.senderDiscardCard()) {
            if (player.canHold(4301000, 1)) {
               newYear.stopNewYearCardTask();
               NewYearCardProcessor.getInstance().updateNewYearCard(newYear);

               player.getAbstractPlayerInteraction().gainItem(4301000, (short) 1);
               if (!newYear.message().isEmpty()) {
                  MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("NEW_YEAR_CARD_MESSAGE").with(newYear.senderName(), newYear.message()));
               }

               player.addNewYearRecord(newYear);
               PacketCreator.announce(player, new NewYearCardResolution(player.getId(), newYear, 6, 0));    // successfully received

               MasterBroadcaster.getInstance().sendToAllInMap(player.getMap(), new NewYearCardResolution(player.getId(), newYear, 0xD, 0));

               c.getWorldServer().getPlayerStorage().getCharacterById(newYear.senderId()).filter(MapleCharacter::isLoggedInWorld).ifPresent(sender -> {
                  MasterBroadcaster.getInstance().sendToAllInMap(sender.getMap(), new NewYearCardResolution(sender.getId(), newYear, 0xD, 0));
                  MessageBroadcaster.getInstance().sendServerNotice(sender, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("NEW_YEAR_CARD_RECEIPT_CONFIRMATION"));
               });
            } else {
               PacketCreator.announce(player, new NewYearCardResolution(player.getId(), player.getNewYearRecord(-1), 5, 0x10));  // inventory full
            }
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("NEW_YEAR_CARD_SENDER_DROPPED_CARD"));
         }
      } else {
         if (newYear == null) {
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("NEW_YEAR_CARD_SENDER_DROPPED_CARD"));
         }
      }
   }

   private void cardHasBeenSent(MapleClient c, MapleCharacter player, short slot, int itemId, String receiver, String message) {
      if (player.haveItem(2160101)) {  // new year's card
         int status = getValidNewYearCardStatus(itemId, player, slot);
         if (status == 0) {
            if (player.canHold(4300000, 1)) {

               int receiverId = getReceiverId(receiver, c.getWorld());
               if (receiverId != -1) {
                  if (receiverId != c.getPlayer().getId()) {

                     NewYearCardRecord newYearCardRecord = new NewYearCardRecord(player.getId(), player.getName(), receiverId, receiver, message);
                     NewYearCardProcessor.getInstance().saveNewYearCard(newYearCardRecord);
                     player.addNewYearRecord(newYearCardRecord);

                     player.getAbstractPlayerInteraction().gainItem(2160101, (short) -1);
                     player.getAbstractPlayerInteraction().gainItem(4300000, (short) 1);

                     Server.getInstance().setNewYearCard(newYearCardRecord);
                     NewYearCardProcessor.getInstance().startNewYearCardTask(newYearCardRecord);
                     PacketCreator.announce(player, new NewYearCardResolution(player.getId(), newYearCardRecord, 4, 0));    // successfully sent
                  } else {
                     PacketCreator.announce(player, new NewYearCardResolution(player.getId(), player.getNewYearRecord(-1), 5, 0xF));   // cannot send to yourself
                  }
               } else {
                  PacketCreator.announce(player, new NewYearCardResolution(player.getId(), player.getNewYearRecord(-1), 5, 0x13));  // cannot find such character
               }
            } else {
               PacketCreator.announce(player, new NewYearCardResolution(player.getId(), player.getNewYearRecord(-1), 5, 0x10));  // inventory full
            }
         } else {
            PacketCreator.announce(player, new NewYearCardResolution(player.getId(), player.getNewYearRecord(-1), 5, status));  // item and inventory errors
         }
      } else {
         PacketCreator.announce(player, new NewYearCardResolution(player.getId(), player.getNewYearRecord(-1), 5, 0x11));  // have no card to send
      }
   }
}
