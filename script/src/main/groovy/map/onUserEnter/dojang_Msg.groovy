package map.onUserEnter


import scripting.map.MapScriptMethods

class MapDojang_Msg {
   String[] messages = ["Your courage for challenging the Mu Lung Dojo is commendable!", "If you want to taste the bitterness of defeat, come on in!", "I will make you thoroughly regret challenging the Mu Lung Dojo! Hurry up!"]

   def start(MapScriptMethods ms) {
      if (ms.getPlayer().getMap().getId() == 925020000) {
         if (ms.getPlayer().getMap().findClosestPlayerSpawnPoint(ms.getPlayer().position()).getId() == 0) {
            int messageIndex = (Math.random() * messages.length) | 0
            ms.getPlayer().startMapEffect(messages[messageIndex], 5120024)
         }

         ms.resetDojoEnergy()
      } else {
         ms.getPlayer().resetEnteredScript() //in case the person dcs in here we set it at dojang_tuto portal
         ms.getPlayer().startMapEffect("Ha! Let's see what you got! I won't let you leave unless you defeat me first!", 5120024)
      }
   }
}

MapDojang_Msg getMap() {
   getBinding().setVariable("map", new MapDojang_Msg())
   return (MapDojang_Msg) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}