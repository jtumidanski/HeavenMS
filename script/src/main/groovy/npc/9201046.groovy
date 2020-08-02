package npc
import tools.I18nMessage

import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201046 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   boolean debug = false
   int curMap, stage

   def start() {
      curMap = cm.getMapId()
      stage = Math.floor((curMap - 670010200) / 100).intValue() + 1
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
         if (curMap == 670010750) {
            if (cm.haveItem(4031597, 35)) {
               if (cm.canHold(1102101) && eim.getIntProperty("marriedGroup") == 0) {
                  eim.setIntProperty("marriedGroup", 1)

                  int baseId = (cm.getPlayer().getGender() == 0) ? 1102101 : 1102104
                  int rnd = Math.floor(Math.random() * 3).intValue()
                  cm.gainItem(baseId + rnd)

                  cm.sendNext(I18nMessage.from("9201046_BRAVO"))
                  cm.gainItem(4031597, (short) -35)
                  cm.gainExp(4000 * cm.getPlayer().getExpRate())
               } else if (eim.getIntProperty("marriedGroup") == 0) {
                  cm.sendNext(I18nMessage.from("9201046_NEED_SLOT_SPACE"))
               } else {
                  cm.sendNext(I18nMessage.from("9201046_NICELY_DONE"))
                  cm.gainItem(4031597, (short) -35)
                  cm.gainExp(4000 * cm.getPlayer().getExpRate())
               }
            } else {
               cm.sendNext(I18nMessage.from("9201046_CLAIM_A_PRIZE"))
            }
         } else {
            cm.sendNext(I18nMessage.from("9201046_HURRY_UP"))
         }

         cm.dispose()
      }
   }
}

NPC9201046 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201046(cm: cm))
   }
   return (NPC9201046) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }