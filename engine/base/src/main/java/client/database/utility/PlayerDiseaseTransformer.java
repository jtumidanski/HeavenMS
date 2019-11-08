package client.database.utility;

import client.database.data.PlayerDiseaseData;
import entity.PlayerDisease;

public class PlayerDiseaseTransformer implements SqlTransformer<PlayerDiseaseData, PlayerDisease> {
   @Override
   public PlayerDiseaseData transform(PlayerDisease resultSet) {
      return new PlayerDiseaseData(resultSet.getDisease(),
            resultSet.getMobSkillId(),
            resultSet.getMobSkillLevel(),
            resultSet.getLength());
   }
}
