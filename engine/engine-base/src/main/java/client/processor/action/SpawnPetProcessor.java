package client.processor.action;

import java.awt.Point;
import java.io.File;

import client.MapleCharacter;
import client.MapleClient;
import client.SkillFactory;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.inventory.manipulator.MapleInventoryManipulator;
import client.processor.PetProcessor;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import tools.MasterBroadcaster;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.I18nMessage;
import tools.packet.spawn.ShowPet;
import tools.packet.stat.EnableActions;
import tools.packet.stat.UpdatePetStats;

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
            if (petid == 5000028 || petid == 5000047) {
               if (chr.haveItem(petid + 1)) {
                  boolean dragon = petid == 5000028;
                  if (dragon) {
                     MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, I18nMessage.from("SPAWN_PET_CANNOT_HATCH_IF_ALREADY_HAVE_DRAGON"));
                  } else {
                     MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, I18nMessage.from("SPAWN_PET_CANNOT_HATCH_IF_ALREADY_HAVE_ROBOT"));
                  }
                  PacketCreator.announce(c, new EnableActions());
                  return;
               } else {
                  int evolveId = MapleDataTool.getInt("info/evol1", dataRoot.getData("Pet/" + petid + ".img"));
                  int petId = PetProcessor.getInstance().createPet(evolveId);
                  if (petId == -1) {
                     return;
                  }
                  long expiration = chr.getInventory(MapleInventoryType.CASH).getItem(slot).expiration();
                  MapleInventoryManipulator.removeById(c, MapleInventoryType.CASH, petid, (short) 1, false, false);
                  MapleInventoryManipulator.addById(c, evolveId, (short) 1, null, petId, expiration);
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
               Point pos = chr.position();
               pos.y -= 12;
               pet.pos_$eq(pos);
               pet.fh_$eq(chr.getMap().getFootholds().findBelow(pet.pos()).id());
               pet.stance_$eq(0);
               pet.summoned_$eq(true);
               PetProcessor.getInstance().saveToDb(pet);
               chr.addPet(pet);
               MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), new ShowPet(c.getPlayer(), pet, false, false), true, chr);
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
