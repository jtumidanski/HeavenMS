package reactor


import scripting.reactor.ReactorActionManager

class Reactor2119006 extends SimpleReactor {
   def act() {

   }

   def hit() {
      rm.hitMonsterWithReactor(6090001, 4)
      rm.getReactor().setEventState(Math.floor(Math.random() * 3).byteValue())
   }
}

Reactor2119006 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2119006(rm: rm))
   return (Reactor2119006) getBinding().getVariable("reactor")
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