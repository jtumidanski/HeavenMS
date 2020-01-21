package reactor


import scripting.reactor.ReactorActionManager
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

class Reactor1209000 extends SimpleReactor {
   def act() {
      if (rm.isQuestStarted(6400)) {
         rm.setQuestProgress(6400, 1, 2)
         rm.setQuestProgress(6400, 6401, "q3")
      }
      MessageBroadcaster.getInstance().sendServerNotice(rm.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("REAL_BART_FOUND"))
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

def release() {
   getReactor().release()
}