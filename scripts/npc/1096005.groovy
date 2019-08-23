package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1096005 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == 0 && type == 0) {
         status--
      } else if (mode == -1) {
         cm.dispose()
         return
      } else {
         status++
      }
      if (status == 0) {
         cm.sendDirectionInfo(4, 1096005)//else you will crash sending sendNext
         cm.sendNext("All right! Let's go!")
      } else if (status == 1) {
         cm.removeNPC(579711)
         cm.removeNPC2(579711)
         cm.updateInfo("fire", "0")
         cm.playSound("cannonshooter/fire")
         cm.sendDirectionInfo("Effect/Direction4.img/effect/cannonshooter/flying/0", 7000, 0, 0, -1, -1)
         cm.sendDirectionInfo("Effect/Direction4.img/effect/cannonshooter/flying1/0", 7000, 0, 0, -1, -1)
         cm.sendDirectionInfo(1, 800)
      } else if (status == 2) {
         cm.warp(912060300, 0)
      } else if (status == 3) {
         cm.sendDirectionInfo("Effect/Direction4.img/effect/cannonshooter/balloon/1", 9000, 0, 0, 0, -1)
         cm.sendDirectionInfo(1, 1500)
      } else if (status == 4) {
         cm.sendDirectionInfo("Effect/Direction4.img/effect/cannonshooter/balloon/2", 9000, 0, 0, 0, -1)
         cm.showIntro("Effect/Direction4.img/cannonshooter/face04")
         cm.showIntro("Effect/Direction4.img/cannonshooter/out01")
         cm.dispose()
      }
   }
}

NPC1096005 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1096005(cm: cm))
   }
   return (NPC1096005) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }