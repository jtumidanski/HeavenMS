package npc


import scripting.npc.NPCConversationManager
import server.gachapon.MapleGachapon

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NpcGachaponInfo {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   String[] gachaponMessages
   MapleGachapon.Gachapon[] gachaponMachines

   def start() {
      gachaponMessages = MapleGachapon.Gachapon.getLootInfo()
      gachaponMachines = MapleGachapon.Gachapon.values()
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
            String sendStr = "Hi, #r#p" + cm.getNpc() + "##k here! I'm announcing all obtainable loots from the Gachapon machines. Which Gachapon machine would you like to look?\r\n\r\n#b" + gachaponMessages[0] + "#k"
            cm.sendSimple(sendStr)
         } else if (status == 1) {
            String sendStr = "Loots from #b" + gachaponMachines[selection].name() + "#k:\r\n\r\n" + gachaponMessages[selection + 1]
            cm.sendPrev(sendStr)
         } else if (status == 2) {
            cm.dispose()
         }
      }
   }
}

NpcGachaponInfo getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NpcGachaponInfo(cm: cm))
   }
   return (NpcGachaponInfo) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }