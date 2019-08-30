package reactor


import scripting.reactor.ReactorActionManager
import tools.MessageBroadcaster
import tools.ServerNoticeType


class Reactor2001016 {
   ReactorActionManager rm

   def act() {
      rm.getMap().killAllMonsters()
      rm.getMap().allowSummonState(false)
      rm.spawnMonster(9300039, 260, 490)
      MessageBroadcaster.getInstance().sendMapServerNotice(rm.getPlayer().getMap(), ServerNoticeType.PINK_TEXT, "As the air on the tower outskirts starts to become more dense, Papa Pixie appears.")
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor2001016 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2001016(rm: rm))
   return (Reactor2001016) getBinding().getVariable("reactor")
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