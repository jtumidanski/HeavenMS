package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1061010 {
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
      }//ExitChat
      else if (mode == 0) {
         cm.dispose()
      }//No
      else {          //Regular Talk
         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            cm.sendYesNo(I18nMessage.from("1061010_WOULD_YOU_LIKE_TO_LEAVE"))
         } else if (status == 1) {
            int mapId = cm.getMapId()
            int exitId = mapId
            if (mapId == 108010101) {
               exitId = 105040305
            } else if (mapId == 108010201) {
               exitId = 100040106
            } else if (mapId == 108010301) {
               exitId = 105070001
            } else if (mapId == 108010401) {
               exitId = 107000402
            } else if (mapId == 108010501) {
               exitId = 105070200
            }

            if (mapId != exitId) {
               cm.getPlayer().changeMap(exitId)
            }
            cm.dispose()
         }
      }
   }
}

NPC1061010 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1061010(cm: cm))
   }
   return (NPC1061010) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }