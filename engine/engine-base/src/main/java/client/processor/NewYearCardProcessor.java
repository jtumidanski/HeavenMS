package client.processor;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

import client.MapleCharacter;
import database.administrator.NewYearAdministrator;
import database.provider.NewYearCardProvider;
import client.newyear.NewYearCardRecord;
import net.server.Server;
import server.TimerManager;
import database.DatabaseConnection;
import tools.MasterBroadcaster;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.packet.NewYearCardResolution;

public class NewYearCardProcessor {
   private static NewYearCardProcessor instance;

   public static NewYearCardProcessor getInstance() {
      if (instance == null) {
         instance = new NewYearCardProcessor();
      }
      return instance;
   }

   private NewYearCardProcessor() {
   }

   public void saveNewYearCard(NewYearCardRecord newyear) {
      DatabaseConnection.getInstance().withConnection(connection -> newyear.id_$eq(NewYearAdministrator.getInstance().create(connection, newyear.senderId(), newyear.senderName(),
            newyear.receiverId(), newyear.receiverName(), newyear.message(),
            newyear.senderDiscardCard(), newyear.receiverDiscardCard(), newyear.receiverReceivedCard(),
            newyear.dateSent(), newyear.dateReceived())));
   }

   public void updateNewYearCard(NewYearCardRecord newyear) {
      newyear.receiverReceivedCard_$eq(true);
      newyear.dateReceived_$eq(System.currentTimeMillis());
      DatabaseConnection.getInstance().withConnection(connection -> NewYearAdministrator.getInstance().setReceived(connection,
            newyear.id(), newyear.dateReceived()));
   }

   public NewYearCardRecord loadNewYearCard(int cardId) {
      NewYearCardRecord nyc = Server.getInstance().getNewYearCard(cardId);
      if (nyc != null) {
         return nyc;
      }

      Optional<NewYearCardRecord> newYearCardRecord = DatabaseConnection.getInstance().withConnectionResultOpt(connection -> NewYearCardProvider.getInstance().getById(connection, cardId));
      if (newYearCardRecord.isPresent()) {
         Server.getInstance().setNewYearCard(newYearCardRecord.get());
         return newYearCardRecord.get();
      } else {
         return null;
      }
   }

   public void loadPlayerNewYearCards(MapleCharacter chr) {
      DatabaseConnection.getInstance().withConnection(connection ->
            NewYearCardProvider.getInstance().getBySenderOrReceiver(connection, chr.getId(), chr.getId())
                  .forEach(chr::addNewYearRecord));
   }

   public void printNewYearRecords(MapleCharacter chr) {
      MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "New Years: " + chr.getNewYearRecords().size());

      for (NewYearCardRecord nyc : chr.getNewYearRecords()) {
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "-------------------------------");

         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "Id: " + nyc.id());

         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "Sender id: " + nyc.senderId());
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "Sender name: " + nyc.senderName());
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "Sender discard: " + nyc.senderDiscardCard());

         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "Receiver id: " + nyc.receiverId());
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "Receiver name: " + nyc.receiverName());
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "Receiver discard: " + nyc.receiverDiscardCard());
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "Received: " + nyc.receiverReceivedCard());

         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "Message: " + nyc.message());
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "Date sent: " + nyc.dateSent());
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "Date recv: " + nyc.dateReceived());
      }
   }

   private void deleteNewYearCard(int id) {
      Server.getInstance().removeNewYearCard(id);
      DatabaseConnection.getInstance().withConnection(connection -> NewYearAdministrator.getInstance().deleteById(connection, id));
   }

   public void removeAllNewYearCard(boolean send, MapleCharacter chr) {
      int cid = chr.getId();

      Set<NewYearCardRecord> set = new HashSet<>(chr.getNewYearRecords());
      for (NewYearCardRecord nyc : set) {
         if (send) {
            if (nyc.senderId() == cid) {
               nyc.senderDiscardCard_$eq(true);
               nyc.receiverReceivedCard_$eq(false);

               chr.removeNewYearRecord(nyc);
               deleteNewYearCard(nyc.id());

               MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), new NewYearCardResolution(chr.getId(), nyc, 0xE, 0));

               chr.getClient().getWorldServer().getPlayerStorage().getCharacterById(nyc.receiverId()).filter(MapleCharacter::isLoggedinWorld).ifPresent(other -> {
                  other.removeNewYearRecord(nyc);
                  MasterBroadcaster.getInstance().sendToAllInMap(other.getMap(), new NewYearCardResolution(other.getId(), nyc, 0xE, 0));
                  MessageBroadcaster.getInstance().sendServerNotice(other, ServerNoticeType.LIGHT_BLUE, "[New Year] " + chr.getName() + " threw away the New Year card.");
               });
            }
         } else {
            if (nyc.receiverId() == cid) {
               nyc.receiverDiscardCard_$eq(true);
               nyc.receiverReceivedCard_$eq(false);

               chr.removeNewYearRecord(nyc);
               deleteNewYearCard(nyc.id());

               MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), new NewYearCardResolution(chr.getId(), nyc, 0xE, 0));

               chr.getClient().getWorldServer().getPlayerStorage().getCharacterById(nyc.senderId())
                     .filter(MapleCharacter::isLoggedinWorld)
                     .ifPresent(other -> {
                        other.removeNewYearRecord(nyc);
                        MasterBroadcaster.getInstance().sendToAllInMap(other.getMap(), new NewYearCardResolution(other.getId(), nyc, 0xE, 0));
                        MessageBroadcaster.getInstance().sendServerNotice(other, ServerNoticeType.LIGHT_BLUE, "[New Year] " + chr.getName() + " threw away the New Year card.");
                     });
            }
         }
      }
   }

   public void startPendingNewYearCardRequests() {
      DatabaseConnection.getInstance().withConnection(connection -> NewYearCardProvider.getInstance().getNotReceived(connection).forEach(newYearCard -> Server.getInstance().setNewYearCard(newYearCard)));
   }

   public void startNewYearCardTask(NewYearCardRecord newYearCardRecord) {
      if (newYearCardRecord.hasSendTask()) {
         return;
      }

      ScheduledFuture<?> sendTask = TimerManager.getInstance().register(() -> {
         Server server = Server.getInstance();

         int world = server.getCharacterWorld(newYearCardRecord.receiverId());
         if (world == -1) {
            newYearCardRecord.stopNewYearCardTask();
            return;
         }

         server.getWorld(world).getPlayerStorage().getCharacterById(newYearCardRecord.receiverId())
               .filter(MapleCharacter::isLoggedinWorld)
               .ifPresent(target -> PacketCreator.announce(target, new NewYearCardResolution(target.getId(), newYearCardRecord, 0xC, 0)));
      }, 1000 * 60 * 60); //1 Hour
      newYearCardRecord.setNewYearCardTask(sendTask);
   }
}