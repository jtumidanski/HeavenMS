package net.server;

import client.MapleAbnormalStatus;

public class PlayerDiseaseValueHolder {

   public long startTime;
   public long length;
   public MapleAbnormalStatus disease;

   public PlayerDiseaseValueHolder(final MapleAbnormalStatus disease, final long startTime, final long length) {
      this.disease = disease;
      this.startTime = startTime;
      this.length = length;
   }
}