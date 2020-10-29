package rest.builders;

import builder.AttributeResultBuilder;
import builder.Builder;
import rest.MessageAttributes;

public class MessageAttributesBuilder extends Builder<MessageAttributes, MessageAttributesBuilder>
      implements AttributeResultBuilder {
   @Override
   public MessageAttributes construct() {
      return new MessageAttributes();
   }

   @Override
   public MessageAttributesBuilder getThis() {
      return this;
   }

   public MessageAttributesBuilder setType(String type) {
      return add(attr -> attr.setType(type));
   }

   public MessageAttributesBuilder setToken(String token) {
      return add(attr -> attr.setToken(token));
   }

   public MessageAttributesBuilder addReplacement(String replacement) {
      return add(attr -> attr.getReplacements().add(replacement));
   }
}
