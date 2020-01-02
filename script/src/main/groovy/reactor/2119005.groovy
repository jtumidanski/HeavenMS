package reactor


import scripting.reactor.ReactorActionManager


class Reactor2119005 {
   ReactorActionManager rm

   def act() {

   }

   def hit() {
      rm.hitMonsterWithReactor(6090001, 4)
      rm.getReactor().setEventState(Math.floor(Math.random() * 3).byteValue())
   }

   def touch() {

   }

   def release() {

   }
}

Reactor2119005 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2119005(rm: rm))
   return (Reactor2119005) getBinding().getVariable("reactor")
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