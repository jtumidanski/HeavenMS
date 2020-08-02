package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		Jack
	Map(s): 		Nautilus' Port
	Description: 	
*/


class NPC1092010 {
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
            if (!cm.haveItem(4220153)) {
               cm.sendOk(I18nMessage.from("1092010_SCRATCH_SCRATCH"))
               cm.dispose()
            } else {
               cm.sendYesNo(I18nMessage.from("1092010_CAN_I_KEEP_THE_MAP"))
            }
         } else if (status == 1) {
            cm.gainItem(4220153, (short) -1)
            cm.dispose()
         }
      }
   }
}

NPC1092010 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1092010(cm: cm))
   }
   return (NPC1092010) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }