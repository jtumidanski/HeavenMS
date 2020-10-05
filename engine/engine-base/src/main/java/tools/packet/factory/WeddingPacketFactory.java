package tools.packet.factory;

import client.MapleCharacter;
import client.inventory.Item;
import client.processor.CharacterProcessor;
import tools.LogType;
import tools.LoggerOriginator;
import tools.LoggerUtil;
import tools.StringUtil;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.wedding.MarriageRequest;
import tools.packet.wedding.MarriageResult;
import tools.packet.wedding.MarriageResultError;
import tools.packet.wedding.SendWishList;
import tools.packet.wedding.TakePhoto;
import tools.packet.wedding.WeddingEnd;
import tools.packet.wedding.WeddingGiftResult;
import tools.packet.wedding.WeddingInvitation;
import tools.packet.wedding.WeddingPartnerTransfer;
import tools.packet.wedding.WeddingProgress;

public class WeddingPacketFactory extends AbstractPacketFactory {
   private static WeddingPacketFactory instance;

   public static WeddingPacketFactory getInstance() {
      if (instance == null) {
         instance = new WeddingPacketFactory();
      }
      return instance;
   }

   private WeddingPacketFactory() {
      Handler.handle(MarriageRequest.class).decorate(this::marriageRequest).register(registry);
      Handler.handle(TakePhoto.class).decorate(this::takePhoto).register(registry);
      Handler.handle(MarriageResult.class).decorate(this::marriageResult).register(registry);
      Handler.handle(MarriageResultError.class).decorate(this::marriageResultError).register(registry);
      Handler.handle(WeddingPartnerTransfer.class).decorate(this::weddingPartnerTransfer).register(registry);
      Handler.handle(WeddingEnd.class).decorate(this::weddingProgress).register(registry);
      Handler.handle(WeddingProgress.class).decorate(this::weddingProgress).register(registry);
      Handler.handle(WeddingInvitation.class).decorate(this::sendWeddingInvitation).register(registry);
      Handler.handle(SendWishList.class).decorate(this::sendWishList).register(registry);
      Handler.handle(WeddingGiftResult.class).decorate(this::weddingGiftResult).register(registry);
   }

   /**
    * <name> has requested engagement. Will you accept this proposal?
    */
   protected void marriageRequest(MaplePacketLittleEndianWriter writer, MarriageRequest packet) {
      writer.write(0); //mode, 0 = engage, 1 = cancel, 2 = answer.. etc
      writer.writeMapleAsciiString(packet.name()); // name
      writer.writeInt(packet.characterId());
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
    */
   protected void takePhoto(MaplePacketLittleEndianWriter writer, TakePhoto packet) { // OnIFailedAtWeddingPhotos
      writer.writeMapleAsciiString(packet.getReservedGroomName());
      writer.writeMapleAsciiString(packet.getReservedBrideName());
      writer.writeInt(packet.getField()); // field id?
      writer.writeInt(packet.getAttendees().size());

      for (MapleCharacter guest : packet.getAttendees()) {
         // Begin Avatar Encoding
         addCharLook(writer, guest, false); // CUser::EncodeAvatar
         writer.writeInt(30000); // v20 = *(_DWORD *)(v13 + 2192) -- new groom marriage ID??
         writer.writeInt(30000); // v20 = *(_DWORD *)(v13 + 2192) -- new bride marriage ID??
         writer.writeMapleAsciiString(guest.getName());

         guest.getGuild().ifPresentOrElse(guild -> {
            writer.writeMapleAsciiString(guild.getName());
            writer.writeShort(guild.getLogoBG());
            writer.write(guild.getLogoBGColor());
            writer.writeShort(guild.getLogo());
            writer.write(guild.getLogoColor());
         }, () -> {
            writer.writeMapleAsciiString("");
            writer.writeShort(0);
            writer.write(0);
            writer.writeShort(0);
            writer.write(0);
         });
         writer.writeShort(guest.position().x);
         writer.writeShort(guest.position().y);
         writer.write(1);
         writer.writeInt(1);
         writer.writeMapleAsciiString(guest.getName());
         writer.writeShort(guest.position().x);
         writer.writeShort(guest.position().y);
         writer.write(guest.stance());
      }
   }

   /**
    * Enable spouse chat and their engagement ring without @relog
    */
   protected void marriageResult(MaplePacketLittleEndianWriter writer, MarriageResult packet) {
      writer.write(11);
      writer.writeInt(packet.getMarriageId());
      writer.writeInt(packet.getCharacter().getGender() == 0 ? packet.getCharacter().getId() : packet.getCharacter().getPartnerId());
      writer.writeInt(packet.getCharacter().getGender() == 0 ? packet.getCharacter().getPartnerId() : packet.getCharacter().getId());
      writer.writeShort(packet.isWedding() ? 3 : 1);
      if (packet.isWedding()) {
         writer.writeInt(packet.getCharacter().getMarriageItemId());
         writer.writeInt(packet.getCharacter().getMarriageItemId());
      } else {
         writer.writeInt(1112803); // Engagement Ring's Outcome (doesn't matter for engagement)
         writer.writeInt(1112803); // Engagement Ring's Outcome (doesn't matter for engagement)
      }
      writer.writeAsciiString(StringUtil.getRightPaddedStr(packet.getCharacter().getGender() == 0 ? packet.getCharacter().getName() : CharacterProcessor.getInstance().getNameById(packet.getCharacter().getPartnerId()), '\0', 13));
      writer.writeAsciiString(StringUtil.getRightPaddedStr(packet.getCharacter().getGender() == 0 ? CharacterProcessor.getInstance().getNameById(packet.getCharacter().getPartnerId()) : packet.getCharacter().getName(), '\0', 13));
   }

   /**
    * To exit the Engagement Window (Waiting for her response...), we send a GMS-like pop-up.
    */
   protected void marriageResultError(MaplePacketLittleEndianWriter writer, MarriageResultError packet) {
      writer.write(packet.message());
      if (packet.message() == 36) {
         writer.write(1);
         writer.writeMapleAsciiString("You are now engaged.");
      }
   }

   /**
    * The World Map includes 'loverPos' in which this packet controls
    */
   protected void weddingPartnerTransfer(MaplePacketLittleEndianWriter writer, WeddingPartnerTransfer packet) {
      writer.writeInt(packet.mapId());
      writer.writeInt(packet.partnerId());
   }

   /**
    * The wedding packet to display Pelvis Bebop and enable the Wedding Ceremony Effect between two characters
    * CField_Wedding::OnWeddingProgress - Stages
    * CField_Wedding::OnWeddingCeremonyEnd - Wedding Ceremony Effect
    */
   protected void weddingProgress(MaplePacketLittleEndianWriter writer, WeddingProgress packet) {
      if (!packet.blessEffect()) { // in order for ceremony packet to send, byte step = 2 must be sent first
         writer.write(packet.step());
      }
      writer.writeInt(packet.groomId());
      writer.writeInt(packet.brideId());
   }

   protected void weddingProgress(MaplePacketLittleEndianWriter writer, WeddingEnd packet) {
      if (!packet.blessEffect()) { // in order for ceremony packet to send, byte step = 2 must be sent first
         writer.write(packet.step());
      }
      writer.writeInt(packet.groomId());
      writer.writeInt(packet.brideId());
   }

   /**
    * When we open a Wedding Invitation, we display the Bride & Groom
    */
   protected void sendWeddingInvitation(MaplePacketLittleEndianWriter writer, WeddingInvitation packet) {
      writer.write(15);
      writer.writeMapleAsciiString(packet.groom());
      writer.writeMapleAsciiString(packet.bride());
      writer.writeShort(1); // 0 = Cathedral Normal?, 1 = Cathedral Premium?, 2 = Chapel Normal?
   }

   protected void sendWishList(MaplePacketLittleEndianWriter writer, SendWishList packet) { // fuck my life
      writer.write(9);
   }

   /**
    * Handles all of WeddingWishList packets
    */
   protected void weddingGiftResult(MaplePacketLittleEndianWriter writer, WeddingGiftResult packet) {
      writer.write(packet.mode());
      switch (packet.mode()) {
         case 0xC: // 12 : You cannot give more than one present for each wish list
         case 0xE: // 14 : Failed to send the gift.
            break;

         case 0x09: { // Load Wedding Registry
            writer.write(packet.itemNames().size());
            for (String names : packet.itemNames()) {
               writer.writeMapleAsciiString(names);
            }
            break;
         }
         case 0xA: // Load Bride's Wish list
         case 0xF: // 10, 15, 16 = CWishListRecvDlg::OnPacket
         case 0xB: { // Add Item to Wedding Registry
            // 11 : You have sent a gift | | 13 : Failed to send the gift. |
            if (packet.mode() == 0xB) {
               writer.write(packet.itemNames().size());
               for (String names : packet.itemNames()) {
                  writer.writeMapleAsciiString(names);
               }
            }
            writer.writeLong(32);
            writer.write(packet.items().size());
            for (Item item : packet.items()) {
               addItemInfo(writer, item, true);
            }
            break;
         }
         default: {
            LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXCEPTION, "Unknown WishList Mode: " + packet.mode());
            break;
         }
      }
   }
}