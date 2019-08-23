package map.onUserEnter

import scripting.event.EventManager
import scripting.map.MapScriptMethods

class Map600010004 {
   String eventName = "Subway"
   int toMap = 600010005

   def start(MapScriptMethods ms) {
      EventManager em = ms.getClient().getEventManager(eventName)

      //is the player late to start the travel?
      if (em.getProperty("docked") == "false") {
         ms.getClient().getPlayer().warpAhead(toMap)
      }
   }
}

Map600010004 getMap() {
   getBinding().setVariable("map", new Map600010004())
   return (Map600010004) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}