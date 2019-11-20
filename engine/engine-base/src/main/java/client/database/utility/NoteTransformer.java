package client.database.utility;

import client.database.data.NoteData;
import entity.Note;

public class NoteTransformer implements SqlTransformer<NoteData, Note> {
   @Override
   public NoteData transform(Note resultSet) {
      return new NoteData(
            resultSet.getId(),
            resultSet.getNoteFrom(),
            resultSet.getMessage(),
            resultSet.getTimestamp(),
            resultSet.getFame()
      );
   }
}
