package client.database.data;

public class KeyMapData {
   private int key;

   private int type;

   private int action;

   public KeyMapData(int key, int type, int action) {
      this.key = key;
      this.type = type;
      this.action = action;
   }

   public int getKey() {
      return key;
   }

   public int getType() {
      return type;
   }

   public int getAction() {
      return action;
   }
}
