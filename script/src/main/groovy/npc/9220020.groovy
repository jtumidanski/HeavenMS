package npc
import tools.I18nMessage

import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9220020 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0 && type > 0) {
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            if (!cm.isEventLeader()) {
               cm.sendNext(I18nMessage.from("9220020_PARTY_LEADER_MUST_TALK"))
               cm.dispose()
               return
            }

            EventInstanceManager eim = cm.getEventInstance()
            if (eim.getIntProperty("statusStg1") == 1) {
               cm.sendNext(I18nMessage.from("9220020_BOSS_BATTLE"))
            } else {
               if (cm.haveItem(4032118, 15)) {
                  cm.gainItem(4032118, (short) -15)

                  eim.setIntProperty("statusStg1", 1)
                  eim.showClearEffect()
                  eim.giveEventPlayersStageReward(1)

                  cm.sendNext(I18nMessage.from("9220020_BE_PREPARED"))
               } else {
                  cm.sendNext(I18nMessage.from("9220020_HAND_ME"))
               }
            }

            cm.dispose()
         }
      }
   }
}

NPC9220020 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9220020(cm: cm))
   }
   return (NPC9220020) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }