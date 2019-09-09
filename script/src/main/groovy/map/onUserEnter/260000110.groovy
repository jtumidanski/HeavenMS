package map.onUserEnter

import scripting.event.EventManager
import scripting.map.MapScriptMethods

class Map260000110 {
   String eventName = "Genie"
   int toMap = 200090410

   def start(MapScriptMethods ms) {
      EventManager em = ms.getClient().getEventManager(eventName)

      //is the player late to start the travel?
      if (em.getProperty("docked") == "false") {
         ms.getClient().getPlayer().warpAhead(toMap)
      }
   }
}

Map260000110 getMap() {
   getBinding().setVariable("map", new Map260000110())
   return (Map260000110) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}