package rest.builders;

import builder.AttributeResultBuilder;
import builder.Builder;
import rest.NpcCoolDownAttributes;

public class NpcCoolDownAttributesBuilder extends Builder<NpcCoolDownAttributes, NpcCoolDownAttributesBuilder>
      implements AttributeResultBuilder {
   @Override
   public NpcCoolDownAttributes construct() {
      return new NpcCoolDownAttributes();
   }

   @Override
   public NpcCoolDownAttributesBuilder getThis() {
      return this;
   }

   public NpcCoolDownAttributesBuilder setCoolDown(Long coolDown) {
      return add(attr -> attr.setCoolDown(coolDown));
   }
}
