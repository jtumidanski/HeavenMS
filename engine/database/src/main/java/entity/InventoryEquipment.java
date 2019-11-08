package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = "inventoryequipment", indexes = {
      @Index(name = "inventoryItemId", columnList = "inventoryItemId")
})
public class InventoryEquipment implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue
   private Integer inventoryEquipmentId;

   @Column(nullable = false)
   private Integer inventoryItemId;

   @Column(nullable = false)
   private Integer upgradeSlots;

   @Column(nullable = false)
   private Integer level;

   @Column(nullable = false)
   private Integer str;

   @Column(nullable = false)
   private Integer dex;

   @Column(nullable = false)
   private Integer intelligence;

   @Column(nullable = false)
   private Integer luk;

   @Column(nullable = false)
   private Integer hp;

   @Column(nullable = false)
   private Integer mp;

   @Column(nullable = false)
   private Integer watk;

   @Column(nullable = false)
   private Integer matk;

   @Column(nullable = false)
   private Integer wdef;

   @Column(nullable = false)
   private Integer mdef;

   @Column(nullable = false)
   private Integer acc;

   @Column(nullable = false)
   private Integer avoid;

   @Column(nullable = false)
   private Integer hands;

   @Column(nullable = false)
   private Integer speed;

   @Column(nullable = false)
   private Integer jump;

   @Column(nullable = false)
   private Integer locked;

   @Column(nullable = false)
   private Integer vicious;

   @Column(nullable = false)
   private Integer itemLevel;

   @Column(nullable = false)
   private Float itemExp;

   @Column(nullable = false)
   private Integer ringId;

   public InventoryEquipment() {
   }

   public Integer getInventoryEquipmentId() {
      return inventoryEquipmentId;
   }

   public void setInventoryEquipmentId(Integer inventoryEquipmentId) {
      this.inventoryEquipmentId = inventoryEquipmentId;
   }

   public Integer getInventoryItemId() {
      return inventoryItemId;
   }

   public void setInventoryItemId(Integer inventoryItemId) {
      this.inventoryItemId = inventoryItemId;
   }

   public Integer getUpgradeSlots() {
      return upgradeSlots;
   }

   public void setUpgradeSlots(Integer upgradeSlots) {
      this.upgradeSlots = upgradeSlots;
   }

   public Integer getLevel() {
      return level;
   }

   public void setLevel(Integer level) {
      this.level = level;
   }

   public Integer getStr() {
      return str;
   }

   public void setStr(Integer str) {
      this.str = str;
   }

   public Integer getDex() {
      return dex;
   }

   public void setDex(Integer dex) {
      this.dex = dex;
   }

   public Integer getIntelligence() {
      return intelligence;
   }

   public void setIntelligence(Integer intelligence) {
      this.intelligence = intelligence;
   }

   public Integer getLuk() {
      return luk;
   }

   public void setLuk(Integer luk) {
      this.luk = luk;
   }

   public Integer getHp() {
      return hp;
   }

   public void setHp(Integer hp) {
      this.hp = hp;
   }

   public Integer getMp() {
      return mp;
   }

   public void setMp(Integer mp) {
      this.mp = mp;
   }

   public Integer getWatk() {
      return watk;
   }

   public void setWatk(Integer watk) {
      this.watk = watk;
   }

   public Integer getMatk() {
      return matk;
   }

   public void setMatk(Integer matk) {
      this.matk = matk;
   }

   public Integer getWdef() {
      return wdef;
   }

   public void setWdef(Integer wdef) {
      this.wdef = wdef;
   }

   public Integer getMdef() {
      return mdef;
   }

   public void setMdef(Integer mdef) {
      this.mdef = mdef;
   }

   public Integer getAcc() {
      return acc;
   }

   public void setAcc(Integer acc) {
      this.acc = acc;
   }

   public Integer getAvoid() {
      return avoid;
   }

   public void setAvoid(Integer avoid) {
      this.avoid = avoid;
   }

   public Integer getHands() {
      return hands;
   }

   public void setHands(Integer hands) {
      this.hands = hands;
   }

   public Integer getSpeed() {
      return speed;
   }

   public void setSpeed(Integer speed) {
      this.speed = speed;
   }

   public Integer getJump() {
      return jump;
   }

   public void setJump(Integer jump) {
      this.jump = jump;
   }

   public Integer getLocked() {
      return locked;
   }

   public void setLocked(Integer locked) {
      this.locked = locked;
   }

   public Integer getVicious() {
      return vicious;
   }

   public void setVicious(Integer vicious) {
      this.vicious = vicious;
   }

   public Integer getItemLevel() {
      return itemLevel;
   }

   public void setItemLevel(Integer itemLevel) {
      this.itemLevel = itemLevel;
   }

   public Float getItemExp() {
      return itemExp;
   }

   public void setItemExp(Float itemExp) {
      this.itemExp = itemExp;
   }

   public Integer getRingId() {
      return ringId;
   }

   public void setRingId(Integer ringId) {
      this.ringId = ringId;
   }
}
