package client.database.utility;

import client.database.data.FamilyData;
import entity.family.FamilyCharacter;

public class FamilyDataFromResultSetTransformer implements SqlTransformer<FamilyData, FamilyCharacter> {
   @Override
   public FamilyData transform(FamilyCharacter resultSet) {
      return new FamilyData(
            resultSet.getCharacterId(),
            resultSet.getFamilyId(),
            resultSet.getSeniorId(),
            resultSet.getReputation(),
            resultSet.getTodaysRep(),
            resultSet.getTotalReputation(),
            resultSet.getRepToSenior(),
            resultSet.getPrecepts());
   }
}
