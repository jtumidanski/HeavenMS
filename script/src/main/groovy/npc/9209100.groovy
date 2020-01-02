package npc

import scripting.npc.NPCConversationManager

import java.awt.*

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9209100 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   static def playerNearby(Point characterPosition, Point portalPoint) {
      return Math.sqrt(Math.pow((portalPoint.getX() - characterPosition.getX()), 2) + Math.pow((portalPoint.getY() - characterPosition.getY()), 2)) < 77
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
            if (playerNearby(cm.getPlayer().position(), cm.getMap().getPortal("chimney01").getPosition())) {
               cm.sendOk("Hey, hey~~ Please don't go sneaking into someone else's house without permission, you don't want to get a naughty remark on Santa's list this year, do you?")
            } else {
               cm.sendOk("Hohoho~~ have you a Great Year full of health, realization and happiness!")
            }
         } else {
            cm.dispose()
         }
      }
   }
}

NPC9209100 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9209100(cm: cm))
   }
   return (NPC9209100) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }