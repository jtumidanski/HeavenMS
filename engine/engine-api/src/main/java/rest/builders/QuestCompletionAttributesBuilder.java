package rest.builders;

import builder.AttributeResultBuilder;
import builder.Builder;
import rest.QuestCompletionAttributes;

public class QuestCompletionAttributesBuilder extends Builder<QuestCompletionAttributes, QuestCompletionAttributesBuilder>
      implements AttributeResultBuilder {
   @Override
   public QuestCompletionAttributes construct() {
      return new QuestCompletionAttributes();
   }

   @Override
   public QuestCompletionAttributesBuilder getThis() {
      return this;
   }

   public QuestCompletionAttributesBuilder setQuestId(Integer questId) {
      return add(attr -> attr.setQuestId(questId));
   }
}
