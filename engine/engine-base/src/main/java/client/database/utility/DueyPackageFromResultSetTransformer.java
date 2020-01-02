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
      DueyPackage dueyPackage;

      if (!dueyItems.isEmpty()) {     // in a duey package there's only one item
         dueyPackage = new DueyPackage(packageId, Option.apply(dueyItems.get(0).getLeft()));
      } else {
         dueyPackage = new DueyPackage(packageId);
      }

      dueyPackage.sender_$eq(resultSet.getSenderName());
      dueyPackage.mesos_$eq(resultSet.getMesos());
      dueyPackage.setSentTime(resultSet.getTimestamp(), resultSet.getType() == 1);
      dueyPackage.message_$eq(resultSet.getMessage());

      return dueyPackage;
   }
}
