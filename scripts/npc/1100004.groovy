package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1100004 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   String[] menu = ["Orbis"]

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0 && status == 0) {
            cm.dispose()
            return
         } else if (mode == 0) {
            cm.sendNext("OK. If you ever change your mind, please let me know.")
            cm.dispose()
            return
         }
         status++
         if (status == 0) {
            String display = ""
            for (def i = 0; i < menu.length; i++) {
               display += "\r\n#L" + i + "##b Orbis (1000 mesos)#k"
            }
            cm.sendSimple("Hmm... The winds are favorable. Are you thinking of leaving ereve and going somwhere else? This ferry sails to Orbis on the Ossyria Continent, Have you taking care of everything you needed to in Ereve? If you happen to be headed toward #bOrbis#k i can take you there. What do you day? Are you going to go to Orbis?\r\n" + display)

         } else if (status == 1) {
            if (cm.getMeso() < 1000) {
               cm.sendNext("Hmm... Are you sure you have #b1000#k Mesos? Check your Inventory and make sure you have enough. You must pay the fee or I can't let you get on...")
               cm.dispose()
            } else {
               cm.gainMeso(-1000)
               cm.warp(200090021)
               cm.dispose()
            }
         }
      }
   }
}

NPC1100004 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1100004(cm: cm))
   }
   return (NPC1100004) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }