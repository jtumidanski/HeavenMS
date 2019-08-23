package reactor


import scripting.reactor.ReactorActionManager


class Reactor2006000 {
   ReactorActionManager rm

   def act() {
      rm.mapMessage(5, "As the light flickers, someone appears out of the light.")
      rm.spawnNpc(2013001)
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor2006000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2006000(rm: rm))
   return (Reactor2006000) getBinding().getVariable("reactor")
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