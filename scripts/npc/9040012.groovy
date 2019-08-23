package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9040012 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendOk("The plaque translates as follows: \r\n\"The knights of Sharenian are proud warriors. Their Longinus Spears are both formidable weapons and the key to the castle's defense: By removing them from their platforms at the highest points of this hall, they block off the entrance from invaders.\"\r\n\r\nSomething seems to be etched in English on the side, barely readable: \r\n\"evil stole spears, chained up behind obstacles. return to top of towers. large spear, grab from higher up.\"\r\n...Obviously whoever figured it out didn't have much time to live. Poor guy.")
      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC9040012 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9040012(cm: cm))
   }
   return (NPC9040012) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }