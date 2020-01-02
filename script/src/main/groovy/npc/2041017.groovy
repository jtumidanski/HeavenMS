package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2041017 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendOk("Hey adventurer! Keep it a secret, ok? We are currently manufacturing the so-called #b#t2049100##k, under Just-in-time marketing strategy. You needed? We're here. So, we act in two fronts: talk to me if you want a good bunch of these. It will be a #bQuest#k-esque procedure, however I will need plenty of #bhard-to-get gadgets#k from you. I will require a #r3 days#k break after the completion to start working for you again.\r\nTalk to my partner here, and he will JIT #bsynthetize#k these scrolls for you, requiring a bunch of #blow-cost items#k, #ranytime anywhere#k.")
      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC2041017 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2041017(cm: cm))
   }
   return (NPC2041017) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }