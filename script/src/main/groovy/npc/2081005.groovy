package npc
import tools.I18nMessage

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
         cm.sendOk(I18nMessage.from("2081005_HORNTAIL_CAVE"))
         cm.dispose()
         return
      }

      cm.sendSimple(I18nMessage.from("2081005_WELCOME"))
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
               cm.sendOk(I18nMessage.from("2081005_NEED_FREE_SPACE"))
            } else {
               cm.gainMeso(-price)
               cm.gainItem(2000005, (short) 10)
               cm.sendOk(I18nMessage.from("2081005_THANK_YOU"))
            }
         } else {
            cm.sendOk(I18nMessage.from("2081005_NOT_ENOUGH_MESOS"))
         }
         cm.dispose()
      } else if (selection == 2) {
         if (cm.getLevel() > 99) {
            cm.warp(240050000, 0)
         } else {
            cm.sendOk(I18nMessage.from("2081005_LEVEL_REQUIREMENT"))
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