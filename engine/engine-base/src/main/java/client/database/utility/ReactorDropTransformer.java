package client.database.utility;

import database.SqlTransformer;
import entity.ReactorDrop;
import server.maps.ReactorDropEntry;

public class ReactorDropTransformer implements SqlTransformer<ReactorDropEntry, ReactorDrop> {
   @Override
   public ReactorDropEntry transform(ReactorDrop resultSet) {
      return new ReactorDropEntry(resultSet.getItemId(),
            resultSet.getChance(),
            resultSet.getQuestId());
   }
}
