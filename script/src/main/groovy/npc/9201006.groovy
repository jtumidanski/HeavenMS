package npc
import tools.I18nMessage

import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201006 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1 || mode == 0) {
         cm.sendOk(I18nMessage.from("9201006_GOOD_BYE"))
         cm.dispose()
         return
      } else if (mode == 1) {
         status++
      } else {
         status--
      }

      EventInstanceManager eim = cm.getEventInstance()
      if (eim == null) {
         cm.warp(680000000, 0)
         cm.dispose()
         return
      }

      boolean isMarrying = (cm.getPlayer().getId() == eim.getIntProperty("groomId") || cm.getPlayer().getId() == eim.getIntProperty("brideId"))

      switch (status) {
         case 0:
            boolean hasEngagement = false
            for (int x = 4031357; x <= 4031364; x++) {
               if (cm.haveItem(x, 1)) {
                  hasEngagement = true
                  break
               }
            }

            if (cm.haveItem(4000313) && isMarrying) {
               if (eim.getIntProperty("weddingStage") == 3) {
                  cm.sendOk(I18nMessage.from("9201006_CONGRATULATIONS"))
                  cm.dispose()
               } else if (hasEngagement) {
                  if (!cm.createMarriageWishList()) {
                     cm.sendOk(I18nMessage.from("9201006_ALREADY_SENT_WISH_LIST"))
                  }
                  cm.dispose()
               } else {
                  cm.sendOk(I18nMessage.from("9201006_MISSING_REQUIRED_ITEM"))
               }
            } else {
               if (eim.getIntProperty("weddingStage") == 3) {
                  if (!isMarrying) {
                     cm.sendYesNo(I18nMessage.from("9201006_WAIT_HERE_FOR_THEM"))
                  } else {
                     cm.sendOk(I18nMessage.from("9201006_CONGRATULATIONS"))
                     cm.dispose()
                  }
               } else {
                  cm.sendYesNo(I18nMessage.from("9201006_ARE_YOU_SURE"))
               }
            }
            break

         case 1:
            cm.warp(680000000, 0)
            cm.dispose()
            break
      }
   }
}

NPC9201006 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201006(cm: cm))
   }
   return (NPC9201006) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }