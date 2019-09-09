package reactor


import scripting.reactor.ReactorActionManager


class Reactor9208004 {
   ReactorActionManager rm

   def act() {
      rm.getGuild().gainGP(20)
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor9208004 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor9208004(rm: rm))
   return (Reactor9208004) getBinding().getVariable("reactor")
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