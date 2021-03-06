package database.transformer;

import client.Ring;
import transformer.SqlTransformer;

public class RingTransformer implements SqlTransformer<Ring, entity.Ring> {
   @Override
   public Ring transform(entity.Ring resultSet) {
      return new Ring(resultSet.getId(),
            resultSet.getPartnerRingId(),
            resultSet.getPartnerCharacterId(),
            resultSet.getItemId(),
            resultSet.getPartnerName());
   }
}
