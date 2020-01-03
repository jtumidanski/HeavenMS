package reactor

import net.server.processor.MapleGuildProcessor
import scripting.reactor.ReactorActionManager

class Reactor9208004 extends SimpleReactor {
   def act() {
      MapleGuildProcessor.getInstance().gainGP(rm.getGuild(), 20)
   }
}

Reactor9208004 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor9208004(rm: rm))
   return (Reactor9208004) getBinding().getVariable("reactor")
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