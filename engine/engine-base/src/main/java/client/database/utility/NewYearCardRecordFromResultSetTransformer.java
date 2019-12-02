package client.database.utility;

import client.newyear.NewYearCardRecord;
import client.processor.NewYearCardProcessor;
import database.SqlTransformer;
import entity.NewYear;

public class NewYearCardRecordFromResultSetTransformer implements SqlTransformer<NewYearCardRecord, NewYear> {
   @Override
   public NewYearCardRecord transform(NewYear resultSet) {
      NewYearCardRecord newYearCard = new NewYearCardRecord(resultSet.getSenderId(), resultSet.getSenderName(),
            resultSet.getReceiverId(), resultSet.getReceiverName(), resultSet.getMessage());
      newYearCard.setExtraNewYearCardRecord(resultSet.getId(), resultSet.getSenderDiscard() == 1,
            resultSet.getReceiverDiscard() == 1, resultSet.getReceived() == 1, resultSet.getTimeSent(), resultSet.getTimerReceived());
      NewYearCardProcessor.getInstance().startNewYearCardTask(newYearCard);
      return newYearCard;
   }
}
