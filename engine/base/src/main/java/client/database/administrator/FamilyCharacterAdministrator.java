package client.database.administrator;

import java.sql.Connection;

import client.database.AbstractQueryExecutor;

public class FamilyCharacterAdministrator extends AbstractQueryExecutor {
   private static FamilyCharacterAdministrator instance;

   public static FamilyCharacterAdministrator getInstance() {
      if (instance == null) {
         instance = new FamilyCharacterAdministrator();
      }
      return instance;
   }

   private FamilyCharacterAdministrator() {
   }

   public void updatePrecepts(Connection connection, int characterId, String message) {
      String sql = "UPDATE family_character SET precepts = ? WHERE cid = ?";
      execute(connection, sql, ps -> {
         ps.setString(1, message);
         ps.setInt(2, characterId);
      });
   }

   public void updateMember(Connection connection, int characterId, int reputation, int todaysReputation, int totalReputation, int reputationToSenior) {
      String sql = "UPDATE family_character SET reputation = ?, todaysrep = ?, totalreputation = ?, reptosenior = ? WHERE cid = ?";
      execute(connection, sql, ps -> {
         ps.setInt(1, reputation);
         ps.setInt(2, todaysReputation);
         ps.setInt(3, totalReputation);
         ps.setInt(4, reputationToSenior);
         ps.setInt(5, characterId);
      });
   }

   public void changeFamily(Connection connection, int characterId, int familyId, int seniorId) {
      String sql = "UPDATE family_character SET familyid = ?, seniorid = ?, reptosenior = 0 WHERE cid = ?";
      execute(connection, sql, ps -> {
         ps.setInt(1, familyId);
         ps.setInt(2, seniorId);
         ps.setInt(3, characterId);
      });
   }

   public void setFamilyForCharacter(Connection connection, int characterId, int familyId) {
      String sql = "UPDATE family_character SET familyid = ? WHERE cid = ?";
      execute(connection, sql, ps -> {
         ps.setInt(1, familyId);
         ps.setInt(2, characterId);
      });
   }

   public void create(Connection connection, int characterId, int familyId, int seniorId) {
      String sql = "INSERT INTO family_character (cid, familyid, seniorid) VALUES (?, ?, ?)";
      execute(connection, sql, ps -> {
         ps.setInt(1, characterId);
         ps.setInt(2, familyId);
         ps.setInt(3, seniorId);
      });
   }

   public void resetReputationOlderThan(Connection connection, long resetTime) {
      String sql = "UPDATE family_character SET todaysrep = 0, reptosenior = 0 WHERE lastresettime <= ?";
      execute(connection, sql, ps -> ps.setLong(1, resetTime));
   }
}