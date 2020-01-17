package client.command.commands.gm0;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.events.gm.MapleEvent;
import server.maps.FieldLimit;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.I18nMessage;

public class JoinEventCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (!FieldLimit.CANNOT_MIGRATE.check(player.getMap().getFieldLimit())) {
         MapleEvent event = c.getChannelServer().getEvent();
         if (event != null) {
            if (event.getMapId() != player.getMapId()) {
               if (event.getLimit() > 0) {
                  player.saveLocation("EVENT");

                  if (event.getMapId() == 109080000 || event.getMapId() == 109060001)
                     player.setTeam(event.getLimit() % 2);

                  event.minusLimit();

                  player.saveLocationOnWarp();
                  player.changeMap(event.getMapId());
               } else {
                  MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("EVENT_PLAYER_LIMIT"));
               }
            } else {
               MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("EVENT_ALREADY_IN"));
            }
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("EVENT_NO_EVENT"));
         }
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("EVENT_CANNOT_JOIN_BECAUSE_OF_MAP"));
      }
   }
}
