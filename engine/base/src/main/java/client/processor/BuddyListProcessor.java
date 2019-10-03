package client.processor;

import client.BuddyList;
import client.BuddyListEntry;
import client.CharacterNameAndId;
import client.MapleCharacter;
import client.database.administrator.BuddyAdministrator;
import client.database.provider.BuddyProvider;
import tools.DatabaseConnection;
import tools.PacketCreator;
import tools.packet.buddy.RequestAddBuddy;

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
         PacketCreator.announce(character, new RequestAddBuddy(cidFrom, character.getId(), nameFrom));
      } else {
         character.getBuddylist().addRequest(new CharacterNameAndId(cidFrom, nameFrom));
      }
   }
}
