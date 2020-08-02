package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage
import tools.SimpleMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1032000 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int[] maps = [104000000, 102000000, 100000000, 103000000, 120000000]
   int[] cost = [1000, 1000, 1000, 1000, 800]
   int selectedMap = -1
   int mesos
   boolean hasCoupon = false

   def start() {
      cm.sendNext(I18nMessage.from("1032000_HELLO"))
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (status == 1 && mode == 0) {
            cm.dispose()
            return
         } else if (status >= 2 && mode == 0) {
            cm.sendNext(I18nMessage.from("1032000_A_LOT_TO_DO"))
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 1) {
            String selStr = ""
            if (cm.getJobId() == 0) {
               selStr += I18nMessage.from("1032000_BEGINNER_SPECIAL").to(cm.getClient()).evaluate()
            }
            selStr += I18nMessage.from("1032000_CHOOSE_DESTINATION").to(cm.getClient()).evaluate()
            for (def i = 0; i < maps.length; i++) {
               selStr += "\r\n#L" + i + "##m" + maps[i] + "# (" + (cm.getJobId() == 0 ? cost[i] / 10 : cost[i]) + " mesos)#l"
            }
            cm.sendSimple(SimpleMessage.from(selStr))
         } else if (status == 2) {
            if (maps[selection] == 100000000 && cm.getMapId() == 101000000 && cm.haveItem(4032288)) {
               cm.sendYesNo(I18nMessage.from("1032000_FREE_OF_CHARGE"))
               hasCoupon = true
            } else {
               cm.sendYesNo(I18nMessage.from("1032000_NOTHING_ELSE_TO_DO").with(maps[selection], (cm.getJobId() == 0 ? cost[selection] / 10 : cost[selection])))
            }

            selectedMap = selection
         } else if (status == 3) {
            if (!hasCoupon) {
               if (cm.getJobId() == 0) {
                  mesos = (cost[selectedMap] / 10).intValue()
               } else {
                  mesos = cost[selectedMap]
               }

               if (cm.getMeso() < mesos) {
                  cm.sendNext(I18nMessage.from("1032000_NOT_ENOUGH_MESOS"))
                  cm.dispose()
                  return
               }

               cm.gainMeso(-mesos)
            } else {
               cm.gainItem(4032288, (short) -1)
            }

            cm.warp(maps[selectedMap], 0)
            cm.dispose()
         }
      }
   }
}

NPC1032000 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1032000(cm: cm))
   }
   return (NPC1032000) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }