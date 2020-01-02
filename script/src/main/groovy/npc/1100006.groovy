package npc


import scripting.npc.NPCConversationManager

class NPC1100006 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendOk("Ah, such lovely winds. This should be a perfect voyage as long as no stupid customer falls off for attempting some weird skill. Of course, I'm talking about you. Please refrain from using your skills.")
      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC1100006 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1100006(cm: cm))
   }
   return (NPC1100006) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }