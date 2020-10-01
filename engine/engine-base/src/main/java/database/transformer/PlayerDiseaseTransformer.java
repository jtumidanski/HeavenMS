package database.transformer;

import client.database.data.PlayerDiseaseData;
import entity.PlayerDisease;
import transformer.SqlTransformer;

public class PlayerDiseaseTransformer implements SqlTransformer<PlayerDiseaseData, PlayerDisease> {
   @Override
   public PlayerDiseaseData transform(PlayerDisease resultSet) {
      return new PlayerDiseaseData(resultSet.getDisease(),
            resultSet.getMobSkillId(),
            resultSet.getMobSkillLevel(),
            resultSet.getLength());
   }
}
