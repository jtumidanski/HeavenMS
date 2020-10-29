package rest.builders;

import builder.AttributeResultBuilder;
import builder.Builder;
import rest.CheckSpaceAttributes;

public class CheckSpaceAttributesBuilder extends Builder<CheckSpaceAttributes, CheckSpaceAttributesBuilder>
      implements AttributeResultBuilder {
   @Override
   public CheckSpaceAttributes construct() {
      return new CheckSpaceAttributes();
   }

   @Override
   public CheckSpaceAttributesBuilder getThis() {
      return this;
   }

   public CheckSpaceAttributesBuilder setItemId(Integer itemId) {
      return add(attr -> attr.setItemId(itemId));
   }

   public CheckSpaceAttributesBuilder setQuantity(Integer quantity) {
      return add(attr -> attr.setQuantity(quantity));
   }

   public CheckSpaceAttributesBuilder setOwner(String owner) {
      return add(attr -> attr.setOwner(owner));
   }

   public CheckSpaceAttributesBuilder setUsedSlots(Integer usedSlots) {
      return add(attr -> attr.setUsedSlots(usedSlots));
   }

   public CheckSpaceAttributesBuilder setUseProofInventory(Boolean useProofInventory) {
      return add(attr -> attr.setUseProofInventory(useProofInventory));
   }
}
