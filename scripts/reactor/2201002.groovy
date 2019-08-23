package reactor


import scripting.reactor.ReactorActionManager


class Reactor2201002 {
   ReactorActionManager rm

   def act() {
      rm.mapMessage(5, "Rombard has been summoned somewhere in the map.")
      rm.spawnMonster(9300010, 1, -211)
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor2201002 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2201002(rm: rm))
   return (Reactor2201002) getBinding().getVariable("reactor")
}

def act() {
   getReactor().act()
}

def hit() {
   getReactor().hit()
}

def touch() {
   getReactor().touch()
}

def untouch() {
   getReactor().untouch()
}