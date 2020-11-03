package rest.builders;

import builder.AttributeResultBuilder;
import builder.Builder;
import rest.FamilyAttributes;

public class FamilyAttributesBuilder extends Builder<FamilyAttributes, FamilyAttributesBuilder> implements AttributeResultBuilder {
   @Override
   public FamilyAttributes construct() {
      return new FamilyAttributes();
   }

   @Override
   public FamilyAttributesBuilder getThis() {
      return this;
   }

   public FamilyAttributesBuilder setJuniorCount(Integer juniorCount) {
      return add(attr -> attr.setJuniorCount(juniorCount));
   }
}
