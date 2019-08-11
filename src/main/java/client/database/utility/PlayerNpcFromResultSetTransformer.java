package client.database.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import constants.GameConstants;
import server.life.MaplePlayerNPC;

public class PlayerNpcFromResultSetTransformer implements SqlTransformer<MaplePlayerNPC, ResultSet> {
   @Override
   public MaplePlayerNPC transform(ResultSet resultSet) throws SQLException {
      return new MaplePlayerNPC(
            resultSet.getInt("id"),
            resultSet.getInt("x"),
            resultSet.getInt("cy"),
            resultSet.getString("name"),
            resultSet.getInt("hair"),
            resultSet.getInt("face"),
            resultSet.getByte("skin"),
            resultSet.getInt("gender"),
            resultSet.getInt("dir"),
            resultSet.getInt("fh"),
            resultSet.getInt("rx0"),
            resultSet.getInt("rx1"),
            resultSet.getInt("scriptid"),
            resultSet.getInt("worldrank"),
            resultSet.getInt("overallrank"),
            resultSet.getInt("worldjobrank"),
            GameConstants.getOverallJobRankByScriptId(resultSet.getInt("scriptid")),
            resultSet.getInt("job")
      );
   }
}
