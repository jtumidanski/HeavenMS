package reactor


import scripting.reactor.ReactorActionManager


class Reactor9208009 {
   ReactorActionManager rm

   def act() {
      if(rm.getEventInstance() != null) {
         rm.getEventInstance().setProperty("canRevive", "1")
      }
   }

   def hit() {

   }

   def touch() {

   }

   def release() {

   }
}

Reactor9208009 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor9208009(rm: rm))
   return (Reactor9208009) getBinding().getVariable("reactor")
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

def release() {
   getReactor().release()
}