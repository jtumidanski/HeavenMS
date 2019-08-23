package portal

import scripting.event.EventManager
import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   EventManager elevator = pi.getEventManager("Elevator")
   if (elevator == null) {
      pi.getPlayer().dropMessage(5, "The elevator is under maintenance.")
   } else if (elevator.getProperty(pi.getMapId() == 222020100 ? ("goingUp") : ("goingDown")) == "false") {
      pi.playPortalSound(); pi.warp(pi.getMapId() == 222020100 ? 222020110 : 222020210, 0)
      return true
   } else if (elevator.getProperty(pi.getMapId() == 222020100 ? ("goingUp") : ("goingDown")) == "true") {
      pi.getPlayer().dropMessage(5, "The elevator is currently moving.")
   }
   return false
}