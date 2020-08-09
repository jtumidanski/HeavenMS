package client.inventory;

import java.util.concurrent.atomic.AtomicInteger;

import constants.inventory.ItemConstants;

public class Item implements Comparable<Item> {
   private final int id;

   private final short position;

   private final short quantity;

   private final MaplePet pet;

   private final int petId;

   private final short flag;

   private final int sn;

   private final String giftFrom;

   private final String owner;

   private final long expiration;

   private AtomicInteger runningCashId = new AtomicInteger(777000000);

   private int cashId = 0;

   public Item(int id, short position, short quantity) {
      this(id, position, quantity, null, -1);
   }

   public Item(int id, short position, short quantity, MaplePet pet, int petId) {
      this(id, position, quantity, pet, petId, (short) 0, 0, "", "", -1);
   }

   public Item(int id, short position, short quantity, MaplePet pet, int petId, short flag, int sn, String giftFrom, String owner, long expiration) {
      this.id = id;
      this.position = position;
      this.quantity = quantity;
      this.pet = pet;
      this.petId = petId;
      this.flag = flag;
      this.sn = sn;
      this.giftFrom = giftFrom;
      this.owner = owner;
      this.expiration = expiration;
   }

   public int id() {
      return id;
   }

   public short position() {
      return position;
   }

   public short quantity() {
      return quantity;
   }

   public MaplePet pet() {
      return pet;
   }

   public int petId() {
      return petId;
   }

   public short flag() {
      return flag;
   }

   public int sn() {
      return sn;
   }

   public String giftFrom() {
      return giftFrom;
   }

   public String owner() {
      return owner;
   }

   public long expiration() {
      return expiration;
   }

   public int cashId() {
      if (cashId == 0) {
         cashId = runningCashId.getAndIncrement();
      }
      return cashId;
   }

   public MapleInventoryType inventoryType() {
      return ItemConstants.getInventoryType(id);
   }

   public Byte itemType() {
      if (petId > -1) {
         return 3;
      }
      return 2;
   }

   @Override
   public String toString() {
      return "Item: " + id + " quantity: " + quantity;
   }

   @Override
   public int compareTo(Item o) {
      if (this.id < o.id()) {
         return -1;
      } else if (this.id > o.id()) {
         return 1;
      }
      return 0;
   }

   public static ItemBuilder<? extends Item, ? extends ItemBuilder> newBuilder(int id) {
      return new ItemBuilder<>(id);
   }

   public static ItemBuilder<? extends Item, ? extends ItemBuilder> newBuilder(Item other) {
      return new ItemBuilder<>(other);
   }

   protected ItemBuilder<? extends Item, ? extends ItemBuilder> getBuilder() {
      return new ItemBuilder<>(this);
   }

   public Item copy() {
      return getBuilder().build();
   }

   public Item updatePosition(short position) {
      return getBuilder().setPosition(position).build();
   }

   public Item updateQuantity(short quantity) {
      return getBuilder().setQuantity(quantity).build();
   }

   public Item setFlag(short flag) {
      return getBuilder().setFlag(flag).build();
   }

   public Item expiration(long expiration) {
      if (!ItemConstants.isPermanentItem(id)) {
         return getBuilder().setExpiration(expiration).build();
      } else {
         if (ItemConstants.isPet(id)) {
            return getBuilder().setExpiration(Long.MAX_VALUE).build();
         } else {
            return getBuilder().setExpiration(-1).build();
         }
      }
   }

   public Item setPosition(short dst) {
      return getBuilder().setPosition(dst).build();
   }

   public Item setQuantity(short quantity) {
      return getBuilder().setQuantity(quantity).build();
   }

   public Item setSn(int sn) {
      return getBuilder().setSn(sn).build();
   }
}
