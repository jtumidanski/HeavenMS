package client.processor;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

import client.MapleCharacter;
import client.newyear.NewYearCardRecord;
import database.DatabaseConnection;
import database.administrator.NewYearAdministrator;
import database.provider.NewYearCardProvider;
import net.server.Server;
import server.TimerManager;
import tools.MasterBroadcaster;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.I18nMessage;
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

   public void saveNewYearCard(NewYearCardRecord newYearCardRecord) {
      DatabaseConnection.getInstance().withConnection(connection -> newYearCardRecord.id_$eq(NewYearAdministrator.getInstance().create(connection, newYearCardRecord.senderId(), newYearCardRecord.senderName(),
            newYearCardRecord.receiverId(), newYearCardRecord.receiverName(), newYearCardRecord.message(),
            newYearCardRecord.senderDiscardCard(), newYearCardRecord.receiverDiscardCard(), newYearCardRecord.receiverReceivedCard(),
            newYearCardRecord.dateSent(), newYearCardRecord.dateReceived())));
   }

   public void updateNewYearCard(NewYearCardRecord newYearCardRecord) {
      newYearCardRecord.receiverReceivedCard_$eq(true);
      newYearCardRecord.dateReceived_$eq(System.currentTimeMillis());
      DatabaseConnection.getInstance().withConnection(connection -> NewYearAdministrator.getInstance().setReceived(connection,
            newYearCardRecord.id(), newYearCardRecord.dateReceived()));
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

               chr.getClient().getWorldServer().getPlayerStorage().getCharacterById(nyc.receiverId()).filter(MapleCharacter::isLoggedInWorld).ifPresent(other -> {
                  other.removeNewYearRecord(nyc);
                  MasterBroadcaster.getInstance().sendToAllInMap(other.getMap(), new NewYearCardResolution(other.getId(), nyc, 0xE, 0));
                  MessageBroadcaster.getInstance().sendServerNotice(other, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("NEW_YEAR_CARD_THROWAWAY").with(chr.getName()));
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
                     .filter(MapleCharacter::isLoggedInWorld)
                     .ifPresent(other -> {
                        other.removeNewYearRecord(nyc);
                        MasterBroadcaster.getInstance().sendToAllInMap(other.getMap(), new NewYearCardResolution(other.getId(), nyc, 0xE, 0));
                        MessageBroadcaster.getInstance().sendServerNotice(other, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("NEW_YEAR_CARD_THROWAWAY").with(chr.getName()));
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
               .filter(MapleCharacter::isLoggedInWorld)
               .ifPresent(target -> PacketCreator.announce(target, new NewYearCardResolution(target.getId(), newYearCardRecord, 0xC, 0)));
      }, 1000 * 60 * 60); //1 Hour
      newYearCardRecord.setNewYearCardTask(sendTask);
   }
}