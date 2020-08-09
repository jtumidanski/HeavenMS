package client.inventory;

import java.awt.Point;
import java.util.List;

import server.movement.AbsoluteLifeMovement;
import server.movement.LifeMovement;
import server.movement.LifeMovementFragment;

public class MaplePet extends Item {
   private final int uniqueId;

   private final String name;

   private final int closeness;

   private final byte level;

   private final int fullness;

   private final long lastFullnessDegrade;

   private final int fh;

   private final Point pos;

   private final int stance;

   private final boolean summoned;

   private final int petFlag;

   public MaplePet(int id, short position, int uniqueId, String name, int closeness, byte level, int fullness, long lastFullnessDegrade, int fh, Point pos, int stance, boolean summoned, int petFlag) {
      super(id, position, (short) 1);
      this.uniqueId = uniqueId;
      this.name = name;
      this.closeness = closeness;
      this.level = level;
      this.fullness = fullness;
      this.lastFullnessDegrade = lastFullnessDegrade;
      this.fh = fh;
      this.pos = pos;
      this.stance = stance;
      this.summoned = summoned;
      this.petFlag = petFlag;
   }

   public int uniqueId() {
      return uniqueId;
   }

   public String name() {
      return name;
   }

   public int closeness() {
      return closeness;
   }

   public byte level() {
      return level;
   }

   public int fullness() {
      return fullness;
   }

   public long lastFullnessDegrade() {
      return lastFullnessDegrade;
   }

   public int fh() {
      return fh;
   }

   public Point pos() {
      return pos;
   }

   public int stance() {
      return stance;
   }

   public boolean summoned() {
      return summoned;
   }

   public int petFlag() {
      return petFlag;
   }

   public MaplePet updatePosition(List<LifeMovementFragment> movement) {
      return movement.stream()
            .filter(fragment -> fragment instanceof LifeMovement)
            .reduce(this,
                  (oldThis, move) -> {
                     if (move instanceof AbsoluteLifeMovement) {
                        return MaplePet.newBuilder(oldThis).setPos(move.position()).build();
                     }
                     return MaplePet.newBuilder(oldThis).setStance(((LifeMovement) move).newState()).build();
                  }, (a, b) -> b);
   }

   public static PetBuilder<? extends MaplePet, ? extends PetBuilder> newBuilder(int id) {
      return new PetBuilder<>(id);
   }

   public static PetBuilder<? extends MaplePet, ? extends PetBuilder> newBuilder(MaplePet other) {
      return new PetBuilder<>(other);
   }

   @Override
   protected PetBuilder<? extends MaplePet, ? extends PetBuilder> getBuilder() {
      return new PetBuilder<>(this);
   }

   public MaplePet updatePos(Point pos) {
      return getBuilder().getThis().setPos(pos).build();
   }

   public MaplePet isSummoned(boolean summoned) {
      return getBuilder().getThis().setSummoned(summoned).build();
   }

   public MaplePet feed(int fullness) {
      return getBuilder().getThis().setFullness(fullness).build();
   }

   public MaplePet gainCloseness(int closeness) {
      return getBuilder().getThis().setCloseness(closeness).build();
   }

   public MaplePet increaseLevel(byte amount) {
      return getBuilder().getThis().setLevel((byte) (level() + amount)).build();
   }

   public MaplePet degradeFullness(int fullness) {
      return getBuilder().getThis().setFullness(fullness).setLastFullnessDegrade(System.currentTimeMillis()).build();
   }

   public MaplePet setPetFlag(int petFlag) {
      return getBuilder().getThis().setPetFlag(petFlag).build();
   }
}
