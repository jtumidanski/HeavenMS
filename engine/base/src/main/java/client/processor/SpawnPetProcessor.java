/*
    This file is part of the HeavenMS MapleStory Server
    Copyleft (L) 2016 - 2018 RonanLana

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
package client.processor;

import java.awt.Point;
import java.io.File;

import client.MapleCharacter;
import client.MapleClient;
import client.SkillFactory;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.inventory.manipulator.MapleInventoryManipulator;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import tools.MaplePacketCreator;
import tools.MasterBroadcaster;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.packet.stat.EnableActions;
import tools.packet.stat.UpdatePetStats;

/**
 * @author RonanLana - just added locking on OdinMS' SpawnPetHandler method body
 */
public class SpawnPetProcessor {
   private static MapleDataProvider dataRoot = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("wzpath") + "/Item.wz"));

   public static void processSpawnPet(MapleClient c, byte slot, boolean lead) {
      if (c.tryAcquireClient()) {
         try {
            MapleCharacter chr = c.getPlayer();
            Item item = chr.getInventory(MapleInventoryType.CASH).getItem(slot);
            if (item.pet().isEmpty()) {
               return;
            }

            MaplePet pet = item.pet().get();
            int petid = pet.id();
            if (petid == 5000028 || petid == 5000047) //Handles Dragon AND Robos
            {
               if (chr.haveItem(petid + 1)) {
                  MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "You can't hatch your " + (petid == 5000028 ? "Dragon egg" : "Robo egg") + " if you already have a Baby " + (petid == 5000028 ? "Dragon." : "Robo."));
                  PacketCreator.announce(c, new EnableActions());
                  return;
               } else {
                  int evolveid = MapleDataTool.getInt("info/evol1", dataRoot.getData("Pet/" + petid + ".img"));
                  int petId = PetProcessor.getInstance().createPet(evolveid);
                  if (petId == -1) {
                     return;
                  }
                  long expiration = chr.getInventory(MapleInventoryType.CASH).getItem(slot).expiration();
                  MapleInventoryManipulator.removeById(c, MapleInventoryType.CASH, petid, (short) 1, false, false);
                  MapleInventoryManipulator.addById(c, evolveid, (short) 1, null, petId, expiration);
                  PetProcessor.getInstance().deleteFromDb(chr, petid);
                  PacketCreator.announce(c, new EnableActions());
                  return;
               }
            }
            if (chr.getPetIndex(pet) != -1) {
               chr.unequipPet(pet, true);
            } else {
               MaplePet firstPet = chr.getPet(0);
               SkillFactory.executeIfSkillMeetsConditional(chr, 8,
                     (skill, skillLevel) -> skillLevel == 0 && firstPet != null,
                     (skill, skillLevel) -> chr.unequipPet(firstPet, false));
               if (lead) {
                  chr.shiftPetsRight();
               }
               Point pos = chr.getPosition();
               pos.y -= 12;
               pet.pos_$eq(pos);
               pet.fh_$eq(chr.getMap().getFootholds().findBelow(pet.pos()).id());
               pet.stance_$eq(0);
               pet.summoned_$eq(true);
               PetProcessor.getInstance().saveToDb(pet);
               chr.addPet(pet);
               MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), character -> MaplePacketCreator.showPet(c.getPlayer(), pet, false, false), true, chr);
               PacketCreator.announce(c, new UpdatePetStats(c.getPlayer().getPets()));
               PacketCreator.announce(c, new EnableActions());
               chr.commitExcludedItems();
               chr.getClient().getWorldServer().registerPetHunger(chr, chr.getPetIndex(pet));
            }
         } finally {
            c.releaseClient();
         }
      }
   }
}
