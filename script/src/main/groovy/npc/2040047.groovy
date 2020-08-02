package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		Sgt.Anderson
	Map(s): 		 Abandoned Tower <Stage 1>
	Description: 	
*/


class NPC2040047 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == 1) {
         status++
      } else {
         cm.dispose()
         return
      }
      int mapId = cm.getPlayer().getMapId()
      if (mapId == 922010000) {
         if (status == 0) {
            cm.sendNext(I18nMessage.from("2040047_RETURN"))

         } else {
            cm.warp(221024500)
            cm.dispose()
         }
      } else {
         if (status == 0) {
            String outText = "Once you leave the map, you'll have to restart the whole quest if you want to try it again.  Do you still want to leave this map?"
            cm.sendYesNo(outText)
         } else if (mode == 1) {
            cm.warp(922010000) // Warp player
            cm.dispose()
         }
      }
   }
}

NPC2040047 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2040047(cm: cm))
   }
   return (NPC2040047) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }