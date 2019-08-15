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
package client;

import java.util.ArrayList;
import java.util.List;

import constants.skills.Evan;
import server.MapleStatEffect;
import server.life.Element;

public class Skill {
   private int id;
   private List<MapleStatEffect> effects = new ArrayList<>();
   private Element element;
   private int animationTime;
   private int job;
   private boolean action;

   public Skill(int id) {
      this.id = id;
      this.job = id / 10000;
   }

   public int getId() {
      return id;
   }

   public MapleStatEffect getEffect(int level) {
      return effects.get(level - 1);
   }

   public int getMaxLevel() {
      return effects.size();
   }

   public boolean isFourthJob() {
      if (job == MapleJob.EVAN4.getId()) {
         return false;
      }
      if (id == Evan.MAGIC_MASTERY || id == Evan.FLAME_WHEEL || id == Evan.HEROS_WILL || id == Evan.DARK_FOG || id == Evan.SOUL_STONE) {
         return true;
      }
      return job % 10 == 2;
   }

   public Element getElement() {
      return element;
   }

   public void setElement(Element elem) {
      element = elem;
   }

   public int getAnimationTime() {
      return animationTime;
   }

   public void setAnimationTime(int time) {
      animationTime = time;
   }

   public void incAnimationTime(int time) {
      animationTime += time;
   }

   public boolean isBeginnerSkill() {
      return id % 10000000 < 10000;
   }

   public boolean getAction() {
      return action;
   }

   public void setAction(boolean act) {
      action = act;
   }

   public void addLevelEffect(MapleStatEffect effect) {
      effects.add(effect);
   }
}