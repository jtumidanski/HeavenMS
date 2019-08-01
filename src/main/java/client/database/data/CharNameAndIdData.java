package client.database.data;

public class CharNameAndIdData {
   private String name;

   private int id;

   public CharNameAndIdData(String name, int id) {
      super();
      this.name = name;
      this.id = id;
   }

   public String getName() {
      return name;
   }

   public int getId() {
      return id;
   }
}
