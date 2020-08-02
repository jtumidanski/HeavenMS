package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

class NPC1032111 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if(mode == -1 || (mode == 0 && status == 0)){
         cm.dispose()
         return
      }
      else if(mode == 0)
         status--
      else
         status++


      if(status == 0){
         if(cm.isQuestStarted(20716)){
            if(!cm.hasItem(4032142)){
               if(cm.canHold(4032142)){
                  cm.gainItem(4032142, (short) 1)
                  cm.sendOk(I18nMessage.from("1032111_BOTTLED_UP_SOME_SAP"))
               }
               else
                  cm.sendOk(I18nMessage.from("1032111_MAKE_SURE_YOU_HAVE_A_FREE_SPOT"))
            }
            else
               cm.sendOk(I18nMessage.from("1032111_NEVER_ENDING_FLOW"))
         }
         else
            cm.sendOk(I18nMessage.from("1032111_NEVER_ENDING_FLOW"))
      }
      else if(status == 1){
         cm.dispose()
      }
   }
}

NPC1032111 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1032111(cm: cm))
   }
   return (NPC1032111) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }