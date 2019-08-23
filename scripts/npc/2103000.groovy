package npc

import client.MapleBuffStat
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2103000 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   static def isTigunMorphed(ch) {
      return ch.getBuffSource(MapleBuffStat.MORPH) == 2210005
   }

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 0) {
         cm.dispose()
      } else {
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 0 && mode == 1) {
            if (cm.isQuestStarted(3900) && cm.getPlayer().getQuestInfo(3900) != 5) {
               cm.sendOk("#b(You drink the water from the oasis and feel refreshed.)", (byte) 2)
               cm.getPlayer().updateQuestInfo(3900, "5")
            } else if (cm.isQuestCompleted(3938)) {
               if (cm.canHold(2210005)) {
                  if (!cm.haveItem(2210005) && !isTigunMorphed(cm.getPlayer())) {
                     cm.gainItem(2210005, (short) 1)
                     cm.sendOk("You found a lock of hair (probably Tigun's) floating by the water and catched it. Remembering how #bJano#k made it last time, you crafted a new #t2210005#", (byte) 2)
                  }
               } else {
                  cm.sendOk("You don't have a USE slot available.", (byte) 2)
               }
            } else if (cm.isQuestStarted(3934) || (cm.isQuestCompleted(3934) && !cm.isQuestCompleted(3935))) {
               if (cm.canHold(2210005)) {
                  if (!cm.haveItem(2210005) && !isTigunMorphed(cm.getPlayer())) {
                     cm.gainItem(2210005, (short) 1)
                     cm.sendOk("You managed to find a strange flask floating on the river. It seems like a transformation bottle mimicking one of the guards of the castle, maybe with it you will be able to roam inside freely.", (byte) 2)
                  }
               } else {
                  cm.sendOk("You found a strange flask floating on the river. But you decided to ignore it since you don't have a USE slot available.", (byte) 2)
               }
            }

            cm.dispose()
         }
      }
   }
}

NPC2103000 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2103000(cm: cm))
   }
   return (NPC2103000) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }