package tools.packet.factory;

import client.database.data.BbsThreadData;
import tools.data.output.MaplePacketLittleEndianWriter;
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
      Handler.handle(ShowThread.class).decorate(this::showThread).register(registry);
      Handler.handle(GetThreadList.class).decorate(this::getThreadList).register(registry);
   }

   protected void showThread(MaplePacketLittleEndianWriter writer, ShowThread packet) throws RuntimeException {
      writer.write(0x07);
      writer.writeInt(packet.localThreadId());
      writer.writeInt(packet.threadData().posterCharacterId());
      writer.writeLong(getTime(packet.threadData().timestamp()));
      writer.writeMapleAsciiString(packet.threadData().name());
      writer.writeMapleAsciiString(packet.threadData().startPost());
      writer.writeInt(packet.threadData().icon());
      if (packet.threadData().replyData() != null) {
         int replyCount = packet.threadData().replyCount();
         writer.writeInt(replyCount);
         if (replyCount != packet.threadData().replyData().size()) {
            throw new RuntimeException(String.valueOf(packet.threadData().threadId()));
         }

         packet.threadData().replyData().forEach(replyData -> {
            writer.writeInt(replyData.replyId());
            writer.writeInt(replyData.posterCharacterId());
            writer.writeLong(getTime(replyData.timestamp()));
            writer.writeMapleAsciiString(replyData.content());
         });
      } else {
         writer.writeInt(0);
      }
   }

   protected void getThreadList(MaplePacketLittleEndianWriter writer, GetThreadList packet) {
      int start = packet.start();
      writer.write(0x06);
      if (packet.threadData().size() == 0) {
         writer.write(0);
         writer.writeInt(0);
         writer.writeInt(0);
         return;
      }

      int threadCount = packet.threadData().size();

      BbsThreadData firstThread = packet.threadData().get(0);
      if (firstThread.threadId() == 0) { //has a notice
         writer.write(1);
         addThread(writer, firstThread);
         threadCount--; //one thread didn't count (because it's a notice)
      } else {
         writer.write(0);
      }
      if (start >= packet.threadData().size()) { //seek to the thread before where we start
         start = 0; //uh, we're trying to start at a place past possible
      }
      writer.writeInt(threadCount);
      writer.writeInt(Math.min(10, threadCount - start));
      for (int i = start; i < Math.min(10, threadCount - start); i++) {
         addThread(writer, packet.threadData().get(i));
      }
   }

   protected void addThread(final MaplePacketLittleEndianWriter writer, BbsThreadData threadData) {
      writer.writeInt(threadData.threadId());
      writer.writeInt(threadData.posterCharacterId());
      writer.writeMapleAsciiString(threadData.name());
      writer.writeLong(getTime(threadData.timestamp()));
      writer.writeInt(threadData.icon());
      writer.writeInt(threadData.replyCount());
   }
}