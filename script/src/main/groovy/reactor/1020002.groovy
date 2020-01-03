package reactor


import scripting.reactor.ReactorActionManager

class Reactor1020002 extends SimpleReactor {
   def act() {
      rm.warp(910200000, "pt02")
   }
}

Reactor1020002 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor1020002(rm: rm))
   return (Reactor1020002) getBinding().getVariable("reactor")
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