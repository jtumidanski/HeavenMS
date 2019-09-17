package reactor


import scripting.reactor.ReactorActionManager
import tools.MessageBroadcaster
import tools.ServerNoticeType


class Reactor2200001 {
   ReactorActionManager rm

   def act() {
      MessageBroadcaster.getInstance().sendServerNotice(rm.getPlayer(), ServerNoticeType.PINK_TEXT, "You have found a secret factory!")
      rm.warp(Math.random() < 0.5 ? 922000020 : 922000021, 0)
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor2200001 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2200001(rm: rm))
   return (Reactor2200001) getBinding().getVariable("reactor")
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