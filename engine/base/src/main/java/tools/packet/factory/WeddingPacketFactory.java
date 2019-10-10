package tools.packet.factory;

import client.MapleCharacter;
import client.inventory.Item;
import client.processor.CharacterProcessor;
import net.opcodes.SendOpcode;
import tools.StringUtil;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.wedding.MarriageRequest;
import tools.packet.wedding.MarriageResult;
import tools.packet.wedding.MarriageResultError;
import tools.packet.wedding.SendWishList;
import tools.packet.wedding.TakePhoto;
import tools.packet.wedding.WeddingGiftResult;
import tools.packet.wedding.WeddingInvitation;
import tools.packet.wedding.WeddingPartnerTransfer;
import tools.packet.wedding.WeddingProgress;

public class WeddingPacketFactory extends AbstractPacketFactory {
   /*
       00000000 CWeddingMan     struc ; (sizeof=0x104)
       00000000 vfptr           dd ?                    ; offset
       00000004 ___u1           $01CBC6800BD386B8A8FD818EAD990BEC ?
       0000000C m_mCharIDToMarriageNo ZMap<unsigned long,unsigned long,unsigned long> ?
       00000024 m_mReservationPending ZMap<unsigned long,ZRef<GW_WeddingReservation>,unsigned long> ?
       0000003C m_mReservationPendingGroom ZMap<unsigned long,ZRef<CUser>,unsigned long> ?
       00000054 m_mReservationPendingBride ZMap<unsigned long,ZRef<CUser>,unsigned long> ?
       0000006C m_mReservationStartUser ZMap<unsigned long,unsigned long,unsigned long> ?
       00000084 m_mReservationCompleted ZMap<unsigned long,ZRef<GW_WeddingReservation>,unsigned long> ?
       0000009C m_mGroomWishList ZMap<unsigned long,ZRef<ZArray<ZXString<char> > >,unsigned long> ?
       000000B4 m_mBrideWishList ZMap<unsigned long,ZRef<ZArray<ZXString<char> > >,unsigned long> ?
       000000CC m_mEngagementPending ZMap<unsigned long,ZRef<GW_MarriageRecord>,unsigned long> ?
       000000E4 m_nCurrentWeddingState dd ?
       000000E8 m_dwCurrentWeddingNo dd ?
       000000EC m_dwCurrentWeddingMap dd ?
       000000F0 m_bIsReservationLoaded dd ?
       000000F4 m_dwNumGuestBless dd ?
       000000F8 m_bPhotoSuccess dd ?
       000000FC m_tLastUpdate   dd ?
       00000100 m_bStartWeddingCeremony dd ?
       00000104 CWeddingMan     ends
   */

   private static WeddingPacketFactory instance;

   public static WeddingPacketFactory getInstance() {
      if (instance == null) {
         instance = new WeddingPacketFactory();
      }
      return instance;
   }

   private WeddingPacketFactory() {
      registry.setHandler(MarriageRequest.class, packet -> this.marriageRequest((MarriageRequest) packet));
      registry.setHandler(TakePhoto.class, packet -> this.takePhoto((TakePhoto) packet));
      registry.setHandler(MarriageResult.class, packet -> this.marriageResult((MarriageResult) packet));
      registry.setHandler(MarriageResultError.class, packet -> this.marriageResultError((MarriageResultError) packet));
      registry.setHandler(WeddingPartnerTransfer.class, packet -> this.weddingPartnerTransfer((WeddingPartnerTransfer) packet));
      registry.setHandler(WeddingProgress.class, packet -> this.weddingProgress((WeddingProgress) packet));
      registry.setHandler(WeddingInvitation.class, packet -> this.sendWeddingInvitation((WeddingInvitation) packet));
      registry.setHandler(SendWishList.class, packet -> this.sendWishList((SendWishList) packet));
      registry.setHandler(WeddingGiftResult.class, packet -> this.weddingGiftResult((WeddingGiftResult) packet));
   }

   /**
    * <name> has requested engagement. Will you accept this proposal?
    *
    * @return mplew
    */
   protected byte[] marriageRequest(MarriageRequest packet) {
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MARRIAGE_REQUEST.getValue());
      mplew.write(0); //mode, 0 = engage, 1 = cancel, 2 = answer.. etc
      mplew.writeMapleAsciiString(packet.name()); // name
      mplew.writeInt(packet.characterId()); // playerid
      return mplew.getPacket();
   }

   /**
    * A quick rundown of how (I think based off of enough BMS searching) WeddingPhoto_OnTakePhoto works:
    * - We send this packet with (first) the Groom / Bride IGNs
    * - We then send a fieldId (unsure about this part at the moment, 90% sure it's the id of the map)
    * - After this, we write an integer of the amount of characters within the current map (which is the Cake Map -- exclude users within Exit Map)
    * - Once we've retrieved the size of the characters, we begin to write information about them (Encode their name, guild, etc info)
    * - Now that we've Encoded our character data, we begin to Encode the ScreenShotPacket which requires a TemplateID, IGN, and their positioning
    * - Finally, after encoding all of our data, we send this packet out to a MapGen application server
    * - The MapGen server will then retrieve the packet byte array and convert the bytes into a ImageIO 2D JPG output
    * - The result after converting into a JPG will then be remotely uploaded to /weddings/ with ReservedGroomName_ReservedBrideName to be displayed on the web server.
    * <p>
    * - Will no longer continue Wedding Photos, needs a WvsMapGen :(
    *
    * @return mplew (MaplePacket) Byte array to be converted and read for byte[]->ImageIO
    */
   protected byte[] takePhoto(TakePhoto packet) { // OnIFailedAtWeddingPhotos
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.WEDDING_PHOTO.getValue()); // v53 header, convert -> v83
      mplew.writeMapleAsciiString(packet.getReservedGroomName());
      mplew.writeMapleAsciiString(packet.getReservedBrideName());
      mplew.writeInt(packet.getField()); // field id?
      mplew.writeInt(packet.getAttendees().size());

      for (MapleCharacter guest : packet.getAttendees()) {
         // Begin Avatar Encoding
         addCharLook(mplew, guest, false); // CUser::EncodeAvatar
         mplew.writeInt(30000); // v20 = *(_DWORD *)(v13 + 2192) -- new groom marriage ID??
         mplew.writeInt(30000); // v20 = *(_DWORD *)(v13 + 2192) -- new bride marriage ID??
         mplew.writeMapleAsciiString(guest.getName());

         guest.getGuild().ifPresentOrElse(guild -> {
            mplew.writeMapleAsciiString(guild.getName());
            mplew.writeShort(guild.getLogoBG());
            mplew.write(guild.getLogoBGColor());
            mplew.writeShort(guild.getLogo());
            mplew.write(guild.getLogoColor());
         }, () -> {
            mplew.writeMapleAsciiString("");
            mplew.writeShort(0);
            mplew.write(0);
            mplew.writeShort(0);
            mplew.write(0);
         });
         mplew.writeShort(guest.getPosition().x); // v18 = *(_DWORD *)(v13 + 3204);
         mplew.writeShort(guest.getPosition().y); // v20 = *(_DWORD *)(v13 + 3208);
         // Begin Screenshot Encoding
         mplew.write(1); // // if ( *(_DWORD *)(v13 + 288) ) { COutPacket::Encode1(&thisa, v20);
         // CPet::EncodeScreenShotPacket(*(CPet **)(v13 + 288), &thisa);
         mplew.writeInt(1); // dwTemplateID
         mplew.writeMapleAsciiString(guest.getName()); // m_sName
         mplew.writeShort(guest.getPosition().x); // m_ptCurPos.x
         mplew.writeShort(guest.getPosition().y); // m_ptCurPos.y
         mplew.write(guest.getStance()); // guest.m_bMoveAction
      }

      return mplew.getPacket();
   }

   /**
    * Enable spouse chat and their engagement ring without @relog
    *
    * @return mplew
    */
   protected byte[] marriageResult(MarriageResult packet) {
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MARRIAGE_RESULT.getValue());
      mplew.write(11);
      mplew.writeInt(packet.getMarriageId());
      mplew.writeInt(packet.getCharacter().getGender() == 0 ? packet.getCharacter().getId() : packet.getCharacter().getPartnerId());
      mplew.writeInt(packet.getCharacter().getGender() == 0 ? packet.getCharacter().getPartnerId() : packet.getCharacter().getId());
      mplew.writeShort(packet.isWedding() ? 3 : 1);
      if (packet.isWedding()) {
         mplew.writeInt(packet.getCharacter().getMarriageItemId());
         mplew.writeInt(packet.getCharacter().getMarriageItemId());
      } else {
         mplew.writeInt(1112803); // Engagement Ring's Outcome (doesn't matter for engagement)
         mplew.writeInt(1112803); // Engagement Ring's Outcome (doesn't matter for engagement)
      }
      mplew.writeAsciiString(StringUtil.getRightPaddedStr(packet.getCharacter().getGender() == 0 ? packet.getCharacter().getName() : CharacterProcessor.getInstance().getNameById(packet.getCharacter().getPartnerId()), '\0', 13));
      mplew.writeAsciiString(StringUtil.getRightPaddedStr(packet.getCharacter().getGender() == 0 ? CharacterProcessor.getInstance().getNameById(packet.getCharacter().getPartnerId()) : packet.getCharacter().getName(), '\0', 13));

      return mplew.getPacket();
   }

   /**
    * To exit the Engagement Window (Waiting for her response...), we send a GMS-like pop-up.
    *
    * @return mplew
    */
   protected byte[] marriageResultError(MarriageResultError packet) {
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MARRIAGE_RESULT.getValue());
      mplew.write(packet.message());
      if (packet.message() == 36) {
         mplew.write(1);
         mplew.writeMapleAsciiString("You are now engaged.");
      }
      return mplew.getPacket();
   }

   /**
    * The World Map includes 'loverPos' in which this packet controls
    *
    * @return mplew
    */
   protected byte[] weddingPartnerTransfer(WeddingPartnerTransfer packet) {
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.NOTIFY_MARRIED_PARTNER_MAP_TRANSFER.getValue());
      mplew.writeInt(packet.mapId());
      mplew.writeInt(packet.partnerId());
      return mplew.getPacket();
   }

   /**
    * The wedding packet to display Pelvis Bebop and enable the Wedding Ceremony Effect between two characters
    * CField_Wedding::OnWeddingProgress - Stages
    * CField_Wedding::OnWeddingCeremonyEnd - Wedding Ceremony Effect
    *
    * @return mplew
    */
   protected byte[] weddingProgress(WeddingProgress packet) {
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(packet.blessEffect() ? SendOpcode.WEDDING_CEREMONY_END.getValue() : SendOpcode.WEDDING_PROGRESS.getValue());
      if (!packet.blessEffect()) { // in order for ceremony packet to send, byte step = 2 must be sent first
         mplew.write(packet.step());
      }
      mplew.writeInt(packet.groomId());
      mplew.writeInt(packet.brideId());
      return mplew.getPacket();
   }

   /**
    * When we open a Wedding Invitation, we display the Bride & Groom
    *
    * @return mplew
    */
   protected byte[] sendWeddingInvitation(WeddingInvitation packet) {
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MARRIAGE_RESULT.getValue());
      mplew.write(15);
      mplew.writeMapleAsciiString(packet.groom());
      mplew.writeMapleAsciiString(packet.bride());
      mplew.writeShort(1); // 0 = Cathedral Normal?, 1 = Cathedral Premium?, 2 = Chapel Normal?
      return mplew.getPacket();
   }

   protected byte[] sendWishList(SendWishList packet) { // fuck my life
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MARRIAGE_REQUEST.getValue());
      mplew.write(9);
      return mplew.getPacket();
   }

   /**
    * Handles all of WeddingWishlist packets
    *
    * @return mplew
    */
   protected byte[] weddingGiftResult(WeddingGiftResult packet) {
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.WEDDING_GIFT_RESULT.getValue());
      mplew.write(packet.mode());
      switch (packet.mode()) {
         case 0xC: // 12 : You cannot give more than one present for each wishlist
         case 0xE: // 14 : Failed to send the gift.
            break;

         case 0x09: { // Load Wedding Registry
            mplew.write(packet.itemNames().size());
            for (String names : packet.itemNames()) {
               mplew.writeMapleAsciiString(names);
            }
            break;
         }
         case 0xA: // Load Bride's Wishlist
         case 0xF: // 10, 15, 16 = CWishListRecvDlg::OnPacket
         case 0xB: { // Add Item to Wedding Registry
            // 11 : You have sent a gift | | 13 : Failed to send the gift. |
            if (packet.mode() == 0xB) {
               mplew.write(packet.itemNames().size());
               for (String names : packet.itemNames()) {
                  mplew.writeMapleAsciiString(names);
               }
            }
            mplew.writeLong(32);
            mplew.write(packet.items().size());
            for (Item item : packet.items()) {
               addItemInfo(mplew, item, true);
            }
            break;
         }
         default: {
            System.out.println("Unknown Wishlist Mode: " + packet.mode());
            break;
         }
      }
      return mplew.getPacket();
   }
}