package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201098 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.getQuestStatus(8223) == 2) {
         if (cm.haveItem(3992041)) {
            cm.sendOk("We, defenders of Yore, are currently meeting at the Inner Sactum inside the Keep, about to start an offensive against the Twisted Masters and their army. Join us there anytime.")
         } else {
            if (!cm.canHold(3992041)) {
               cm.sendOk("Please make a slot on your SETUP ready for the key I have to give to you. It is fundamental to enter the Inner Sanctum, inside the Keep.")
            } else {
               cm.sendOk("So you did lost your key, right? Very well, I will craft you another one, but please don't lose it again. It is fundamental to enter the Inner Sanctum, inside the Keep.")
               cm.gainItem(3992041, (short) 1)
            }
         }
      } else {
         cm.sendOk("O, brave adventurer. The Stormcasters house, from which I belong, guards the surrounding area of Yore, this landscape, from the forces of the Twisted Masters' guard that daily threatens the citizens. Please help us on the defense of Yore.")
      }

      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC9201098 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201098(cm: cm))
   }
   return (NPC9201098) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }