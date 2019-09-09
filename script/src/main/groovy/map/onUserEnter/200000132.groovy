package map.onUserEnter

import scripting.event.EventManager
import scripting.map.MapScriptMethods

class Map200000132 {
   String eventName = "Cabin"
   int toMap = 200090200

   def start(MapScriptMethods ms) {
      EventManager em = ms.getClient().getEventManager(eventName)

      //is the player late to start the travel?
      if (em.getProperty("docked") == "false") {
         ms.getClient().getPlayer().warpAhead(toMap)
      }
   }
}

Map200000132 getMap() {
   getBinding().setVariable("map", new Map200000132())
   return (Map200000132) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}