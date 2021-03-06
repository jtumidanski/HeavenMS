package net.server.channel.handlers;

import java.util.List;
import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import client.database.data.BbsThreadData;
import client.database.data.BbsThreadReplyData;
import com.ms.logs.LogType;
import com.ms.logs.LoggerOriginator;
import com.ms.logs.LoggerUtil;
import database.DatabaseConnection;
import database.administrator.BbsThreadAdministrator;
import database.administrator.BbsThreadReplyAdministrator;
import database.provider.BbsThreadProvider;
import database.provider.BbsThreadReplyProvider;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.bbs.BaseBBSOperationPacket;
import net.server.channel.packet.bbs.DeleteReplyPacket;
import net.server.channel.packet.bbs.DeleteThreadPacket;
import net.server.channel.packet.bbs.DisplayThreadPacket;
import net.server.channel.packet.bbs.EditBBSThreadPacket;
import net.server.channel.packet.bbs.ListThreadsPacket;
import net.server.channel.packet.bbs.NewBBSThreadPacket;
import net.server.channel.packet.bbs.ReplyToThreadPacket;
import net.server.channel.packet.reader.BBSOperationReader;
import tools.PacketCreator;
import tools.packet.guild.bbs.GetThreadList;
import tools.packet.guild.bbs.ShowThread;

public final class BBSOperationHandler extends AbstractPacketHandler<BaseBBSOperationPacket> {
   @Override
   public Class<BBSOperationReader> getReaderClass() {
      return BBSOperationReader.class;
   }

   @Override
   public boolean successfulProcess(MapleClient client) {
      return client.getPlayer().getGuildId() >= 1;
   }

   @Override
   public void handlePacket(BaseBBSOperationPacket packet, MapleClient client) {
      if (packet instanceof NewBBSThreadPacket) {
         newThread((NewBBSThreadPacket) packet, client);
      } else if (packet instanceof EditBBSThreadPacket) {
         editThread((EditBBSThreadPacket) packet, client);
      } else if (packet instanceof DeleteThreadPacket) {
         deleteBBSThread(client, ((DeleteThreadPacket) packet).threadId());
      } else if (packet instanceof ListThreadsPacket) {
         listBBSThreads(client, ((ListThreadsPacket) packet).start() * 10);
      } else if (packet instanceof DisplayThreadPacket) {
         displayThread(client, ((DisplayThreadPacket) packet).threadId());
      } else if (packet instanceof ReplyToThreadPacket) {
         newBBSReply(client, ((ReplyToThreadPacket) packet).threadId(), ((ReplyToThreadPacket) packet).message());
      } else if (packet instanceof DeleteReplyPacket) {
         deleteBBSReply(client, ((DeleteReplyPacket) packet).replyId());
      } else {
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.UNHANDLED_EVENT, "Unhandled BBS mode: " + packet.toString());
      }
   }

   private void editThread(EditBBSThreadPacket packet, MapleClient client) {
      if (packet.icon() >= 0x64 && packet.icon() <= 0x6a) {
         if (!client.getPlayer().haveItemWithId(5290000 + packet.icon() - 0x64, false)) {
            return;
         }
      } else if (packet.icon() < 0 || packet.icon() > 3) {
         return;
      }
      editBBSThread(client, packet.title(), packet.text(), packet.icon(), packet.threadId());
   }

   private void newThread(NewBBSThreadPacket packet, MapleClient client) {
      if (packet.icon() >= 0x64 && packet.icon() <= 0x6a) {
         if (!client.getPlayer().haveItemWithId(5290000 + packet.icon() - 0x64, false)) {
            return;
         }
      } else if (packet.icon() < 0 || packet.icon() > 3) {
         return;
      }
      newBBSThread(client, packet.title(), packet.text(), packet.icon(), packet.isNotice());
   }

   private void listBBSThreads(MapleClient c, int start) {
      DatabaseConnection.getInstance().withConnection(connection -> {
         List<BbsThreadData> threadData = BbsThreadProvider.getInstance().getThreadsForGuild(connection, c.getPlayer().getGuildId());
         PacketCreator.announce(c, new GetThreadList(threadData, start));
      });
   }

   private void newBBSReply(MapleClient c, int localThreadId, String text) {
      if (c.getPlayer().getGuildId() <= 0) {
         return;
      }

      DatabaseConnection.getInstance().withConnection(connection -> {
         Optional<BbsThreadData> threadData = BbsThreadProvider.getInstance().getByThreadAndGuildId(connection, localThreadId, c.getPlayer().getGuildId(), true);
         if (threadData.isEmpty()) {
            return;
         }
         int threadId = threadData.get().threadId();
         BbsThreadReplyAdministrator.getInstance().create(connection, threadId, c.getPlayer().getId(), text);
         BbsThreadAdministrator.getInstance().incrementReplyCount(connection, threadId);
      });
   }

   private void editBBSThread(MapleClient client, String title, String text, int icon, int localThreadId) {
      MapleCharacter c = client.getPlayer();
      if (c.getGuildId() < 1) {
         return;
      }

      DatabaseConnection.getInstance().withConnection(connection -> {
         BbsThreadAdministrator.getInstance().editThread(connection, localThreadId, c.getGuildId(), c.getId(), c.getGuildRank() < 3, title, icon, text);
         displayThread(client, localThreadId);
      });
   }

   private void newBBSThread(MapleClient client, String title, String text, int icon, boolean bNotice) {
      MapleCharacter c = client.getPlayer();
      if (c.getGuildId() <= 0) {
         return;
      }

      DatabaseConnection.getInstance().withConnection(connection -> {
         int nextId = 0;
         if (!bNotice) {
            nextId = BbsThreadProvider.getInstance().getNextLocalThreadId(connection, c.getGuildId());
         }
         BbsThreadAdministrator.getInstance().create(connection, c.getId(), title, icon, text, c.getGuildId(), nextId);
         displayThread(client, nextId);
      });
   }

   private void deleteBBSThread(MapleClient client, int localThreadId) {
      MapleCharacter mc = client.getPlayer();
      if (mc.getGuildId() <= 0) {
         return;
      }

      DatabaseConnection.getInstance().withConnection(connection -> {
         Optional<BbsThreadData> threadData = BbsThreadProvider.getInstance().getByThreadAndGuildId(connection, localThreadId, mc.getGuildId(), true);
         if (threadData.isEmpty()) {
            return;
         }
         if (threadData.get().posterCharacterId() != mc.getId() && mc.getGuildRank() > 2) {
            return;
         }

         int threadId = threadData.get().threadId();

         BbsThreadReplyAdministrator.getInstance().deleteByThreadId(connection, threadId);
         BbsThreadAdministrator.getInstance().deleteById(connection, threadId);
      });
   }

   private void deleteBBSReply(MapleClient client, int replyId) {
      MapleCharacter mc = client.getPlayer();
      if (mc.getGuildId() <= 0) {
         return;
      }

      DatabaseConnection.getInstance().withConnection(connection -> {
         Optional<BbsThreadReplyData> threadReplyData = BbsThreadReplyProvider.getInstance().getByReplyId(connection, replyId);
         if (threadReplyData.isEmpty()) {
            return;
         }
         if (threadReplyData.get().posterCharacterId() != mc.getId() && mc.getGuildRank() > 2) {
            return;
         }

         int threadId = threadReplyData.get().threadId();
         BbsThreadReplyAdministrator.getInstance().deleteById(connection, replyId);
         BbsThreadAdministrator.getInstance().decrementReplyCount(connection, threadId);
         displayThread(client, threadId, false);
      });
   }

   private void displayThread(MapleClient client, int threadId) {
      displayThread(client, threadId, true);
   }

   private void displayThread(MapleClient client, int threadId, boolean bIsThreadIdLocal) {
      MapleCharacter mc = client.getPlayer();
      if (mc.getGuildId() <= 0) {
         return;
      }

      DatabaseConnection.getInstance().withConnection(connection -> BbsThreadProvider.getInstance().getByThreadAndGuildId(connection, threadId, mc.getGuildId(), bIsThreadIdLocal)
            .ifPresent(threadData -> PacketCreator.announce(client, new ShowThread(bIsThreadIdLocal ? threadId : threadData.threadId(), threadData))));
   }
}
