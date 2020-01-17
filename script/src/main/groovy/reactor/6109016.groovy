package reactor

import scripting.event.EventInstanceManager
import scripting.reactor.ReactorActionManager
import tools.MessageBroadcaster
import tools.ServerNoticeType
import tools.I18nMessage

class Reactor6109016 extends SimpleReactor {
   def act() {
      EventInstanceManager eim = rm.getEventInstance()
      if (eim != null) {
         MessageBroadcaster.getInstance().sendServerNotice(eim.getPlayers(), ServerNoticeType.LIGHT_BLUE, I18nMessage.from("WARRIOR_SIGIL_ACTIVATED"))
         eim.setIntProperty("glpq4", eim.getIntProperty("glpq4") + 1)
         if (eim.getIntProperty("glpq4") == 5) { //all 5 done
            MessageBroadcaster.getInstance().sendServerNotice(eim.getPlayers(), ServerNoticeType.LIGHT_BLUE, I18nMessage.from("ANTELLION_NEXT"))

            eim.showClearEffect(610030400, "4pt", 2)
            eim.giveEventPlayersStageReward(4)
         }
      }
   }
}

Reactor6109016 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor6109016(rm: rm))
   return (Reactor6109016) getBinding().getVariable("reactor")
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