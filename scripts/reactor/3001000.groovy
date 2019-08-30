package reactor


import scripting.reactor.ReactorActionManager
import tools.MessageBroadcaster
import tools.ServerNoticeType


class Reactor3001000 {
   ReactorActionManager rm

   def act() {
      MessageBroadcaster.getInstance().sendServerNotice(rm.getPlayer(), ServerNoticeType.PINK_TEXT, "Poison Golem has been spawned.")
      rm.spawnMonster(9300180,1)
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor3001000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor3001000(rm: rm))
   return (Reactor3001000) getBinding().getVariable("reactor")
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