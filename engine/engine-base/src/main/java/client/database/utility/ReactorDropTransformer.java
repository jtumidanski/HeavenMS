package client.database.utility;

import entity.ReactorDrop;
import server.maps.ReactorDropEntry;
import transformer.SqlTransformer;

public class ReactorDropTransformer implements SqlTransformer<ReactorDropEntry, ReactorDrop> {
   @Override
   public ReactorDropEntry transform(ReactorDrop resultSet) {
      return new ReactorDropEntry(resultSet.getItemId(),
            resultSet.getChance(),
            resultSet.getQuestId());
   }
}
