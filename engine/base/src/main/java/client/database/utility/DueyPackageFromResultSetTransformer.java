package client.database.utility;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import client.inventory.Item;
import client.inventory.ItemFactory;
import client.inventory.MapleInventoryType;
import scala.Option;
import server.DueyPackage;
import tools.Pair;

public class DueyPackageFromResultSetTransformer implements SqlTransformer<DueyPackage, ResultSet> {
   @Override
   public DueyPackage transform(ResultSet resultSet) throws SQLException {
      int packageId = resultSet.getInt("PackageId");

      List<Pair<Item, MapleInventoryType>> dueyItems = ItemFactory.DUEY.loadItems(packageId, false);
      DueyPackage dueypack;

      if (!dueyItems.isEmpty()) {     // in a duey package there's only one item
         dueypack = new DueyPackage(packageId, Option.apply(dueyItems.get(0).getLeft()));
      } else {
         dueypack = new DueyPackage(packageId, Option.empty());
      }

      dueypack.sender_$eq(resultSet.getString("SenderName"));
      dueypack.mesos_$eq(resultSet.getInt("Mesos"));
      dueypack.setSentTime(resultSet.getTimestamp("TimeStamp"), resultSet.getBoolean("Type"));
      dueypack.message_$eq(resultSet.getString("Message"));

      return dueypack;
   }
}
