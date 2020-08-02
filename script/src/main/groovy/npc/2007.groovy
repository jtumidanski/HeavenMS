package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*

*/

class NPC2007 {
    NPCConversationManager cm
   int status = -1
   int sel = -1

    def start() {
        action((byte) 1, (byte) 0, 0)
    }

    def action(Byte mode, Byte type, Integer selection) {
        if (mode == -1) {
            cm.sendNext(I18nMessage.from("2007_ENJOY"))
            cm.dispose()
        } else {
            if (status == 0 && mode == 0) {
                cm.sendNext(I18nMessage.from("2007_ENJOY"))
                cm.dispose()
            }
            if (mode == 1)
                status++
            else
                status--
            if (status == 0)
                cm.sendYesNo(I18nMessage.from("2007_WOULD_YOU_LIKE_TO_SKIP_THE_TUTORIALS"))
            else if (status == 1) {
                cm.warp(104000000, 0)
                cm.dispose()
            }
        }
    }
}

NPC2007 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2007(cm: cm))
   }
   return (NPC2007) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }