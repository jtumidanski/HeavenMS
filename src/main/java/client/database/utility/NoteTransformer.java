package client.database.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import client.database.data.NoteData;

public class NoteTransformer implements SqlTransformer<NoteData, ResultSet> {
   @Override
   public NoteData transform(ResultSet resultSet) throws SQLException {
      return new NoteData(
            resultSet.getInt("id"),
            resultSet.getString("from"),
            resultSet.getString("message"),
            resultSet.getLong("timestamp"),
            resultSet.getByte("fame")
      );
   }
}
