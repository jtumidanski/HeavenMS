package database.transformer;

import client.database.data.QuestProgressData;
import entity.quest.QuestProgress;
import transformer.SqlTransformer;

public class QuestProgressTransformer implements SqlTransformer<QuestProgressData, QuestProgress> {
   @Override
   public QuestProgressData transform(QuestProgress resultSet) {
      return new QuestProgressData(resultSet.getQuestStatusId(),
            resultSet.getProgressId(),
            resultSet.getProgress());
   }
}