package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2023000 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int[] toMap = [211040200, 220050300, 220000000, 240030000]
   int[] inMap = [211000000, 220000000, 221000000, 240000000]
   int[] cost = [10000, 25000, 25000, 65000]
   int location

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0 && type > 0) {
            cm.sendNext("Hmm, please think this over. It's not cheap, but you will NOT be disappointed with our premier service!")
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            for (int i = 0; i < toMap.length; i++) {
               if (inMap[i] == cm.getPlayer().getMap().getId()) {
                  location = i
                  break
               }
            }
            cm.sendNext("Hello there! This taxi will take you to dangerous places in Ossyria faster than an arrow! We go from #m" + inMap[location] + "# to #b#m" + toMap[location] + "##k on this Ossyria Continent! It'll cost you #b" + cost[location] + " meso#k. I know it's a bit expensive, but it's well worth passing all the dangerous areas!")
         } else if (status == 1) {
            cm.sendYesNo("Would you like to pay #b" + cost[location] + " mesos#k to travel to the #b#m" + toMap[location] + "##k?")
         } else if (status == 2) {
            if (cm.getMeso() < cost[location]) {
               cm.sendNext("You don't seem to have enough mesos. I am terribly sorry, but I cannot help you unless you pay up. Bring in the mesos by hunting more and come back when you have enough.")
            } else {
               cm.warp(toMap[location], location != 1 ? 0 : 1)
               cm.gainMeso(-cost[location])
            }
            cm.dispose()
         }
      }
   }
}

NPC2023000 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2023000(cm: cm))
   }
   return (NPC2023000) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }