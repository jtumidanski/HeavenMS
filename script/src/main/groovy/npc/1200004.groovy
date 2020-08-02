package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		Puro
	Map(s): 		Whale Between Lith harbor and Rien
	Description: 	
*/


class NPC1200004 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   String[] menu = ["Rien"]

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
            cm.sendNext(I18nMessage.from("1200004_LET_ME_KNOW"))
            cm.dispose()
            return
         }
         status++
         if (status == 0) {
            String display = ""
            for (def i = 0; i < menu.length; i++) {
               display += "\r\n#L" + i + "##b Rien (800 mesos)#k"
            }
            cm.sendSimple("Are you thinking about leaving Victoria Island and heading to our town? If you board this ship, I can take you from #bLith Harbor#k to #bRien#k and back. But you must pay a #bfee of 800#k Mesos. Would you like to go to Rien? It'll take about a minute to get there.\r\n" + display)

         } else if (status == 1) {
            if (cm.getMeso() < 800) {
               cm.sendNext(I18nMessage.from("1200004_NOT_ENOUGH_MESOS"))
               cm.dispose()
            } else {
               cm.gainMeso(-800)
               cm.warp(200090060)
               cm.dispose()
            }
         }
      }
   }
}

NPC1200004 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1200004(cm: cm))
   }
   return (NPC1200004) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }