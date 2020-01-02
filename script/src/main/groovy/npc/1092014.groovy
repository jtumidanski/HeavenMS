package npc


import scripting.npc.NPCConversationManager

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
      cm.sendNext("Hello, I drive the Nautilus' Mid-Sized Taxi. If you want to go from town to town safely and fast, then ride our cab. We'll gladly take you to your destination with an affordable price.")
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (status == 1 && mode == 0) {
            cm.dispose()
            return
         } else if (status >= 2 && mode == 0) {
            cm.sendNext("There's a lot to see in this town, too. Come back and find us when you need to go to a different town.")
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
            cm.sendYesNo("You don't have anything else to do here, huh? Do you really want to go to #b#m" + maps[selection] + "##k? It'll cost you #b" + (cm.getJobId() == 0 ? cost[selection] / 10 : cost[selection]) + " mesos#k.")
            selectedMap = selection
         } else if (status == 3) {
            int mesos
            if (cm.getJobId() == 0) {
               mesos = (cost[selectedMap] / 10).intValue()
            } else {
               mesos = cost[selectedMap]
            }

            if (cm.getMeso() < mesos) {
               cm.sendNext("You don't have enough mesos. Sorry to say this, but without them, you won't be able to ride the cab.")
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