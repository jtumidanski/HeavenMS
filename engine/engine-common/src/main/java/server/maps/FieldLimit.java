package server.maps;

public enum FieldLimit {
   JUMP(0x01),
   MOVEMENT_SKILLS(0x02),
   SUMMON(0x04),
   DOOR(0x08),
   CANNOT_MIGRATE(0x10),    //change channel, town portal scroll, access cash shop, etc etc
   //NO_NOTES(0x20),
   CANNOT_VIP_ROCK(0x40),
   CANNOT_MINI_GAME(0x80),
   //SPECIFIC_PORTAL_SCROLL_LIMIT(0x100), // APQ and a couple quest maps have this
   CANNOT_USE_MOUNTS(0x200),
   //STAT_CHANGE_ITEM_CONSUME_LIMIT(0x400), // Monster carnival?
   //PARTY_BOSS_CHANGE_LIMIT(0x800), // Monster carnival?
   CANNOT_USE_POTION(0x1000),
   //WEDDING_INVITATION_LIMIT(0x2000), // No notes
   //CASH_WEATHER_CONSUME_LIMIT(0x4000),
   //NO_PET(0x8000), // Ariant colosseum-related?
   //ANTI_MACRO_LIMIT(0x10000), // No notes
   CANNOT_JUMP_DOWN(0x20000),
   //SUMMON_NPC_LIMIT(0x40000); // Seems to .. disable Rush if 0x2 is set

   //......... EVEN MORE LIMITS ............
   //SUMMON_NPC_LIMIT(0x40000),
   NO_EXP_DECREASE(0x80000),
   //NO_DAMAGE_ON_FALLING(0x100000),
   //PARCEL_OPEN_LIMIT(0x200000),
   DROP_LIMIT(0x400000);
   //ROCKET_BOOSTER_LIMIT(0x800000)     //lol we don't even have mechanics <3

   private long i;

   FieldLimit(long i) {
      this.i = i;
   }

   public long getValue() {
      return i;
   }

   public boolean check(int fieldLimit) {
      return (fieldLimit & i) == i;
   }
}
