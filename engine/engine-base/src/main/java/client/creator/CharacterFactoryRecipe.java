package client.creator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import client.MapleCharacter;
import client.SkillFactory;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventory;
import config.YamlConfig;
import constants.MapleInventoryType;
import constants.MapleJob;
import constants.game.GameConstants;
import server.MapleItemInformationProvider;
import tools.Pair;

public class CharacterFactoryRecipe {
   private Integer str;

   private Integer dex;

   private Integer intelligence;

   private Integer luk;

   private Integer maxHp;

   private Integer maxMp;

   private Integer remainingAp;

   private Integer remainingSp;

   private Integer meso;

   private MapleJob job;

   private Integer level;

   private Integer map;

   private Integer top;

   private Integer bottom;

   private Integer shoes;

   private Integer weapon;

   private List<Pair<Integer, Integer>> skills;

   private List<Pair<Item, MapleInventoryType>> itemsWithType;

   private Map<MapleInventoryType, AtomicInteger> runningTypePosition;

   protected CharacterFactoryRecipe() {
      if (!YamlConfig.config.server.USE_STARTING_AP_4) {
         if (YamlConfig.config.server.USE_AUTOASSIGN_STARTERS_AP) {
            str = 12;
            dex = 5;
            intelligence = 4;
            luk = 4;
            remainingAp = 0;
         } else {
            str = 4;
            dex = 4;
            intelligence = 4;
            luk = 4;
            remainingAp = 9;
         }
      } else {
         str = 4;
         dex = 4;
         intelligence = 4;
         luk = 4;
         remainingAp = 0;
      }

      maxHp = 50;
      maxMp = 5;
      remainingSp = 0;
      meso = 0;
      skills = new ArrayList<>();
      itemsWithType = new ArrayList<>();
      runningTypePosition = new HashMap<>();
   }

   public CharacterFactoryRecipe(MapleJob job, Integer level, Integer map, Integer top, Integer bottom, Integer shoes,
                                 Integer weapon) {
      this();
      this.job = job;
      this.level = level;
      this.map = map;
      this.top = top;
      this.bottom = bottom;
      this.shoes = shoes;
      this.weapon = weapon;
   }

   public void addStartingSkillLevel(Integer skillId, Integer level) {
      this.skills.add(new Pair<>(skillId, level));
   }

   public void addStartingEquipment(Item item) {
      itemsWithType.add(new Pair<>(item, MapleInventoryType.EQUIP));
   }

   public void addStartingItem(int itemId, int quantity, MapleInventoryType itemType) {
      AtomicInteger p = runningTypePosition.get(itemType);
      if (p == null) {
         p = new AtomicInteger(0);
         runningTypePosition.put(itemType, p);
      }

      itemsWithType.add(new Pair<>(new Item(itemId, (short) p.getAndIncrement(), (short) quantity), itemType));
   }

   public List<Pair<Integer, Integer>> getStartingSkillLevel() {
      return skills;
   }

   public List<Pair<Item, MapleInventoryType>> getStartingItems() {
      return itemsWithType;
   }

   public CharacterFactoryRecipe setStr(Integer str) {
      this.str = str;
      return this;
   }

   public CharacterFactoryRecipe setDex(Integer dex) {
      this.dex = dex;
      return this;
   }

   public CharacterFactoryRecipe setIntelligence(Integer intelligence) {
      this.intelligence = intelligence;
      return this;
   }

   public CharacterFactoryRecipe setLuk(Integer luk) {
      this.luk = luk;
      return this;
   }

   public CharacterFactoryRecipe setMaxHp(Integer maxHp) {
      this.maxHp = maxHp;
      return this;
   }

   public CharacterFactoryRecipe setMaxMp(Integer maxMp) {
      this.maxMp = maxMp;
      return this;
   }

   public CharacterFactoryRecipe setRemainingAp(Integer remainingAp) {
      this.remainingAp = remainingAp;
      return this;
   }

   public CharacterFactoryRecipe setRemainingSp(Integer remainingSp) {
      this.remainingSp = remainingSp;
      return this;
   }

   public CharacterFactoryRecipe increaseRemainingSp(Integer amount) {
      this.remainingSp += amount;
      return this;
   }

   public CharacterFactoryRecipe setMeso(Integer meso) {
      this.meso = meso;
      return this;
   }

   public void apply(MapleCharacter character) {

      character.init(str, dex, intelligence, luk, maxHp, maxMp, meso);
      character.setLevel(level);
      character.setJob(job);
      character.setMapId(map);
      character.setMaxHp(maxHp);
      character.setMaxMp(maxMp);
      character.setLevel(level);
      character.setRemainingAp(remainingAp);
      character.setRemainingSp(GameConstants.getSkillBook(character.getJob().getId()), remainingSp);

      MapleInventory equipped = character.getInventory(MapleInventoryType.EQUIPPED);
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

      if (top > 0) {
         Equip eq_top = Equip.newBuilder(ii.getEquipById(top)).setPosition((byte) -5).build();
         equipped.addItemFromDB(eq_top);
      }

      if (bottom > 0) {
         Equip eq_bottom = Equip.newBuilder(ii.getEquipById(bottom)).setPosition((byte) -6).build();
         equipped.addItemFromDB(eq_bottom);
      }

      if (shoes > 0) {
         Equip eq_shoes = Equip.newBuilder(ii.getEquipById(shoes)).setPosition((byte) -7).build();
         equipped.addItemFromDB(eq_shoes);
      }

      if (weapon > 0) {
         Equip eq_weapon = Equip.newBuilder(ii.getEquipById(weapon)).setPosition((byte) -11).build();
         equipped.addItemFromDB(eq_weapon.copy());
      }

      skills.forEach(entry -> SkillFactory.getSkill(entry.getLeft())
            .ifPresent(skill -> character.changeSkillLevel(skill, entry.getRight().byteValue(), skill.getMaxLevel(), -1)));

      itemsWithType.forEach(entry -> character.getInventory(entry.getRight()).addItem(entry.getLeft()));
   }
}
