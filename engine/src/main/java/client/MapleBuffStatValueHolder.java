package client;

import server.MapleStatEffect;

public class MapleBuffStatValueHolder {

   public MapleStatEffect effect;
   public long startTime;
   public int value;
   public boolean bestApplied;

   public MapleBuffStatValueHolder(MapleStatEffect effect, long startTime, int value) {
      super();
      this.effect = effect;
      this.startTime = startTime;
      this.value = value;
      this.bestApplied = false;
   }
}