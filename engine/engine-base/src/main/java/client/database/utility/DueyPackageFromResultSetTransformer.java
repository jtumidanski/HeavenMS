package client.database.utility;

import java.util.List;
import java.util.Optional;

import client.inventory.Item;
import client.inventory.ItemFactory;
import client.inventory.MapleInventoryType;
import server.DueyPackage;
import tools.Pair;
import transformer.SqlTransformer;

public class DueyPackageFromResultSetTransformer implements SqlTransformer<DueyPackage, entity.duey.DueyPackage> {
   @Override
   public DueyPackage transform(entity.duey.DueyPackage resultSet) {
      int packageId = resultSet.getPackageId();

      List<Pair<Item, MapleInventoryType>> dueyItems = ItemFactory.DUEY.loadItems(packageId, false);
      DueyPackage dueyPackage;

      if (!dueyItems.isEmpty()) {     // in a duey package there's only one item
         dueyPackage = new DueyPackage(packageId, Optional.of(dueyItems.get(0).getLeft()));
      } else {
         dueyPackage = new DueyPackage(packageId);
      }

      dueyPackage.setSender(resultSet.getSenderName());
      dueyPackage.setMesos(resultSet.getMesos());
      dueyPackage.setSentTime(resultSet.getTimestamp(), resultSet.getType() == 1);
      dueyPackage.setMessage(resultSet.getMessage());

      return dueyPackage;
   }
}
