package client;

public enum MapleAbnormalStatus {
   NULL(0x0),
   SLOW(0x1, 126),
   SEDUCE(0x80, 128),
   FISHABLE(0x100),
   ZOMBIFY(0x4000),
   CONFUSE(0x80000, 132),
   STUN(0x2000000000000L, 123),
   POISON(0x4000000000000L, 125),
   SEAL(0x8000000000000L, 120),
   DARKNESS(0x10000000000000L, 121),
   WEAKEN(0x4000000000000000L, 122),
   CURSE(0x8000000000000000L, 124);

   private long i;
   private boolean first;
   private int mobSkill;

   MapleAbnormalStatus(long i) {
      this(i, false, 0);
   }

   MapleAbnormalStatus(long i, int skill) {
      this(i, false, skill);
   }

   MapleAbnormalStatus(long i, boolean first, int skill) {
      this.i = i;
      this.first = first;
      this.mobSkill = skill;
   }

   public static MapleAbnormalStatus ordinal(int ord) {
      try {
         return MapleAbnormalStatus.values()[ord];
      } catch (IndexOutOfBoundsException io) {
         return NULL;
      }
   }

   public static final MapleAbnormalStatus[] CPQ_DISEASES = {MapleAbnormalStatus.SLOW, MapleAbnormalStatus.SEDUCE, MapleAbnormalStatus.STUN, MapleAbnormalStatus.POISON,
         MapleAbnormalStatus.SEAL, MapleAbnormalStatus.DARKNESS, MapleAbnormalStatus.WEAKEN, MapleAbnormalStatus.CURSE};

   public static MapleAbnormalStatus getRandom() {
      MapleAbnormalStatus[] diseases = CPQ_DISEASES;
      return diseases[(int) (Math.random() * diseases.length)];
   }

   public static MapleAbnormalStatus getBySkill(final int skill) {
      for (MapleAbnormalStatus d : MapleAbnormalStatus.values()) {
         if (d.getDisease() == skill && d.getDisease() != 0) {
            return d;
         }
      }
      return null;
   }

   public long getValue() {
      return i;
   }

   public boolean isFirst() {
      return first;
   }

   public int getDisease() {
      return mobSkill;
   }

}