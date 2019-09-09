package client.database.data;

public class CharacterIdNameAccountId {
   private int id;

   private int accountId;

   private String name;

   public CharacterIdNameAccountId(int id, int accountId, String name) {
      this.id = id;
      this.accountId = accountId;
      this.name = name;
   }

   public int getId() {
      return id;
   }

   public int getAccountId() {
      return accountId;
   }

   public String getName() {
      return name;
   }
}
