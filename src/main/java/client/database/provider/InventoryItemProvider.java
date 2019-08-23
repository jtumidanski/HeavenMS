package client.database.provider;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import client.database.AbstractQueryExecutor;
import client.database.utility.EquipFromResultSetTransformer;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import tools.Pair;

public class InventoryItemProvider extends AbstractQueryExecutor {
   private static InventoryItemProvider instance;

   public static InventoryItemProvider getInstance() {
      if (instance == null) {
         instance = new InventoryItemProvider();
      }
      return instance;
   }

   private InventoryItemProvider() {
   }

   public List<Pair<Item, MapleInventoryType>> getItemsByCharacterAndType(Connection connection, int characterId, int type, boolean loggedIn) {
      String sql = constructGetItemsByQuery(false, loggedIn);
      return getListNew(connection, sql, ps -> {
         ps.setInt(1, type);
         ps.setInt(2, characterId);
      }, this::processGetItemsByTypeResults);
   }

   public List<Pair<Item, MapleInventoryType>> getItemsByAccountAndType(Connection connection, int accountId, int type, boolean loggedIn) {
      String sql = constructGetItemsByQuery(true, loggedIn);
      return getListNew(connection, sql, ps -> {
         ps.setInt(1, type);
         ps.setInt(2, accountId);
      }, this::processGetItemsByTypeResults);
   }

   public List<Pair<Item, Integer>> getEquipsByCharacter(Connection connection, int characterId, boolean loggedIn) {
      String sql = constructGetEquipsByQuery(false, loggedIn);
      return getListNew(connection, sql, ps -> ps.setInt(1, characterId), this::processGetEquipsByTypeResults);
   }

   public List<Pair<Item, Integer>> getEquipsByAccount(Connection connection, int accountId, boolean loggedIn) {
      String sql = constructGetEquipsByQuery(true, loggedIn);
      return getListNew(connection, sql, ps -> ps.setInt(1, accountId), this::processGetEquipsByTypeResults);
   }

   private Pair<Item, Integer> processGetEquipsByTypeResults(ResultSet rs) throws SQLException {
      EquipFromResultSetTransformer equipTransformer = new EquipFromResultSetTransformer();
      Integer cid = rs.getInt("characterid");
      return new Pair<>(equipTransformer.transform(rs), cid);
   }

   private Pair<Item, MapleInventoryType> processGetItemsByTypeResults(ResultSet resultSet) throws SQLException {
      EquipFromResultSetTransformer equipTransformer = new EquipFromResultSetTransformer();

      MapleInventoryType inventoryType = MapleInventoryType.getByType(resultSet.getByte("inventorytype"));
      if (inventoryType != null) {
         if (inventoryType.equals(MapleInventoryType.EQUIP) || inventoryType.equals(MapleInventoryType.EQUIPPED)) {
            return new Pair<>(equipTransformer.transform(resultSet), inventoryType);
         } else {
            Item item = new Item(resultSet.getInt("itemid"), (byte) resultSet.getInt("position"),
                  (short) resultSet.getInt("quantity"), resultSet.getInt("petid"));
            item.setOwner(resultSet.getString("owner"));
            item.setExpiration(resultSet.getLong("expiration"));
            item.setGiftFrom(resultSet.getString("giftFrom"));
            item.setFlag((short) resultSet.getInt("flag"));
            return new Pair<>(item, inventoryType);
         }
      }
      return null;
   }

   private String constructGetItemsByQuery(boolean account, boolean loggedIn) {
      StringBuilder query = new StringBuilder();
      query.append("SELECT * FROM `inventoryitems` LEFT JOIN `inventoryequipment` USING(`inventoryitemid`) WHERE `type` = ? AND `");
      query.append(account ? "accountid" : "characterid").append("` = ?");

      if (loggedIn) {
         query.append(" AND `inventorytype` = ").append(MapleInventoryType.EQUIPPED.getType());
      }
      return query.toString();
   }

   private String constructGetEquipsByQuery(boolean account, boolean loggedIn) {
      return "SELECT * FROM " +
            "(SELECT id, accountid FROM characters) AS accountterm " +
            "RIGHT JOIN " +
            "(SELECT * FROM (`inventoryitems` LEFT JOIN `inventoryequipment` USING(`inventoryitemid`))) AS equipterm" +
            " ON accountterm.id=equipterm.characterid " +
            "WHERE accountterm.`" +
            (account ? "accountid" : "characterid") +
            "` = ?" +
            (loggedIn ? " AND `inventorytype` = " + MapleInventoryType.EQUIPPED.getType() : "");
   }

   public List<Integer> getPetsForCharacter(Connection connection, int characterId) {
      String sql = "SELECT petid FROM inventoryitems WHERE characterid = ? AND petid > -1";
      return getListNew(connection, sql, ps -> ps.setInt(1, characterId), rs -> rs.getInt(1));
   }

   public List<Pair<Integer, Integer>> get(Connection connection, int characterId) {
      String sql = "SELECT inventoryitemid, petid FROM inventoryitems WHERE characterid = ?";
      return getListNew(connection, sql, ps -> ps.setInt(1, characterId), rs -> new Pair<>(rs.getInt("inventoryitemid"), rs.getInt("petid")));
   }
}