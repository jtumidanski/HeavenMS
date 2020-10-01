package database.transformer;

import client.database.data.QuestData;
import entity.quest.QuestStatus;
import transformer.SqlTransformer;

public class QuestStatusTransformer implements SqlTransformer<QuestData, QuestStatus> {
   @Override
   public QuestData transform(QuestStatus resultSet) {
      return new QuestData(resultSet.getQuest().shortValue(),
            resultSet.getStatus(),
            resultSet.getTime(),
            resultSet.getExpires(),
            resultSet.getForfeited(),
            resultSet.getCompleted(),
            resultSet.getQuestStatusId());
   }
}
