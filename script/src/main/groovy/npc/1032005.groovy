package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1032005 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int cost = 10000

   def start() {
      cm.sendNext("Hi there! This cab is for VIP customers only. Instead of just taking you to different towns like the regular cabs, we offer a much better service worthy of VIP class. It's a bit pricey, but... for only 10,000 mesos, we'll take you safely to the \r\n#bAnt Tunnel#k.")
   }

   def action(Byte mode, Byte type, Integer selection) {
      status++
      if (mode == -1) {
         cm.dispose()
         return
      } else if (mode == 0) {
         cm.sendOk("This town also has a lot to offer. Find us if and when you feel the need to go to the Ant Tunnel Park.")
         cm.dispose()
         return
      }
      if (status == 1) {
         cm.sendYesNo(cm.getJobId() == 0 ? "We have a special 90% discount for beginners. The Ant Tunnel is located deep inside in the dungeon that's placed at the center of the Victoria Island, where the 24 Hr Mobile Store is. Would you like to go there for #b1,000 mesos#k?" : "The regular fee applies for all non-beginners. The Ant Tunnel is located deep inside in the dungeon that's placed at the center of the Victoria Island, where 24 Hr Mobile Store is. Would you like to go there for #b10,000 mesos#k?")
         cost /= ((cm.getJobId() == 0) ? 10 : 1)
      } else if (status == 2) {
         if (cm.getMeso() < cost) {
            cm.sendNext("It looks like you don't have enough mesos. Sorry but you won't be able to use this without it.")
         } else {
            cm.gainMeso(-cost)
            cm.warp(105070001)
         }
         cm.dispose()
      }
   }
}

NPC1032005 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1032005(cm: cm))
   }
   return (NPC1032005) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }