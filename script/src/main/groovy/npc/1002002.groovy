package npc

import scripting.npc.NPCConversationManager
import tools.I18nMessage

class NPC1002002 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   def start() {
      cm.sendSimple(I18nMessage.from("1002002_HAVE_YOU_HEARD"))
   }

   def action(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if ((mode == 0 && type == 1) || mode == -1 || (mode == 0 && status == 1)) {
            if (type == 1) {
               cm.sendNext(I18nMessage.from("1002002_UNFINISHED_BUSINESS"))
            }
            cm.dispose()
            return
         } else {
            status -= 2
         }
      }
      if (selection == 0) {
         status++
      }
      if (status == 1) {
         if (selection == 1) {
            cm.sendYesNo(I18nMessage.from("1002002_VIP_TICKET"))
         } else if (selection == 2) {
            cm.sendNext(I18nMessage.from("1002002_HOW_TO_VIP"))
         }
      } else if (status == 2) {
         if (type != 1 && selection != 0) {
            cm.sendNextPrev(I18nMessage.from("1002002_MAY_BE_ABLE_TO_PICK_IT_UP"))
            cm.dispose()
         } else {
            if (cm.getMeso() < 1500 && selection == 0) {
               cm.sendNext(I18nMessage.from("1002002_LACKING_MESO"))
            } else if (!cm.haveItem(4031134) && selection != 0) {
               cm.sendNext(I18nMessage.from("1002002_ARE_YOU_SURE_YOU_HAVE_VIP"))
            } else {
               if (selection == 0) {
                  cm.gainMeso(-1500)
               }
               cm.getPlayer().saveLocation("FLORINA")
               cm.warp(110000000, "st00")
            }
            cm.dispose()
         }
      }
   }
}

NPC1002002 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1002002(cm: cm))
   }
   return (NPC1002002) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }