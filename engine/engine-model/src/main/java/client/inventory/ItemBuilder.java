package client.inventory;

public class ItemBuilder<T extends Item, U extends ItemBuilder<T, U>> {
   protected int id;

   protected short position;

   protected short quantity;

   protected MaplePet pet;

   protected int petId;

   protected short flag;

   protected int sn;

   protected String giftFrom;

   protected String owner;

   protected long expiration;

   public ItemBuilder(int id) {
      this.id = id;
   }

   public ItemBuilder(int id, short position, short quantity, MaplePet pet, int petId) {
      this(id);
      int adjustedPetId = petId;

      if (petId > -1) {
         if (pet == null) {
            adjustedPetId = -1;
         }
      }

      this.position = position;
      this.quantity = quantity;
      this.pet = pet;
      this.petId = adjustedPetId;
   }

   public ItemBuilder(T other) {
      this(other.id());
      this.position = other.position();
      this.quantity = other.quantity();
      this.pet = other.pet();
      this.petId = other.petId();
      this.flag = other.flag();
      this.sn = other.sn();
      this.giftFrom = other.giftFrom();
      this.owner = other.owner();
      this.expiration = other.expiration();
   }

   public T build() {
      return (T) new Item(id, position, quantity, pet, petId, flag, sn, giftFrom, owner, expiration);
   }

   public U getThis() {
      return (U) this;
   }

   public U setId(int id) {
      this.id = id;
      return getThis();
   }

   public U setPosition(short position) {
      this.position = position;
      return getThis();
   }

   public U setQuantity(short quantity) {
      this.quantity = quantity;
      return getThis();
   }

   public U setPet(MaplePet pet) {
      this.pet = pet;
      return getThis();
   }

   public U setPetId(int petId) {
      this.petId = petId;
      return getThis();
   }

   public U setFlag(short flag) {
      this.flag = flag;
      return getThis();
   }

   public U orFlag(short flag) {
      this.flag |= flag;
      return getThis();
   }

   public U setSn(int sn) {
      this.sn = sn;
      return getThis();
   }

   public U setGiftFrom(String giftFrom) {
      this.giftFrom = giftFrom;
      return getThis();
   }

   public U setOwner(String owner) {
      this.owner = owner;
      return getThis();
   }

   //TODO JDT this probably should inherit the item's expiration logic
   public U setExpiration(long expiration) {
      this.expiration = expiration;
      return getThis();
   }
}
