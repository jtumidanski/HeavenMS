package rest.builders;

import builder.AttributeResultBuilder;
import builder.Builder;
import rest.QuestFinishAttributes;

public class QuestFinishAttributesBuilder extends Builder<QuestFinishAttributes, QuestFinishAttributesBuilder>
      implements AttributeResultBuilder {
   @Override
   public QuestFinishAttributes construct() {
      return new QuestFinishAttributes();
   }

   @Override
   public QuestFinishAttributesBuilder getThis() {
      return this;
   }

   public QuestFinishAttributesBuilder setNpcId(Integer npcId) {
      return add(attr -> attr.setNpcId(npcId));
   }

   public QuestFinishAttributesBuilder setNextQuestId(Short nextQuestId) {
      return add(attr -> attr.setNextQuestId(nextQuestId));
   }
}
