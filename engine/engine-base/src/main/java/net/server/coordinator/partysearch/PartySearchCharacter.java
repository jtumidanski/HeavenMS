package net.server.coordinator.partysearch;

import java.lang.ref.WeakReference;

import client.MapleCharacter;

public class PartySearchCharacter {

   private WeakReference<MapleCharacter> player;
   private int level;
   private boolean queued;

   public PartySearchCharacter(MapleCharacter chr) {
      player = new WeakReference<>(chr);
      level = chr.getLevel();
      queued = true;
   }

   @Override
   public String toString() {
      MapleCharacter chr = player.get();
      return chr == null ? "[empty]" : chr.toString();
   }

   public MapleCharacter callPlayer(int leaderId, int callerMapId) {
      MapleCharacter chr = player.get();
      if (chr == null || !MaplePartySearchCoordinator.isInVicinity(callerMapId, chr.getMapId())) {
         return null;
      }

      if (chr.hasDisabledPartySearchInvite(leaderId)) {
         return null;
      }

      queued = false;
      if (chr.isLoggedInWorld() && chr.getParty().isEmpty()) {
         return chr;
      } else {
         return null;
      }
   }

   public MapleCharacter getPlayer() {
      return player.get();
   }

   public int getLevel() {
      return level;
   }

   public boolean isQueued() {
      return queued;
   }

}
