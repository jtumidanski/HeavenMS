package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1100003 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   String[] menu = ["Victoria Island"]


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
            cm.sendNext(I18nMessage.from("1100003_OH_WELL"))
            cm.dispose()
            return
         }
         status++
         if (status == 0) {
            String display = ""
            for (def i = 0; i < menu.length; i++) {
               display += "\r\n#L" + i + "##b Victoria Island (1000 mesos)#k"
            }
            cm.sendSimple("Eh, Hello...again. Do you want to leave Ereve and go somewhere else? If so, you've come to the right place. I operate a ferry that goes from #bEreve#k to #bVictoria Island#k, I can take you to #bVictoria Island#k if you want... You'll have to pay a fee of #b1000#k Mesos.\r\n" + display)
         } else if (status == 1) {
            if (cm.getMeso() < 1000) {
               cm.sendNext(I18nMessage.from("1100003_NOT_ENOUGH_MESO"))
               cm.dispose()
            } else {
               cm.gainMeso(-1000)
               cm.warp(200090031)
               cm.dispose()
            }
         }
      }
   }
}

NPC1100003 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1100003(cm: cm))
   }
   return (NPC1100003) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }