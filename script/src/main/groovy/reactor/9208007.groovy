package reactor

import net.server.processor.MapleGuildProcessor
import scripting.event.EventInstanceManager
import scripting.reactor.ReactorActionManager
import server.maps.MapleReactor


class Reactor9208007 {
   ReactorActionManager rm

   def act() {
      MapleReactor react = rm.getPlayer().getEventInstance().getMapInstance(990000400).getReactorByName("speargate")
      react.forceHitReactor((byte) (react.getState() + 1))

      if (react.getState() == ((byte) 4)) {
         EventInstanceManager eim = rm.getPlayer().getEventInstance()

         int[] maps = [990000400, 990000410, 990000420, 990000430, 990000431, 990000440]
         for (int i = 0; i < maps.length; i++) {
            eim.showClearEffect(false, maps[i])
         }

         MapleGuildProcessor.getInstance().gainGP(rm.getGuild(), 20)
      }
   }

   def hit() {

   }

   def touch() {

   }

   def release() {

   }
}

Reactor9208007 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor9208007(rm: rm))
   return (Reactor9208007) getBinding().getVariable("reactor")
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