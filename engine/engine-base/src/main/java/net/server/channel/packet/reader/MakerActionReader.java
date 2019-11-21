package net.server.channel.packet.reader;

import constants.inventory.ItemConstants;
import net.server.PacketReader;
import net.server.channel.packet.maker.BaseMakerActionPacket;
import net.server.channel.packet.maker.MakerDisassemblingPacket;
import net.server.channel.packet.maker.MakerReagentPacket;
import server.MapleItemInformationProvider;
import tools.data.input.SeekableLittleEndianAccessor;

public class MakerActionReader implements PacketReader<BaseMakerActionPacket> {
   @Override
   public BaseMakerActionPacket read(SeekableLittleEndianAccessor accessor) {
      int type = accessor.readInt();
      int toCreate = accessor.readInt();

      if (type == 3) {
         return new BaseMakerActionPacket(type, toCreate);
      } else if (type == 4) {
         int inventoryType = accessor.readInt();
         int position = accessor.readInt();
         return new MakerDisassemblingPacket(type, toCreate, inventoryType, position);
      } else {
         if (ItemConstants.isEquipment(toCreate)) {
            boolean isReagent = accessor.readByte() != 0;

            int reagents = Math.min(accessor.readInt(), getMakerReagentSlots(toCreate));
            int[] reagentIds = new int[reagents];
            for (int i = 0; i < reagents; i++) {  // crystals
               reagentIds[i] = accessor.readInt();
            }
            return new MakerReagentPacket(type, toCreate, isReagent, reagents, reagentIds);
         } else {
            return new BaseMakerActionPacket(type, toCreate);
         }
      }
   }

   private static int getMakerReagentSlots(int itemId) {
      try {
         int eqpLevel = MapleItemInformationProvider.getInstance().getEquipLevelReq(itemId);

         if (eqpLevel < 78) {
            return 1;
         } else if (eqpLevel >= 78 && eqpLevel < 108) {
            return 2;
         } else {
            return 3;
         }
      } catch (NullPointerException npe) {
         return 0;
      }
   }
}
