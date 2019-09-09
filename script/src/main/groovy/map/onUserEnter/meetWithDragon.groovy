package map.onUserEnter


import scripting.map.MapScriptMethods

class MapmeetWithDragon {

   static def start(MapScriptMethods ms) {
      ms.lockUI()
      ms.showIntro("Effect/Direction4.img/meetWithDragon/Scene" + ms.getPlayer().getGender())
   }
}

MapmeetWithDragon getMap() {
   getBinding().setVariable("map", new MapmeetWithDragon())
   return (MapmeetWithDragon) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}