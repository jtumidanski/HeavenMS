package net.server.channel.handlers;

import client.MapleClient;
import config.YamlConfig;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.AdminChatPacket;
import net.server.channel.packet.reader.AdminChatReader;
import tools.LogHelper;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class AdminChatHandler extends AbstractPacketHandler<AdminChatPacket> {
   @Override
   public Class<AdminChatReader> getReaderClass() {
      return AdminChatReader.class;
   }

   @Override
   public boolean successfulProcess(MapleClient client) {
      //if ( (signed int)CWvsContext::GetAdminLevel((void *)v294) > 2 )
      return client.getPlayer().isGM();
   }

   @Override
   public void handlePacket(AdminChatPacket packet, MapleClient client) {
      switch (packet.mode()) {
         case 0:// /alert all, /notice all, /slide all
            MessageBroadcaster.getInstance().sendWorldServerNotice(client.getWorld(), ServerNoticeType.get(packet.noticeType()), packet.message());
            if (YamlConfig.config.server.USE_ENABLE_CHAT_LOG) {
               LogHelper.logChat(client, "Alert All", packet.message());
            }
            break;
         case 1:// /alert channel, /notice channel, /slide channel
            MessageBroadcaster.getInstance().sendChannelServerNotice(client.getWorld(), client.getChannel(), ServerNoticeType.get(packet.noticeType()), packet.message());
            if (YamlConfig.config.server.USE_ENABLE_CHAT_LOG) {
               LogHelper.logChat(client, "Alert Ch", packet.message());
            }
            break;
         case 2:// /alert map /alert map, /notice map /notice map, /slide map /slide map
            MessageBroadcaster.getInstance().sendMapServerNotice(client.getPlayer().getMap(), ServerNoticeType.get(packet.noticeType()), packet.message());
            if (YamlConfig.config.server.USE_ENABLE_CHAT_LOG) {
               LogHelper.logChat(client, "Alert Map", packet.message());
            }
            break;
      }
   }
}
