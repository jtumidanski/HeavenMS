package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1072008 {
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
            if (cm.getMapId() == 108000502) {
               if (!(cm.haveItem(4031856, 15))) {
                  cm.sendSimple(I18nMessage.from("1072008_MISSING_CRYSTALS"))
               } else {
                  status++
                  cm.sendNext(I18nMessage.from("1072008_CONGRATULATIONS"))
               }
            } else if (cm.getMapId() == 108000501) {
               if (!(cm.haveItem(4031857, 15))) {
                  cm.sendSimple(I18nMessage.from("1072008_MISSING_CRYSTALS"))
               } else {
                  status++
                  cm.sendNext(I18nMessage.from("1072008_CONGRATULATIONS"))
               }
            } else {
               cm.sendNext(I18nMessage.from("1072008_ERROR"))
               cm.dispose()
            }
         } else if (status == 1) {
            cm.removeAll(4031856)
            cm.removeAll(4031857)
            cm.warp(120000101, 0)
            cm.dispose()
         } else if (status == 2) {
            cm.warp(120000101, 0)
            cm.dispose()
         }
      }
   }
}

NPC1072008 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1072008(cm: cm))
   }
   return (NPC1072008) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }