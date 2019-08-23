package map.onUserEnter

import scripting.event.EventManager
import scripting.map.MapScriptMethods

class Map600010002 {
   String eventName = "Subway"
   int toMap = 600010003

   def start(MapScriptMethods ms) {
      EventManager em = ms.getClient().getEventManager(eventName)

      //is the player late to start the travel?
      if (em.getProperty("docked") == "false") {
         ms.getClient().getPlayer().warpAhead(toMap)
      }
   }
}

Map600010002 getMap() {
   getBinding().setVariable("map", new Map600010002())
   return (Map600010002) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}