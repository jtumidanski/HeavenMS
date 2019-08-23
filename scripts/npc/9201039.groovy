package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201039 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int[] mhair_q = [30270, 30240, 30020, 30000, 30132, 30192, 30032, 30112, 30162]
   int[] fhair_q = [31150, 31250, 31310, 31050, 31050, 31030, 31070, 31091, 31001]
   int[] hairnew = []

   def start() {
      if (cm.isQuestCompleted(8860) && !cm.haveItem(4031528)) {
         cm.sendNext("I've already done your hair once as a trade-for-services, sport. You'll have to snag an EXP Hair coupon from the Cash Shop if you want to change it again!")
         cm.dispose()
      } else {
         cm.sendYesNo("Ready for an awesome hairdo? I think you are! Just say the word, and we'll get started!")
      }
   }

   def pushIfItemExists(int[] array, int itemid) {
      if ((itemid = cm.getCosmeticItem(itemid)) != -1 && !cm.isCosmeticEquipped(itemid)) {
         array << itemid
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 1) {  // disposing issue with stylishs found thanks to Vcoc
         if (type == 7) {
            cm.sendNext("Ok, I'll give you a minute.")
         }

         cm.dispose()
      }
      status++
      if (status == 1) {
         hairnew = []
         if (cm.getPlayer().getGender() == 0) {
            for (int i = 0; i < mhair_q.length; i++) {
               pushIfItemExists(hairnew, mhair_q[i])
            }
         } else {
            for (int j = 0; j < fhair_q.length; j++) {
               pushIfItemExists(hairnew, fhair_q[j])
            }
         }
         cm.sendNext("Here we go!")
      } else {
         if (cm.haveItem(4031528)) {
            cm.gainItem(4031528, (short) -1)
            cm.setHair(hairnew[Math.floor(Math.random() * hairnew.length).intValue()])
            cm.sendNextPrev("Not bad, if I do say so myself! I knew those books I studied would come in handy...")
            cm.dispose()
         } else {
            cm.sendNext("Hmmm...are you sure you have our designated free coupon? Sorry but no haircut without it.")
            cm.dispose()
         }
      }
   }
}

NPC9201039 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201039(cm: cm))
   }
   return (NPC9201039) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }