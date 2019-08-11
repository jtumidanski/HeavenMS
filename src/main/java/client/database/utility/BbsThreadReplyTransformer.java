package client.database.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import client.database.data.BbsThreadReplyData;

public class BbsThreadReplyTransformer implements SqlTransformer<BbsThreadReplyData, ResultSet> {
   @Override
   public BbsThreadReplyData transform(ResultSet resultSet) throws SQLException {
      return new BbsThreadReplyData(
            resultSet.getInt("replyid"),
            resultSet.getInt("postercid"),
            resultSet.getLong("timestamp"),
            resultSet.getString("content"),
            resultSet.getInt("threadId")
      );
   }
}
