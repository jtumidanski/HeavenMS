package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1092014 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int[] maps = [104000000, 102000000, 100000000, 101000000, 103000000]
   int[] cost = [1000, 1000, 1000, 800, 1000]
   int selectedMap = -1

   def start() {
      cm.sendNext(I18nMessage.from("1092014_NAUTILUS_TAXI_HELLO"))
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (status == 1 && mode == 0) {
            cm.dispose()
            return
         } else if (status >= 2 && mode == 0) {
            cm.sendNext(I18nMessage.from("1092014_A_LOT_TO_SEE"))
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
               selStr += "We have a special 90% discount for beginners."
            }
            selStr += "Choose your destination, for fees will change from place to place.#b"
            for (def i = 0; i < maps.length; i++) {
               selStr += "\r\n#L" + i + "##m" + maps[i] + "# (" + (cm.getJobId() == 0 ? cost[i] / 10 : cost[i]) + " mesos)#l"
            }
            cm.sendSimple(selStr)
         } else if (status == 2) {
            cm.sendYesNo(I18nMessage.from("1092014_NOTHING_ELSE_TO_DO").with(maps[selection], (cm.getJobId() == 0 ? cost[selection] / 10 : cost[selection])))
            selectedMap = selection
         } else if (status == 3) {
            int mesos
            if (cm.getJobId() == 0) {
               mesos = (cost[selectedMap] / 10).intValue()
            } else {
               mesos = cost[selectedMap]
            }

            if (cm.getMeso() < mesos) {
               cm.sendNext(I18nMessage.from("1092014_NOT_ENOUGH_MESOS"))
               cm.dispose()
               return
            }

            cm.gainMeso(-mesos)
            cm.warp(maps[selectedMap], 0)
            cm.dispose()
         }
      }
   }
}

NPC1092014 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1092014(cm: cm))
   }
   return (NPC1092014) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }