package rest.builders;

import java.util.ArrayList;
import java.util.List;

import builder.AttributeResultBuilder;
import builder.Builder;
import rest.CanHoldAllAttributes;
import rest.CanHoldItem;

public class CanHoldAllAttributesBuilder extends Builder<CanHoldAllAttributes, CanHoldAllAttributesBuilder>
      implements AttributeResultBuilder {
   private List<CanHoldItem> addables;

   private List<CanHoldItem> removables;

   public CanHoldAllAttributesBuilder() {
      addables = new ArrayList<>();
      removables = new ArrayList<>();
      add(attr -> attr.setAddable(addables));
      add(attr -> attr.setRemovables(removables));
   }

   @Override
   public CanHoldAllAttributes construct() {
      return new CanHoldAllAttributes();
   }

   @Override
   public CanHoldAllAttributesBuilder getThis() {
      return this;
   }

   public CanHoldAllAttributesBuilder addAddable(CanHoldItem item) {
      addables.add(item);
      return getThis();
   }

   public CanHoldAllAttributesBuilder addRemovable(CanHoldItem item) {
      removables.add(item);
      return getThis();
   }
}
