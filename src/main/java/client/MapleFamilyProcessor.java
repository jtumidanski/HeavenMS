package client;

import java.util.Optional;

import client.database.provider.FamilyCharacterProvider;
import tools.DatabaseConnection;

public class MapleFamilyProcessor {
   private static MapleFamilyProcessor ourInstance = new MapleFamilyProcessor();

   public static MapleFamilyProcessor getInstance() {
      return ourInstance;
   }

   private MapleFamilyProcessor() {
   }

   public MapleFamily loadFamilyForCharacter(int characterId) {
      Optional<Integer> familyId = DatabaseConnection.withConnectionResult(connection -> FamilyCharacterProvider.getInstance().getFamilyIdFromCharacter(connection, characterId));
      if (familyId.isEmpty()) {
         return null;
      }

      MapleFamily family = new MapleFamily(familyId.get());

      DatabaseConnection.withConnectionResult(connection -> FamilyCharacterProvider.getInstance().getMapleFamily(connection, familyId.get()))
            .ifPresent(result -> result.forEach(family::addMember));
      return family;
   }

   public void broadcast(byte[] packet) {
      // family currently not developed
   }
}
