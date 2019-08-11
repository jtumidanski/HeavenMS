package client.database.data;

public class AccountData {
   private int id;

   private String name;

   private String password;

   private byte gender;

   private boolean banned;

   private String pin;

   private String pic;

   private byte characterSlots;

   private byte tos;

   private int language;

   public AccountData(int id, String name, String password, byte gender, boolean banned, String pin, String pic, byte characterSlots, byte tos, int language) {
      this.id = id;
      this.name = name;
      this.password = password;
      this.gender = gender;
      this.banned = banned;
      this.pin = pin;
      this.pic = pic;
      this.characterSlots = characterSlots;
      this.tos = tos;
      this.language = language;
   }

   public int getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public String getPassword() {
      return password;
   }

   public byte getGender() {
      return gender;
   }

   public boolean isBanned() {
      return banned;
   }

   public String getPin() {
      return pin;
   }

   public String getPic() {
      return pic;
   }

   public byte getCharacterSlots() {
      return characterSlots;
   }

   public byte getTos() {
      return tos;
   }

   public int getLanguage() {
      return language;
   }
}
