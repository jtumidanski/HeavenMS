package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201057 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.c.getPlayer().getMapId() == 103000100 || cm.c.getPlayer().getMapId() == 600010001) {
         cm.sendYesNo(I18nMessage.from("9201057_THE_RIDE").with((cm.c.getPlayer().getMapId() == 103000100 ? "New Leaf City of Masteria" : "Kerning City of Victoria Island")))
      } else if (cm.c.getPlayer().getMapId() == 600010002 || cm.c.getPlayer().getMapId() == 600010004) {
         cm.sendYesNo(I18nMessage.from("9201057_NO_REFUND"))
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode != 1) {
         cm.dispose()
         return
      }
      if (cm.c.getPlayer().getMapId() == 103000100 || cm.c.getPlayer().getMapId() == 600010001) {
         int item = 4031711 + (cm.c.getPlayer().getMapId() / 300000000).intValue()

         if (!cm.canHold(item)) {
            cm.sendNext(I18nMessage.from("9201057_NO_ETC_SPACE"))
         } else if (cm.getMeso() >= 5000) {
            cm.gainMeso(-5000)
            cm.gainItem(item, (short) 1)
            cm.sendNext(I18nMessage.from("9201057_THERE_YOU_GO"))
         } else {
            cm.sendNext(I18nMessage.from("9201057_NOT_ENOUGH_MESO"))
         }
      } else {
         cm.warp(cm.c.getPlayer().getMapId() == 600010002 ? 600010001 : 103000100)
      }
      cm.dispose()
   }
}

NPC9201057 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201057(cm: cm))
   }
   return (NPC9201057) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }