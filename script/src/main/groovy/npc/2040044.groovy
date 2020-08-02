package npc

import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2040044 {
   NPCConversationManager cm
   int status = 0
   int sel = -1
   int curMap, stage

   def start() {
      curMap = cm.getMapId()
      stage = Math.floor((curMap - 922010100) / 100).toInteger() + 1
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else if (mode == 0) {
         cm.dispose()
      } else {
         if (mode == 1) {
            status++
         } else {
            status--
         }

         EventInstanceManager eim = cm.getPlayer().getEventInstance()

         if (eim.getProperty(stage.toString() + "stageclear") != null) {
            cm.sendNext(I18nMessage.from("2040044_GO_TO"))

         } else {
            if (eim.isEventLeader(cm.getPlayer())) {
               int state = eim.getIntProperty("statusStg" + stage)

               if (state == -1) {           // preamble
                  cm.sendOk(I18nMessage.from("2040044_WELCOME"))

                  eim.setProperty("statusStg" + stage, 0)
               } else {                      // check stage completion
                  if (cm.haveItem(4001023, 1)) {
                     cm.gainItem(4001023, (short) -1)
                     eim.setProperty("statusStg" + stage, 1)

                     List<Integer> list = eim.getClearStageBonus(stage)
                     // will give bonus exp & mesos to everyone in the event
                     eim.giveEventPlayersExp(list.get(0))
                     eim.giveEventPlayersMeso(list.get(1))

                     eim.setProperty(stage + "stageclear", "true")
                     eim.showClearEffect(true)

                     eim.clearPQ()
                  } else {
                     cm.sendNext(I18nMessage.from("2040044_DEFEAT_ALISHAR"))

                  }
               }
            } else {
               cm.sendNext(I18nMessage.from("2040044_PARTY_LEADER_MUST_TALK"))

            }
         }

         cm.dispose()
      }
   }
}

NPC2040044 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2040044(cm: cm))
   }
   return (NPC2040044) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }