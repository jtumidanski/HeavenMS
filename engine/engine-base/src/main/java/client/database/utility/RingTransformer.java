package client.database.utility;

import client.Ring;
import database.SqlTransformer;

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
