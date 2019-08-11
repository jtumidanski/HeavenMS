package client.database.provider;

import java.sql.Connection;
import java.util.List;

import client.database.AbstractQueryExecutor;
import tools.Pair;

public class PlayerNpcEquipProvider extends AbstractQueryExecutor {
   private static PlayerNpcEquipProvider instance;

   public static PlayerNpcEquipProvider getInstance() {
      if (instance == null) {
         instance = new PlayerNpcEquipProvider();
      }
      return instance;
   }

   private PlayerNpcEquipProvider() {
   }

   public List<Pair<Short, Integer>> getEquips(Connection connection, int npcId) {
      String sql = "SELECT equippos, equipid FROM playernpcs_equip WHERE npcid = ?";
      return getListNew(connection, sql, ps -> ps.setInt(1, npcId),
            rs -> new Pair<>(rs.getShort("equippos"), rs.getInt("equipid")));
   }
}