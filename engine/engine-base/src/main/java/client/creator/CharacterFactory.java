package client.creator;

import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleJob;
import client.MapleSkinColor;
import client.Skill;
import client.SkillFactory;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.processor.CharacterProcessor;
import config.YamlConfig;
import constants.skills.Magician;
import constants.skills.Warrior;
import net.server.Server;
import server.MapleItemInformationProvider;
import server.events.RescueGaga;
import tools.LogType;
import tools.LoggerOriginator;
import tools.LoggerUtil;
import tools.PacketCreator;
import tools.packet.AddNewCharacter;
import tools.packet.message.YellowTip;

public class CharacterFactory {
   private static CharacterFactory instance;

   public static CharacterFactory getInstance() {
      if (instance == null) {
         instance = new CharacterFactory();
      }
      return instance;
   }

   private CharacterFactory() {
   }

   public int createBeginner(MapleClient c, String name, int face, int hair, int skin, int top, int bottom, int shoes, int weapon, int gender) {
      CharacterFactoryRecipe recipe = new CharacterFactoryRecipe(MapleJob.BEGINNER, 1, 10000, top, bottom, shoes, weapon);
      giveItem(recipe, 4161001, 1, MapleInventoryType.ETC);
      return createNewCharacter(c, name, face, hair, skin, gender, recipe);
   }

   public int createLegend(MapleClient c, String name, int face, int hair, int skin, int top, int bottom, int shoes, int weapon, int gender) {
      CharacterFactoryRecipe recipe = new CharacterFactoryRecipe(MapleJob.LEGEND, 1, 914000000, top, bottom, shoes, weapon);
      giveItem(recipe, 4161048, 1, MapleInventoryType.ETC);
      return createNewCharacter(c, name, face, hair, skin, gender, recipe);
   }

   public int createNoblesse(MapleClient c, String name, int face, int hair, int skin, int top, int bottom, int shoes, int weapon, int gender) {
      CharacterFactoryRecipe recipe = new CharacterFactoryRecipe(MapleJob.NOBLESSE, 1, 130030000, top, bottom, shoes, weapon);
      giveItem(recipe, 4161047, 1, MapleInventoryType.ETC);
      return createNewCharacter(c, name, face, hair, skin, gender, recipe);
   }

   public int createBowman(MapleClient c, String name, int face, int hair, int skin, int gender, int improveSp) {
      int[] equips = {1040067, 1041054, 1060056, 1061050, 1072081};
      int[] weapons = {1452005, 1462000};
      int[] startingHpMp = {797, 404};
      CharacterFactoryRecipe recipe = new CharacterFactoryRecipe(MapleJob.BOWMAN, 30, 100000000, equips[gender], equips[2 + gender], equips[4], weapons[0])
            .setDex(25)
            .setRemainingAp(133)
            .setRemainingSp(61)
            .setMaxHp(startingHpMp[0])
            .setMaxMp(startingHpMp[1])
            .setMeso(100000);
      for (int i = 1; i < weapons.length; i++) {
         giveEquipment(recipe, weapons[i]);
      }
      giveItem(recipe, 2000002, 100, MapleInventoryType.USE);
      giveItem(recipe, 2000003, 100, MapleInventoryType.USE);
      giveItem(recipe, 3010000, 1, MapleInventoryType.SETUP);
      return createNewCharacter(c, name, face, hair, skin, gender, recipe);
   }

   public int createMagician(MapleClient c, String name, int face, int hair, int skin, int gender, int improveSp) {
      int[] equips = {0, 1041041, 0, 1061034, 1072075};
      int[] weapons = {1372003, 1382017};
      int[] startingHpMp = {405, 729};
      int[] mpGain = {0, 40, 80, 118, 156, 194, 230, 266, 302, 336, 370};

      CharacterFactoryRecipe recipe = new CharacterFactoryRecipe(MapleJob.MAGICIAN, 30, 101000000, equips[gender], equips[2 + gender], equips[4], weapons[0])
            .setIntelligence(20)
            .setRemainingAp(138)
            .setRemainingSp(67)
            .setMaxMp(startingHpMp[0])
            .setMaxMp(startingHpMp[1] + mpGain[improveSp])
            .setMeso(100000);

      if (gender == 0) {
         giveEquipment(recipe, 1050003);
      }

      for (int i = 1; i < weapons.length; i++) {
         giveEquipment(recipe, weapons[i]);
      }

      giveItem(recipe, 2000001, 100, MapleInventoryType.USE);
      giveItem(recipe, 2000006, 100, MapleInventoryType.USE);
      giveItem(recipe, 3010000, 1, MapleInventoryType.SETUP);

      if (improveSp > 0) {
         improveSp += 5;
         recipe.increaseRemainingSp(-improveSp);

         int toUseSp = 5;
         Optional<Skill> improveMpRecovery = SkillFactory.getSkill(Magician.IMPROVED_MP_RECOVERY);
         if (improveMpRecovery.isPresent()) {
            recipe.addStartingSkillLevel(improveMpRecovery.get().getId(), toUseSp);
            improveSp -= toUseSp;
         }

         if (improveSp > 0) {
            Optional<Skill> improveMaxMpIncrease = SkillFactory.getSkill(Magician.IMPROVED_MAX_MP_INCREASE);
            if (improveMaxMpIncrease.isPresent()) {
               recipe.addStartingSkillLevel(improveMaxMpIncrease.get().getId(), improveSp);
            }
         }
      }
      return createNewCharacter(c, name, face, hair, skin, gender, recipe);
   }

   public int createPirate(MapleClient c, String name, int face, int hair, int skin, int gender, int improveSp) {
      int[] equips = {0, 0, 0, 0, 1072294};
      int[] weapons = {1482004, 1492004};
      int[] startingHpMp = {846, 503};
      CharacterFactoryRecipe recipe = new CharacterFactoryRecipe(MapleJob.PIRATE, 30, 120000000, equips[gender], equips[2 + gender], equips[4], weapons[0])
            .setDex(20)
            .setRemainingAp(138)
            .setRemainingSp(61)
            .setMaxHp(startingHpMp[0])
            .setMaxMp(startingHpMp[1])
            .setMeso(100000);

      giveEquipment(recipe, 1052107);

      for (int i = 1; i < weapons.length; i++) {
         giveEquipment(recipe, weapons[i]);
      }

      giveItem(recipe, 2330000, 800, MapleInventoryType.USE);

      giveItem(recipe, 2000002, 100, MapleInventoryType.USE);
      giveItem(recipe, 2000003, 100, MapleInventoryType.USE);
      giveItem(recipe, 3010000, 1, MapleInventoryType.SETUP);
      return createNewCharacter(c, name, face, hair, skin, gender, recipe);
   }

   public int createThief(MapleClient c, String name, int face, int hair, int skin, int gender, int improveSp) {
      int[] equips = {1040057, 1041047, 1060043, 1061043, 1072032};
      int[] weapons = {1472008, 1332012};
      int[] startingHpMp = {794, 407};
      CharacterFactoryRecipe recipe = new CharacterFactoryRecipe(MapleJob.THIEF, 30, 103000000, equips[gender], equips[2 + gender], equips[4], weapons[0])
            .setDex(25)
            .setRemainingAp(133)
            .setRemainingSp(61)
            .setMaxHp(startingHpMp[0])
            .setMaxMp(startingHpMp[1])
            .setMeso(100000);

      for (int i = 1; i < weapons.length; i++) {
         giveEquipment(recipe, weapons[i]);
      }

      giveItem(recipe, 2070000, 500, MapleInventoryType.USE);

      giveItem(recipe, 2000002, 100, MapleInventoryType.USE);
      giveItem(recipe, 2000003, 100, MapleInventoryType.USE);
      giveItem(recipe, 3010000, 1, MapleInventoryType.SETUP);
      return createNewCharacter(c, name, face, hair, skin, gender, recipe);
   }

   public int createWarrior(MapleClient c, String name, int face, int hair, int skin, int gender, int improveSp) {
      int[] equips = {1040021, 0, 1060016, 0, 1072039};
      int[] weapons = {1302008, 1442001, 1422001, 1312005};
      int[] startingHpMp = {905, 208};
      int[] hpGain = {0, 72, 144, 212, 280, 348, 412, 476, 540, 600, 660};
      CharacterFactoryRecipe recipe = new CharacterFactoryRecipe(MapleJob.WARRIOR, 30, 102000000, equips[gender], equips[2 + gender], equips[4], weapons[0])
            .setStr(35)
            .setRemainingAp(123)
            .setRemainingSp(61)
            .setMaxMp(startingHpMp[0] + hpGain[improveSp])
            .setMaxMp(startingHpMp[1])
            .setMeso(100000);

      if (gender == 1) {
         giveEquipment(recipe, 1051010);
      }

      for (int i = 1; i < weapons.length; i++) {
         giveEquipment(recipe, weapons[i]);
      }

      if (improveSp > 0) {
         improveSp += 5;
         recipe.increaseRemainingSp(-improveSp);

         int toUseSp = 5;
         Skill improveHpRec = SkillFactory.getSkill(Warrior.IMPROVED_HP_RECOVERY).orElseThrow();
         recipe.addStartingSkillLevel(improveHpRec.getId(), toUseSp);
         improveSp -= toUseSp;

         if (improveSp > 0) {
            Skill improveMaxHp = SkillFactory.getSkill(Warrior.IMPROVED_MAX_HP).orElseThrow();
            recipe.addStartingSkillLevel(improveMaxHp.getId(), improveSp);
         }
      }

      giveItem(recipe, 2000002, 100, MapleInventoryType.USE);
      giveItem(recipe, 2000003, 100, MapleInventoryType.USE);
      giveItem(recipe, 3010000, 1, MapleInventoryType.SETUP);
      return createNewCharacter(c, name, face, hair, skin, gender, recipe);
   }

   protected void giveItem(CharacterFactoryRecipe recipe, int itemId, int quantity, MapleInventoryType itemType) {
      recipe.addStartingItem(itemId, quantity, itemType);
   }

   protected void giveEquipment(CharacterFactoryRecipe recipe, int equipmentId) {
      Item nEquip = MapleItemInformationProvider.getInstance().getEquipById(equipmentId);
      recipe.addStartingEquipment(nEquip);
   }

   protected synchronized int createNewCharacter(MapleClient c, String name, int face, int hair, int skin, int gender, CharacterFactoryRecipe recipe) {
      if (YamlConfig.config.server.COLLECTIVE_CHARSLOT ? c.getAvailableCharacterSlots() <= 0 : c.getAvailableCharacterWorldSlots() <= 0) {
         return -3;
      }

      if (!CharacterProcessor.getInstance().canCreateChar(name)) {
         return -1;
      }

      MapleCharacter newCharacter = CharacterProcessor.getInstance().getDefault(c);
      newCharacter.setWorld(c.getWorld());
      newCharacter.setSkinColor(MapleSkinColor.getById(skin));
      newCharacter.setGender(gender);
      newCharacter.setName(name);
      newCharacter.setHair(hair);
      newCharacter.setFace(face);
      newCharacter.getEvents().put("rescueGaga", new RescueGaga(0));

      recipe.apply(newCharacter);

      if (!CharacterProcessor.getInstance().insertNewChar(newCharacter)) {
         return -2;
      }
      PacketCreator.announce(c, new AddNewCharacter(newCharacter));

      Server.getInstance().createCharacterEntry(newCharacter);
      Server.getInstance().broadcastGMMessage(c.getWorld(), PacketCreator.create(new YellowTip("[New Char]: " + c.getAccountName() + " has created a new character with IGN " + name)));
      LoggerUtil.printInfo(LoggerOriginator.ENGINE, LogType.CREATED_CHAR, c.getAccountName() + " created character with IGN " + name);
      return 0;
   }
}