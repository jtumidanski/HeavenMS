package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1072008 {
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
            if (cm.getMapId() == 108000502) {
               if (!(cm.haveItem(4031856, 15))) {
                  cm.sendSimple("You haven't brought me all the crystals yet. I'm looking forward for your progress, mate! \r\n#b#L1#I would like to leave#l")
               } else {
                  status++
                  cm.sendNext("Wow, you have brought me 15 #b#t4031856##k! Congratulations. Let me warp you out now.")
               }
            } else if (cm.getMapId() == 108000501) {
               if (!(cm.haveItem(4031857, 15))) {
                  cm.sendSimple("You haven't brought me all the crystals yet. I'm looking forward for your progress, mate! \r\n#b#L1#I would like to leave#l")
               } else {
                  status++
                  cm.sendNext("Wow, you have brought me 15 #b#t4031857##k! Congratulations. Let me warp you out now.")
               }
            } else {
               cm.sendNext("Error. Please report this.")
               cm.dispose()
            }
         } else if (status == 1) {
            cm.removeAll(4031856)
            cm.removeAll(4031857)
            cm.warp(120000101, 0)
            cm.dispose()
         } else if (status == 2) {
            cm.warp(120000101, 0)
            cm.dispose()
         }
      }
   }
}

NPC1072008 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1072008(cm: cm))
   }
   return (NPC1072008) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }