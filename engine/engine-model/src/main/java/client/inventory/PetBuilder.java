package client.inventory;

import java.awt.Point;

public class PetBuilder<R extends MaplePet, B extends PetBuilder<R, B>> extends ItemBuilder<R, B> {
   protected int uniqueId;

   protected String name;

   protected int closeness;

   protected byte level;

   protected int fullness;

   protected long lastFullnessDegrade;

   protected int fh;

   protected Point pos;

   protected int stance;

   protected boolean summoned;

   protected int petFlag;

   public PetBuilder(int id) {
      super(id);
   }

   public PetBuilder(R other) {
      super(other);
   }

   @Override
   public B getThis() {
      return (B) this;
   }

   @Override
   public R build() {
      return (R) new MaplePet(id, position, uniqueId, name, closeness, level, fullness, lastFullnessDegrade, fh, pos,
            stance, summoned, petFlag);
   }

   public PetBuilder<R, B> setUniqueId(int uniqueId) {
      this.uniqueId = uniqueId;
      return getThis();
   }

   public PetBuilder<R, B> setName(String name) {
      this.name = name;
      return getThis();
   }

   public PetBuilder<R, B> setCloseness(int closeness) {
      this.closeness = closeness;
      return getThis();
   }

   public PetBuilder<R, B> setLevel(byte level) {
      this.level = level;
      return getThis();
   }

   public PetBuilder<R, B> setFullness(int fullness) {
      this.fullness = fullness;
      return getThis();
   }

   public PetBuilder<R, B> setLastFullnessDegrade(long lastFullnessDegrade) {
      this.lastFullnessDegrade = lastFullnessDegrade;
      return getThis();
   }

   public PetBuilder<R, B> setFh(int fh) {
      this.fh = fh;
      return getThis();
   }

   public PetBuilder<R, B> setPos(Point pos) {
      this.pos = pos;
      return getThis();
   }

   public PetBuilder<R, B> setStance(int stance) {
      this.stance = stance;
      return getThis();
   }

   public PetBuilder<R, B> setSummoned(boolean summoned) {
      this.summoned = summoned;
      return getThis();
   }

   public PetBuilder<R, B> setPetFlag(int petFlag) {
      this.petFlag = petFlag;
      return getThis();
   }
}
