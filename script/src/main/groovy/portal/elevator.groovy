package portal

import scripting.event.EventManager
import scripting.portal.PortalPlayerInteraction
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   EventManager elevator = pi.getEventManager("Elevator")
   if (elevator == null) {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("ELEVATOR_MAINTENANCE"))
   } else if (elevator.getProperty(pi.getMapId() == 222020100 ? ("goingUp") : ("goingDown")) == "false") {
      pi.playPortalSound(); pi.warp(pi.getMapId() == 222020100 ? 222020110 : 222020210, 0)
      return true
   } else if (elevator.getProperty(pi.getMapId() == 222020100 ? ("goingUp") : ("goingDown")) == "true") {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("ELEVATOR_MOVING"))
   }
   return false
}