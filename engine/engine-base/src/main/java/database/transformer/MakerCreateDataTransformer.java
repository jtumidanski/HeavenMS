package database.transformer;

import entity.maker.MakerCreateData;
import transformer.SqlTransformer;

public class MakerCreateDataTransformer implements SqlTransformer<client.database.data.MakerCreateData, MakerCreateData> {
   @Override
   public client.database.data.MakerCreateData transform(MakerCreateData makerCreateData) {
      return new client.database.data.MakerCreateData(makerCreateData.getRequiredLevel(), makerCreateData.getRequiredMakerLevel(), makerCreateData.getRequiredMeso(), makerCreateData.getQuantity());
   }
}
