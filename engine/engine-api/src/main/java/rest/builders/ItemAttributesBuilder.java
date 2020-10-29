package rest.builders;

import builder.AttributeResultBuilder;
import builder.Builder;
import rest.ItemAttributes;

public class ItemAttributesBuilder extends Builder<ItemAttributes, ItemAttributesBuilder> implements AttributeResultBuilder {
   @Override
   public ItemAttributes construct() {
      return new ItemAttributes();
   }

   @Override
   public ItemAttributesBuilder getThis() {
      return this;
   }

   public ItemAttributesBuilder setName(String name) {
      return add(attr -> attr.setName(name));
   }

   public ItemAttributesBuilder setQuestItem(Boolean questItem) {
      return add(attr -> attr.setQuestItem(questItem));
   }

   public ItemAttributesBuilder setPickupRestricted(Boolean pickupRestricted) {
      return add(attr -> attr.setPickupRestricted(pickupRestricted));
   }
}
