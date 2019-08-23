package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1100007 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   String[] menu = ["Ereve"]

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
            cm.sendNext("If you're not interested, then oh well...")
            cm.dispose()
            return
         }
         status++
         if (status == 0) {
            String display = ""
            for (def i = 0; i < menu.length; i++) {
               display += "\r\n#L" + i + "##b Ereve (1000 mesos)#k"
            }
            cm.sendSimple("Eh... So... Um... Are you trying to leave Victoria to go to a different region? You can take this boat to #bEreve#k. There, you will see bright sunlight shinning on the leaves and feel a gentle breeze on your skin. It's where Shinsoo and Empress Cygnus are. Would you like to go to Ereve? It will take about #b2 Minutes#k, and it will cost you #b1000#k mesos.\r\n" + display)

         } else if (status == 1) {
            if (cm.getMeso() < 1000) {
               cm.sendNext("Hmm... Are you sure you have #b1000#k Mesos? Check your Inventory and make sure you have enough. You must pay the fee or I can't let you get on...")
               cm.dispose()
            } else {
               cm.gainMeso(-1000)
               cm.warp(200090030)
               cm.dispose()
            }
         }
      }
   }
}

NPC1100007 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1100007(cm: cm))
   }
   return (NPC1100007) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }