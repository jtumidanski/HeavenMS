package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9270018 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int k2s
   int airport
   int s2k

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
         return
      }
      if (mode == 1) {
         status++
      }
      if (mode == 0) {
         if (cm.getMapId() == 540010101) {
            cm.sendOk("Please hold on for a sec, and we're reaching Singapore! Thanks for your patience.")
            cm.dispose()
            return
         } else {
            cm.sendOk("Please hold on for a sec, and we're reaching Kerning City! Thanks for your patience.")
            cm.dispose()
            return
         }
      }
      if (status == 0) {
         if (cm.getMapId() == 540010001) {
            cm.sendYesNo("The plane is taking off soon, are you sure you want to leave now? The ticket is not refundable.")
            airport = 1
         } else if (cm.getMapId() == 540010002) {
            cm.sendOk("We're reaching Kerning City in a minute, please sit down and wait.")
            cm.dispose()
            s2k = 1
         } else if (cm.getMapId() == 540010101) {
            cm.sendOk("We're reaching Singapore in a minute, please sit down and wait.")
            cm.dispose()
            k2s = 1
         }
      } else if (status == 1) {
         if (k2s == 1) {
            cm.warp(103000000)
            cm.sendOk("Hope to see you again soon!")
            cm.dispose()
         } else if (airport == 1) {
            cm.warp(540010000)
            cm.sendOk("Hope to see you again soon!")
            cm.dispose()
         } else if (s2k == 1) {
            cm.warp(540010000)
            cm.sendOk("Hope to see you again soon!")
            cm.dispose()
         }
      }
   }
}

NPC9270018 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9270018(cm: cm))
   }
   return (NPC9270018) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }