package map.onUserEnter


import scripting.map.MapScriptMethods

class MapcygnusJobTutorial {

   def start(MapScriptMethods ms) {
      ms.displayCygnusIntro()
   }
}

MapcygnusJobTutorial getMap() {
   getBinding().setVariable("map", new MapcygnusJobTutorial())
   return (MapcygnusJobTutorial) getBinding().getVariable("map")
}

def start(MapScriptMethods ms) {
   getMap().start(ms)
}