package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201043 {
   NPCConversationManager cm
   int status = -1
   int MySelection = -1

   def start() {
      cm.sendSimple("My name is Amos the Strong. What would you like to do?\r\n#b#L0#Enter the Amorian Challenge!!#l\r\n#L1#Trade 10 Keys for a Ticket!#l\r\n#k")
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (status >= 0 && mode == 0) {
            cm.sendOk("Ok come back when you're ready.")
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 1 && selection == 0) {
            if (cm.haveItem(4031592, 1)) {
               cm.sendYesNo("So you would like to enter the #bEntrance#k?")
               MySelection = selection
            } else {
               cm.sendOk("You must have an Entrance Ticket to enter.")
               cm.dispose()
            }
         } else if (status == 1 && selection == 1) {
            if (cm.haveItem(4031592)) {
               cm.sendOk("You already have an Entrance Ticket!")
               cm.dispose()
            } else if (cm.haveItem(4031593, 10)) {
               cm.sendYesNo("So you would like a Ticket?")
               MySelection = selection
            } else {
               cm.sendOk("Please get me 10 Keys first!")
               cm.dispose()
            }
         } else if (status == 2 && MySelection == 0) {
            cm.warp(670010100, 0)
            cm.gainItem(4031592, (short) -1)
            cm.dispose()
         } else if (status == 2 && MySelection == 1) {
            cm.gainItem(4031593, (short) -10)
            cm.gainItem(4031592, (short) 1)
            cm.dispose()
         }
      }
   }
}

NPC9201043 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201043(cm: cm))
   }
   return (NPC9201043) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }