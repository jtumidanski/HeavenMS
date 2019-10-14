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
package server.maps;

import client.MapleCharacter;
import server.processor.maps.MapleMapObjectProcessor;


public class MapleDragon extends AbstractAnimatedMapleMapObject {

   private MapleCharacter owner;

   public MapleDragon(MapleCharacter chr) {
      super();
      this.owner = chr;
      this.position_$eq(chr.position());
      this.stance_$eq(chr.stance());
      MapleMapObjectProcessor.getInstance().sendSpawnData(this, chr.getClient());
   }

   @Override
   public MapleMapObjectType type() {
      return MapleMapObjectType.DRAGON;
   }

   @Override
   public int objectId() {
      return owner.objectId();
   }

   public MapleCharacter getOwner() {
      return owner;
   }
}