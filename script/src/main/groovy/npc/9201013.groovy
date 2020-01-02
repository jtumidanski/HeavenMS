package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201013 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendOk("Welcome to the Cathedral.\r\n\r\n#bCouples#k wanting to marry on the Cathedral should first #barrange a reservation#k with #r#p9201005##k. When the arranged time comes #bboth#k must show up here, on the same channel from the reservation, and start the ceremony (there is a 10-minutes fault policy to this) by talking to #r#p9201002##k. Once arranged, both can #bdistribute tickets#k to friends or acquaintances to become the guests for the marriage.\r\n\r\nThe ceremony will start accepting #bguests#k after the groom and the bride has entered the building. Show the #b#t4000313##k to #r#p9201005##k to access the inner rooms. No one without a ticket is allowed to enter the stage!")
      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC9201013 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201013(cm: cm))
   }
   return (NPC9201013) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }