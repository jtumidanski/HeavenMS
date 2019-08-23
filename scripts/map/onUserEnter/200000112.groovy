package map.onUserEnter

import scripting.event.EventManager
import scripting.map.MapScriptMethods

class Map200000112 {
   String eventName = "Boats"
   int toMap = 200090000

   def start(MapScriptMethods ms) {
      EventManager em = ms.getClient().getEventManager(eventName)

      //is the player late to start the travel?
      if (em.getProperty("docked") == "false") {
         ms.getClient().getPlayer().warpAhead(toMap)
      }
   }
}

Map200000112 getMap() {
   getBinding().setVariable("map", new Map200000112())
   return (Map200000112) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}