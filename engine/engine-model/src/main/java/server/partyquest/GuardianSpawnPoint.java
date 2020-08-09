package server.partyquest;

import java.awt.Point;

public class GuardianSpawnPoint {
   private final Point position;

   private Boolean taken;

   private Integer team;

   public GuardianSpawnPoint(Point position) {
      this.position = position;
      this.taken = true;
      this.team = -1;
   }

   public Point position() {
      return position;
   }

   public Boolean taken() {
      return taken;
   }

   public void setTaken(Boolean taken) {
      this.taken = taken;
   }

   public Integer team() {
      return team;
   }

   public void setTeam(Integer team) {
      this.team = team;
   }
}
