package client.database.utility;

import java.util.List;

import client.inventory.Item;
import client.inventory.ItemFactory;
import client.inventory.MapleInventoryType;
import database.SqlTransformer;
import scala.Option;
import server.DueyPackage;
import tools.Pair;

public class DueyPackageFromResultSetTransformer implements SqlTransformer<DueyPackage, entity.duey.DueyPackage> {
   @Override
   public DueyPackage transform(entity.duey.DueyPackage resultSet) {
      int packageId = resultSet.getPackageId();

      List<Pair<Item, MapleInventoryType>> dueyItems = ItemFactory.DUEY.loadItems(packageId, false);
      DueyPackage dueypack;

      if (!dueyItems.isEmpty()) {     // in a duey package there's only one item
         dueypack = new DueyPackage(packageId, Option.apply(dueyItems.get(0).getLeft()));
      } else {
         dueypack = new DueyPackage(packageId);
      }

      dueypack.sender_$eq(resultSet.getSenderName());
      dueypack.mesos_$eq(resultSet.getMesos());
      dueypack.setSentTime(resultSet.getTimestamp(), resultSet.getType() == 1);
      dueypack.message_$eq(resultSet.getMessage());

      return dueypack;
   }
}
