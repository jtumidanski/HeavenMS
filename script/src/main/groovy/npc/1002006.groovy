package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*

*/

class NPC1002006 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendNext(I18nMessage.from("1002006_HELLO"))
      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {
   }
}

NPC1002006 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1002006(cm: cm))
   }
   return (NPC1002006) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }