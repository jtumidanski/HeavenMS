package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.bbs.BaseBBSOperationPacket;
import net.server.channel.packet.bbs.DeleteReplyPacket;
import net.server.channel.packet.bbs.DeleteThreadPacket;
import net.server.channel.packet.bbs.DisplayThreadPacket;
import net.server.channel.packet.bbs.EditBBSThreadPacket;
import net.server.channel.packet.bbs.ListThreadsPacket;
import net.server.channel.packet.bbs.NewBBSThreadPacket;
import net.server.channel.packet.bbs.ReplyToThreadPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class BBSOperationReader implements PacketReader<BaseBBSOperationPacket> {
   @Override
   public BaseBBSOperationPacket read(SeekableLittleEndianAccessor accessor) {
      byte mode = accessor.readByte();
      return switch (mode) {
         case 0 -> readUpdateOrInsert(accessor, mode);
         case 1 -> readDelete(accessor, mode);
         case 2 -> readListThreads(accessor, mode);
         case 3 -> readDisplayThread(accessor, mode);
         case 4 -> readReply(accessor, mode);
         case 5 -> readDeleteReply(accessor, mode);
         default -> new BaseBBSOperationPacket(mode);
      };

   }

   private BaseBBSOperationPacket readDeleteReply(SeekableLittleEndianAccessor accessor, byte mode) {
      accessor.readInt(); // we don't use this
      int replyId = accessor.readInt();
      return new DeleteReplyPacket(mode, replyId);
   }

   private BaseBBSOperationPacket readReply(SeekableLittleEndianAccessor accessor, byte mode) {
      int localThreadId = accessor.readInt();
      String text = correctLength(accessor.readMapleAsciiString(), 25);
      return new ReplyToThreadPacket(mode, localThreadId, text);
   }

   private BaseBBSOperationPacket readDisplayThread(SeekableLittleEndianAccessor accessor, byte mode) {
      int localThreadId = accessor.readInt();
      return new DisplayThreadPacket(mode, localThreadId);
   }

   private BaseBBSOperationPacket readListThreads(SeekableLittleEndianAccessor accessor, byte mode) {
      int start = accessor.readInt();
      return new ListThreadsPacket(mode, start);
   }

   private BaseBBSOperationPacket readDelete(SeekableLittleEndianAccessor accessor, byte mode) {
      int localThreadId = accessor.readInt();
      return new DeleteThreadPacket(mode, localThreadId);
   }

   private BaseBBSOperationPacket readUpdateOrInsert(SeekableLittleEndianAccessor accessor, byte mode) {
      boolean edit = accessor.readByte() == 1;
      if (edit) {
         return readEdit(accessor, mode);
      } else {
         return readNew(accessor, mode);
      }
   }

   private BaseBBSOperationPacket readNew(SeekableLittleEndianAccessor accessor, byte mode) {
      boolean bNotice = accessor.readByte() == 1;
      String title = correctLength(accessor.readMapleAsciiString(), 25);
      String text = correctLength(accessor.readMapleAsciiString(), 600);
      int icon = accessor.readInt();
      return new NewBBSThreadPacket(mode, bNotice, title, text, icon);
   }

   private BaseBBSOperationPacket readEdit(SeekableLittleEndianAccessor accessor, byte mode) {
      int localThreadId = accessor.readInt();
      boolean bNotice = accessor.readByte() == 1;
      String title = correctLength(accessor.readMapleAsciiString(), 25);
      String text = correctLength(accessor.readMapleAsciiString(), 600);
      int icon = accessor.readInt();
      return new EditBBSThreadPacket(mode, localThreadId, bNotice, title, text, icon);
   }

   private String correctLength(String in, int maxSize) {
      return in.length() > maxSize ? in.substring(0, maxSize) : in;
   }
}
