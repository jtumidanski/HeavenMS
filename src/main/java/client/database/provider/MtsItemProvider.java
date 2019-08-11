package client.database.provider;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import client.database.AbstractQueryExecutor;
import client.inventory.Equip;
import client.inventory.Item;
import server.MTSItemInfo;
import tools.Pair;

public class MtsItemProvider extends AbstractQueryExecutor {
   private static MtsItemProvider instance;

   public static MtsItemProvider getInstance() {
      if (instance == null) {
         instance = new MtsItemProvider();
      }
      return instance;
   }

   private MtsItemProvider() {
   }

   public List<MTSItemInfo> getByTabAndType(Connection connection, int tab, int type, int limit) {
      String sql = "SELECT * FROM mts_items WHERE tab = ? AND type = ? AND transfer = 0 ORDER BY id DESC LIMIT ?, 16";
      return getListNew(connection, sql, ps -> {
         ps.setInt(1, tab);
         ps.setInt(2, type);
         ps.setInt(3, limit);
      }, this::produceItem);
   }

   public int countByTabAndType(Connection connection, int tab, int type) {
      String sql = "SELECT COUNT(*) FROM mts_items WHERE tab = ? AND type = ? AND transfer = 0";
      Optional<Integer> result = getSingle(connection, sql, ps -> {
         ps.setInt(1, tab);
         ps.setInt(2, type);
      }, 1);
      return result.orElse(0);
   }

   public List<MTSItemInfo> getByTab(Connection connection, int tab, int limit) {
      String sql = "SELECT * FROM mts_items WHERE tab = ? AND transfer = 0 ORDER BY id DESC LIMIT ?, 16";
      return getListNew(connection, sql, ps -> {
         ps.setInt(1, tab);
         ps.setInt(2, limit);
      }, this::produceItem);
   }

   public int countByTab(Connection connection, int tab) {
      String sql = "SELECT COUNT(*) FROM mts_items WHERE tab = ? AND transfer = 0";
      Optional<Integer> result = getSingle(connection, sql, ps -> ps.setInt(1, tab), 1);
      return result.orElse(0);
   }

   public int countBySeller(Connection connection, int characterId) {
      String sql = "SELECT COUNT(*) FROM mts_items WHERE seller = ?";
      Optional<Integer> result = getSingle(connection, sql, ps -> ps.setInt(1, characterId), 1);
      return result.orElse(0);
   }

   public Optional<MTSItemInfo> getById(Connection connection, int id) {
      String sql = "SELECT * FROM mts_items WHERE id = ?";
      return getNew(connection, sql, ps -> ps.setInt(1, id), this::produceItem);
   }

   public Optional<Pair<Integer, Integer>> getSaleInfoById(Connection connection, int id) {
      String sql = "SELECT * FROM mts_items WHERE id = ? ORDER BY id DESC";
      return getNew(connection, sql, ps -> ps.setInt(1, id),
            rs -> new Pair<>(rs.getInt("seller"), rs.getInt("price")));
   }

   public List<MTSItemInfo> getTransferItems(Connection connection, int sellerId) {
      String sql = "SELECT * FROM mts_items WHERE transfer = 1 AND seller = ? ORDER BY id DESC";
      return getListNew(connection, sql, ps -> ps.setInt(1, sellerId), this::produceItem);
   }

   public Optional<Item> getTransferItem(Connection connection, int characterId, int itemId) {
      String sql = "SELECT * FROM mts_items WHERE seller = ? AND transfer = 1  AND id= ? ORDER BY id DESC";
      return getNew(connection, sql, ps -> {
         ps.setInt(1, characterId);
         ps.setInt(2, itemId);
      }, this::produceTransferItem);
   }

   public boolean isItemForSaleBySomeoneElse(Connection connection, int itemId, int characterId) {
      String sql = "SELECT id FROM mts_items WHERE id = ? AND seller <> ?";
      Optional<Integer> result = getSingle(connection, sql, ps -> {
         ps.setInt(1, itemId);
         ps.setInt(2, characterId);
      }, "id");
      return result.isPresent();
   }

   public List<MTSItemInfo> getUnsoldItems(Connection connection, int sellerId) {
      String sql = "SELECT * FROM mts_items WHERE seller = ? AND transfer = 0 ORDER BY id DESC";
      return getListNew(connection, sql, ps -> ps.setInt(1, sellerId), this::produceItem);
   }

   public int countSearchItems(Connection connection, int tab, int type, int characterId, String search, List<Pair<Integer, String>> items) {
      StringBuilder clause = constructSearchClause(characterId, search, items);
      String sql;
      if (type != 0) {
         sql = "SELECT COUNT(*) FROM mts_items WHERE tab = ? " + clause + " AND type = ? AND transfer = 0";
      } else {
         sql = "SELECT COUNT(*) FROM mts_items WHERE tab = ? " + clause + " AND transfer = 0";
      }
      Optional<Integer> result = getSingle(connection, sql, ps -> {
         ps.setInt(1, tab);
         if (type != 0) {
            ps.setInt(2, type);
         }
      }, 1);
      return result.orElse(0);
   }

   public List<MTSItemInfo> getSearchItems(Connection connection, int tab, int type, int characterId, String search, int page, List<Pair<Integer, String>> items) {
      StringBuilder clause = constructSearchClause(characterId, search, items);
      String sql;
      if (type != 0) {
         sql = "SELECT * FROM mts_items WHERE tab = ? " + clause + " AND type = ? AND transfer = 0 ORDER BY id DESC LIMIT ?, 16";
      } else {
         sql = "SELECT * FROM mts_items WHERE tab = ? " + clause + " AND transfer = 0 ORDER BY id DESC LIMIT ?, 16";
      }
      return getListNew(connection, sql, ps -> {
         ps.setInt(1, tab);
         if (type != 0) {
            ps.setInt(2, type);
            ps.setInt(3, page * 16);
         } else {
            ps.setInt(2, page * 16);
         }
      }, this::produceItem);
   }

   private StringBuilder constructSearchClause(int characterId, String search, List<Pair<Integer, String>> items) {
      StringBuilder listOfItems = new StringBuilder();
      if (characterId != 0) {
         List<String> retItems = new ArrayList<>();
         for (Pair<Integer, String> itemPair : items) {
            if (itemPair.getRight().toLowerCase().contains(search.toLowerCase())) {
               retItems.add(" itemid=" + itemPair.getLeft() + " OR ");
            }
         }
         listOfItems.append(" AND (");
         if (retItems.size() > 0) {
            for (String singleRetItem : retItems) {
               listOfItems.append(singleRetItem);
            }
            listOfItems.append(" itemid=0 )");
         }
      } else {
         listOfItems = new StringBuilder(" AND sellername LIKE CONCAT('%','" + search + "', '%')");
      }
      return listOfItems;
   }

   private MTSItemInfo produceItem(ResultSet resultSet) throws SQLException {
      if (resultSet.getInt("type") != 1) {
         Item i = new Item(resultSet.getInt("itemid"), (short) 0, (short) resultSet.getInt("quantity"));
         i.setOwner(resultSet.getString("owner"));
         return new MTSItemInfo(i, resultSet.getInt("price"), resultSet.getInt("id"), resultSet.getInt("seller"), resultSet.getString("sellername"), resultSet.getString("sell_ends"));
      } else {
         Equip equip = new Equip(resultSet.getInt("itemid"), (byte) resultSet.getInt("position"), -1);
         equip.setOwner(resultSet.getString("owner"));
         equip.setQuantity((short) 1);
         equip.setAcc((short) resultSet.getInt("acc"));
         equip.setAvoid((short) resultSet.getInt("avoid"));
         equip.setDex((short) resultSet.getInt("dex"));
         equip.setHands((short) resultSet.getInt("hands"));
         equip.setHp((short) resultSet.getInt("hp"));
         equip.setInt((short) resultSet.getInt("int"));
         equip.setJump((short) resultSet.getInt("jump"));
         equip.setVicious((short) resultSet.getInt("vicious"));
         equip.setLuk((short) resultSet.getInt("luk"));
         equip.setMatk((short) resultSet.getInt("matk"));
         equip.setMdef((short) resultSet.getInt("mdef"));
         equip.setMp((short) resultSet.getInt("mp"));
         equip.setSpeed((short) resultSet.getInt("speed"));
         equip.setStr((short) resultSet.getInt("str"));
         equip.setWatk((short) resultSet.getInt("watk"));
         equip.setWdef((short) resultSet.getInt("wdef"));
         equip.setUpgradeSlots((byte) resultSet.getInt("upgradeslots"));
         equip.setLevel((byte) resultSet.getInt("level"));
         equip.setFlag((short) resultSet.getInt("flag"));
         return new MTSItemInfo(equip, resultSet.getInt("price"), resultSet.getInt("id"), resultSet.getInt("seller"), resultSet.getString("sellername"), resultSet.getString("sell_ends"));
      }
   }

   private Item produceTransferItem(ResultSet resultSet) throws SQLException {
      Item item;
      if (resultSet.getInt("type") != 1) {
         Item internalItem = new Item(resultSet.getInt("itemid"), (short) 0, (short) resultSet.getInt("quantity"));
         internalItem.setOwner(resultSet.getString("owner"));
         item = internalItem.copy();
      } else {
         Equip equip = new Equip(resultSet.getInt("itemid"), (byte) resultSet.getInt("position"), -1);
         equip.setOwner(resultSet.getString("owner"));
         equip.setQuantity((short) 1);
         equip.setAcc((short) resultSet.getInt("acc"));
         equip.setAvoid((short) resultSet.getInt("avoid"));
         equip.setDex((short) resultSet.getInt("dex"));
         equip.setHands((short) resultSet.getInt("hands"));
         equip.setHp((short) resultSet.getInt("hp"));
         equip.setInt((short) resultSet.getInt("int"));
         equip.setJump((short) resultSet.getInt("jump"));
         equip.setLuk((short) resultSet.getInt("luk"));
         equip.setMatk((short) resultSet.getInt("matk"));
         equip.setMdef((short) resultSet.getInt("mdef"));
         equip.setMp((short) resultSet.getInt("mp"));
         equip.setSpeed((short) resultSet.getInt("speed"));
         equip.setStr((short) resultSet.getInt("str"));
         equip.setWatk((short) resultSet.getInt("watk"));
         equip.setWdef((short) resultSet.getInt("wdef"));
         equip.setUpgradeSlots((byte) resultSet.getInt("upgradeslots"));
         equip.setLevel((byte) resultSet.getInt("level"));
         equip.setVicious((byte) resultSet.getInt("vicious"));
         equip.setFlag((short) resultSet.getInt("flag"));
         item = equip.copy();
      }
      return item;
   }
}