package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1104200 {
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
            cm.sendNext("#b#p1104002##k... The black witch... Trapped me here... There's no time now, she's already on her way to #rattack Ereve#k!")
         } else if (status == 1) {
            cm.sendYesNo("Fellow Knight, you must reach to #rEreve#k right now, #rthe Empress is in danger#k!! Even in this condition, I can still Magic Warp you there. When you're ready talk to me. #bAre you ready to face Eleanor?#k")
         } else if (status == 2) {
            if (cm.getWarpMap(913030000).countPlayers() == 0) {
               cm.warp(913030000, 0)
            } else {
               cm.sendOk("There's someone already challenging her. Please wait awhile.")
            }

            cm.dispose()
         }
      }
   }
}

NPC1104200 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1104200(cm: cm))
   }
   return (NPC1104200) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }