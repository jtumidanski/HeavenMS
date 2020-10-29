package rest.builders;

import builder.AttributeResultBuilder;
import builder.Builder;
import rest.QuestUpdateAttributes;

public class QuestUpdateAttributesBuilder extends Builder<QuestUpdateAttributes, QuestUpdateAttributesBuilder>
      implements AttributeResultBuilder {
   @Override
   public QuestUpdateAttributes construct() {
      return new QuestUpdateAttributes();
   }

   @Override
   public QuestUpdateAttributesBuilder getThis() {
      return this;
   }

   public QuestUpdateAttributesBuilder setQuestId(int questId) {
      return add(attr -> attr.setQuestId(questId));
   }

   public QuestUpdateAttributesBuilder setQuestStatusId(int questStatusId) {
      return add(attr -> attr.setQuestStatusId(questStatusId));
   }

   public QuestUpdateAttributesBuilder setInfoNumber(int infoNumber) {
      return add(attr -> attr.setInfoNumber(infoNumber));
   }

   public QuestUpdateAttributesBuilder setProgress(String progress) {
      return add(attr -> attr.setProgress(progress));
   }

   public QuestUpdateAttributesBuilder setDelayType(String delayType) {
      return add(attr -> attr.setDelayType(delayType));
   }

   public QuestUpdateAttributesBuilder setInfoUpdate(boolean infoUpdate) {
      return add(attr -> attr.setInfoUpdate(infoUpdate));
   }

   public QuestUpdateAttributesBuilder setNpcId(int npcId) {
      return add(attr -> attr.setNpcId(npcId));
   }

   public QuestUpdateAttributesBuilder setCompletionTime(Long completionTime) {
      return add(attr -> attr.setCompletionTime(completionTime));
   }
}
