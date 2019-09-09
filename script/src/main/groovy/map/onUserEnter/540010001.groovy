package map.onUserEnter

import scripting.event.EventManager
import scripting.map.MapScriptMethods

class Map540010001 {
   String eventName = "AirPlane"
   int toMap = 540010002

   def start(MapScriptMethods ms) {
      EventManager em = ms.getClient().getEventManager(eventName)

      //is the player late to start the travel?
      if (em.getProperty("docked") == "false") {
         ms.getClient().getPlayer().warpAhead(toMap)
      }
   }
}

Map540010001 getMap() {
   getBinding().setVariable("map", new Map540010001())
   return (Map540010001) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}