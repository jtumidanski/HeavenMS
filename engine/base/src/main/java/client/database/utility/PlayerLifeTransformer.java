package client.database.utility;

import client.database.data.PlayerLifeData;
import entity.PLife;

public class PlayerLifeTransformer implements SqlTransformer<PlayerLifeData, PLife> {
   @Override
   public PlayerLifeData transform(PLife resultSet) {
      return new PlayerLifeData(
            resultSet.getLife(),
            resultSet.getType(),
            resultSet.getCy(),
            resultSet.getF(),
            resultSet.getFh(),
            resultSet.getRx0(),
            resultSet.getRx1(),
            resultSet.getX(),
            resultSet.getY(),
            resultSet.getHide(),
            resultSet.getMobTime(),
            resultSet.getTeam()
      );
   }
}
