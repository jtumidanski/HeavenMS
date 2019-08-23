package map.onUserEnter

import scripting.event.EventManager
import scripting.map.MapScriptMethods

class Map220000111 {
   String eventName = "Trains"
   int toMap = 200090110

   def start(MapScriptMethods ms) {
      EventManager em = ms.getClient().getEventManager(eventName)

      //is the player late to start the travel?
      if (em.getProperty("docked") == "false") {
         ms.getClient().getPlayer().warpAhead(toMap)
      }
   }
}

Map220000111 getMap() {
   getBinding().setVariable("map", new Map220000111())
   return (Map220000111) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}