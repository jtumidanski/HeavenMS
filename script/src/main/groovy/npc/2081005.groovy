package npc

import client.MapleBuffStat
import client.MapleCharacter
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2081005 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int price = 100000

   def start() {
      if (!(isTransformed(cm.getPlayer()) || cm.haveItem(4001086))) {
         cm.sendOk("This is the cave of the mighty Horntail, supreme ruler of the Leafre Canyons. Only those #bdeemed worthy#k to meet him can pass here, #boutsiders#k are not welcome. Get lost!")
         cm.dispose()
         return
      }

      cm.sendSimple("Welcome to Cave of Life - Entrance ! Would you like to go inside and fight #rHorntail#k ? If you want to fight him, you may might need some #b#v2000005##k, so you can recover some HP if you have been hit by #rHorntail#k.\r\n#L1#I would like to buy 10 for 100,000 Mesos!#l\r\n#L2#No thanks, let me in now!#l")
   }

   static def isTransformed(MapleCharacter ch) {
      return ch.getBuffSource(MapleBuffStat.MORPH) == 2210003
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 1) {
         cm.dispose()
      } else if (selection == 1) {
         if (cm.getMeso() >= price) {
            if (!cm.canHold(2000005)) {
               cm.sendOk("Sorry, you don't have a slot on your inventory for the item!")
            } else {
               cm.gainMeso(-price)
               cm.gainItem(2000005, (short) 10)
               cm.sendOk("Thank you for buying the potion. Use it as well!")
            }
         } else {
            cm.sendOk("Sorry, you don't have enough mesos to buy them!")
         }
         cm.dispose()
      } else if (selection == 2) {
         if (cm.getLevel() > 99) {
            cm.warp(240050000, 0)
         } else {
            cm.sendOk("I'm sorry. You need to be at least level 100 or above to enter.")
         }
         cm.dispose()
      }
   }
}

NPC2081005 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2081005(cm: cm))
   }
   return (NPC2081005) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }