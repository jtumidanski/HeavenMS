package reactor


import scripting.reactor.ReactorActionManager

class Reactor6741001 extends SimpleReactor {
   def act() {
      rm.spawnMonster(9400589)
   }
}

Reactor6741001 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor6741001(rm: rm))
   return (Reactor6741001) getBinding().getVariable("reactor")
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