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

import java.util.List;
import java.util.stream.Collectors;

import client.MapleCharacter;
import server.TimerManager;
import server.maps.MapleMap;
import tools.MasterBroadcaster;
import tools.PacketCreator;
import tools.packet.event.HitSnowBall;
import tools.packet.event.RollSnowBall;
import tools.packet.event.SnowBallMessage;
import tools.packet.ui.GetClock;

/**
 * @author kevintjuh93
 */
public class MapleSnowball {
   private List<MapleCharacter> characters;
   private MapleMap map;
   private int position = 0;
   private int hits = 3;
   private int snowmanHp = 1000;
   private boolean hittable = false;
   private int team;
   private boolean winner = false;

   public MapleSnowball(int team, MapleMap map) {
      this.map = map;
      this.team = team;

      characters = map.getAllPlayers().stream()
            .filter(character -> character.getTeam() == team)
            .collect(Collectors.toList());
   }

   public void startEvent() {
      if (hittable) {
         return;
      }

      characters.parallelStream().forEach(character -> {
         setSnowballState(1, 0, 1);
         PacketCreator.announce(character, new GetClock(600));
      });

      hittable = true;
      TimerManager.getInstance().schedule(() -> {
         if (map.getSnowball(team).getPosition() > map.getSnowball(getOpposingTeam(team)).getPosition()) {
            characters.parallelStream().forEach(character -> setSnowballState(3, 0, 0));
            winner = true;
         } else if (map.getSnowball(getOpposingTeam(team)).getPosition() > map.getSnowball(team).getPosition()) {
            characters.parallelStream().forEach(character -> setSnowballState(4, 0, 0));
            winner = true;
         } //Else
         warpOut();
      }, 600000);
   }

   protected int getOpposingTeam(int team) {
      return team == 0 ? 1 : 0;
   }

   public boolean isHittable() {
      return hittable;
   }

   public void setHittable(boolean hit) {
      this.hittable = hit;
   }

   public int getPosition() {
      return position;
   }

   public int getSnowmanHP() {
      return snowmanHp;
   }

   public void setSnowmanHP(int hp) {
      this.snowmanHp = hp;
   }

   public void hit(int what, int damage) {
      if (what < 2) {
         if (damage > 0) {
            this.hits--;
         } else {
            if (this.snowmanHp - damage < 0) {
               this.snowmanHp = 0;

               TimerManager.getInstance().schedule(() -> {
                  setSnowmanHP(7500);
                  message(5);
               }, 10000);
            } else {
               this.snowmanHp -= damage;
            }
            setSnowballState(1, 0, 1);
         }
      }

      if (this.hits == 0) {
         this.position += 1;
         if (this.position == 45) {
            map.getSnowball(getOpposingTeam(team)).message(1);
         } else if (this.position == 290) {
            map.getSnowball(getOpposingTeam(team)).message(2);
         } else if (this.position == 560) {
            map.getSnowball(getOpposingTeam(team)).message(3);
         }

         this.hits = 3;
         setSnowballState(0, 0, 1);
         setSnowballState(1, 0, 1);
      }
      MasterBroadcaster.getInstance().sendToAllInMap(map, new HitSnowBall(what, damage));
   }

   protected void setSnowballState(int state, int firstBall, int secondBall) {
      MapleSnowball firstSnowBall = map.getSnowball(firstBall);
      MapleSnowball secondSnowBall = map.getSnowball(secondBall);
      MasterBroadcaster.getInstance().sendToAllInMap(map, new RollSnowBall(false, state,
            firstSnowBall.getSnowmanHP(), firstSnowBall.getPosition(),
            secondSnowBall.getSnowmanHP(), secondSnowBall.getPosition()));
   }

   public void message(int message) {
      characters.parallelStream().forEach(character -> PacketCreator.announce(character, new SnowBallMessage(team, message)));
   }

   protected void warpOut() {
      TimerManager.getInstance().schedule(() -> {
         map.warpOutByTeam(team, winner ? 109050000 : 109050001);
         map.setSnowball(team, null);
      }, 10000);
   }
}