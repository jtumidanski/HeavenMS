package client.processor;

import java.util.Arrays;
import java.util.Optional;

import client.BuddyList;
import client.BuddyListEntry;
import client.CharacterNameAndId;
import client.MapleCharacter;
import client.database.administrator.BuddyAdministrator;
import client.database.provider.BuddyProvider;
import net.server.PlayerStorage;
import tools.DatabaseConnection;
import tools.MaplePacketCreator;

public class BuddyListProcessor {
   private static BuddyListProcessor ourInstance = new BuddyListProcessor();

   public static BuddyListProcessor getInstance() {
      return ourInstance;
   }

   private BuddyListProcessor() {
   }

   public void loadFromDb(int characterId, BuddyList buddyList) {
      DatabaseConnection.getInstance().withConnection(connection -> {
         BuddyProvider.getInstance().getInfoForBuddies(connection, characterId).forEach(buddyList::put);
         BuddyProvider.getInstance().getInfoForPendingBuddies(connection, characterId).forEach(buddyList::addRequest);
         BuddyAdministrator.getInstance().deletePendingForCharacter(connection, characterId);
      });
   }

   public void addBuddyRequest(MapleCharacter character, int cidFrom, String nameFrom, int channelFrom) {
      character.getBuddylist().put(new BuddyListEntry(nameFrom, "Default Group", cidFrom, channelFrom, false));
      if (character.getBuddylist().hasPendingRequest()) {
         character.getClient().announce(MaplePacketCreator.requestBuddylistAdd(cidFrom, character.getId(), nameFrom));
      } else {
         character.getBuddylist().addRequest(new CharacterNameAndId(cidFrom, nameFrom));
      }
   }
}
