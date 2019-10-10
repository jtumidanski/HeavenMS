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
      registry.setHandler(LoadFamily.class, packet -> create(SendOpcode.FAMILY_PRIVILEGE_LIST, this::loadFamily, packet));
      registry.setHandler(FamilyMessage.class, packet -> create(SendOpcode.FAMILY_RESULT, this::sendFamilyMessage, packet, 6));
      registry.setHandler(GetFamilyInfo.class, packet -> create(SendOpcode.FAMILY_INFO_RESULT, this::getFamilyInfo, packet));
      registry.setHandler(ShowPedigree.class, packet -> create(SendOpcode.FAMILY_CHART_RESULT, this::showPedigree, packet));
      registry.setHandler(SendFamilyInvite.class, packet -> create(SendOpcode.FAMILY_JOIN_REQUEST, this::sendFamilyInvite, packet));
      registry.setHandler(FamilySummonRequest.class, packet -> create(SendOpcode.FAMILY_SUMMON_REQUEST, this::sendFamilySummonRequest, packet));
      registry.setHandler(FamilyLogonNotice.class, packet -> create(SendOpcode.FAMILY_NOTIFY_LOGIN_OR_LOGOUT, this::sendFamilyLoginNotice, packet));
      registry.setHandler(FamilyJoinResponse.class, packet -> create(SendOpcode.FAMILY_JOIN_REQUEST_RESULT, this::sendFamilyJoinResponse, packet));
      registry.setHandler(SeniorMessage.class, packet -> create(SendOpcode.FAMILY_JOIN_ACCEPTED, this::getSeniorMessage, packet));
      registry.setHandler(FamilyGainReputation.class, packet -> create(SendOpcode.FAMILY_REP_GAIN, this::sendGainRep, packet));
   }

   protected void loadFamily(MaplePacketLittleEndianWriter writer, LoadFamily packet) {
      writer.writeInt(MapleFamilyEntitlement.values().length);
      for (int i = 0; i < MapleFamilyEntitlement.values().length; i++) {
         MapleFamilyEntitlement entitlement = MapleFamilyEntitlement.values()[i];
         writer.write(i <= 1 ? 1 : 2); //type
         writer.writeInt(entitlement.getRepCost());
         writer.writeInt(entitlement.getUsageLimit());
         writer.writeMapleAsciiString(entitlement.getName());
         writer.writeMapleAsciiString(entitlement.getDescription());
      }
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
   protected void sendFamilyMessage(MaplePacketLittleEndianWriter writer, FamilyMessage packet) {
      writer.writeInt(packet.theType());
      writer.writeInt(packet.mesos());
   }

   protected void getFamilyInfo(MaplePacketLittleEndianWriter writer, GetFamilyInfo packet) {
      if (packet.getFamilyEntry() == null) {
         getEmptyFamilyInfo(writer);
         return;
      }
      writer.writeInt(packet.getFamilyEntry().getReputation()); // cur rep left
      writer.writeInt(packet.getFamilyEntry().getTotalReputation()); // tot rep left
      writer.writeInt(packet.getFamilyEntry().getTodaysRep()); // todays rep
      writer.writeShort(packet.getFamilyEntry().getJuniorCount()); // juniors added
      writer.writeShort(2); // juniors allowed
      writer.writeShort(0); //Unknown
      writer.writeInt(packet.getFamilyEntry().getFamily().getLeader().getChrId()); // Leader ID (Allows setting message)
      writer.writeMapleAsciiString(packet.getFamilyEntry().getFamily().getName());
      writer.writeMapleAsciiString(packet.getFamilyEntry().getFamily().getMessage()); //family message
      writer.writeInt(MapleFamilyEntitlement.values().length); //Entitlement info count
      for (MapleFamilyEntitlement entitlement : MapleFamilyEntitlement.values()) {
         writer.writeInt(entitlement.ordinal()); //ID
         writer.writeInt(packet.getFamilyEntry().isEntitlementUsed(entitlement) ? 1 : 0); //Used count
      }
   }

   protected void getEmptyFamilyInfo(MaplePacketLittleEndianWriter writer) {
      writer.writeInt(0); // cur rep left
      writer.writeInt(0); // tot rep left
      writer.writeInt(0); // todays rep
      writer.writeShort(0); // juniors added
      writer.writeShort(2); // juniors allowed
      writer.writeShort(0); //Unknown
      writer.writeInt(0); // Leader ID (Allows setting message)
      writer.writeMapleAsciiString("");
      writer.writeMapleAsciiString(""); //family message
      writer.writeInt(0);
   }

   protected void showPedigree(MaplePacketLittleEndianWriter writer, ShowPedigree packet) {
      writer.writeInt(packet.getFamilyEntry().getChrId()); //ID of viewed player's pedigree, can't be leader?
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
      writer.writeInt(entryCount); //player count
      addPedigreeEntry(writer, packet.getFamilyEntry().getFamily().getLeader());
      if (packet.getFamilyEntry().getSenior() != null) {
         if (packet.getFamilyEntry().getSenior().getSenior() != null) {
            addPedigreeEntry(writer, packet.getFamilyEntry().getSenior().getSenior());
         }
         addPedigreeEntry(writer, packet.getFamilyEntry().getSenior());
      }
      addPedigreeEntry(writer, packet.getFamilyEntry());
      if (hasOtherJunior) { //must be sent after own entry
         MapleFamilyEntry otherJunior = packet.getFamilyEntry().getSenior().getOtherJunior(packet.getFamilyEntry());
         if (otherJunior != null) {
            addPedigreeEntry(writer, otherJunior);
         }
      }
      if (missingEntries) {
         addPedigreeEntry(writer, packet.getFamilyEntry());
      }
      for (MapleFamilyEntry junior : packet.getFamilyEntry().getJuniors()) {
         if (junior == null) {
            continue;
         }
         addPedigreeEntry(writer, junior);
         for (MapleFamilyEntry superJunior : junior.getJuniors()) {
            if (superJunior != null) {
               addPedigreeEntry(writer, superJunior);
            }
         }
      }
      writer.writeInt(2 + superJuniors.size()); //member info count
      // 0 = total seniors, -1 = total members, otherwise junior count of ID
      writer.writeInt(-1);
      writer.writeInt(packet.getFamilyEntry().getFamily().getTotalMembers());
      writer.writeInt(0);
      writer.writeInt(packet.getFamilyEntry().getTotalSeniors()); //client subtracts provided seniors
      for (MapleFamilyEntry superJunior : superJuniors) {
         writer.writeInt(superJunior.getChrId());
         writer.writeInt(superJunior.getTotalJuniors());
      }
      writer.writeInt(0); //another loop count (entitlements used)
      //writer.writeInt(1); //entitlement index
      //writer.writeInt(2); //times used
      writer.writeShort(packet.getFamilyEntry().getJuniorCount() >= 2 ? 0 : 2); //0 disables Add button (only if viewing own pedigree)
   }

   protected void addPedigreeEntry(MaplePacketLittleEndianWriter writer, MapleFamilyEntry entry) {
      MapleCharacter chr = entry.getChr();
      boolean isOnline = chr != null;
      writer.writeInt(entry.getChrId()); //ID
      writer.writeInt(entry.getSenior() != null ? entry.getSenior().getChrId() : 0); //parent ID
      writer.writeShort(entry.getJob().getId()); //job id
      writer.write(entry.getLevel()); //level
      writer.writeBool(isOnline); //isOnline
      writer.writeInt(entry.getReputation()); //current rep
      writer.writeInt(entry.getTotalReputation()); //total rep
      writer.writeInt(entry.getRepsToSenior()); //reps recorded to senior
      writer.writeInt(entry.getTodaysRep());
      writer.writeInt(isOnline ? ((chr.isAwayFromWorld() || chr.getCashShop().isOpened()) ? -1 : chr.getClient().getChannel() - 1) : 0);
      writer.writeInt(isOnline ? (int) (chr.getLoggedInTime() / 60000) : 0); //time online in minutes
      writer.writeMapleAsciiString(entry.getName()); //name
   }

   protected void sendFamilyInvite(MaplePacketLittleEndianWriter writer, SendFamilyInvite packet) {
      writer.writeInt(packet.characterId());
      writer.writeMapleAsciiString(packet.inviter());
   }

   protected void sendFamilySummonRequest(MaplePacketLittleEndianWriter writer, FamilySummonRequest packet) {
      writer.writeMapleAsciiString(packet.characterNameFrom());
      writer.writeMapleAsciiString(packet.familyName());
   }

   protected void sendFamilyLoginNotice(MaplePacketLittleEndianWriter writer, FamilyLogonNotice packet) {
      writer.writeBool(packet.loggedIn());
      writer.writeMapleAsciiString(packet.characterName());
   }

   protected void sendFamilyJoinResponse(MaplePacketLittleEndianWriter writer, FamilyJoinResponse packet) {
      writer.write(packet.accepted() ? 1 : 0);
      writer.writeMapleAsciiString(packet.characterNameAdded());
   }

   protected void getSeniorMessage(MaplePacketLittleEndianWriter writer, SeniorMessage packet) {
      writer.writeMapleAsciiString(packet.characterName());
      writer.writeInt(0);
   }

   protected void sendGainRep(MaplePacketLittleEndianWriter writer, FamilyGainReputation packet) {
      writer.writeInt(packet.gain());
      writer.writeMapleAsciiString(packet.characterNameFrom());
   }
}