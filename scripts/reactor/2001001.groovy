package reactor


import scripting.reactor.ReactorActionManager


class Reactor2001001 {
   ReactorActionManager rm

   def act() {
      if (rm.getMap().getSummonState()) {
         int count = rm.getEventInstance().getIntProperty("statusStg7_c")

         if (count < 7) {
            int nextCount = (count + 1)

            rm.spawnMonster(Math.random() >= 0.6 ? 9300049 : 9300048)
            rm.getEventInstance().setProperty("statusStg7_c", nextCount)
         } else {
            rm.spawnMonster(9300049)
         }
      }
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor2001001 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2001001(rm: rm))
   return (Reactor2001001) getBinding().getVariable("reactor")
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