package rest;

import java.util.List;

public class CanHoldAllAttributes implements AttributeResult {
   private List<CanHoldItem> addable;

   private List<CanHoldItem> removables;

   public List<CanHoldItem> getAddable() {
      return addable;
   }

   public void setAddable(List<CanHoldItem> addable) {
      this.addable = addable;
   }

   public List<CanHoldItem> getRemovables() {
      return removables;
   }

   public void setRemovables(List<CanHoldItem> removables) {
      this.removables = removables;
   }
}
