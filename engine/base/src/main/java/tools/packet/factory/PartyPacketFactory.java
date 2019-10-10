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
      registry.setHandler(PartyCreated.class, packet -> this.partyCreated((PartyCreated) packet));
      registry.setHandler(PartyInvite.class, packet -> this.partyInvite((PartyInvite) packet));
      registry.setHandler(PartySearchInvite.class, packet -> this.partySearchInvite((PartySearchInvite) packet));
      registry.setHandler(PartyStatusMessage.class, packet -> this.partyStatusMessage((PartyStatusMessage) packet));
      registry.setHandler(PartyPortal.class, packet -> this.partyPortal((PartyPortal) packet));
      registry.setHandler(UpdateParty.class, packet -> this.updateParty((UpdateParty) packet));
      registry.setHandler(UpdatePartyMemberHp.class, packet -> this.updatePartyMemberHP((UpdatePartyMemberHp) packet));
   }

   protected byte[] partyCreated(PartyCreated packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.PARTY_OPERATION.getValue());
      mplew.write(8);
      mplew.writeInt(packet.getParty().getId());

      Map<Integer, MapleDoor> partyDoors = packet.getParty().getDoors();
      if (partyDoors.size() > 0) {
         MapleDoor door = partyDoors.get(packet.getPartyCharacterId());

         if (door != null) {
            MapleDoorObject mdo = door.getAreaDoor();
            mplew.writeInt(mdo.getTo().getId());
            mplew.writeInt(mdo.getFrom().getId());
            mplew.writeInt(mdo.getPosition().x);
            mplew.writeInt(mdo.getPosition().y);
         } else {
            mplew.writeInt(999999999);
            mplew.writeInt(999999999);
            mplew.writeInt(0);
            mplew.writeInt(0);
         }
      } else {
         mplew.writeInt(999999999);
         mplew.writeInt(999999999);
         mplew.writeInt(0);
         mplew.writeInt(0);
      }
      return mplew.getPacket();
   }

   protected byte[] partyInvite(PartyInvite packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.PARTY_OPERATION.getValue());
      mplew.write(4);
      mplew.writeInt(packet.partyId());
      mplew.writeMapleAsciiString(packet.fromCharacterName());
      mplew.write(0);
      return mplew.getPacket();
   }

   protected byte[] partySearchInvite(PartySearchInvite packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.PARTY_OPERATION.getValue());
      mplew.write(4);
      mplew.writeInt(packet.partyId());
      mplew.writeMapleAsciiString("PS: " + packet.fromCharacterName());
      mplew.write(0);
      return mplew.getPacket();
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
   protected byte[] partyStatusMessage(PartyStatusMessage packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.PARTY_OPERATION.getValue());
      mplew.write(packet.message());
      if (packet.fromCharacterName().isDefined()) {
         mplew.writeMapleAsciiString(packet.fromCharacterName().get());
      }
      return mplew.getPacket();
   }

   protected byte[] partyPortal(PartyPortal packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.PARTY_OPERATION.getValue());
      mplew.writeShort(0x23);
      mplew.writeInt(packet.townId());
      mplew.writeInt(packet.targetId());
      mplew.writePos(packet.position());
      return mplew.getPacket();
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

   protected byte[] updateParty(UpdateParty packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.PARTY_OPERATION.getValue());
      switch (packet.getOperation()) {
         case DISBAND:
         case EXPEL:
         case LEAVE:
            mplew.write(0x0C);
            mplew.writeInt(packet.getParty().getId());
            mplew.writeInt(packet.getTarget().getId());
            if (packet.getOperation() == DISBAND) {
               mplew.write(0);
               mplew.writeInt(packet.getParty().getId());
            } else {
               mplew.write(1);
               if (packet.getOperation() == EXPEL) {
                  mplew.write(1);
               } else {
                  mplew.write(0);
               }
               mplew.writeMapleAsciiString(packet.getTarget().getName());
               addPartyStatus(packet.getForChannel(), packet.getParty(), mplew, false);
            }
            break;
         case JOIN:
            mplew.write(0xF);
            mplew.writeInt(packet.getParty().getId());
            mplew.writeMapleAsciiString(packet.getTarget().getName());
            addPartyStatus(packet.getForChannel(), packet.getParty(), mplew, false);
            break;
         case SILENT_UPDATE:
         case LOG_ONOFF:
            mplew.write(0x7);
            mplew.writeInt(packet.getParty().getId());
            addPartyStatus(packet.getForChannel(), packet.getParty(), mplew, false);
            break;
         case CHANGE_LEADER:
            mplew.write(0x1B);
            mplew.writeInt(packet.getTarget().getId());
            mplew.write(0);
            break;
      }
      return mplew.getPacket();
   }

   protected byte[] updatePartyMemberHP(UpdatePartyMemberHp packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.UPDATE_PARTYMEMBER_HP.getValue());
      mplew.writeInt(packet.characterId());
      mplew.writeInt(packet.currentHp());
      mplew.writeInt(packet.maximumHp());
      return mplew.getPacket();
   }
}