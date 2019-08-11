package client.database.data;

public class PlayerLifeData {
   private int lifeId;

   private String type;

   private int cy;

   private int f;

   private int fh;

   private int rx0;

   private int rx1;

   private int x;

   private int y;

   private int hide;

   private int mobTime;

   private int team;

   public PlayerLifeData(int lifeId, String type, int cy, int f, int fh, int rx0, int rx1, int x, int y, int hide, int mobTime, int team) {
      this.lifeId = lifeId;
      this.type = type;
      this.cy = cy;
      this.f = f;
      this.fh = fh;
      this.rx0 = rx0;
      this.rx1 = rx1;
      this.x = x;
      this.y = y;
      this.hide = hide;
      this.mobTime = mobTime;
      this.team = team;
   }

   public int getLifeId() {
      return lifeId;
   }

   public String getType() {
      return type;
   }

   public int getCy() {
      return cy;
   }

   public int getF() {
      return f;
   }

   public int getFh() {
      return fh;
   }

   public int getRx0() {
      return rx0;
   }

   public int getRx1() {
      return rx1;
   }

   public int getX() {
      return x;
   }

   public int getY() {
      return y;
   }

   public int getHide() {
      return hide;
   }

   public int getMobTime() {
      return mobTime;
   }

   public int getTeam() {
      return team;
   }
}
