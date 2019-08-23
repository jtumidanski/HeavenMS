package reactor


import scripting.reactor.ReactorActionManager


class Reactor2008006 {
   ReactorActionManager rm

   def act() {
      rm.getEventInstance().setProperty("statusStg3", "0")
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor2008006 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2008006(rm: rm))
   return (Reactor2008006) getBinding().getVariable("reactor")
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