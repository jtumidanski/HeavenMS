package client.command.commands.gm0;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import config.YamlConfig;
import server.maps.MapleMap;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.I18nMessage;

public class MapOwnerClaimCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      if (c.tryAcquireClient()) {
         try {
            MapleCharacter chr = c.getPlayer();

            if (YamlConfig.config.server.USE_MAP_OWNERSHIP_SYSTEM) {
               if (chr.getEventInstance() == null) {
                  MapleMap map = chr.getMap();
                  if (map.countBosses() == 0) {
                     MapleMap ownedMap = chr.getOwnedMap();
                     if (ownedMap != null) {
                        ownedMap.relinquishOwnership(chr);

                        if (map == ownedMap) {
                           MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, I18nMessage.from("LAWN_IS_FREE"));
                           return;
                        }
                     }

                     if (map.claimOwnership(chr)) {
                        MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, I18nMessage.from("LAWN_IS_LEASED"));
                     } else {
                        MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, I18nMessage.from("LAWN_ALREADY_LEASED"));
                     }
                  } else {
                     MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, I18nMessage.from("LAWN_HAS_BOSS_SIEGE"));
                  }
               } else {
                  MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, I18nMessage.from("LAWN_CANNOT_BE_LEASED"));
               }
            } else {
               MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, I18nMessage.from("FEATURE_UNAVAILABLE"));
            }
         } finally {
            c.releaseClient();
         }
      }
   }
}
