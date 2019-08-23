package reactor


import scripting.reactor.ReactorActionManager


class Reactor5411000 {
   ReactorActionManager rm

   def act() {
      rm.changeMusic("Bgm09/TimeAttack")
      rm.spawnMonster(9420513, -146, 225)
      rm.getEventInstance().setIntProperty("boss", 1)
      rm.mapMessage(5, "As you wish, here comes Capt Latanica.")
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor5411000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor5411000(rm: rm))
   return (Reactor5411000) getBinding().getVariable("reactor")
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