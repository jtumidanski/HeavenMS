package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2012006 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   String[] destinations = ["Ellinia", "Ludibrium", "Leafre", "Mu Lung", "Ariant", "Ereve"]
   String[] boatType = ["the ship", "the train", "the bird", "Hak", "Genie", "the ship"]

   def start() {
      String message = "Orbis Station has lots of platforms available to choose from. You need to choose the one that'll take you to the destination of your choice. Which platform will you take?\r\n"
      for (def i = 0; i < destinations.length; i++) {
         message += "\r\n#L" + i + "##bThe platform to " + boatType[i] + " that heads to " + destinations[i] + ".#l"
      }
      cm.sendSimple(message)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 1) {
         cm.dispose()
         return
      }
      status++
      if (status == 0) {
         sel = selection
         cm.sendNext(I18nMessage.from("2012006_I_WILL_SEND_YOU").with(200000110 + (sel * 10)))
      } else if (status == 1) {
         cm.warp(200000110 + (sel * 10), "west00")
         cm.dispose()
      }
   }
}

NPC2012006 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2012006(cm: cm))
   }
   return (NPC2012006) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }