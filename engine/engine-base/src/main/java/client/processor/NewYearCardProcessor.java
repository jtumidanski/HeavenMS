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
import tools.I18nMessage;
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

   public NewYearCardRecord saveNewYearCard(NewYearCardRecord newYearCardRecord) {
      int id = DatabaseConnection.getInstance().withConnectionResult(connection -> NewYearAdministrator.getInstance().create(connection, newYearCardRecord.senderId(), newYearCardRecord.senderName(),
            newYearCardRecord.receiverId(), newYearCardRecord.receiverName(), newYearCardRecord.message(),
            newYearCardRecord.senderDiscardCard(), newYearCardRecord.receiverDiscardCard(), newYearCardRecord.receiverReceivedCard(),
            newYearCardRecord.dateSent(), newYearCardRecord.dateReceived())).orElse(-1);
      return newYearCardRecord.setId(id);
   }

   public NewYearCardRecord updateNewYearCard(NewYearCardRecord newYearCardRecord) {
      NewYearCardRecord result = newYearCardRecord.setReceived();
      DatabaseConnection.getInstance().withConnection(connection -> NewYearAdministrator.getInstance().setReceived(connection, result.id(), result.dateReceived()));
      return result;
   }

   public NewYearCardRecord loadNewYearCard(int cardId) {
      NewYearCardRecord nyc = Server.getInstance().getNewYearCard(cardId);
      if (nyc != null) {
         return nyc;
      }

      Optional<NewYearCardRecord> newYearCardRecord = DatabaseConnection.getInstance().withConnectionResult(connection -> NewYearCardProvider.getInstance().getById(connection, cardId).orElse(null));
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
               NewYearCardRecord updatedCard = nyc.senderDiscard();
               chr.removeNewYearRecord(updatedCard);
               deleteNewYearCard(updatedCard.id());

               MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), new NewYearCardResolution(chr.getId(), updatedCard, 0xE, 0));

               chr.getClient().getWorldServer().getPlayerStorage().getCharacterById(updatedCard.receiverId()).filter(MapleCharacter::isLoggedInWorld).ifPresent(other -> {
                  other.removeNewYearRecord(updatedCard);
                  MasterBroadcaster.getInstance().sendToAllInMap(other.getMap(), new NewYearCardResolution(other.getId(), updatedCard, 0xE, 0));
                  MessageBroadcaster.getInstance().sendServerNotice(other, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("NEW_YEAR_CARD_THROWAWAY").with(chr.getName()));
               });
            }
         } else {
            if (nyc.receiverId() == cid) {
               NewYearCardRecord updatedCard = nyc.receiverDiscard();
               chr.removeNewYearRecord(updatedCard);
               deleteNewYearCard(updatedCard.id());

               MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), new NewYearCardResolution(chr.getId(), updatedCard, 0xE, 0));

               chr.getClient().getWorldServer().getPlayerStorage().getCharacterById(updatedCard.senderId())
                     .filter(MapleCharacter::isLoggedInWorld)
                     .ifPresent(other -> {
                        other.removeNewYearRecord(updatedCard);
                        MasterBroadcaster.getInstance().sendToAllInMap(other.getMap(), new NewYearCardResolution(other.getId(), updatedCard, 0xE, 0));
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