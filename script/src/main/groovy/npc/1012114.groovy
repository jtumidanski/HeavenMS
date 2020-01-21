package npc

import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager
import server.maps.MapleMap
import tools.I18nMessage

/*
	NPC Name: 		Growlie
	Map(s): 		
	Description: 	
*/

class NPC1012114 {
   NPCConversationManager cm
   int status = 0
   int sel = -1
   int chosen = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 0) {
         cm.dispose()
      } else {
         if (mode == 0 && status == 0) {
            cm.dispose()
            return
         }
         if (mode == 0) {
            status += ((chosen == 2) ? 1 : -1)
         } else {
            status++
         }

         if (status == 0) {
            if (cm.isEventLeader()) {
               cm.sendSimple(I18nMessage.from("1012114_GROWL_LEADER"))
            } else {
               cm.sendSimple(I18nMessage.from("1012114_GROWL"))
            }
         } else if (status == 1) {
            if (chosen == -1) {
               chosen = selection
            }
            if (chosen == 0) {
               cm.sendNext(I18nMessage.from("1012114_PRIME_SPOT"))
            } else if (chosen == 1) {
               if (cm.haveItem(4001101, 10)) {
                  cm.sendNext(I18nMessage.from("1012114_EXCHANGE"))
               } else {
                  cm.sendOk(I18nMessage.from("1012114_NOT_ENOUGH_TO_EXCHANGE"))
                  cm.dispose()
               }
            } else if (chosen == 2) {
               cm.sendYesNo(I18nMessage.from("1012114_ARE_YOU_SURE"))
            } else {
               cm.dispose()
            }
         } else if (status == 2) {
            if (chosen == 0) {
               cm.sendNextPrev(I18nMessage.from("1012114_GATHER_PRIMROSE"))
            } else if (chosen == 1) {
               cm.gainItem(4001101, (short) -10)

               EventInstanceManager eim = cm.getEventInstance()
               clearStage(1, eim)

               MapleMap map = eim.getMapInstance(cm.getPlayer().getMapId())
               map.killAllMonstersNotFriendly()

               eim.clearPQ()
               cm.dispose()
            } else {
               if (mode == 1) {
                  cm.warp(910010300)
               } else {
                  cm.sendOk(I18nMessage.from("1012114_TIME_RUNNING_OUT"))
               }
               cm.dispose()
            }
         } else if (status == 3) {
            if (chosen == 0) {
               cm.sendNextPrev(I18nMessage.from("1012114_TASK_INFO"))
            }
         } else if (status == 4) {
            if (chosen == 0) {
               cm.sendNextPrev(I18nMessage.from("1012114_TASK_INFO_SHORT"))
            }
         } else {
            cm.dispose()
         }
      }
   }

   static def clearStage(int stage, EventInstanceManager eim) {
      eim.setProperty(stage + "stageclear", "true")
      eim.showClearEffect(true)
      eim.giveEventPlayersStageReward(stage)
   }
}

NPC1012114 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1012114(cm: cm))
   }
   return (NPC1012114) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }