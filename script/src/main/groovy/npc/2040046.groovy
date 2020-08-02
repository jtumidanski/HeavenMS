package npc

import client.processor.BuddyListProcessor
import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		Robert Holly
	Map(s): 		Ludibrium: Ludibrium
	Description: 	
*/


class NPC2040046 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (status == 0 && mode == 0) {
            cm.sendNext(I18nMessage.from("2040046_I_SEE"))

            cm.dispose()
            return
         } else if (status >= 1 && mode == 0) {
            cm.sendNext(I18nMessage.from("2040046_NOT_AS_MANY_FRIENDS"))

            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 0) {
            cm.sendYesNo(I18nMessage.from("2040046_I_HOPE_I_CAN"))

         } else if (status == 1) {
            cm.sendYesNo(I18nMessage.from("2040046_ALRIGHT"))

         } else if (status == 2) {
            int capacity = cm.getPlayer().getBuddyList().capacity()
            if (capacity >= 50 || cm.getMeso() < 240000) {
               cm.sendNext(I18nMessage.from("2040046_ARE_YOU_SURE"))

               cm.dispose()
            } else {
               int newCapacity = capacity + 5
               BuddyListProcessor.getInstance().updateCapacity(cm.getPlayer(), newCapacity, {
                  cm.gainMeso(-240000)
                  cm.sendOk(I18nMessage.from("2040046_SUCCESS"))

               }, {
                  cm.sendOk(I18nMessage.from("2040046_ISSUE_INCREASING_SIZE"))

               })
               cm.dispose()
            }
         }
      }
   }
}

NPC2040046 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2040046(cm: cm))
   }
   return (NPC2040046) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }