package net.server;

import server.MapleStatEffect;

public class PlayerBuffValueHolder {
   public int usedTime;
   public MapleStatEffect effect;

   public PlayerBuffValueHolder(int usedTime, MapleStatEffect effect) {
      this.usedTime = usedTime;
      this.effect = effect;
   }
}
