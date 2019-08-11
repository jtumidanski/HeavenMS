package client.database.provider;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import client.MapleFamilyEntry;
import client.database.AbstractQueryExecutor;

public class FamilyCharacterProvider extends AbstractQueryExecutor {
   private static FamilyCharacterProvider instance;

   public static FamilyCharacterProvider getInstance() {
      if (instance == null) {
         instance = new FamilyCharacterProvider();
      }
      return instance;
   }

   private FamilyCharacterProvider() {
   }

   public int getFamilyIdFromCharacter(Connection connection, int characterId) {
      String sql = "SELECT familyid FROM family_character WHERE cid = ?";
      Optional<Integer> result = getSingle(connection, sql, ps -> ps.setInt(1, characterId), "familyid");
      return result.orElse(-1);
   }

   public List<MapleFamilyEntry> getMapleFamily(Connection connection, int familyId) {
      String sql = "SELECT * FROM family_character WHERE familyid = ?";
      return getList(connection, sql, ps -> ps.setInt(1, familyId), rs -> {
         List<MapleFamilyEntry> familyEntries = new ArrayList<>();
         while (rs != null && rs.next()) {
            familyEntries.add(new MapleFamilyEntry(familyId, rs.getInt("rank"), rs.getInt("reputation"), rs.getInt("totaljuniors"), rs.getString("name"), rs.getInt("juniorsadded"),
                  rs.getInt("todaysrep"), rs.getInt("cid")));
         }
         return familyEntries;
      });
   }
}