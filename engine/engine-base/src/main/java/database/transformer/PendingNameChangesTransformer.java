package database.transformer;

import client.database.data.PendingNameChanges;
import entity.NameChange;
import transformer.SqlTransformer;

public class PendingNameChangesTransformer implements SqlTransformer<PendingNameChanges, NameChange> {
   @Override
   public PendingNameChanges transform(NameChange nameChange) {
      return new PendingNameChanges(nameChange.getId(), nameChange.getCharacterId(), nameChange.getOld(), nameChange.getNewName());
   }
}
