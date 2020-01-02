package map.onUserEnter


import scripting.map.MapScriptMethods

class MapCygnusJobTutorial {

   def start(MapScriptMethods ms) {
      ms.displayCygnusIntro()
   }
}

MapCygnusJobTutorial getMap() {
   getBinding().setVariable("map", new MapCygnusJobTutorial())
   return (MapCygnusJobTutorial) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}