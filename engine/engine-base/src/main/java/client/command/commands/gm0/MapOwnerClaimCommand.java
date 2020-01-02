package client.command.commands.gm0;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import config.YamlConfig;
import server.maps.MapleMap;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

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
                           MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "This lawn is now free real estate.");
                           return;
                        }
                     }

                     if (map.claimOwnership(chr)) {
                        MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "You have leased this lawn for a while, until you leave here or after 1 minute of inactivity.");
                     } else {
                        MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "This lawn has already been leased by a player.");
                     }
                  } else {
                     MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "This lawn is currently under a boss siege.");
                  }
               } else {
                  MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "This lawn cannot be leased.");
               }
            } else {
               MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "Feature unavailable.");
            }
         } finally {
            c.releaseClient();
         }
      }
   }
}
