package client.database.provider;

import java.sql.Connection;
import java.util.ArrayList;
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
      return getList(connection, sql, ps -> ps.setInt(1, npcId), rs -> {
         List<Pair<Short, Integer>> equips = new ArrayList<>();
         while (rs != null && rs.next()) {
            equips.add(new Pair<>(rs.getShort("equippos"), rs.getInt("equipid")));
         }
         return equips;
      });
   }
}