package client.database.data;

public class CharNameAndIdData {
   private String name;

   private int id;

   private int buddyCapacity;

   public CharNameAndIdData(String name, int id) {
      super();
      this.name = name;
      this.id = id;
   }

   public CharNameAndIdData(String name, int id, int buddyCapacity) {
      this.name = name;
      this.id = id;
      this.buddyCapacity = buddyCapacity;
   }

   public String getName() {
      return name;
   }

   public int getId() {
      return id;
   }

   public int getBuddyCapacity() {
      return buddyCapacity;
   }
}
