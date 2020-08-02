package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9220019 {
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
            int mapId = cm.getMapId()
            if (mapId == 674030100) {
               cm.sendNext(I18nMessage.from("9220019_HELLO"))
               cm.dispose()
               return
            } else if (mapId == 674030300) {
               cm.sendNext(I18nMessage.from("9220019_TREASURE_ROOM"))
               cm.dispose()
               return
            }

            cm.sendYesNo(I18nMessage.from("9220019_WANT_TO_RETURN"))
         } else if (status == 1) {
            cm.warp(674030100)
            cm.dispose()
         }
      }
   }
}

NPC9220019 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9220019(cm: cm))
   }
   return (NPC9220019) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }