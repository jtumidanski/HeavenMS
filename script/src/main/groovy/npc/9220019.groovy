package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9220019 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0 && type > 0) {
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            int mapid = cm.getMapId()
            if (mapid == 674030100) {
               cm.sendNext("Hi, I'm #p9220019#.")
               cm.dispose()
               return
            } else if (mapid == 674030300) {
               cm.sendNext("Hi there, #h0#. This is the MV's treasure room. Use the time you have here to do whatever you want, there are a lot of things to uncover here, actually. Or else you can use the portal here to #rgo back#k to the entrance.")
               cm.dispose()
               return
            }

            cm.sendYesNo("Are you sure you want to return? By returning now you are leaving your partners behind, do you really want to do it?")
         } else if (status == 1) {
            cm.warp(674030100)
            cm.dispose()
         }
      }
   }
}

NPC9220019 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9220019(cm: cm))
   }
   return (NPC9220019) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }