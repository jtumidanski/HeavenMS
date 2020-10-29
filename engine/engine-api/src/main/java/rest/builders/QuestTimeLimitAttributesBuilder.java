package rest.builders;

import builder.AttributeResultBuilder;
import builder.Builder;
import rest.QuestTimeLimitAttributes;

public class QuestTimeLimitAttributesBuilder extends Builder<QuestTimeLimitAttributes, QuestTimeLimitAttributesBuilder>
      implements AttributeResultBuilder {
   @Override
   public QuestTimeLimitAttributes construct() {
      return new QuestTimeLimitAttributes();
   }

   @Override
   public QuestTimeLimitAttributesBuilder getThis() {
      return this;
   }

   public QuestTimeLimitAttributesBuilder setLimit(Long limit) {
      return add(attr -> attr.setLimit(limit));
   }
}
