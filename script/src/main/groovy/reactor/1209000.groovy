package reactor


import scripting.reactor.ReactorActionManager
import tools.MessageBroadcaster
import tools.ServerNoticeType


class Reactor1209000 {
   ReactorActionManager rm

   def act() {
      if (rm.isQuestStarted(6400)) {
         rm.setQuestProgress(6400, 1, 2)
         rm.setQuestProgress(6400, 6401, "q3")
      }
      MessageBroadcaster.getInstance().sendServerNotice(rm.getPlayer(), ServerNoticeType.PINK_TEXT, "Real Bart has found. Return to Jonathan through portal.")
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor1209000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor1209000(rm: rm))
   return (Reactor1209000) getBinding().getVariable("reactor")
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