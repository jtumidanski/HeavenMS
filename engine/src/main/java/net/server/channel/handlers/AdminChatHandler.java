package net.server.channel.handlers;

import client.MapleClient;
import constants.ServerConstants;
import net.AbstractMaplePacketHandler;
import tools.LogHelper;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.data.input.SeekableLittleEndianAccessor;

/**
 * @author kevintjuh93
 */
public class AdminChatHandler extends AbstractMaplePacketHandler {

   @Override
   public final void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
      if (!c.getPlayer().isGM()) {//if ( (signed int)CWvsContext::GetAdminLevel((void *)v294) > 2 )
         return;
      }
      byte mode = slea.readByte();
      //not saving slides...
      String message = slea.readMapleAsciiString();
      int noticeType = (int) slea.readByte();
      switch (mode) {
         case 0:// /alertall, /noticeall, /slideall
            MessageBroadcaster.getInstance().sendWorldServerNotice(c.getWorld(), ServerNoticeType.get(noticeType), message);
            if (ServerConstants.USE_ENABLE_CHAT_LOG) {
               LogHelper.logChat(c, "Alert All", message);
            }
            break;
         case 1:// /alertch, /noticech, /slidech
            MessageBroadcaster.getInstance().sendChannelServerNotice(c.getWorld(), c.getChannel(), ServerNoticeType.get(noticeType), message);
            if (ServerConstants.USE_ENABLE_CHAT_LOG) {
               LogHelper.logChat(c, "Alert Ch", message);
            }
            break;
         case 2:// /alertm /alertmap, /noticem /noticemap, /slidem /slidemap
            MessageBroadcaster.getInstance().sendMapServerNotice(c.getPlayer().getMap(), ServerNoticeType.get(noticeType), message);
            if (ServerConstants.USE_ENABLE_CHAT_LOG) {
               LogHelper.logChat(c, "Alert Map", message);
            }
            break;

      }
   }
}
