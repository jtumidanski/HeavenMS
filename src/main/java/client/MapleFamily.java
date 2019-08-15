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

import java.util.HashMap;
import java.util.Map;

import client.database.provider.FamilyCharacterProvider;
import tools.DatabaseConnection;

/**
 * @author Jay Estrella - Mr.Trash :3
 */
public class MapleFamily {
   private int id;
   private Map<Integer, MapleFamilyEntry> members = new HashMap<>();

   public MapleFamily(int id) {
      this.id = id;
   }

   public MapleFamilyEntry getMember(int cid) {
      if (members.containsKey(cid)) {
         return members.get(cid);
      }
      return null;
   }

   public void addMember(MapleFamilyEntry member) {
      members.put(member.getChrId(), member);
   }

   public Map<Integer, MapleFamilyEntry> getMembers() {
      return members;
   }
}
