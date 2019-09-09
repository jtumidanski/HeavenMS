package npc


import scripting.npc.NPCConversationManager

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
            cm.sendNext("Enjoy your trip.")
            cm.dispose()
        } else {
            if (status == 0 && mode == 0) {
                cm.sendNext("Enjoy your trip.")
                cm.dispose()
            }
            if (mode == 1)
                status++
            else
                status--
            if (status == 0)
                cm.sendYesNo("Would you like to skip the tutorials and head straight to Lith Harbor?")
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