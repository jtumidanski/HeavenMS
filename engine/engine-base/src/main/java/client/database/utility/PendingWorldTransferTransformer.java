package client.database.utility;

import client.database.data.PendingWorldTransfers;
import entity.WorldTransfer;
import transformer.SqlTransformer;

public class PendingWorldTransferTransformer implements SqlTransformer<PendingWorldTransfers, WorldTransfer> {
   @Override
   public PendingWorldTransfers transform(WorldTransfer worldTransfer) {
      return new PendingWorldTransfers(worldTransfer.getId(), worldTransfer.getCharacterId(), worldTransfer.getFromWorld(),
            worldTransfer.getToWorld());
   }
}
