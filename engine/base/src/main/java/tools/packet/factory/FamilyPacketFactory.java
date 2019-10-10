package tools.packet.factory;

import java.util.ArrayList;
import java.util.List;

import client.MapleCharacter;
import client.MapleFamilyEntitlement;
import client.MapleFamilyEntry;
import net.opcodes.SendOpcode;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.family.FamilyGainReputation;
import tools.packet.family.FamilyJoinResponse;
import tools.packet.family.FamilyLogonNotice;
import tools.packet.family.FamilyMessage;
import tools.packet.family.FamilySummonRequest;
import tools.packet.family.GetFamilyInfo;
import tools.packet.family.LoadFamily;
import tools.packet.family.SendFamilyInvite;
import tools.packet.family.SeniorMessage;
import tools.packet.family.ShowPedigree;

public class FamilyPacketFactory extends AbstractPacketFactory {
   private static FamilyPacketFactory instance;

   public static FamilyPacketFactory getInstance() {
      if (instance == null) {
         instance = new FamilyPacketFactory();
      }
      return instance;
   }

   private FamilyPacketFactory() {
      registry.setHandler(LoadFamily.class, packet -> this.loadFamily((LoadFamily) packet));
      registry.setHandler(FamilyMessage.class, packet -> this.sendFamilyMessage((FamilyMessage) packet));
      registry.setHandler(GetFamilyInfo.class, packet -> this.getFamilyInfo((GetFamilyInfo) packet));
      registry.setHandler(ShowPedigree.class, packet -> this.showPedigree((ShowPedigree) packet));
      registry.setHandler(SendFamilyInvite.class, packet -> this.sendFamilyInvite((SendFamilyInvite) packet));
      registry.setHandler(FamilySummonRequest.class, packet -> this.sendFamilySummonRequest((FamilySummonRequest) packet));
      registry.setHandler(FamilyLogonNotice.class, packet -> this.sendFamilyLoginNotice((FamilyLogonNotice) packet));
      registry.setHandler(FamilyJoinResponse.class, packet -> this.sendFamilyJoinResponse((FamilyJoinResponse) packet));
      registry.setHandler(SeniorMessage.class, packet -> this.getSeniorMessage((SeniorMessage) packet));
      registry.setHandler(FamilyGainReputation.class, packet -> this.sendGainRep((FamilyGainReputation) packet));
   }

   protected byte[] loadFamily(LoadFamily packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.FAMILY_PRIVILEGE_LIST.getValue());
      mplew.writeInt(MapleFamilyEntitlement.values().length);
      for (int i = 0; i < MapleFamilyEntitlement.values().length; i++) {
         MapleFamilyEntitlement entitlement = MapleFamilyEntitlement.values()[i];
         mplew.write(i <= 1 ? 1 : 2); //type
         mplew.writeInt(entitlement.getRepCost());
         mplew.writeInt(entitlement.getUsageLimit());
         mplew.writeMapleAsciiString(entitlement.getName());
         mplew.writeMapleAsciiString(entitlement.getDescription());
      }
      return mplew.getPacket();
   }

   /**
    * Family Result Message
    * <p>
    * Possible values for <code>type</code>:<br>
    * 64: You cannot add this character as a junior.
    * 65: The name could not be found or is not online.
    * 66: You belong to the same family.
    * 67: You do not belong to the same family.<br>
    * 69: The character you wish to add as\r\na Junior must be in the same
    * map.<br>
    * 70: This character is already a Junior of another character.<br>
    * 71: The Junior you wish to add\r\nmust be at a lower rank.<br>
    * 72: The gap between you and your\r\njunior must be within 20 levels.<br>
    * 73: Another character has requested to add this character.\r\nPlease try
    * again later.<br>
    * 74: Another character has requested a summon.\r\nPlease try again
    * later.<br>
    * 75: The summons has failed. Your current location or state does not allow
    * a summons.<br>
    * 76: The family cannot extend more than 1000 generations from above and
    * below.<br>
    * 77: The Junior you wish to add\r\nmust be over Level 10.<br>
    * 78: You cannot add a Junior \r\nthat has requested to change worlds.<br>
    * 79: You cannot add a Junior \r\nsince you've requested to change
    * worlds.<br>
    * 80: Separation is not possible due to insufficient Mesos.\r\nYou will
    * need %d Mesos to\r\nseparate with a Senior.<br>
    * 81: Separation is not possible due to insufficient Mesos.\r\nYou will
    * need %d Mesos to\r\nseparate with a Junior.<br>
    * 82: The Entitlement does not apply because your level does not match the
    * corresponding area.<br>
    *
    * @return Family Result packet
    */
   protected byte[] sendFamilyMessage(FamilyMessage packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(6);
      mplew.writeShort(SendOpcode.FAMILY_RESULT.getValue());
      mplew.writeInt(packet.theType());
      mplew.writeInt(packet.mesos());
      return mplew.getPacket();
   }

   protected byte[] getFamilyInfo(GetFamilyInfo packet) {
      if (packet.getFamilyEntry() == null) {
         return getEmptyFamilyInfo();
      }
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.FAMILY_INFO_RESULT.getValue());
      mplew.writeInt(packet.getFamilyEntry().getReputation()); // cur rep left
      mplew.writeInt(packet.getFamilyEntry().getTotalReputation()); // tot rep left
      mplew.writeInt(packet.getFamilyEntry().getTodaysRep()); // todays rep
      mplew.writeShort(packet.getFamilyEntry().getJuniorCount()); // juniors added
      mplew.writeShort(2); // juniors allowed
      mplew.writeShort(0); //Unknown
      mplew.writeInt(packet.getFamilyEntry().getFamily().getLeader().getChrId()); // Leader ID (Allows setting message)
      mplew.writeMapleAsciiString(packet.getFamilyEntry().getFamily().getName());
      mplew.writeMapleAsciiString(packet.getFamilyEntry().getFamily().getMessage()); //family message
      mplew.writeInt(MapleFamilyEntitlement.values().length); //Entitlement info count
      for (MapleFamilyEntitlement entitlement : MapleFamilyEntitlement.values()) {
         mplew.writeInt(entitlement.ordinal()); //ID
         mplew.writeInt(packet.getFamilyEntry().isEntitlementUsed(entitlement) ? 1 : 0); //Used count
      }
      return mplew.getPacket();
   }

   protected byte[] getEmptyFamilyInfo() {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.FAMILY_INFO_RESULT.getValue());
      mplew.writeInt(0); // cur rep left
      mplew.writeInt(0); // tot rep left
      mplew.writeInt(0); // todays rep
      mplew.writeShort(0); // juniors added
      mplew.writeShort(2); // juniors allowed
      mplew.writeShort(0); //Unknown
      mplew.writeInt(0); // Leader ID (Allows setting message)
      mplew.writeMapleAsciiString("");
      mplew.writeMapleAsciiString(""); //family message
      mplew.writeInt(0);
      return mplew.getPacket();
   }

   protected byte[] showPedigree(ShowPedigree packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.FAMILY_CHART_RESULT.getValue());
      mplew.writeInt(packet.getFamilyEntry().getChrId()); //ID of viewed player's pedigree, can't be leader?
      List<MapleFamilyEntry> superJuniors = new ArrayList<>(4);
      boolean hasOtherJunior = false;
      int entryCount = 2; //2 guaranteed, leader and self
      entryCount += Math.min(2, packet.getFamilyEntry().getTotalSeniors());
      //needed since MaplePacketLittleEndianWriter doesn't have any seek functionality
      if (packet.getFamilyEntry().getSenior() != null) {
         if (packet.getFamilyEntry().getSenior().getJuniorCount() == 2) {
            entryCount++;
            hasOtherJunior = true;
         }
      }
      for (MapleFamilyEntry junior : packet.getFamilyEntry().getJuniors()) {
         if (junior == null) {
            continue;
         }
         entryCount++;
         for (MapleFamilyEntry superJunior : junior.getJuniors()) {
            if (superJunior == null) {
               continue;
            }
            entryCount++;
            superJuniors.add(superJunior);
         }
      }
      //write entries
      boolean missingEntries = entryCount == 2; //pedigree requires at least 3 entries to show leader, might only have 2 if leader's juniors leave
      if (missingEntries) {
         entryCount++;
      }
      mplew.writeInt(entryCount); //player count
      addPedigreeEntry(mplew, packet.getFamilyEntry().getFamily().getLeader());
      if (packet.getFamilyEntry().getSenior() != null) {
         if (packet.getFamilyEntry().getSenior().getSenior() != null) {
            addPedigreeEntry(mplew, packet.getFamilyEntry().getSenior().getSenior());
         }
         addPedigreeEntry(mplew, packet.getFamilyEntry().getSenior());
      }
      addPedigreeEntry(mplew, packet.getFamilyEntry());
      if (hasOtherJunior) { //must be sent after own entry
         MapleFamilyEntry otherJunior = packet.getFamilyEntry().getSenior().getOtherJunior(packet.getFamilyEntry());
         if (otherJunior != null) {
            addPedigreeEntry(mplew, otherJunior);
         }
      }
      if (missingEntries) {
         addPedigreeEntry(mplew, packet.getFamilyEntry());
      }
      for (MapleFamilyEntry junior : packet.getFamilyEntry().getJuniors()) {
         if (junior == null) {
            continue;
         }
         addPedigreeEntry(mplew, junior);
         for (MapleFamilyEntry superJunior : junior.getJuniors()) {
            if (superJunior != null) {
               addPedigreeEntry(mplew, superJunior);
            }
         }
      }
      mplew.writeInt(2 + superJuniors.size()); //member info count
      // 0 = total seniors, -1 = total members, otherwise junior count of ID
      mplew.writeInt(-1);
      mplew.writeInt(packet.getFamilyEntry().getFamily().getTotalMembers());
      mplew.writeInt(0);
      mplew.writeInt(packet.getFamilyEntry().getTotalSeniors()); //client subtracts provided seniors
      for (MapleFamilyEntry superJunior : superJuniors) {
         mplew.writeInt(superJunior.getChrId());
         mplew.writeInt(superJunior.getTotalJuniors());
      }
      mplew.writeInt(0); //another loop count (entitlements used)
      //mplew.writeInt(1); //entitlement index
      //mplew.writeInt(2); //times used
      mplew.writeShort(packet.getFamilyEntry().getJuniorCount() >= 2 ? 0 : 2); //0 disables Add button (only if viewing own pedigree)
      return mplew.getPacket();
   }

   protected void addPedigreeEntry(MaplePacketLittleEndianWriter mplew, MapleFamilyEntry entry) {
      MapleCharacter chr = entry.getChr();
      boolean isOnline = chr != null;
      mplew.writeInt(entry.getChrId()); //ID
      mplew.writeInt(entry.getSenior() != null ? entry.getSenior().getChrId() : 0); //parent ID
      mplew.writeShort(entry.getJob().getId()); //job id
      mplew.write(entry.getLevel()); //level
      mplew.writeBool(isOnline); //isOnline
      mplew.writeInt(entry.getReputation()); //current rep
      mplew.writeInt(entry.getTotalReputation()); //total rep
      mplew.writeInt(entry.getRepsToSenior()); //reps recorded to senior
      mplew.writeInt(entry.getTodaysRep());
      mplew.writeInt(isOnline ? ((chr.isAwayFromWorld() || chr.getCashShop().isOpened()) ? -1 : chr.getClient().getChannel() - 1) : 0);
      mplew.writeInt(isOnline ? (int) (chr.getLoggedInTime() / 60000) : 0); //time online in minutes
      mplew.writeMapleAsciiString(entry.getName()); //name
   }

   protected byte[] sendFamilyInvite(SendFamilyInvite packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.FAMILY_JOIN_REQUEST.getValue());
      mplew.writeInt(packet.characterId());
      mplew.writeMapleAsciiString(packet.inviter());
      return mplew.getPacket();
   }

   protected byte[] sendFamilySummonRequest(FamilySummonRequest packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.FAMILY_SUMMON_REQUEST.getValue());
      mplew.writeMapleAsciiString(packet.characterNameFrom());
      mplew.writeMapleAsciiString(packet.familyName());
      return mplew.getPacket();
   }

   protected byte[] sendFamilyLoginNotice(FamilyLogonNotice packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.FAMILY_NOTIFY_LOGIN_OR_LOGOUT.getValue());
      mplew.writeBool(packet.loggedIn());
      mplew.writeMapleAsciiString(packet.characterName());
      return mplew.getPacket();
   }

   protected byte[] sendFamilyJoinResponse(FamilyJoinResponse packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.FAMILY_JOIN_REQUEST_RESULT.getValue());
      mplew.write(packet.accepted() ? 1 : 0);
      mplew.writeMapleAsciiString(packet.characterNameAdded());
      return mplew.getPacket();
   }

   protected byte[] getSeniorMessage(SeniorMessage packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.FAMILY_JOIN_ACCEPTED.getValue());
      mplew.writeMapleAsciiString(packet.characterName());
      mplew.writeInt(0);
      return mplew.getPacket();
   }

   protected byte[] sendGainRep(FamilyGainReputation packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.FAMILY_REP_GAIN.getValue());
      mplew.writeInt(packet.gain());
      mplew.writeMapleAsciiString(packet.characterNameFrom());
      return mplew.getPacket();
   }
}