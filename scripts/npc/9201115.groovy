package npc

import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager
import server.life.MapleMonster
import tools.MessageBroadcaster
import tools.ServerNoticeType

import java.awt.*

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201115 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      EventInstanceManager eim = cm.getEventInstance()
      if (eim != null && eim.getIntProperty("glpq6") == 3) {
         cm.sendOk("... Well played. You overtook the Twisted Masters. Pass through that gate to receive your prizes.")
         cm.dispose()
         return
      }

      if (!cm.isEventLeader()) {
         cm.sendNext("I wish for your leader to talk to me.")
         cm.dispose()
         return
      }

      if (mode == 1) {
         status++
      } else {
         status--
      }

      if (eim != null) {
         if (eim.getIntProperty("glpq6") == 0) {
            if (status == 0) {
               cm.sendNext("Welcome to the Twisted Masters' Keep. I will be your host for this evening...")
            } else if (status == 1) {
               cm.sendNext("Tonight, we have a feast of a squad of Maplers.. ahaha...")
            } else if (status == 2) {
               cm.sendNext("Let our specially trained Master Guardians escort you!")
               MessageBroadcaster.getInstance().sendMapServerNotice(cm.getPlayer().getMap(), ServerNoticeType.LIGHT_BLUE, "Engarde! Master Guardians approach!")
               for (int i = 0; i < 10; i++) {
                  MapleMonster mob = eim.getMonster(9400594)
                  cm.getMap().spawnMonsterOnGroundBelow(mob, new Point(-1337 + (Math.random() * 1337).intValue(), 276))
               }
               for (int i = 0; i < 20; i++) {
                  MapleMonster mob = eim.getMonster(9400582)
                  cm.getMap().spawnMonsterOnGroundBelow(mob, new Point(-1337 + (Math.random() * 1337).intValue(), 276))
               }
               eim.setIntProperty("glpq6", 1)
               cm.dispose()
            }
         } else if (eim.getIntProperty("glpq6") == 1) {
            if (cm.getMap().countMonsters() == 0) {
               if (status == 0) {
                  cm.sendOk("Eh. What is this? You've defeated them?")
               } else if (status == 1) {
                  cm.sendNext("Well, no matter! The Twisted Masters will be glad to welcome you.")
                  MessageBroadcaster.getInstance().sendMapServerNotice(cm.getPlayer().getMap(), ServerNoticeType.LIGHT_BLUE, "Twisted Masters approach!")

                  //Margana
                  MapleMonster mob = eim.getMonster(9400590)
                  cm.getMap().spawnMonsterOnGroundBelow(mob, new Point(-22, 1))

                  //Red Nirg
                  MapleMonster mob2 = eim.getMonster(9400591)
                  cm.getMap().spawnMonsterOnGroundBelow(mob2, new Point(-22, 276))

                  //Hsalf
                  MapleMonster mob4 = eim.getMonster(9400593)
                  cm.getMap().spawnMonsterOnGroundBelow(mob4, new Point(496, 276))

                  //Rellik
                  MapleMonster mob3 = eim.getMonster(9400592)
                  cm.getMap().spawnMonsterOnGroundBelow(mob3, new Point(-496, 276))

                  eim.setIntProperty("glpq6", 2)
                  cm.dispose()
               }
            } else {
               cm.sendOk("Pay no attention to me. The Master Guardians will escort you!")
               cm.dispose()
            }
         } else if (eim.getIntProperty("glpq6") == 2) {
            if (cm.getMap().countMonsters() == 0) {
               cm.sendOk("WHAT? Ugh... this can't be happening.")
               MessageBroadcaster.getInstance().sendMapServerNotice(cm.getPlayer().getMap(), ServerNoticeType.PINK_TEXT, "The portal to the next stage has opened!")
               eim.setIntProperty("glpq6", 3)

               eim.showClearEffect(true)
               eim.giveEventPlayersStageReward(6)

               eim.clearPQ()
               cm.dispose()
            } else {
               cm.sendOk("Pay no attention to me. The Twisted Masters will escort you!")
               cm.dispose()
            }
         } else {
            cm.sendOk("... Well played. You overtook the Twisted Masters. Pass through that gate to receive your prizes.")
            cm.dispose()
         }
      } else {
         cm.dispose()
      }
   }
}

NPC9201115 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201115(cm: cm))
   }
   return (NPC9201115) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }