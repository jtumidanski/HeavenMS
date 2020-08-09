package client.database.utility;

import client.newyear.NewYearCardRecord;
import client.processor.NewYearCardProcessor;
import entity.NewYear;
import transformer.SqlTransformer;

public class NewYearCardRecordFromResultSetTransformer implements SqlTransformer<NewYearCardRecord, NewYear> {
   @Override
   public NewYearCardRecord transform(NewYear resultSet) {
      NewYearCardRecord newYearCard = new NewYearCardRecord(resultSet.getId(), resultSet.getSenderId(),
            resultSet.getSenderName(), resultSet.getReceiverId(), resultSet.getReceiverName(), resultSet.getMessage(),
            resultSet.getTimeSent(), resultSet.getTimerReceived(), resultSet.getSenderDiscard() == 1,
            resultSet.getReceiverDiscard() == 1, resultSet.getReceived() == 1,
            null);
      NewYearCardProcessor.getInstance().startNewYearCardTask(newYearCard);
      return newYearCard;
   }
}
