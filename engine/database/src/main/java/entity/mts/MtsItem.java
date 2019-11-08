package entity.mts;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "mts_items")
public class MtsItem implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue
   private Integer id;

   @Column(nullable = false)
   private Integer tab;

   @Column(nullable = false)
   private Integer type;

   @Column(nullable = false)
   private Integer itemId;

   @Column(nullable = false)
   private Integer quantity;

   @Column(nullable = false)
   private Integer seller;

   @Column(nullable = false)
   private Integer price;

   @Column(nullable = false)
   private Integer bidIncrease;

   @Column(nullable = false)
   private Integer buyNow;

   @Column(nullable = false)
   private Integer position;

   @Column(nullable = false)
   private Integer upgradeSlots;

   @Column(nullable = false)
   private Integer level;

   @Column(nullable = false)
   private Integer itemLevel = 1;

   @Column(nullable = false)
   private Float itemExp;

   @Column(nullable = false)
   private Integer ringId = -1;

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
   private Integer isEquip;

   @Column(nullable = false)
   private String owner;

   @Column(nullable = false)
   private String sellerName;

   @Column(nullable = false)
   private String sellEnds;

   @Column(nullable = false)
   private Integer transfer;

   @Column(nullable = false)
   private Integer vicious;

   @Column(nullable = false)
   private Integer flag;

   @Column(nullable = false)
   private Long expiration = -1L;

   @Column(nullable = false)
   private String giftFrom;

   public MtsItem() {
   }

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public Integer getTab() {
      return tab;
   }

   public void setTab(Integer tab) {
      this.tab = tab;
   }

   public Integer getType() {
      return type;
   }

   public void setType(Integer type) {
      this.type = type;
   }

   public Integer getItemId() {
      return itemId;
   }

   public void setItemId(Integer itemId) {
      this.itemId = itemId;
   }

   public Integer getQuantity() {
      return quantity;
   }

   public void setQuantity(Integer quantity) {
      this.quantity = quantity;
   }

   public Integer getSeller() {
      return seller;
   }

   public void setSeller(Integer seller) {
      this.seller = seller;
   }

   public Integer getPrice() {
      return price;
   }

   public void setPrice(Integer price) {
      this.price = price;
   }

   public Integer getBidIncrease() {
      return bidIncrease;
   }

   public void setBidIncrease(Integer bidIncrease) {
      this.bidIncrease = bidIncrease;
   }

   public Integer getBuyNow() {
      return buyNow;
   }

   public void setBuyNow(Integer buyNow) {
      this.buyNow = buyNow;
   }

   public Integer getPosition() {
      return position;
   }

   public void setPosition(Integer position) {
      this.position = position;
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

   public Integer getIsEquip() {
      return isEquip;
   }

   public void setIsEquip(Integer isEquip) {
      this.isEquip = isEquip;
   }

   public String getOwner() {
      return owner;
   }

   public void setOwner(String owner) {
      this.owner = owner;
   }

   public String getSellerName() {
      return sellerName;
   }

   public void setSellerName(String sellerName) {
      this.sellerName = sellerName;
   }

   public String getSellEnds() {
      return sellEnds;
   }

   public void setSellEnds(String sellEnds) {
      this.sellEnds = sellEnds;
   }

   public Integer getTransfer() {
      return transfer;
   }

   public void setTransfer(Integer transfer) {
      this.transfer = transfer;
   }

   public Integer getVicious() {
      return vicious;
   }

   public void setVicious(Integer vicious) {
      this.vicious = vicious;
   }

   public Integer getFlag() {
      return flag;
   }

   public void setFlag(Integer flag) {
      this.flag = flag;
   }

   public Long getExpiration() {
      return expiration;
   }

   public void setExpiration(Long expiration) {
      this.expiration = expiration;
   }

   public String getGiftFrom() {
      return giftFrom;
   }

   public void setGiftFrom(String giftFrom) {
      this.giftFrom = giftFrom;
   }
}
