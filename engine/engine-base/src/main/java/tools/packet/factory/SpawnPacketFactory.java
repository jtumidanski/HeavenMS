package tools.packet.factory;

import java.awt.*;
import java.util.Set;

import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import client.MapleMount;
import client.inventory.MaplePet;
import client.newyear.NewYearCardRecord;
import constants.ItemConstants;
import constants.MapleInventoryType;
import net.server.guild.MapleGuildSummary;
import server.life.MapleMonster;
import server.maps.MapleMiniGame;
import server.maps.MaplePlayerShop;
import tools.Randomizer;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.spawn.CannotSpawnKite;
import tools.packet.spawn.ControlMonster;
import tools.packet.spawn.MakeMonsterInvisible;
import tools.packet.spawn.MakeMonsterReal;
import tools.packet.spawn.RemoveDoor;
import tools.packet.spawn.RemoveMonsterInvisibility;
import tools.packet.spawn.RemoveNPCController;
import tools.packet.spawn.ShowPet;
import tools.packet.spawn.SpawnDoor;
import tools.packet.spawn.SpawnDragon;
import tools.packet.spawn.SpawnFakeMonster;
import tools.packet.spawn.SpawnGuide;
import tools.packet.spawn.SpawnHiredMerchant;
import tools.packet.spawn.SpawnKite;
import tools.packet.spawn.SpawnMist;
import tools.packet.spawn.SpawnMonster;
import tools.packet.spawn.SpawnNPC;
import tools.packet.spawn.SpawnNPCRequestController;
import tools.packet.spawn.SpawnPlayer;
import tools.packet.spawn.SpawnPlayerNPC;
import tools.packet.spawn.SpawnPortal;
import tools.packet.spawn.SpawnSummon;
import tools.packet.spawn.StopMonsterControl;

public class SpawnPacketFactory extends AbstractPacketFactory {
   private static SpawnPacketFactory instance;

   public static SpawnPacketFactory getInstance() {
      if (instance == null) {
         instance = new SpawnPacketFactory();
      }
      return instance;
   }

   private SpawnPacketFactory() {
      Handler.handle(SpawnPortal.class).decorate(this::spawnPortal).size(14).register(registry);
      Handler.handle(SpawnDoor.class).decorate(this::spawnDoor).size(11).register(registry);
      Handler.handle(RemoveDoor.class).decorate(this::removeDoor).size(10).register(registry);
      Handler.handle(SpawnSummon.class).decorate(this::spawnSummon).size(25).register(registry);
      Handler.handle(SpawnNPC.class).decorate(this::spawnNPC).size(24).register(registry);
      Handler.handle(SpawnNPCRequestController.class).decorate(this::spawnNPCRequestController).size(23).register(registry);
      Handler.handle(RemoveMonsterInvisibility.class).decorate(this::removeMonsterInvisibility).register(registry);
      Handler.handle(SpawnFakeMonster.class).decorate(this::spawnFakeMonster).register(registry);
      Handler.handle(MakeMonsterReal.class).decorate(this::makeMonsterReal).register(registry);
      Handler.handle(StopMonsterControl.class).decorate(this::stopControllingMonster).size(7).register(registry);
      Handler.handle(SpawnPlayer.class).decorate(this::spawnPlayerMapObject).register(registry);
      Handler.handle(SpawnKite.class).decorate(this::spawnKite).register(registry);
      Handler.handle(SpawnMist.class).decorate(this::spawnMist).register(registry);
      Handler.handle(ShowPet.class).decorate(this::showPet).register(registry);
      Handler.handle(SpawnHiredMerchant.class).decorate(this::spawnHiredMerchantBox).register(registry);
      Handler.handle(SpawnPlayerNPC.class).decorate(this::spawnPlayerNPC).register(registry);
      Handler.handle(RemoveNPCController.class).decorate(this::removeNPCController).register(registry);
      Handler.handle(SpawnGuide.class).decorate(this::spawnGuide).size(3).register(registry);
      Handler.handle(SpawnDragon.class).decorate(this::spawnDragon).register(registry);
      Handler.handle(SpawnMonster.class).decorate(this::spawnMonster).register(registry);
      Handler.handle(ControlMonster.class).decorate(this::controlMonster).register(registry);
      Handler.handle(MakeMonsterInvisible.class).decorate(this::makeMonsterInvisible).register(registry);
      Handler.handle(CannotSpawnKite.class).decorate(this::sendCannotSpawnKite).register(registry);
   }

   /**
    * Gets a packet to spawn a portal.
    */
   protected void spawnPortal(MaplePacketLittleEndianWriter writer, SpawnPortal packet) {
      writer.writeInt(packet.townId());
      writer.writeInt(packet.targetId());
      writer.writePos(packet.position());
   }

   /**
    * Gets a packet to spawn a door.
    */
   protected void spawnDoor(MaplePacketLittleEndianWriter writer, SpawnDoor packet) {
      writer.writeBool(packet.launched());
      writer.writeInt(packet.ownerId());
      writer.writePos(packet.position());
   }

   /**
    * Gets a packet to remove a door.
    */
   protected void removeDoor(MaplePacketLittleEndianWriter writer, RemoveDoor packet) {
      if (packet.town()) {
         writer.writeInt(999999999);
         writer.writeInt(999999999);
      } else {
         writer.write(0);
         writer.writeInt(packet.ownerId());
      }
   }

   /**
    * Gets a packet to spawn a special map object.
    */
   protected void spawnSummon(MaplePacketLittleEndianWriter writer, SpawnSummon packet) {
      writer.writeInt(packet.ownerId());
      writer.writeInt(packet.objectId());
      writer.writeInt(packet.skillId());
      writer.write(0x0A); //v83
      writer.write(packet.skillLevel());
      writer.writePos(packet.position());
      writer.write(packet.stance());
      writer.writeShort(0);
      writer.write(
            packet.movementType()); // 0 = don't move, 1 = follow (4th magician summons?), 2/4 = only teleport follow, 3 = bird follow
      writer.write(packet.puppet() ? 0 : 1); // 0 and the summon can't attack - but puppets don't attack with 1 either ^.-
      writer.write(packet.animated() ? 0 : 1);
   }

   protected void spawnNPC(MaplePacketLittleEndianWriter writer, SpawnNPC packet) {
      writer.writeInt(packet.getNpc().objectId());
      writer.writeInt(packet.getNpc().id());
      writer.writeShort(packet.getNpc().position().x);
      writer.writeShort(packet.getNpc().cy());
      if (packet.getNpc().f() == 1) {
         writer.write(0);
      } else {
         writer.write(1);
      }
      writer.writeShort(packet.getNpc().fh());
      writer.writeShort(packet.getNpc().rx0());
      writer.writeShort(packet.getNpc().rx1());
      writer.write(1);
   }

   protected void spawnNPCRequestController(MaplePacketLittleEndianWriter writer, SpawnNPCRequestController packet) {
      writer.write(1);
      writer.writeInt(packet.getNpc().objectId());
      writer.writeInt(packet.getNpc().id());
      writer.writeShort(packet.getNpc().position().x);
      writer.writeShort(packet.getNpc().cy());
      if (packet.getNpc().f() == 1) {
         writer.write(0);
      } else {
         writer.write(1);
      }
      writer.writeShort(packet.getNpc().fh());
      writer.writeShort(packet.getNpc().rx0());
      writer.writeShort(packet.getNpc().rx1());
      writer.writeBool(packet.isMiniMap());
   }

   /**
    * Removes a monster invisibility.
    */
   protected void removeMonsterInvisibility(MaplePacketLittleEndianWriter writer, RemoveMonsterInvisibility packet) {
      writer.write(1);
      writer.writeInt(packet.monsterObjectId());
   }

   /**
    * Handles monsters not being target-able, such as Zakum's first body.
    */
   protected void spawnFakeMonster(MaplePacketLittleEndianWriter writer, SpawnFakeMonster packet) {
      writer.write(1);
      writer.writeInt(packet.getMonster().objectId());
      writer.write(5);
      writer.writeInt(packet.getMonster().id());
      writer.skip(15);
      writer.write(0x88);
      writer.skip(6);
      writer.writePos(packet.getMonster().position());
      writer.write(packet.getMonster().stance());
      writer.writeShort(0);//life.getStartFh()
      writer.writeShort(packet.getMonster().fh());
      if (packet.getEffectId() > 0) {
         writer.write(packet.getEffectId());
         writer.write(0);
         writer.writeShort(0);
      }
      writer.writeShort(-2);
      writer.write(packet.getMonster().getTeam());
      writer.writeInt(0);
   }

   /**
    * Makes a monster previously spawned as non-target-able, target-able.
    */
   protected void makeMonsterReal(MaplePacketLittleEndianWriter writer, MakeMonsterReal packet) {
      writer.writeInt(packet.getMonster().objectId());
      writer.write(5);
      writer.writeInt(packet.getMonster().id());
      writer.skip(15);
      writer.write(0x88);
      writer.skip(6);
      writer.writePos(packet.getMonster().position());
      writer.write(packet.getMonster().stance());
      writer.writeShort(0);//life.getStartFh()
      writer.writeShort(packet.getMonster().fh());
      writer.writeShort(-1);
      writer.writeInt(0);
   }

   /**
    * Gets a stop control monster packet.
    */
   protected void stopControllingMonster(MaplePacketLittleEndianWriter writer, StopMonsterControl packet) {
      writer.write(0);
      writer.writeInt(packet.monsterId());
   }

   /**
    * Gets a packet spawning a player as a map object to other clients.
    */
   protected void spawnPlayerMapObject(MaplePacketLittleEndianWriter writer, SpawnPlayer packet) {
      MapleCharacter chr = packet.getCharacter();
      MapleClient target = packet.getTarget();
      boolean enteringField = packet.isEnteringField();

      writer.writeInt(chr.getId());
      writer.write(chr.getLevel()); //v83
      writer.writeMapleAsciiString(chr.getName());
      if (chr.getGuildId() < 1) {
         writer.writeMapleAsciiString("");
         writer.write(new byte[6]);
      } else {
         MapleGuildSummary gs = chr.getClient().getWorldServer().getGuildSummary(chr.getGuildId(), chr.getWorld());
         if (gs != null) {
            writer.writeMapleAsciiString(gs.getName());
            writer.writeShort(gs.getLogoBG());
            writer.write(gs.getLogoBGColor());
            writer.writeShort(gs.getLogo());
            writer.write(gs.getLogoColor());
         } else {
            writer.writeMapleAsciiString("");
            writer.write(new byte[6]);
         }
      }

      writeForeignBuffs(writer, chr);

      writer.writeShort(chr.getJob().getId());

                /* replace "writer.writeShort(chr.getJob().getId())" with this snippet for 3rd person FJ animation on all classes
                if (chr.getJob().isA(MapleJob.HERMIT) || chr.getJob().isA(MapleJob.DAWNWARRIOR2) || chr.getJob().isA(MapleJob.NIGHTWALKER2)) {
			writer.writeShort(chr.getJob().getId());
                } else {
			writer.writeShort(412);
                }*/

      addCharLook(writer, chr, false);
      writer.writeInt(chr.getInventory(MapleInventoryType.CASH).countById(5110000));
      writer.writeInt(chr.getItemEffect());
      writer.writeInt(ItemConstants.getInventoryType(chr.getChair()) == MapleInventoryType.SETUP ? chr.getChair() : 0);

      if (enteringField) {
         Point spawnPos = new Point(chr.position());
         spawnPos.y -= 42;
         writer.writePos(spawnPos);
         writer.write(6);
      } else {
         writer.writePos(chr.position());
         writer.write(chr.stance());
      }

      writer.writeShort(0);//chr.getFh()
      writer.write(0);
      MaplePet[] pet = chr.getPets();
      for (int i = 0; i < 3; i++) {
         if (pet[i] != null) {
            addPetInfo(writer, pet[i], false);
         }
      }
      writer.write(0); //end of pets
      if (chr.getMount() == null) {
         writer.writeInt(1); // mob level
         writer.writeLong(0); // mob exp + tiredness
      } else {
         writer.writeInt(chr.getMount().level());
         writer.writeInt(chr.getMount().exp());
         writer.writeInt(chr.getMount().tiredness());
      }

      MaplePlayerShop mps = chr.getPlayerShop();
      if (mps != null && mps.isOwner(chr)) {
         if (mps.hasFreeSlot()) {
            addAnnounceBox(writer, mps, mps.getVisitors().length);
         } else {
            addAnnounceBox(writer, mps, 1);
         }
      } else {
         MapleMiniGame miniGame = chr.getMiniGame();
         if (miniGame != null && miniGame.isOwner(chr)) {
            if (miniGame.hasFreeSlot()) {
               addAnnounceBox(writer, miniGame, 1, 0);
            } else {
               addAnnounceBox(writer, miniGame, 2, miniGame.isMatchInProgress() ? 1 : 0);
            }
         } else {
            writer.write(0);
         }
      }

      if (chr.getChalkboard() != null) {
         writer.write(1);
         writer.writeMapleAsciiString(chr.getChalkboard());
      } else {
         writer.write(0);
      }
      addRingLook(writer, chr, true);  // crush
      addRingLook(writer, chr, false); // friendship
      addMarriageRingLook(target, writer, chr);
      encodeNewYearCardInfo(writer, chr);  // new year seems to crash sometimes...
      writer.write(0);
      writer.write(0);
      writer.write(chr.getTeam());//only needed in specific fields
   }

   protected void writeForeignBuffs(MaplePacketLittleEndianWriter writer, MapleCharacter chr) {
      writer.writeInt(0);
      writer.writeShort(0); //v83
      writer.write(0xFC);
      writer.write(1);
      if (chr.getBuffedValue(MapleBuffStat.MORPH) != null) {
         writer.writeInt(2);
      } else {
         writer.writeInt(0);
      }
      long buffMask = 0;
      Integer buffValue = null;
      if (chr.getBuffedValue(MapleBuffStat.DARK_SIGHT) != null && !chr.isHidden()) {
         buffMask |= MapleBuffStat.DARK_SIGHT.getValue();
      }
      if (chr.getBuffedValue(MapleBuffStat.COMBO) != null) {
         buffMask |= MapleBuffStat.COMBO.getValue();
         buffValue = chr.getBuffedValue(MapleBuffStat.COMBO);
      }
      if (chr.getBuffedValue(MapleBuffStat.SHADOW_PARTNER) != null) {
         buffMask |= MapleBuffStat.SHADOW_PARTNER.getValue();
      }
      if (chr.getBuffedValue(MapleBuffStat.SOUL_ARROW) != null) {
         buffMask |= MapleBuffStat.SOUL_ARROW.getValue();
      }
      if (chr.getBuffedValue(MapleBuffStat.MORPH) != null) {
         buffValue = chr.getBuffedValue(MapleBuffStat.MORPH);
      }
      if (chr.getBuffedValue(MapleBuffStat.ENERGY_CHARGE) != null) {
         buffMask |= MapleBuffStat.ENERGY_CHARGE.getValue();
         buffValue = chr.getBuffedValue(MapleBuffStat.ENERGY_CHARGE);
      }//AREN'T THESE
      writer.writeInt((int) ((buffMask >> 32) & 0xffffffffL));
      if (buffValue != null) {
         if (chr.getBuffedValue(MapleBuffStat.MORPH) != null) { //TEST
            writer.writeShort(buffValue);
         } else {
            writer.write(buffValue.byteValue());
         }
      }
      writer.writeInt((int) (buffMask & 0xffffffffL));
      int CHAR_MAGIC_SPAWN = Randomizer.nextInt();
      writer.skip(6);
      writer.writeInt(CHAR_MAGIC_SPAWN);
      writer.skip(11);
      writer.writeInt(CHAR_MAGIC_SPAWN);//v74
      writer.skip(11);
      writer.writeInt(CHAR_MAGIC_SPAWN);
      writer.writeShort(0);
      writer.write(0);

      Integer bv = chr.getBuffedValue(MapleBuffStat.MONSTER_RIDING);
      if (bv != null) {
         MapleMount mount = chr.getMount();
         if (mount != null) {
            writer.writeInt(mount.itemId());
            writer.writeInt(mount.skillId());
         } else {
            writer.writeLong(0);
         }
      } else {
         writer.writeLong(0);
      }

      writer.writeInt(CHAR_MAGIC_SPAWN);
      writer.skip(9);
      writer.writeInt(CHAR_MAGIC_SPAWN);
      writer.writeShort(0);
      writer.writeInt(0); // actually not 0, why is it 0 then?
      writer.skip(10);
      writer.writeInt(CHAR_MAGIC_SPAWN);
      writer.skip(13);
      writer.writeInt(CHAR_MAGIC_SPAWN);
      writer.writeShort(0);
      writer.write(0);
   }

   protected void addPetInfo(final MaplePacketLittleEndianWriter writer, MaplePet pet, boolean showPet) {
      writer.write(1);
      if (showPet) {
         writer.write(0);
      }

      writer.writeInt(pet.id());
      writer.writeMapleAsciiString(pet.name());
      writer.writeLong(pet.uniqueId());
      writer.writePos(pet.pos());
      writer.write(pet.stance());
      writer.writeInt(pet.fh());
   }

   /**
    * Adds a announcement box to an existing MaplePacketLittleEndianWriter.
    *
    * @param writer The MaplePacketLittleEndianWriter to add an announcement box to.
    * @param shop   The shop to announce.
    */
   protected void addAnnounceBox(final MaplePacketLittleEndianWriter writer, MaplePlayerShop shop, int availability) {
      writer.write(4);
      writer.writeInt(shop.objectId());
      writer.writeMapleAsciiString(shop.getDescription());
      writer.write(0);
      writer.write(0);
      writer.write(1);
      writer.write(availability);
      writer.write(0);
   }

   protected void encodeNewYearCardInfo(MaplePacketLittleEndianWriter writer, MapleCharacter chr) {
      Set<NewYearCardRecord> newYearRecords = chr.getReceivedNewYearRecords();
      if (!newYearRecords.isEmpty()) {
         writer.write(1);

         writer.writeInt(newYearRecords.size());
         for (NewYearCardRecord nyc : newYearRecords) {
            writer.writeInt(nyc.id());
         }
      } else {
         writer.write(0);
      }
   }

   protected void spawnKite(MaplePacketLittleEndianWriter writer, SpawnKite packet) {
      writer.writeInt(packet.objectId());
      writer.writeInt(packet.itemId());
      writer.writeMapleAsciiString(packet.message());
      writer.writeMapleAsciiString(packet.name());
      writer.writeShort(packet.position().x);
      writer.writeShort(packet.ft());
   }

   protected void spawnMist(MaplePacketLittleEndianWriter writer, SpawnMist packet) {
      writer.writeInt(packet.getObjectId());
      writer.writeInt(packet.getMist().isMobMist() ? 0 : packet.getMist().isPoisonMist() ? 1 :
            packet.getMist().isRecoveryMist() ? 4 :
                  2); // mob mist = 0, player poison = 1, smokescreen = 2, unknown = 3, recovery = 4
      writer.writeInt(packet.getOwnerId());
      writer.writeInt(packet.getSkillId());
      writer.write(packet.getLevel());
      writer.writeShort(packet.getMist().getSkillDelay()); // Skill delay
      writer.writeInt(packet.getMist().getBox().x);
      writer.writeInt(packet.getMist().getBox().y);
      writer.writeInt(packet.getMist().getBox().x + packet.getMist().getBox().width);
      writer.writeInt(packet.getMist().getBox().y + packet.getMist().getBox().height);
      writer.writeInt(0);
   }

   protected void showPet(MaplePacketLittleEndianWriter writer, ShowPet packet) {
      writer.writeInt(packet.getCharacter().getId());
      writer.write(packet.getCharacter().getPetIndex(packet.getPet()));
      if (packet.isRemove()) {
         writer.write(0);
         writer.write(packet.isHunger() ? 1 : 0);
      } else {
         addPetInfo(writer, packet.getPet(), true);
      }
   }

   protected void spawnHiredMerchantBox(MaplePacketLittleEndianWriter writer, SpawnHiredMerchant packet) {
      writer.writeInt(packet.getHiredMerchant().getOwnerId());
      writer.writeInt(packet.getHiredMerchant().getItemId());
      writer.writeShort((short) packet.getHiredMerchant().position().getX());
      writer.writeShort((short) packet.getHiredMerchant().position().getY());
      writer.writeShort(0);
      writer.writeMapleAsciiString(packet.getHiredMerchant().getOwner());
      writer.write(0x05);
      writer.writeInt(packet.getHiredMerchant().objectId());
      writer.writeMapleAsciiString(packet.getHiredMerchant().getDescription());
      writer.write(packet.getHiredMerchant().getItemId() % 100);
      writer.write(new byte[]{1, 4});
   }

   protected void spawnPlayerNPC(MaplePacketLittleEndianWriter writer, SpawnPlayerNPC packet) {
      writer.write(1);
      writer.writeInt(packet.getPlayerNPC().objectId());
      writer.writeInt(packet.getPlayerNPC().getScriptId());
      writer.writeShort(packet.getPlayerNPC().position().x);
      writer.writeShort(packet.getPlayerNPC().getCY());
      writer.write(packet.getPlayerNPC().getDirection());
      writer.writeShort(packet.getPlayerNPC().getFH());
      writer.writeShort(packet.getPlayerNPC().getRX0());
      writer.writeShort(packet.getPlayerNPC().getRX1());
      writer.write(1);
   }

   protected void removeNPCController(MaplePacketLittleEndianWriter writer, RemoveNPCController packet) {
      writer.write(0);
      writer.writeInt(packet.objectId());
   }

   protected void spawnGuide(MaplePacketLittleEndianWriter writer, SpawnGuide packet) {
      if (packet.spawn()) {
         writer.write(1);
      } else {
         writer.write(0);
      }
   }

   protected void spawnDragon(MaplePacketLittleEndianWriter writer, SpawnDragon packet) {
      writer.writeInt(packet.getDragon().ownerId());
      writer.writeShort(packet.getDragon().position().x);
      writer.writeShort(0);
      writer.writeShort(packet.getDragon().position().y);
      writer.writeShort(0);
      writer.write(packet.getDragon().stance());
      writer.write(0);
      writer.writeShort(packet.getDragon().ownerId());
   }

   /**
    * Internal function to handler monster spawning and controlling.
    */
   protected void spawnMonster(MaplePacketLittleEndianWriter writer, SpawnMonster packet) {
      MapleMonster life = packet.getMonster();
      writer.writeInt(life.objectId());
      writer.write(life.getController() == null ? 5 : 1);
      writer.writeInt(life.id());
      writer.skip(15);
      writer.write(0x88);
      writer.skip(6);
      writer.writePos(life.position());
      writer.write(life.stance());
      writer.writeShort(0); //Origin FH //life.getStartFh()
      writer.writeShort(life.fh());

      /*
       * -4: Fake -3: Appear after linked mob is dead -2: Fade in 1: Smoke 3:
       * King Slime spawn 4: Summoning rock thing, used for 3rd job? 6:
       * Magical shit 7: Smoke shit 8: 'The Boss' 9/10: Grim phantom shit?
       * 11/12: Nothing? 13: Frankenstein 14: Angry ^ 15: Orb animation thing,
       * ?? 16: ?? 19: Mushroom castle boss thing
       */

      if (life.getParentMobOid() != 0) {
         MapleMonster parentMob = life.getMap().getMonsterByOid(life.getParentMobOid());
         if (parentMob != null && parentMob.isAlive()) {
            writer.write(packet.getEffect() != 0 ? packet.getEffect() : -3);
            writer.writeInt(life.getParentMobOid());
         } else {
            encodeParentlessMobSpawnEffect(writer, packet.isNewSpawn(), packet.getEffect());
         }
      } else {
         encodeParentlessMobSpawnEffect(writer, packet.isNewSpawn(), packet.getEffect());
      }

      writer.write(life.getTeam());
      writer.writeInt(0); // getItemEffect
   }

   protected void encodeParentlessMobSpawnEffect(MaplePacketLittleEndianWriter writer, boolean newSpawn, int effect) {
      if (effect > 0) {
         writer.write(effect);
         writer.write(0);
         writer.writeShort(0);
         if (effect == 15) {
            writer.write(0);
         }
      }
      writer.write(newSpawn ? -2 : -1);
   }

   /**
    * Internal function to handler monster spawning and controlling.
    */
   protected void controlMonster(MaplePacketLittleEndianWriter writer, ControlMonster packet) {
      writer.write(packet.isAggro() ? 2 : 1);
      writer.writeInt(packet.getMonster().objectId());
      writer.write(packet.getMonster().getController() == null ? 5 : 1);
      writer.writeInt(packet.getMonster().id());
      writer.skip(15);
      writer.write(0x88);
      writer.skip(6);
      writer.writePos(packet.getMonster().position());
      writer.write(packet.getMonster().stance());
      writer.writeShort(0); //Origin FH //life.getStartFh()
      writer.writeShort(packet.getMonster().fh());


      /*
       * -4: Fake -3: Appear after linked mob is dead -2: Fade in 1: Smoke 3:
       * King Slime spawn 4: Summoning rock thing, used for 3rd job? 6:
       * Magical shit 7: Smoke shit 8: 'The Boss' 9/10: Grim phantom shit?
       * 11/12: Nothing? 13: Frankenstein 14: Angry ^ 15: Orb animation thing,
       * ?? 16: ?? 19: Mushroom castle boss thing
       */

      if (packet.getMonster().getParentMobOid() != 0) {
         MapleMonster parentMob = packet.getMonster().getMap().getMonsterByOid(packet.getMonster().getParentMobOid());
         if (parentMob != null && parentMob.isAlive()) {
            writer.write(-3);
            writer.writeInt(packet.getMonster().getParentMobOid());
         } else {
            encodeParentlessMobSpawnEffect(writer, packet.isNewSpawn(), 0);
         }
      } else {
         encodeParentlessMobSpawnEffect(writer, packet.isNewSpawn(), 0);
      }

      writer.write(packet.getMonster().getTeam());
      writer.writeInt(0); // getItemEffect
   }

   /**
    * Makes a monster invisible for Ariant PQ.
    */
   protected void makeMonsterInvisible(MaplePacketLittleEndianWriter writer, MakeMonsterInvisible packet) {
      writer.write(0);
      writer.writeInt(packet.monsterObjectId());
   }

   protected void sendCannotSpawnKite(MaplePacketLittleEndianWriter writer, CannotSpawnKite packet) {
   }
}