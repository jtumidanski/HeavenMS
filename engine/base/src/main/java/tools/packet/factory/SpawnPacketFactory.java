package tools.packet.factory;

import java.awt.Point;
import java.util.Set;

import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import client.MapleMount;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.newyear.NewYearCardRecord;
import constants.ItemConstants;
import net.opcodes.SendOpcode;
import net.server.guild.MapleGuildSummary;
import server.life.MapleMonster;
import server.maps.MapleDragon;
import server.maps.MapleMiniGame;
import server.maps.MaplePlayerShop;
import tools.FilePrinter;
import tools.Randomizer;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;
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
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof SpawnPortal) {
         return create(this::spawnPortal, packetInput);
      } else if (packetInput instanceof SpawnDoor) {
         return create(this::spawnDoor, packetInput);
      } else if (packetInput instanceof RemoveDoor) {
         return create(this::removeDoor, packetInput);
      } else if (packetInput instanceof SpawnSummon) {
         return create(this::spawnSummon, packetInput);
      } else if (packetInput instanceof SpawnNPC) {
         return create(this::spawnNPC, packetInput);
      } else if (packetInput instanceof SpawnNPCRequestController) {
         return create(this::spawnNPCRequestController, packetInput);
      } else if (packetInput instanceof RemoveMonsterInvisibility) {
         return create(this::removeMonsterInvisibility, packetInput);
      } else if (packetInput instanceof SpawnFakeMonster) {
         return create(this::spawnFakeMonster, packetInput);
      } else if (packetInput instanceof MakeMonsterReal) {
         return create(this::makeMonsterReal, packetInput);
      } else if (packetInput instanceof StopMonsterControl) {
         return create(this::stopControllingMonster, packetInput);
      } else if (packetInput instanceof SpawnPlayer) {
         return create(this::spawnPlayerMapObject, packetInput);
      } else if (packetInput instanceof SpawnKite) {
         return create(this::spawnKite, packetInput);
      } else if (packetInput instanceof SpawnMist) {
         return create(this::spawnMist, packetInput);
      } else if (packetInput instanceof ShowPet) {
         return create(this::showPet, packetInput);
      } else if (packetInput instanceof SpawnHiredMerchant) {
         return create(this::spawnHiredMerchantBox, packetInput);
      } else if (packetInput instanceof SpawnPlayerNPC) {
         return create(this::spawnPlayerNPC, packetInput);
      } else if (packetInput instanceof RemoveNPCController) {
         return create(this::removeNPCController, packetInput);
      } else if (packetInput instanceof SpawnGuide) {
         return create(this::spawnGuide, packetInput);
      } else if (packetInput instanceof SpawnDragon) {
         return create(this::spawnDragon, packetInput);
      } else if (packetInput instanceof SpawnMonster) {
         return create(this::spawnMonster, packetInput);
      } else if (packetInput instanceof ControlMonster) {
         return create(this::controlMonster, packetInput);
      } else if (packetInput instanceof MakeMonsterInvisible) {
         return create(this::makeMonsterInvisible, packetInput);
      } else if (packetInput instanceof CannotSpawnKite) {
         return create(this::sendCannotSpawnKite, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }

   /**
    * Gets a packet to spawn a portal.
    *
    * @return The portal spawn packet.
    */
   protected byte[] spawnPortal(SpawnPortal packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(14);
      mplew.writeShort(SendOpcode.SPAWN_PORTAL.getValue());
      mplew.writeInt(packet.townId());
      mplew.writeInt(packet.targetId());
      mplew.writePos(packet.position());
      return mplew.getPacket();
   }

   /**
    * Gets a packet to spawn a door.
    *
    * @return The remove door packet.
    */
   protected byte[] spawnDoor(SpawnDoor packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(11);
      mplew.writeShort(SendOpcode.SPAWN_DOOR.getValue());
      mplew.writeBool(packet.launched());
      mplew.writeInt(packet.ownerId());
      mplew.writePos(packet.position());
      return mplew.getPacket();
   }


   /**
    * Gets a packet to remove a door.
    *
    * @return The remove door packet.
    */
   protected byte[] removeDoor(RemoveDoor packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(10);
      if (packet.town()) {
         mplew.writeShort(SendOpcode.SPAWN_PORTAL.getValue());
         mplew.writeInt(999999999);
         mplew.writeInt(999999999);
      } else {
         mplew.writeShort(SendOpcode.REMOVE_DOOR.getValue());
         mplew.write(0);
         mplew.writeInt(packet.ownerId());
      }
      return mplew.getPacket();
   }

   /**
    * Gets a packet to spawn a special map object.
    *
    * @return The spawn packet for the map object.
    */
   protected byte[] spawnSummon(SpawnSummon packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(25);
      mplew.writeShort(SendOpcode.SPAWN_SPECIAL_MAPOBJECT.getValue());
      mplew.writeInt(packet.ownerId());
      mplew.writeInt(packet.objectId());
      mplew.writeInt(packet.skillId());
      mplew.write(0x0A); //v83
      mplew.write(packet.skillLevel());
      mplew.writePos(packet.position());
      mplew.write(packet.stance());    //bMoveAction & foothold, found thanks to Rien dev team
      mplew.writeShort(0);
      mplew.write(packet.movementType()); // 0 = don't move, 1 = follow (4th mage summons?), 2/4 = only tele follow, 3 = bird follow
      mplew.write(packet.puppet() ? 0 : 1); // 0 and the summon can't attack - but puppets don't attack with 1 either ^.-
      mplew.write(packet.animated() ? 0 : 1);
      return mplew.getPacket();
   }

   protected byte[] spawnNPC(SpawnNPC packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(24);
      mplew.writeShort(SendOpcode.SPAWN_NPC.getValue());
      mplew.writeInt(packet.getNpc().getObjectId());
      mplew.writeInt(packet.getNpc().getId());
      mplew.writeShort(packet.getNpc().getPosition().x);
      mplew.writeShort(packet.getNpc().getCy());
      if (packet.getNpc().getF() == 1) {
         mplew.write(0);
      } else {
         mplew.write(1);
      }
      mplew.writeShort(packet.getNpc().getFh());
      mplew.writeShort(packet.getNpc().getRx0());
      mplew.writeShort(packet.getNpc().getRx1());
      mplew.write(1);
      return mplew.getPacket();
   }

   protected byte[] spawnNPCRequestController(SpawnNPCRequestController packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(23);
      mplew.writeShort(SendOpcode.SPAWN_NPC_REQUEST_CONTROLLER.getValue());
      mplew.write(1);
      mplew.writeInt(packet.getNpc().getObjectId());
      mplew.writeInt(packet.getNpc().getId());
      mplew.writeShort(packet.getNpc().getPosition().x);
      mplew.writeShort(packet.getNpc().getCy());
      if (packet.getNpc().getF() == 1) {
         mplew.write(0);
      } else {
         mplew.write(1);
      }
      mplew.writeShort(packet.getNpc().getFh());
      mplew.writeShort(packet.getNpc().getRx0());
      mplew.writeShort(packet.getNpc().getRx1());
      mplew.writeBool(packet.isMiniMap());
      return mplew.getPacket();
   }

   /**
    * Removes a monster invisibility.
    *
    * @return
    */
   protected byte[] removeMonsterInvisibility(RemoveMonsterInvisibility packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SPAWN_MONSTER_CONTROL.getValue());
      mplew.write(1);
      mplew.writeInt(packet.getMonster().getObjectId());
      return mplew.getPacket();
   }

   /**
    * Handles monsters not being targettable, such as Zakum's first body.
    *
    * @return The packet to spawn the mob as non-targettable.
    */
   protected byte[] spawnFakeMonster(SpawnFakeMonster packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SPAWN_MONSTER_CONTROL.getValue());
      mplew.write(1);
      mplew.writeInt(packet.getMonster().getObjectId());
      mplew.write(5);
      mplew.writeInt(packet.getMonster().getId());
      mplew.skip(15);
      mplew.write(0x88);
      mplew.skip(6);
      mplew.writePos(packet.getMonster().getPosition());
      mplew.write(packet.getMonster().getStance());
      mplew.writeShort(0);//life.getStartFh()
      mplew.writeShort(packet.getMonster().getFh());
      if (packet.getEffectId() > 0) {
         mplew.write(packet.getEffectId());
         mplew.write(0);
         mplew.writeShort(0);
      }
      mplew.writeShort(-2);
      mplew.write(packet.getMonster().getTeam());
      mplew.writeInt(0);
      return mplew.getPacket();
   }

   /**
    * Makes a monster previously spawned as non-targettable, targettable.
    *
    * @return The packet to make the mob targettable.
    */
   protected byte[] makeMonsterReal(MakeMonsterReal packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SPAWN_MONSTER.getValue());
      mplew.writeInt(packet.getMonster().getObjectId());
      mplew.write(5);
      mplew.writeInt(packet.getMonster().getId());
      mplew.skip(15);
      mplew.write(0x88);
      mplew.skip(6);
      mplew.writePos(packet.getMonster().getPosition());
      mplew.write(packet.getMonster().getStance());
      mplew.writeShort(0);//life.getStartFh()
      mplew.writeShort(packet.getMonster().getFh());
      mplew.writeShort(-1);
      mplew.writeInt(0);
      return mplew.getPacket();
   }

   /**
    * Gets a stop control monster packet.
    *
    * @return The stop control monster packet.
    */
   protected byte[] stopControllingMonster(StopMonsterControl packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(7);
      mplew.writeShort(SendOpcode.SPAWN_MONSTER_CONTROL.getValue());
      mplew.write(0);
      mplew.writeInt(packet.monsterId());
      return mplew.getPacket();
   }

   /**
    * Gets a packet spawning a player as a mapobject to other clients.
    *
    * @return The spawn player packet.
    */
   protected byte[] spawnPlayerMapObject(SpawnPlayer packet) {
      MapleCharacter chr = packet.getCharacter();
      MapleClient target = packet.getTarget();
      boolean enteringField = packet.isEnteringField();

      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SPAWN_PLAYER.getValue());
      mplew.writeInt(chr.getId());
      mplew.write(chr.getLevel()); //v83
      mplew.writeMapleAsciiString(chr.getName());
      if (chr.getGuildId() < 1) {
         mplew.writeMapleAsciiString("");
         mplew.write(new byte[6]);
      } else {
         MapleGuildSummary gs = chr.getClient().getWorldServer().getGuildSummary(chr.getGuildId(), chr.getWorld());
         if (gs != null) {
            mplew.writeMapleAsciiString(gs.getName());
            mplew.writeShort(gs.getLogoBG());
            mplew.write(gs.getLogoBGColor());
            mplew.writeShort(gs.getLogo());
            mplew.write(gs.getLogoColor());
         } else {
            mplew.writeMapleAsciiString("");
            mplew.write(new byte[6]);
         }
      }

      writeForeignBuffs(mplew, chr);

      mplew.writeShort(chr.getJob().getId());

                /* replace "mplew.writeShort(chr.getJob().getId())" with this snippet for 3rd person FJ animation on all classes
                if (chr.getJob().isA(MapleJob.HERMIT) || chr.getJob().isA(MapleJob.DAWNWARRIOR2) || chr.getJob().isA(MapleJob.NIGHTWALKER2)) {
			mplew.writeShort(chr.getJob().getId());
                } else {
			mplew.writeShort(412);
                }*/

      addCharLook(mplew, chr, false);
      mplew.writeInt(chr.getInventory(MapleInventoryType.CASH).countById(5110000));
      mplew.writeInt(chr.getItemEffect());
      mplew.writeInt(ItemConstants.getInventoryType(chr.getChair()) == MapleInventoryType.SETUP ? chr.getChair() : 0);

      if (enteringField) {
         Point spawnPos = new Point(chr.getPosition());
         spawnPos.y -= 42;
         mplew.writePos(spawnPos);
         mplew.write(6);
      } else {
         mplew.writePos(chr.getPosition());
         mplew.write(chr.getStance());
      }

      mplew.writeShort(0);//chr.getFh()
      mplew.write(0);
      MaplePet[] pet = chr.getPets();
      for (int i = 0; i < 3; i++) {
         if (pet[i] != null) {
            addPetInfo(mplew, pet[i], false);
         }
      }
      mplew.write(0); //end of pets
      if (chr.getMount() == null) {
         mplew.writeInt(1); // mob level
         mplew.writeLong(0); // mob exp + tiredness
      } else {
         mplew.writeInt(chr.getMount().getLevel());
         mplew.writeInt(chr.getMount().getExp());
         mplew.writeInt(chr.getMount().getTiredness());
      }

      MaplePlayerShop mps = chr.getPlayerShop();
      if (mps != null && mps.isOwner(chr)) {
         if (mps.hasFreeSlot()) {
            addAnnounceBox(mplew, mps, mps.getVisitors().length);
         } else {
            addAnnounceBox(mplew, mps, 1);
         }
      } else {
         MapleMiniGame miniGame = chr.getMiniGame();
         if (miniGame != null && miniGame.isOwner(chr)) {
            if (miniGame.hasFreeSlot()) {
               addAnnounceBox(mplew, miniGame, 1, 0);
            } else {
               addAnnounceBox(mplew, miniGame, 2, miniGame.isMatchInProgress() ? 1 : 0);
            }
         } else {
            mplew.write(0);
         }
      }

      if (chr.getChalkboard() != null) {
         mplew.write(1);
         mplew.writeMapleAsciiString(chr.getChalkboard());
      } else {
         mplew.write(0);
      }
      addRingLook(mplew, chr, true);  // crush
      addRingLook(mplew, chr, false); // friendship
      addMarriageRingLook(target, mplew, chr);
      encodeNewYearCardInfo(mplew, chr);  // new year seems to crash sometimes...
      mplew.write(0);
      mplew.write(0);
      mplew.write(chr.getTeam());//only needed in specific fields
      return mplew.getPacket();
   }

   protected void writeForeignBuffs(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
      mplew.writeInt(0);
      mplew.writeShort(0); //v83
      mplew.write(0xFC);
      mplew.write(1);
      if (chr.getBuffedValue(MapleBuffStat.MORPH) != null) {
         mplew.writeInt(2);
      } else {
         mplew.writeInt(0);
      }
      long buffmask = 0;
      Integer buffvalue = null;
      if (chr.getBuffedValue(MapleBuffStat.DARKSIGHT) != null && !chr.isHidden()) {
         buffmask |= MapleBuffStat.DARKSIGHT.getValue();
      }
      if (chr.getBuffedValue(MapleBuffStat.COMBO) != null) {
         buffmask |= MapleBuffStat.COMBO.getValue();
         buffvalue = chr.getBuffedValue(MapleBuffStat.COMBO);
      }
      if (chr.getBuffedValue(MapleBuffStat.SHADOWPARTNER) != null) {
         buffmask |= MapleBuffStat.SHADOWPARTNER.getValue();
      }
      if (chr.getBuffedValue(MapleBuffStat.SOULARROW) != null) {
         buffmask |= MapleBuffStat.SOULARROW.getValue();
      }
      if (chr.getBuffedValue(MapleBuffStat.MORPH) != null) {
         buffvalue = chr.getBuffedValue(MapleBuffStat.MORPH);
      }
      if (chr.getBuffedValue(MapleBuffStat.ENERGY_CHARGE) != null) {
         buffmask |= MapleBuffStat.ENERGY_CHARGE.getValue();
         buffvalue = chr.getBuffedValue(MapleBuffStat.ENERGY_CHARGE);
      }//AREN'T THESE
      mplew.writeInt((int) ((buffmask >> 32) & 0xffffffffL));
      if (buffvalue != null) {
         if (chr.getBuffedValue(MapleBuffStat.MORPH) != null) { //TEST
            mplew.writeShort(buffvalue);
         } else {
            mplew.write(buffvalue.byteValue());
         }
      }
      mplew.writeInt((int) (buffmask & 0xffffffffL));
      int CHAR_MAGIC_SPAWN = Randomizer.nextInt();
      mplew.skip(6);
      mplew.writeInt(CHAR_MAGIC_SPAWN);
      mplew.skip(11);
      mplew.writeInt(CHAR_MAGIC_SPAWN);//v74
      mplew.skip(11);
      mplew.writeInt(CHAR_MAGIC_SPAWN);
      mplew.writeShort(0);
      mplew.write(0);

      Integer bv = chr.getBuffedValue(MapleBuffStat.MONSTER_RIDING);
      if (bv != null) {
         MapleMount mount = chr.getMount();
         if (mount != null) {
            mplew.writeInt(mount.getItemId());
            mplew.writeInt(mount.getSkillId());
         } else {
            mplew.writeLong(0);
         }
      } else {
         mplew.writeLong(0);
      }

      mplew.writeInt(CHAR_MAGIC_SPAWN);
      mplew.skip(9);
      mplew.writeInt(CHAR_MAGIC_SPAWN);
      mplew.writeShort(0);
      mplew.writeInt(0); // actually not 0, why is it 0 then?
      mplew.skip(10);
      mplew.writeInt(CHAR_MAGIC_SPAWN);
      mplew.skip(13);
      mplew.writeInt(CHAR_MAGIC_SPAWN);
      mplew.writeShort(0);
      mplew.write(0);
   }

   protected void addPetInfo(final MaplePacketLittleEndianWriter mplew, MaplePet pet, boolean showpet) {
      mplew.write(1);
      if (showpet) {
         mplew.write(0);
      }

      mplew.writeInt(pet.id());
      mplew.writeMapleAsciiString(pet.name());
      mplew.writeLong(pet.uniqueId());
      mplew.writePos(pet.pos());
      mplew.write(pet.stance());
      mplew.writeInt(pet.fh());
   }

   /**
    * Adds a announcement box to an existing MaplePacketLittleEndianWriter.
    *
    * @param mplew The MaplePacketLittleEndianWriter to add an announcement box to.
    * @param shop  The shop to announce.
    */
   protected void addAnnounceBox(final MaplePacketLittleEndianWriter mplew, MaplePlayerShop shop, int availability) {
      mplew.write(4);
      mplew.writeInt(shop.getObjectId());
      mplew.writeMapleAsciiString(shop.getDescription());
      mplew.write(0);
      mplew.write(0);
      mplew.write(1);
      mplew.write(availability);
      mplew.write(0);
   }

   protected void encodeNewYearCardInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
      Set<NewYearCardRecord> newyears = chr.getReceivedNewYearRecords();
      if (!newyears.isEmpty()) {
         mplew.write(1);

         mplew.writeInt(newyears.size());
         for (NewYearCardRecord nyc : newyears) {
            mplew.writeInt(nyc.getId());
         }
      } else {
         mplew.write(0);
      }
   }

   protected byte[] spawnKite(SpawnKite packet) {
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SPAWN_KITE.getValue());
      mplew.writeInt(packet.objectId());
      mplew.writeInt(packet.itemId());
      mplew.writeMapleAsciiString(packet.message());
      mplew.writeMapleAsciiString(packet.name());
      mplew.writeShort(packet.position().x);
      mplew.writeShort(packet.ft());
      return mplew.getPacket();
   }

   protected byte[] spawnMist(SpawnMist packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SPAWN_MIST.getValue());
      mplew.writeInt(packet.getObjectId());
      mplew.writeInt(packet.getMist().isMobMist() ? 0 : packet.getMist().isPoisonMist() ? 1 : packet.getMist().isRecoveryMist() ? 4 : 2); // mob mist = 0, player poison = 1, smokescreen = 2, unknown = 3, recovery = 4
      mplew.writeInt(packet.getOwnerId());
      mplew.writeInt(packet.getSkillId());
      mplew.write(packet.getLevel());
      mplew.writeShort(packet.getMist().getSkillDelay()); // Skill delay
      mplew.writeInt(packet.getMist().getBox().x);
      mplew.writeInt(packet.getMist().getBox().y);
      mplew.writeInt(packet.getMist().getBox().x + packet.getMist().getBox().width);
      mplew.writeInt(packet.getMist().getBox().y + packet.getMist().getBox().height);
      mplew.writeInt(0);
      return mplew.getPacket();
   }

   protected byte[] showPet(ShowPet packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SPAWN_PET.getValue());
      mplew.writeInt(packet.getCharacter().getId());
      mplew.write(packet.getCharacter().getPetIndex(packet.getPet()));
      if (packet.isRemove()) {
         mplew.write(0);
         mplew.write(packet.isHunger() ? 1 : 0);
      } else {
         addPetInfo(mplew, packet.getPet(), true);
      }
      return mplew.getPacket();
   }

   protected byte[] spawnHiredMerchantBox(SpawnHiredMerchant packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SPAWN_HIRED_MERCHANT.getValue());
      mplew.writeInt(packet.getHiredMerchant().getOwnerId());
      mplew.writeInt(packet.getHiredMerchant().getItemId());
      mplew.writeShort((short) packet.getHiredMerchant().getPosition().getX());
      mplew.writeShort((short) packet.getHiredMerchant().getPosition().getY());
      mplew.writeShort(0);
      mplew.writeMapleAsciiString(packet.getHiredMerchant().getOwner());
      mplew.write(0x05);
      mplew.writeInt(packet.getHiredMerchant().getObjectId());
      mplew.writeMapleAsciiString(packet.getHiredMerchant().getDescription());
      mplew.write(packet.getHiredMerchant().getItemId() % 100);
      mplew.write(new byte[]{1, 4});
      return mplew.getPacket();
   }

   protected byte[] spawnPlayerNPC(SpawnPlayerNPC packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SPAWN_NPC_REQUEST_CONTROLLER.getValue());
      mplew.write(1);
      mplew.writeInt(packet.getPlayerNPC().getObjectId());
      mplew.writeInt(packet.getPlayerNPC().getScriptId());
      mplew.writeShort(packet.getPlayerNPC().getPosition().x);
      mplew.writeShort(packet.getPlayerNPC().getCY());
      mplew.write(packet.getPlayerNPC().getDirection());
      mplew.writeShort(packet.getPlayerNPC().getFH());
      mplew.writeShort(packet.getPlayerNPC().getRX0());
      mplew.writeShort(packet.getPlayerNPC().getRX1());
      mplew.write(1);
      return mplew.getPacket();
   }

   protected byte[] removeNPCController(RemoveNPCController packet) {
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

      mplew.writeShort(SendOpcode.SPAWN_NPC_REQUEST_CONTROLLER.getValue());
      mplew.write(0);
      mplew.writeInt(packet.objectId());

      return mplew.getPacket();
   }

   protected byte[] spawnGuide(SpawnGuide packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);
      mplew.writeShort(SendOpcode.SPAWN_GUIDE.getValue());
      if (packet.spawn()) {
         mplew.write(1);
      } else {
         mplew.write(0);
      }
      return mplew.getPacket();
   }

   protected byte[] spawnDragon(MapleDragon dragon) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SPAWN_DRAGON.getValue());
      mplew.writeInt(dragon.getOwner().getId());//objectid = owner id
      mplew.writeShort(dragon.getPosition().x);
      mplew.writeShort(0);
      mplew.writeShort(dragon.getPosition().y);
      mplew.writeShort(0);
      mplew.write(dragon.getStance());
      mplew.write(0);
      mplew.writeShort(dragon.getOwner().getJob().getId());
      return mplew.getPacket();
   }

   /**
    * Internal function to handler monster spawning and controlling.
    *
    * @return The spawn/control packet.
    */
   protected byte[] spawnMonster(SpawnMonster packet) {
      MapleMonster life = packet.getMonster();
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SPAWN_MONSTER.getValue());
      mplew.writeInt(life.getObjectId());
      mplew.write(life.getController() == null ? 5 : 1);
      mplew.writeInt(life.getId());
      mplew.skip(15);
      mplew.write(0x88);
      mplew.skip(6);
      mplew.writePos(life.getPosition());
      mplew.write(life.getStance());
      mplew.writeShort(0); //Origin FH //life.getStartFh()
      mplew.writeShort(life.getFh());

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
            mplew.write(packet.getEffect() != 0 ? packet.getEffect() : -3);
            mplew.writeInt(life.getParentMobOid());
         } else {
            encodeParentlessMobSpawnEffect(mplew, packet.isNewSpawn(), packet.getEffect());
         }
      } else {
         encodeParentlessMobSpawnEffect(mplew, packet.isNewSpawn(), packet.getEffect());
      }

      mplew.write(life.getTeam());
      mplew.writeInt(0); // getItemEffect
      return mplew.getPacket();
   }

   protected void encodeParentlessMobSpawnEffect(MaplePacketLittleEndianWriter mplew, boolean newSpawn, int effect) {
      if (effect > 0) {
         mplew.write(effect);
         mplew.write(0);
         mplew.writeShort(0);
         if (effect == 15) {
            mplew.write(0);
         }
      }
      mplew.write(newSpawn ? -2 : -1);
   }

   /**
    * Internal function to handler monster spawning and controlling.
    *
    * @return The spawn/control packet.
    */
   protected byte[] controlMonster(ControlMonster packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SPAWN_MONSTER_CONTROL.getValue());
      mplew.write(packet.isAggro() ? 2 : 1);
      mplew.writeInt(packet.getMonster().getObjectId());
      mplew.write(packet.getMonster().getController() == null ? 5 : 1);
      mplew.writeInt(packet.getMonster().getId());
      mplew.skip(15);
      mplew.write(0x88);
      mplew.skip(6);
      mplew.writePos(packet.getMonster().getPosition());
      mplew.write(packet.getMonster().getStance());
      mplew.writeShort(0); //Origin FH //life.getStartFh()
      mplew.writeShort(packet.getMonster().getFh());


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
            mplew.write(-3);
            mplew.writeInt(packet.getMonster().getParentMobOid());
         } else {
            encodeParentlessMobSpawnEffect(mplew, packet.isNewSpawn(), 0);
         }
      } else {
         encodeParentlessMobSpawnEffect(mplew, packet.isNewSpawn(), 0);
      }

      mplew.write(packet.getMonster().getTeam());
      mplew.writeInt(0); // getItemEffect
      return mplew.getPacket();
   }

   /**
    * Makes a monster invisible for Ariant PQ.
    */
   protected byte[] makeMonsterInvisible(MakeMonsterInvisible packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SPAWN_MONSTER_CONTROL.getValue());
      mplew.write(0);
      mplew.writeInt(packet.getMonster().getObjectId());
      return mplew.getPacket();
   }

   protected byte[] sendCannotSpawnKite(CannotSpawnKite packet) {
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.CANNOT_SPAWN_KITE.getValue());
      return mplew.getPacket();
   }
}