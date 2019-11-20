package constants.items;

public final class UseableCashItems {
   public enum Types {
      TELEPORTERS(CharacterTeleporters.TYPE),
      STAT_MODIFIERS(CharacterStatModifiers.TYPE),
      ITEM_AUGMENTERS(ItemAugmenters.TYPE),
      BROADCASTERS(Broadcasters.TYPE),
      KITES(Kites.TYPE),
      NOTES(Notes.TYPE),
      JUKE_BOXES(JukeBoxes.TYPE),
      MAP_EFFECTS(512),
      PET_NAME_CHANGE(517),
      ITEM_BAGS(ItemBags.TYPE),
      OWL_SEARCH(523),
      PET_CONSUME(524),
      CHARACTER_EFFECT(530),
      DUEY(533),
      CHALKBOARD(537),
      AVATAR_BROADCASTERS(539),
      CHARACTER_MODIFIERS(CharacterModifiers.TYPE),
      CHARACTER_CREATORS(CharacterCreators.TYPE),
      MIU_MIU(545),
      EXPIRATION_EXTENDERS(ExpirationExtenders.TYPE),
      SCISSORS_OF_KARMA(552),
      HAMMER(557),
      VEGA_SPELLS(VegasSpells.TYPE);

      private int value;

      Types(int value) {
         this.value = value;
      }

      public int getValue() {
         return value;
      }

      public static UseableCashItems.Types getByItemId(int itemId) {
         int itemType = itemId / 10000;
         if (itemType == CharacterTeleporters.TYPE) {
            return TELEPORTERS;
         } else if (itemType == CharacterStatModifiers.TYPE) {
            return STAT_MODIFIERS;
         } else if (itemType == ItemAugmenters.TYPE) {
            return ITEM_AUGMENTERS;
         } else if (itemType == Broadcasters.TYPE) {
            return BROADCASTERS;
         } else if (itemType == Kites.TYPE) {
            return KITES;
         } else if (itemType == Notes.TYPE) {
            return NOTES;
         } else if (itemType == JukeBoxes.TYPE) {
            return JUKE_BOXES;
         } else if (itemType == 512) {
            return MAP_EFFECTS;
         } else if (itemType == 517) {
            return PET_NAME_CHANGE;
         } else if (itemType == ItemBags.TYPE) {
            return ITEM_BAGS;
         } else if (itemType == 523) {
            return OWL_SEARCH;
         } else if (itemType == 524) {
            return PET_CONSUME;
         } else if (itemType == 530) {
            return CHARACTER_EFFECT;
         } else if (itemType == 533) {
            return DUEY;
         } else if (itemType == 537) {
            return CHALKBOARD;
         } else if (itemType == 539) {
            return AVATAR_BROADCASTERS;
         } else if (itemType == CharacterModifiers.TYPE) {
            return CHARACTER_MODIFIERS;
         } else if (itemType == CharacterCreators.TYPE) {
            return CHARACTER_CREATORS;
         } else if (itemType == 545) {
            return MIU_MIU;
         } else if (itemType == ExpirationExtenders.TYPE) {
            return EXPIRATION_EXTENDERS;
         } else if (itemType == 552) {
            return SCISSORS_OF_KARMA;
         } else if (itemType == 557) {
            return HAMMER;
         } else if (itemType == VegasSpells.TYPE) {
            return VEGA_SPELLS;
         }
         return null;
      }
   }

   public enum BroadcasterCategories {
      CHEAP_MEGAPHONE(0),
      MEGAPHONE(1),
      SUPER_MEGAPHONE(2),
      HEART_MEGAPHONE(3),
      SKULL_MEGAPHONE(4),
      MAPLE_TV(5),
      ITEM_MEGAPHONE(6),
      TRIPLE_MEGAPHONE(7);

      private int category;

      BroadcasterCategories(int category) {
         this.category = category;
      }

      public int getCategory() {
         return category;
      }

      public static BroadcasterCategories getByItemId(int itemId) {
         return getByValue(Broadcasters.getCategory(itemId));
      }

      public static BroadcasterCategories getByValue(int category) {
         switch (category) {
            case 0:
               return CHEAP_MEGAPHONE;
            case 1:
               return MEGAPHONE;
            case 2:
               return SUPER_MEGAPHONE;
            case 3:
               return HEART_MEGAPHONE;
            case 4:
               return SKULL_MEGAPHONE;
            case 5:
               return MAPLE_TV;
            case 6:
               return ITEM_MEGAPHONE;
            case 7:
               return TRIPLE_MEGAPHONE;
         }
         return null;
      }
   }

   public enum Broadcasters {
      CHEAP_MEGAPHONE(5070000), // NOT IMPLEMENTED CLIENT SIDE
      MEGAPHONE(5071000),
      SUPER_MEGAPHONE(5072000),
      HEART_MEGAPHONE(5073000), // NOT IMPLEMENTED CLIENT SIDE
      SKULL_MEGAPHONE(5074000), // NOT IMPLEMENTED CLIENT SIDE
      MAPLE_TV_MESSENGER(5075000),
      MAPLE_TV_STAR_MESSENGER(5075001),
      MAPLE_TV_HEART_MESSENGER(5075002),
      MAPLE_TV_MEGA_MESSENGER(5075003),
      MAPLE_TV_STAR_MEGA_MESSENGER(5075004),
      MAPLE_TV_HEART_MEGA_MESSENGER(5075005),
      ITEM_MEGAPHONE(5076000),
      TRIPLE_MEGAPHONE(5077000);

      private int itemId;

      public static final int TYPE = 507;

      Broadcasters(int itemId) {
         this.itemId = itemId;
      }

      public int getItemId() {
         return itemId;
      }

      public int getCategory() {
         return getCategory(itemId);
      }

      public static int getCategory(int itemId) {
         return (itemId / 1000) % 10;
      }

      public static boolean isMapleTvMegaMessenger(int itemId) {
         boolean mapleTv = BroadcasterCategories.getByItemId(itemId) == BroadcasterCategories.MAPLE_TV;
         return mapleTv && itemId % 10 >= 3;
      }
   }

   public enum ItemAugmenters {
      ITEM_TAG(5060000),
      ITEM_GUARD(5060001),
      INCUBATOR(5060002),
      ITEM_GUARD_7(5061000),
      ITEM_GUARD_30(5061001),
      ITEM_GUARD_90(5061002),
      ITEM_GUARD_365(5061003);

      public static int TYPE = 506;

      private int itemId;

      ItemAugmenters(int itemId) {
         this.itemId = itemId;
      }

      public boolean is(int itemId) {
         return this.itemId == itemId;
      }

      public static ItemAugmenters getByItemId(int itemId) {
         switch (itemId) {
            case 5060000:
               return ITEM_TAG;
            case 5060001:
               return ITEM_GUARD;
            case 5060002:
               return INCUBATOR;
            case 5061000:
               return ITEM_GUARD_7;
            case 5061001:
               return ITEM_GUARD_30;
            case 5061002:
               return ITEM_GUARD_90;
            case 5061003:
               return ITEM_GUARD_365;
         }
         return null;
      }
   }

   public enum CharacterCreators {
      EXTRA_SLOT(5430000),
      MAPLE_LIFE_A(5431000),
      MAPLE_LIFE_B(5432000);

      public static int TYPE = 543;

      private int itemId;

      CharacterCreators(int itemId) {
         this.itemId = itemId;
      }

      public boolean is(int itemId) {
         return this.itemId == itemId;
      }
   }

   public enum CharacterModifiers {
      NAME_CHANGE(5400000),
      WORLD_CHANGE(5401000);

      public static int TYPE = 540;

      private int itemId;

      CharacterModifiers(int itemId) {
         this.itemId = itemId;
      }

      public static CharacterModifiers getByItemId(int itemId) {
         switch (itemId) {
            case 5400000:
               return NAME_CHANGE;
            case 5401000:
               return WORLD_CHANGE;
         }
         return null;
      }
   }

   public enum CharacterTeleporters {
      TELEPORT_ROCK(5040000),
      TELEPORT_COKE(5040001),
      VIP_TELEPORT_ROCK(5041000);

      public static int TYPE = 504;

      private int itemId;

      CharacterTeleporters(int itemId) {
         this.itemId = itemId;
      }

      public static boolean isVip(int itemId) {
         return VIP_TELEPORT_ROCK == getByItemId(itemId);
      }

      public static CharacterTeleporters getByItemId(int itemId) {
         switch (itemId) {
            case 5040000:
               return TELEPORT_ROCK;
            case 5040001:
               return TELEPORT_COKE;
            case 5041000:
               return VIP_TELEPORT_ROCK;
         }
         return null;
      }
   }

   public enum CharacterStatModifiers {
      AP_RESET(5050000),
      SP_RESET_FIRST(5050001),
      SP_RESET_SECOND(5050002),
      SP_RESET_THIRD(5050003),
      SP_RESET_FOURTH(5050004);

      public static int TYPE = 505;

      private int itemId;

      CharacterStatModifiers(int itemId) {
         this.itemId = itemId;
      }

      public int getItemId() {
         return itemId;
      }

      public static boolean isAPReset(int itemId) {
         return itemId == AP_RESET.getItemId();
      }
   }

   public enum Kites {
      KOREAN_KITE(5080000),
      HEART_BALLOON(5080001),
      GRADUATION_BANNER(5080002),
      ADMISSION_BANNER(5080003);

      public static int TYPE = 508;

      private int itemId;

      Kites(int itemId) {
         this.itemId = itemId;
      }
   }

   public enum Notes {
      NOTE(5090000);

      public static int TYPE = 509;

      private int itemId;

      Notes(int itemId) {
         this.itemId = itemId;
      }
   }

   public enum JukeBoxes {
      CONGRATULATORY(5100000);

      public static int TYPE = 510;

      private int itemId;

      JukeBoxes(int itemId) {
         this.itemId = itemId;
      }
   }

   public enum ItemBags {
      BRONZE_MESO(5200000),
      SILVER_MESO(5200001),
      GOLD_MESO(5200002),
      SMALL_BALL_BOX(5201000), // NOT IMPLEMENTED CLIENT SIDE
      LARGE_BALL_BOX(5201001); // NOT IMPLEMENTED CLIENT SIDE

      public static int TYPE = 520;

      private int itemId;

      ItemBags(int itemId) {
         this.itemId = itemId;
      }
   }

   public enum ExpirationExtenders {
      MAGICAL_SANDGLASS_7(5500001),
      MAGICAL_SANDGLASS_20(5500002);

      public static int TYPE = 550;

      private int itemId;

      ExpirationExtenders(int itemId) {
         this.itemId = itemId;
      }
   }

   public enum VegasSpells {
      PERCENT_10(5610000),
      PERCENT_60(5610001);

      public static int TYPE = 561;

      private int itemId;

      VegasSpells(int itemId) {
         this.itemId = itemId;
      }
   }
}
