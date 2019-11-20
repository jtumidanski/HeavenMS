package client.database.utility;

import constants.GameConstants;
import entity.PlayerNpc;
import server.life.MaplePlayerNPC;

public class PlayerNpcFromResultSetTransformer implements SqlTransformer<MaplePlayerNPC, PlayerNpc> {
   @Override
   public MaplePlayerNPC transform(PlayerNpc resultSet) {
      return new MaplePlayerNPC(
            resultSet.getId(),
            resultSet.getX(),
            resultSet.getCy(),
            resultSet.getName(),
            resultSet.getHair(),
            resultSet.getFace(),
            resultSet.getSkin(),
            resultSet.getGender(),
            resultSet.getDir(),
            resultSet.getFh(),
            resultSet.getRx0(),
            resultSet.getRx1(),
            resultSet.getScriptId(),
            resultSet.getWorldRank(),
            resultSet.getOverallRank(),
            resultSet.getWorldJobRank(),
            GameConstants.getOverallJobRankByScriptId(resultSet.getScriptId()),
            resultSet.getJob()
      );
   }
}
