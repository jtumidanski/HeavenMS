package npc


import scripting.npc.NPCConversationManager

class NPC1012115 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      status = cm.getQuestStatus(20706)

      if (status == 0) {
         cm.sendNext("It looks like there's nothing suspicious in the area.")
      } else if (status == 1) {
         cm.forceCompleteQuest(20706)
         cm.sendNext("You have spotted the shadow! Better report to #p1103001#.")
      } else if (status == 2) {
         cm.sendNext("The shadow has already been spotted. Better report to #p1103001#.")
      }
      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC1012115 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1012115(cm: cm))
   }
   return (NPC1012115) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }