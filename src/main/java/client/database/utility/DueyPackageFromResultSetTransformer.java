package client.database.utility;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import client.inventory.Item;
import client.inventory.ItemFactory;
import client.inventory.MapleInventoryType;
import server.DueyPackage;
import tools.Pair;

public class DueyPackageFromResultSetTransformer implements SqlTransformer<DueyPackage, ResultSet> {
   @Override
   public DueyPackage transform(ResultSet resultSet) throws SQLException {
      int packageId = resultSet.getInt("PackageId");

      List<Pair<Item, MapleInventoryType>> dueyItems = ItemFactory.DUEY.loadItems(packageId, false);
      DueyPackage dueypack;

      if (!dueyItems.isEmpty()) {     // in a duey package there's only one item
         dueypack = new DueyPackage(packageId, dueyItems.get(0).getLeft());
      } else {
         dueypack = new DueyPackage(packageId);
      }

      dueypack.setSender(resultSet.getString("SenderName"));
      dueypack.setMesos(resultSet.getInt("Mesos"));
      dueypack.setSentTime(resultSet.getTimestamp("TimeStamp"));
      dueypack.setMessage(resultSet.getString("Message"));

      return dueypack;
   }
}
