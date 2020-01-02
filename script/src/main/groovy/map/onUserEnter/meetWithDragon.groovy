package map.onUserEnter


import scripting.map.MapScriptMethods

class MapMeetWithDragon {

   static def start(MapScriptMethods ms) {
      ms.lockUI()
      ms.showIntro("Effect/Direction4.img/meetWithDragon/Scene" + ms.getPlayer().getGender())
   }
}

MapMeetWithDragon getMap() {
   getBinding().setVariable("map", new MapMeetWithDragon())
   return (MapMeetWithDragon) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}