package server.events.gm;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import client.MapleCharacter;
import server.TimerManager;
import server.maps.MapleMap;
import tools.MasterBroadcaster;
import tools.PacketCreator;
import tools.packet.event.CoconutHit;
import tools.packet.field.effect.PlaySound;
import tools.packet.field.effect.ShowEffect;
import tools.packet.ui.GetClock;

public class MapleCoconut extends MapleEvent {
   private MapleMap map;
   private int MapleScore = 0;
   private int StoryScore = 0;
   private int countBombing = 80;
   private int countFalling = 401;
   private int countStopped = 20;
   private List<MapleCoconuts> coconuts = new LinkedList<>();

   public MapleCoconut(MapleMap map) {
      super(1, 50);
      this.map = map;
   }

   protected void announceResult(MapleCharacter chr, boolean win) {
      if (win) {
         PacketCreator.announce(chr, new ShowEffect("event/coconut/victory"));
         PacketCreator.announce(chr, new PlaySound("Coconut/Victory"));
      } else {
         PacketCreator.announce(chr, new ShowEffect("event/coconut/lose"));
         PacketCreator.announce(chr, new PlaySound("Coconut/Failed"));
      }
   }

   protected void showResults(int winningTeam) {
      map.getCharacters().parallelStream().forEach(character -> announceResult(character, character.getTeam() == winningTeam));
      warpOut();
   }

   public void startEvent() {
      map.startEvent();
      for (int i = 0; i < 506; i++) {
         coconuts.add(new MapleCoconuts(i));
      }
      MasterBroadcaster.getInstance().sendToAllInMap(map, new CoconutHit(true, 0, 0));
      setCoconutsHittable(true);
      MasterBroadcaster.getInstance().sendToAllInMap(map, new GetClock(300));

      TimerManager.getInstance().schedule(() -> {
         if (map.getId() == 109080000) {
            if (getMapleScore() == getStoryScore()) {
               bonusTime();
            } else if (getMapleScore() > getStoryScore()) {
               showResults(0);
            } else {
               showResults(1);
            }
         }
      }, 300000);
   }

   public void bonusTime() {
      MasterBroadcaster.getInstance().sendToAllInMap(map, new GetClock(120));
      TimerManager.getInstance().schedule(() -> {
         if (getMapleScore() == getStoryScore()) {
            map.getCharacters().parallelStream().forEach(character -> announceResult(character, false));
            warpOut();
         } else if (getMapleScore() > getStoryScore()) {
            showResults(0);
         } else {
            showResults(1);
         }
      }, 120000);

   }

   public void warpOut() {
      setCoconutsHittable(false);
      TimerManager.getInstance().schedule(() -> {
         List<MapleCharacter> chars = new ArrayList<>(map.getCharacters());

         for (MapleCharacter chr : chars) {
            if ((getMapleScore() > getStoryScore() && chr.getTeam() == 0) || (getStoryScore() > getMapleScore() && chr.getTeam() == 1)) {
               chr.changeMap(109050000);
            } else {
               chr.changeMap(109050001);
            }
         }
         map.setCoconut(null);
      }, 12000);
   }

   public int getMapleScore() {
      return MapleScore;
   }

   public int getStoryScore() {
      return StoryScore;
   }

   public void addMapleScore() {
      this.MapleScore += 1;
   }

   public void addStoryScore() {
      this.StoryScore += 1;
   }

   public int getBombings() {
      return countBombing;
   }

   public void bombCoconut() {
      countBombing--;
   }

   public int getFalling() {
      return countFalling;
   }

   public void fallCoconut() {
      countFalling--;
   }

   public int getStopped() {
      return countStopped;
   }

   public void stopCoconut() {
      countStopped--;
   }

   public MapleCoconuts getCoconut(int id) {
      return coconuts.get(id);
   }

   public List<MapleCoconuts> getAllCoconuts() {
      return coconuts;
   }

   public void setCoconutsHittable(boolean hittable) {
      coconuts.forEach(coconut -> coconut.setHittable(hittable));
   }
}  