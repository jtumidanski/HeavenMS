/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as
 published by the Free Software Foundation version 3 as published by
 the Free Software Foundation. You may not use, modify or distribute
 this program under any other version of the GNU Affero General Public
 License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.server.channel.handlers;

import java.util.List;
import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import client.database.administrator.BbsThreadAdministrator;
import client.database.administrator.BbsThreadReplyAdministrator;
import client.database.data.BbsThreadData;
import client.database.data.BbsThreadReplyData;
import client.database.provider.BbsThreadProvider;
import client.database.provider.BbsThreadReplyProvider;
import net.AbstractMaplePacketHandler;
import tools.DatabaseConnection;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;

public final class BBSOperationHandler extends AbstractMaplePacketHandler {

   private static void listBBSThreads(MapleClient c, int start) {
      DatabaseConnection.withConnection(connection -> {
         List<BbsThreadData> threadData = BbsThreadProvider.getInstance().getThreadsForGuild(connection, c.getPlayer().getGuildId());
         c.announce(MaplePacketCreator.BBSThreadList(threadData, start));
      });
   }

   private static void newBBSReply(MapleClient c, int localThreadId, String text) {
      if (c.getPlayer().getGuildId() <= 0) {
         return;
      }

      DatabaseConnection.withConnection(connection -> {
         Optional<BbsThreadData> threadData = BbsThreadProvider.getInstance().getByThreadAndGuildId(connection, localThreadId, c.getPlayer().getGuildId(), true);
         if (threadData.isEmpty()) {
            return;
         }
         int threadId = threadData.get().getThreadId();
         BbsThreadReplyAdministrator.getInstance().create(connection, threadId, c.getPlayer().getId(), text);
         BbsThreadAdministrator.getInstance().incrementReplyCount(connection, threadId);
      });
   }

   private static void editBBSThread(MapleClient client, String title, String text, int icon, int localThreadId) {
      MapleCharacter c = client.getPlayer();
      if (c.getGuildId() < 1) {
         return;
      }

      DatabaseConnection.withConnection(connection -> {
         BbsThreadAdministrator.getInstance().editThread(connection, localThreadId, c.getGuildId(), c.getId(), c.getGuildRank() < 3, title, icon, text);
         displayThread(client, localThreadId);
      });
   }

   private static void newBBSThread(MapleClient client, String title, String text, int icon, boolean bNotice) {
      MapleCharacter c = client.getPlayer();
      if (c.getGuildId() <= 0) {
         return;
      }

      DatabaseConnection.withConnection(connection -> {
         int nextId = 0;
         if (!bNotice) {
            nextId = BbsThreadProvider.getInstance().getNextLocalThreadId(connection, c.getGuildId());
         }
         BbsThreadAdministrator.getInstance().create(connection, c.getId(), title, icon, text, c.getGuildId(), nextId);
         displayThread(client, nextId);
      });
   }

   public static void deleteBBSThread(MapleClient client, int localThreadId) {
      MapleCharacter mc = client.getPlayer();
      if (mc.getGuildId() <= 0) {
         return;
      }

      DatabaseConnection.withConnection(connection -> {
         Optional<BbsThreadData> threadData = BbsThreadProvider.getInstance().getByThreadAndGuildId(connection, localThreadId, mc.getGuildId(), true);
         if (threadData.isEmpty()) {
            return;
         }
         if (threadData.get().getPosterCharacterId() != mc.getId() && mc.getGuildRank() > 2) {
            return;
         }

         int threadId = threadData.get().getThreadId();

         BbsThreadReplyAdministrator.getInstance().deleteByThreadId(connection, threadId);
         BbsThreadAdministrator.getInstance().deleteById(connection, threadId);
      });
   }

   public static void deleteBBSReply(MapleClient client, int replyId) {
      MapleCharacter mc = client.getPlayer();
      if (mc.getGuildId() <= 0) {
         return;
      }

      DatabaseConnection.withConnection(connection -> {
         Optional<BbsThreadReplyData> threadReplyData = BbsThreadReplyProvider.getInstance().getByReplyId(connection, replyId);
         if (threadReplyData.isEmpty()) {
            return;
         }
         if (threadReplyData.get().getPosterCharacterId() != mc.getId() && mc.getGuildRank() > 2) {
            return;
         }

         int threadId = threadReplyData.get().getThreadId();
         BbsThreadReplyAdministrator.getInstance().deleteById(connection, replyId);
         BbsThreadAdministrator.getInstance().decrementReplyCount(connection, threadId);
         displayThread(client, threadId, false);
      });
   }

   public static void displayThread(MapleClient client, int threadid) {
      displayThread(client, threadid, true);
   }

   public static void displayThread(MapleClient client, int threadid, boolean bIsThreadIdLocal) {
      MapleCharacter mc = client.getPlayer();
      if (mc.getGuildId() <= 0) {
         return;
      }

      DatabaseConnection.withConnection(connection -> BbsThreadProvider.getInstance().getByThreadAndGuildId(connection, threadid, mc.getGuildId(), bIsThreadIdLocal)
            .ifPresent(threadData -> client.announce(MaplePacketCreator.showThread(bIsThreadIdLocal ? threadid : threadData.getThreadId(), threadData))));
   }

   private String correctLength(String in, int maxSize) {
      return in.length() > maxSize ? in.substring(0, maxSize) : in;
   }

   @Override
   public final void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
      if (c.getPlayer().getGuildId() < 1) {
         return;
      }
      byte mode = slea.readByte();
      int localthreadid = 0;
      switch (mode) {
         case 0:
            boolean bEdit = slea.readByte() == 1;
            if (bEdit) {
               localthreadid = slea.readInt();
            }
            boolean bNotice = slea.readByte() == 1;
            String title = correctLength(slea.readMapleAsciiString(), 25);
            String text = correctLength(slea.readMapleAsciiString(), 600);
            int icon = slea.readInt();
            if (icon >= 0x64 && icon <= 0x6a) {
               if (!c.getPlayer().haveItemWithId(5290000 + icon - 0x64, false)) {
                  return;
               }
            } else if (icon < 0 || icon > 3) {
               return;
            }
            if (!bEdit) {
               newBBSThread(c, title, text, icon, bNotice);
            } else {
               editBBSThread(c, title, text, icon, localthreadid);
            }
            break;
         case 1:
            localthreadid = slea.readInt();
            deleteBBSThread(c, localthreadid);
            break;
         case 2:
            int start = slea.readInt();
            listBBSThreads(c, start * 10);
            break;
         case 3: // list thread + reply, followed by id (int)
            localthreadid = slea.readInt();
            displayThread(c, localthreadid);
            break;
         case 4: // reply
            localthreadid = slea.readInt();
            text = correctLength(slea.readMapleAsciiString(), 25);
            newBBSReply(c, localthreadid, text);
            break;
         case 5: // delete reply
            slea.readInt(); // we don't use this
            int replyid = slea.readInt();
            deleteBBSReply(c, replyid);
            break;
         default:
            //System.out.println("Unhandled BBS mode: " + slea.toString());
      }
   }
}
