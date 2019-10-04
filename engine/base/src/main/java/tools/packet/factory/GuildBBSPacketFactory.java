package tools.packet.factory;

import client.database.data.BbsThreadData;
import net.opcodes.SendOpcode;
import tools.FilePrinter;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;
import tools.packet.guild.bbs.GetThreadList;
import tools.packet.guild.bbs.ShowThread;

public class GuildBBSPacketFactory extends AbstractPacketFactory {
   private static GuildBBSPacketFactory instance;

   public static GuildBBSPacketFactory getInstance() {
      if (instance == null) {
         instance = new GuildBBSPacketFactory();
      }
      return instance;
   }

   private GuildBBSPacketFactory() {
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof ShowThread) {
         return create(this::showThread, packetInput);
      } else if (packetInput instanceof GetThreadList) {
         return create(this::getThreadList, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }

   protected byte[] showThread(ShowThread packet) throws RuntimeException {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.GUILD_BBS_PACKET.getValue());
      mplew.write(0x07);
      mplew.writeInt(packet.localThreadId());
      mplew.writeInt(packet.threadData().posterCharacterId());
      mplew.writeLong(getTime(packet.threadData().timestamp()));
      mplew.writeMapleAsciiString(packet.threadData().name());
      mplew.writeMapleAsciiString(packet.threadData().startPost());
      mplew.writeInt(packet.threadData().icon());
      if (packet.threadData().getReplyData() != null) {
         int replyCount = packet.threadData().replyCount();
         mplew.writeInt(replyCount);
         if (replyCount != packet.threadData().getReplyData().size()) {
            throw new RuntimeException(String.valueOf(packet.threadData().threadId()));
         }

         packet.threadData().getReplyData().forEach(replyData -> {
            mplew.writeInt(replyData.replyId());
            mplew.writeInt(replyData.posterCharacterId());
            mplew.writeLong(getTime(replyData.timestamp()));
            mplew.writeMapleAsciiString(replyData.content());
         });
      } else {
         mplew.writeInt(0);
      }
      return mplew.getPacket();
   }

   protected byte[] getThreadList(GetThreadList packet) {
      int start = packet.start();
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.GUILD_BBS_PACKET.getValue());
      mplew.write(0x06);
      if (packet.threadData().size() == 0) {
         mplew.write(0);
         mplew.writeInt(0);
         mplew.writeInt(0);
         return mplew.getPacket();
      }

      int threadCount = packet.threadData().size();

      BbsThreadData firstThread = packet.threadData().get(0);
      if (firstThread.threadId() == 0) { //has a notice
         mplew.write(1);
         addThread(mplew, firstThread);
         threadCount--; //one thread didn't count (because it's a notice)
      } else {
         mplew.write(0);
      }
      if (start >= packet.threadData().size()) { //seek to the thread before where we start
         start = 0; //uh, we're trying to start at a place past possible
      }
      mplew.writeInt(threadCount);
      mplew.writeInt(Math.min(10, threadCount - start));
      for (int i = start; i < Math.min(10, threadCount - start); i++) {
         addThread(mplew, packet.threadData().get(i));
      }
      return mplew.getPacket();
   }

   protected void addThread(final MaplePacketLittleEndianWriter mplew, BbsThreadData threadData) {
      mplew.writeInt(threadData.threadId());
      mplew.writeInt(threadData.posterCharacterId());
      mplew.writeMapleAsciiString(threadData.name());
      mplew.writeLong(getTime(threadData.timestamp()));
      mplew.writeInt(threadData.icon());
      mplew.writeInt(threadData.replyCount());
   }
}