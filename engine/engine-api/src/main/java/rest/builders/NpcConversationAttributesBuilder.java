package rest.builders;

import java.util.List;

import builder.AttributeResultBuilder;
import builder.Builder;
import rest.NpcConversationAttributes;
import rest.NpcConversationType;

public class NpcConversationAttributesBuilder extends Builder<NpcConversationAttributes, NpcConversationAttributesBuilder>
      implements AttributeResultBuilder {
   public NpcConversationAttributesBuilder() {
      setSpeaker((byte) 0);
   }

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

   public NpcConversationAttributesBuilder setSpeaker(Byte speaker) {
      return add(attr -> attr.setSpeaker(speaker));
   }

   public NpcConversationAttributesBuilder setToken(String token) {
      return add(attr -> attr.setToken(token));
   }

   public NpcConversationAttributesBuilder setArguments(List<String> arguments) {
      return add(attr -> attr.setArguments(arguments));
   }

   public NpcConversationAttributesBuilder setDef(Integer def) {
      return add(attr -> attr.setDef(def));
   }

   public NpcConversationAttributesBuilder setMin(Integer min) {
      return add(attr -> attr.setMin(min));
   }

   public NpcConversationAttributesBuilder setMax(Integer max) {
      return add(attr -> attr.setMax(max));
   }
}
