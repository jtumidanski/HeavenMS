package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1061018 {
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
            if (cm.getEventInstance().isEventCleared()) {
               cm.sendOk("Wow! You defeated the balrog.")
            } else if (cm.getPlayer().getMap().getCharacters().size() > 1) {
               cm.sendYesNo("Are you really going to leave this battle and leave your fellow travelers to die?")
            } else {
               cm.sendYesNo("If you're a coward, you will leave.")
            }
         } else if (status == 1) {
            if (cm.getEventInstance().isEventCleared()) {
               cm.warp(cm.getMapId() == 105100300 ? 105100301 : 105100401, 0)
            } else {
               cm.warp(105100100)
            }

            cm.dispose()
         }
      }
   }
}

NPC1061018 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1061018(cm: cm))
   }
   return (NPC1061018) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }