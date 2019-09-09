package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		Tommy (HPQ)
	Map(s): 		
	Description: 	
*/


class NPC1012113 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 1) {
         cm.dispose()
      } else {
         status++
         if (cm.getPlayer().getMap().getId() == 910010100) { //Clear map
            if (status == 0) {
               cm.sendNext("Hello, there! I'm Tommy. There's a Pig Town nearby where we're standing. The pigs there are rowdy and uncontrollable to the point where they have stolen numerous weapons from travelers. They were kicked out from their towns, and are currently hiding out at the Pig Town.")
            } else if (status == 1) {
               if (cm.isEventLeader()) {
                  cm.sendYesNo("What do you think about making your way there with your party members and teach those rowdy pigs a lesson?")
               } else {
                  cm.sendOk("Interessed? Tell your party leader to talk to me to head there!")
                  cm.dispose()
               }
            } else if (status == 2) {
               cm.getEventInstance().startEventTimer(5 * 60000)
               cm.getEventInstance().warpEventTeam(910010200)
               cm.dispose()
            }
         } else if (cm.getPlayer().getMap().getId() == 910010200) { //Bonus map
            if (status == 0) {
               cm.sendYesNo("Would you like to exit the bonus now?")
            } else {
               cm.warp(910010400)
               cm.dispose()
            }
         } else if (cm.getPlayer().getMap().getId() == 910010300) { //Exit map
            if (status == 0) {
               cm.sendOk("You will now be warped out, thank you for helping us!")
            } else {
               cm.warp(100000200)
               cm.dispose()
            }
         }
      }
   }
}

NPC1012113 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1012113(cm: cm))
   }
   return (NPC1012113) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }