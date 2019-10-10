package tools.packet.factory;

import static net.server.world.PartyOperation.DISBAND;
import static net.server.world.PartyOperation.EXPEL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.opcodes.SendOpcode;
import net.server.world.MapleParty;
import net.server.world.MaplePartyCharacter;
import server.maps.MapleDoor;
import server.maps.MapleDoorObject;
import tools.StringUtil;
import tools.data.output.LittleEndianWriter;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.party.PartyCreated;
import tools.packet.party.PartyInvite;
import tools.packet.party.PartyPortal;
import tools.packet.party.PartySearchInvite;
import tools.packet.party.PartyStatusMessage;
import tools.packet.party.UpdateParty;
import tools.packet.party.UpdatePartyMemberHp;

public class PartyPacketFactory extends AbstractPacketFactory {
   private static PartyPacketFactory instance;

   public static PartyPacketFactory getInstance() {
      if (instance == null) {
         instance = new PartyPacketFactory();
      }
      return instance;
   }

   private PartyPacketFactory() {
      registry.setHandler(PartyCreated.class, packet -> create(SendOpcode.PARTY_OPERATION, this::partyCreated, packet));
      registry.setHandler(PartyInvite.class, packet -> create(SendOpcode.PARTY_OPERATION, this::partyInvite, packet));
      registry.setHandler(PartySearchInvite.class, packet -> create(SendOpcode.PARTY_OPERATION, this::partySearchInvite, packet));
      registry.setHandler(PartyStatusMessage.class, packet -> create(SendOpcode.PARTY_OPERATION, this::partyStatusMessage, packet));
      registry.setHandler(PartyPortal.class, packet -> create(SendOpcode.PARTY_OPERATION, this::partyPortal, packet));
      registry.setHandler(UpdateParty.class, packet -> create(SendOpcode.PARTY_OPERATION, this::updateParty, packet));
      registry.setHandler(UpdatePartyMemberHp.class, packet -> create(SendOpcode.UPDATE_PARTYMEMBER_HP, this::updatePartyMemberHP, packet));
   }

   protected void partyCreated(MaplePacketLittleEndianWriter writer, PartyCreated packet) {
      writer.write(8);
      writer.writeInt(packet.getParty().getId());

      Map<Integer, MapleDoor> partyDoors = packet.getParty().getDoors();
      if (partyDoors.size() > 0) {
         MapleDoor door = partyDoors.get(packet.getPartyCharacterId());

         if (door != null) {
            MapleDoorObject mdo = door.getAreaDoor();
            writer.writeInt(mdo.getTo().getId());
            writer.writeInt(mdo.getFrom().getId());
            writer.writeInt(mdo.getPosition().x);
            writer.writeInt(mdo.getPosition().y);
         } else {
            writer.writeInt(999999999);
            writer.writeInt(999999999);
            writer.writeInt(0);
            writer.writeInt(0);
         }
      } else {
         writer.writeInt(999999999);
         writer.writeInt(999999999);
         writer.writeInt(0);
         writer.writeInt(0);
      }
   }

   protected void partyInvite(MaplePacketLittleEndianWriter writer, PartyInvite packet) {
      writer.write(4);
      writer.writeInt(packet.partyId());
      writer.writeMapleAsciiString(packet.fromCharacterName());
      writer.write(0);
   }

   protected void partySearchInvite(MaplePacketLittleEndianWriter writer, PartySearchInvite packet) {
      writer.write(4);
      writer.writeInt(packet.partyId());
      writer.writeMapleAsciiString("PS: " + packet.fromCharacterName());
      writer.write(0);
   }

   /**
    * 10: A beginner can't create a party. 1/5/6/11/14/19: Your request for a
    * party didn't work due to an unexpected error. 12: Quit as leader of the
    * party. 13: You have yet to join a party.
    * 16: Already have joined a party. 17: The party you're trying to join is
    * already in full capacity. 19: Unable to find the requested character in
    * this channel. 25: Cannot kick another user in this map. 28/29: Leadership
    * can only be given to a party member in the vicinity. 30: Change leadership
    * only on same channel.
    * 21: Player is blocking any party invitations, 22: Player is taking care of
    * another invitation, 23: Player have denied request to the party.
    *
    * @return
    */
   protected void partyStatusMessage(MaplePacketLittleEndianWriter writer, PartyStatusMessage packet) {
      writer.write(packet.message());
      if (packet.fromCharacterName().isDefined()) {
         writer.writeMapleAsciiString(packet.fromCharacterName().get());
      }
   }

   protected void partyPortal(MaplePacketLittleEndianWriter writer, PartyPortal packet) {
      writer.writeShort(0x23);
      writer.writeInt(packet.townId());
      writer.writeInt(packet.targetId());
      writer.writePos(packet.position());
   }

   protected void addPartyStatus(int forChannel, MapleParty party, LittleEndianWriter lew, boolean leaving) {
      List<MaplePartyCharacter> partymembers = new ArrayList<>(party.getMembers());
      while (partymembers.size() < 6) {
         partymembers.add(new MaplePartyCharacter());
      }
      for (MaplePartyCharacter partychar : partymembers) {
         lew.writeInt(partychar.getId());
      }
      for (MaplePartyCharacter partychar : partymembers) {
         lew.writeAsciiString(StringUtil.getRightPaddedStr(partychar.getName(), '\0', 13));
      }
      for (MaplePartyCharacter partychar : partymembers) {
         lew.writeInt(partychar.getJobId());
      }
      for (MaplePartyCharacter partychar : partymembers) {
         lew.writeInt(partychar.getLevel());
      }
      for (MaplePartyCharacter partychar : partymembers) {
         if (partychar.isOnline()) {
            lew.writeInt(partychar.getChannel() - 1);
         } else {
            lew.writeInt(-2);
         }
      }
      lew.writeInt(party.getLeader().getId());
      for (MaplePartyCharacter partychar : partymembers) {
         if (partychar.getChannel() == forChannel) {
            lew.writeInt(partychar.getMapId());
         } else {
            lew.writeInt(0);
         }
      }

      Map<Integer, MapleDoor> partyDoors = party.getDoors();
      for (MaplePartyCharacter partychar : partymembers) {
         if (partychar.getChannel() == forChannel && !leaving) {
            if (partyDoors.size() > 0) {
               MapleDoor door = partyDoors.get(partychar.getId());
               if (door != null) {
                  MapleDoorObject mdo = door.getTownDoor();
                  lew.writeInt(mdo.getTown().getId());
                  lew.writeInt(mdo.getArea().getId());
                  lew.writeInt(mdo.getPosition().x);
                  lew.writeInt(mdo.getPosition().y);
               } else {
                  lew.writeInt(999999999);
                  lew.writeInt(999999999);
                  lew.writeInt(0);
                  lew.writeInt(0);
               }
            } else {
               lew.writeInt(999999999);
               lew.writeInt(999999999);
               lew.writeInt(0);
               lew.writeInt(0);
            }
         } else {
            lew.writeInt(999999999);
            lew.writeInt(999999999);
            lew.writeInt(0);
            lew.writeInt(0);
         }
      }
   }

   protected void updateParty(MaplePacketLittleEndianWriter writer, UpdateParty packet) {
      switch (packet.getOperation()) {
         case DISBAND:
         case EXPEL:
         case LEAVE:
            writer.write(0x0C);
            writer.writeInt(packet.getParty().getId());
            writer.writeInt(packet.getTarget().getId());
            if (packet.getOperation() == DISBAND) {
               writer.write(0);
               writer.writeInt(packet.getParty().getId());
            } else {
               writer.write(1);
               if (packet.getOperation() == EXPEL) {
                  writer.write(1);
               } else {
                  writer.write(0);
               }
               writer.writeMapleAsciiString(packet.getTarget().getName());
               addPartyStatus(packet.getForChannel(), packet.getParty(), writer, false);
            }
            break;
         case JOIN:
            writer.write(0xF);
            writer.writeInt(packet.getParty().getId());
            writer.writeMapleAsciiString(packet.getTarget().getName());
            addPartyStatus(packet.getForChannel(), packet.getParty(), writer, false);
            break;
         case SILENT_UPDATE:
         case LOG_ONOFF:
            writer.write(0x7);
            writer.writeInt(packet.getParty().getId());
            addPartyStatus(packet.getForChannel(), packet.getParty(), writer, false);
            break;
         case CHANGE_LEADER:
            writer.write(0x1B);
            writer.writeInt(packet.getTarget().getId());
            writer.write(0);
            break;
      }
   }

   protected void updatePartyMemberHP(MaplePacketLittleEndianWriter writer, UpdatePartyMemberHp packet) {
      writer.writeInt(packet.characterId());
      writer.writeInt(packet.currentHp());
      writer.writeInt(packet.maximumHp());
   }
}