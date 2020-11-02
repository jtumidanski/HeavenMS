package rest.builders;

import builder.AttributeResultBuilder;
import builder.Builder;
import rest.NpcConversationAttributes;
import rest.NpcConversationType;

public class NpcConversationAttributesBuilder extends Builder<NpcConversationAttributes, NpcConversationAttributesBuilder>
      implements AttributeResultBuilder {
   @Override
   public NpcConversationAttributes construct() {
      return new NpcConversationAttributes();
   }

   @Override
   public NpcConversationAttributesBuilder getThis() {
      return this;
   }

   public NpcConversationAttributesBuilder setType(NpcConversationType type) {
      return add(attr -> attr.setType(type));
   }

   public NpcConversationAttributesBuilder setToken(String token) {
      return add(attr -> attr.setToken(token));
   }
}
