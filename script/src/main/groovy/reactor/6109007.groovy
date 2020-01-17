package reactor

import scripting.event.EventInstanceManager
import scripting.reactor.ReactorActionManager
import tools.MessageBroadcaster
import tools.ServerNoticeType
import tools.I18nMessage

class Reactor6109007 extends SimpleReactor {
   def act() {
      EventInstanceManager eim = rm.getEventInstance()
      if (eim != null) {
         MessageBroadcaster.getInstance().sendServerNotice(eim.getPlayers(), ServerNoticeType.LIGHT_BLUE, I18nMessage.from("RELIC_OF_MASTERY_AWARDED"))
         eim.setIntProperty("glpq5", eim.getIntProperty("glpq5") + 1)
         if (eim.getIntProperty("glpq5") == 5) { //all 5 done
            MessageBroadcaster.getInstance().sendServerNotice(eim.getPlayers(), ServerNoticeType.LIGHT_BLUE, I18nMessage.from("ANTELLION_NEXT"))

            eim.showClearEffect(610030500, "5pt", 2)
            eim.giveEventPlayersStageReward(5)
         }
      }
   }
}

Reactor6109007 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor6109007(rm: rm))
   return (Reactor6109007) getBinding().getVariable("reactor")
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