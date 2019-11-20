/*
    This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
               Matthias Butz <matze@odinms.de>
               Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

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

/**
 * @author kevintjuh93
 */
//Make them better :)
public class MapleCoconut extends MapleEvent {
   private MapleMap map = null;
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

   public void startEvent() {
      map.startEvent();
      for (int i = 0; i < 506; i++) {
         coconuts.add(new MapleCoconuts(i));
      }
      MasterBroadcaster.getInstance().sendToAllInMap(map, new CoconutHit(true, 0, 0));
      setCoconutsHittable(true);
      MasterBroadcaster.getInstance().sendToAllInMap(map, new GetClock(300));

      TimerManager.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            if (map.getId() == 109080000) {
               if (getMapleScore() == getStoryScore()) {
                  bonusTime();
               } else if (getMapleScore() > getStoryScore()) {
                  for (MapleCharacter chr : map.getCharacters()) {
                     if (chr.getTeam() == 0) {
                        PacketCreator.announce(chr, new ShowEffect("event/coconut/victory"));
                        PacketCreator.announce(chr, new PlaySound("Coconut/Victory"));
                     } else {
                        PacketCreator.announce(chr, new ShowEffect("event/coconut/lose"));
                        PacketCreator.announce(chr, new PlaySound("Coconut/Failed"));
                     }
                  }
                  warpOut();
               } else {
                  for (MapleCharacter chr : map.getCharacters()) {
                     if (chr.getTeam() == 1) {
                        PacketCreator.announce(chr, new ShowEffect("event/coconut/victory"));
                        PacketCreator.announce(chr, new PlaySound("Coconut/Victory"));
                     } else {
                        PacketCreator.announce(chr, new ShowEffect("event/coconut/lose"));
                        PacketCreator.announce(chr, new PlaySound("Coconut/Failed"));
                     }
                  }
                  warpOut();
               }
            }
         }
      }, 300000);
   }

   public void bonusTime() {
      MasterBroadcaster.getInstance().sendToAllInMap(map, new GetClock(120));
      TimerManager.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            if (getMapleScore() == getStoryScore()) {
               for (MapleCharacter chr : map.getCharacters()) {
                  PacketCreator.announce(chr, new ShowEffect( "event/coconut/lose"));
                  PacketCreator.announce(chr, new PlaySound("Coconut/Failed"));
               }
               warpOut();
            } else if (getMapleScore() > getStoryScore()) {
               for (MapleCharacter chr : map.getCharacters()) {
                  if (chr.getTeam() == 0) {
                     PacketCreator.announce(chr, new ShowEffect("event/coconut/victory"));
                     PacketCreator.announce(chr, new PlaySound("Coconut/Victory"));
                  } else {
                     PacketCreator.announce(chr, new ShowEffect("event/coconut/lose"));
                     PacketCreator.announce(chr, new PlaySound("Coconut/Failed"));
                  }
               }
               warpOut();
            } else {
               for (MapleCharacter chr : map.getCharacters()) {
                  if (chr.getTeam() == 1) {
                     PacketCreator.announce(chr, new ShowEffect("event/coconut/victory"));
                     PacketCreator.announce(chr, new PlaySound("Coconut/Victory"));
                  } else {
                     PacketCreator.announce(chr, new ShowEffect("event/coconut/lose"));
                     PacketCreator.announce(chr, new PlaySound("Coconut/Failed"));
                  }
               }
               warpOut();
            }
         }
      }, 120000);

   }

   public void warpOut() {
      setCoconutsHittable(false);
      TimerManager.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            List<MapleCharacter> chars = new ArrayList<>(map.getCharacters());

            for (MapleCharacter chr : chars) {
               if ((getMapleScore() > getStoryScore() && chr.getTeam() == 0) || (getStoryScore() > getMapleScore() && chr.getTeam() == 1)) {
                  chr.changeMap(109050000);
               } else {
                  chr.changeMap(109050001);
               }
            }
            map.setCoconut(null);
         }
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
      for (MapleCoconuts nut : coconuts) {
         nut.setHittable(hittable);
      }
   }
}  