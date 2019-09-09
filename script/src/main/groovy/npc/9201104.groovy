package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201104 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.getMapId() == 610020000) {
         cm.sendOk("O, brave adventurer. Just by reaching this spot, you are truly distinct among the masses, congratulations. However, #rpay heed#k: on the path ahead, which leads to the mighty fortress of #bCrimsonwood Keep#k, #rdeadly Menhirs#k are deployed as traps for those unaware of the dangers ahead. #rOne hit from it is enough to take you down#k, so beware. If you aim to reach the Keep, follow the trail ahead carefully.")
      } else if (cm.getMapId() == 610020003) {
         cm.sendOk("You seem worthy now to receive a hint for what lies ahead. Once inside the main room of the Keep, make sure you remember the layout of the statue you see there. That's it.")
      } else if (cm.getMapId() == 610020004) {
         cm.sendOk("You seem worthy now to receive a hint for what lies ahead. Devices known as Sigils are activated by detection when some skills of certain jobs are activated nearby, make sure your team is made whole for when the time comes. That's it.")
      } else {
         cm.sendOk("So far your progress is splendid, good job. However, to make it to the Keep, you must face and accomplish this ordeal, carry on.")
      }
      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC9201104 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201104(cm: cm))
   }
   return (NPC9201104) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }