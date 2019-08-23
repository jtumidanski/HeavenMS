package map.onUserEnter

import scripting.event.EventManager
import scripting.map.MapScriptMethods

class Map101000301 {
   String eventName = "Boats"
   int toMap = 200090010

   def start(MapScriptMethods ms) {
      EventManager em = ms.getClient().getEventManager(eventName)

      //is the player late to start the travel?
      if (em.getProperty("docked") == "false") {
         ms.getClient().getPlayer().warpAhead(toMap)
      }
   }
}

Map101000301 getMap() {
   getBinding().setVariable("map", new Map101000301())
   return (Map101000301) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}