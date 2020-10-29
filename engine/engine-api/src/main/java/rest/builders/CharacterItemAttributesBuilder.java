package rest.builders;

import builder.AttributeResultBuilder;
import builder.Builder;
import rest.CharacterItemAttributes;

public class CharacterItemAttributesBuilder extends Builder<CharacterItemAttributes, CharacterItemAttributesBuilder> implements AttributeResultBuilder {
   @Override
   public CharacterItemAttributes construct() {
      return new CharacterItemAttributes();
   }

   @Override
   protected CharacterItemAttributesBuilder getThis() {
      return this;
   }

   public CharacterItemAttributesBuilder setItemId(int itemId) {
      return add(attr -> attr.setItemId(itemId));
   }

   public CharacterItemAttributesBuilder setQuantity(short quantity) {
      return add(attr -> attr.setQuantity(quantity));
   }

   public CharacterItemAttributesBuilder setOwner(String owner) {
      return add(attr -> attr.setOwner(owner));
   }

   public CharacterItemAttributesBuilder setPetId(Integer petId) {
      return add(attr -> attr.setPetId(petId));
   }

   public CharacterItemAttributesBuilder setExpiration(Long expiration) {
      return add(attr -> attr.setExpiration(expiration));
   }
}
