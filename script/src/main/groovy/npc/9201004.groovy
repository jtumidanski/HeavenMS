package npc

import client.MapleCharacter
import client.Ring
import net.server.channel.handlers.RingActionHandler
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201004 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int[] rings = [1112806, 1112803, 1112807, 1112809]
   int divorceFee = 500000
   Ring ringObj

   def getWeddingRingItemId(MapleCharacter player) {
      for (int i = 0; i < rings.length; i++) {
         if (player.haveItemWithId(rings[i], false)) {
            return rings[i]
         }
      }

      return null
   }

   def hasEquippedWeddingRing(MapleCharacter player) {
      for (int i = 0; i < rings.length; i++) {
         if (player.haveItemEquipped(rings[i])) {
            return true
         }
      }

      return false
   }

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
            String[] questionStr = ["How can I engage someone?", "How can I marry?", "How can I divorce?"]

            if (!(!cm.getPlayer().isMarried() && getWeddingRingItemId(cm.getPlayer()))) {
               questionStr << "I want a divorce..."
            } else {
               questionStr << "I wanna remove my old wedding ring..."
            }

            cm.sendSimple("Hello, welcome to #bAmoria#k, a beautiful land where maplers can find love and, if inspired enough, even marry. Do you have any questions about Amoria? Talk it to me.#b\r\n\r\n" + generateSelectionMenu(questionStr))
         } else if (status == 1) {
            switch (selection) {
               case 0:
                  cm.sendOk("The #bengagement process#k is as straightforward as it can be. Starting from a prequest from the #bring maker, #p9201000##k, gather #b#t4031367#'s#k thoughout the Maple world.\r\n\r\nCompleting it, you will be able to craft an engagement ring. With one in hands, declare yourself to someone you become fond of, and hope that person feels the same way.")
                  cm.dispose()
                  break

               case 1:
                  cm.sendOk("For the #bmarriage process#k you must be already engaged. The loving couple must choose a venue they want to hold their marriage. Amoria offers two: the #rCathedral#k and the #rChapel#k.\r\nThen, one of the partners must buy a #bWedding Ticket#k, available through the Cash Shop, and book their ceremony with the Wedding Assistant. Each partner will receive #rguest tickets#k to be distributed to their acquaintances.")
                  cm.dispose()
                  break

               case 2:
                  cm.sendOk("Unfortunately the love of long may fizzle someday. Well, I hope that's not the case for any loving couple that once married, is marrying today or is going to do so tomorrow. But, if that ever happens, I myself will be at service to make a safe divorce, by the fee of #r" + divorceFee + "#k mesos.")
                  cm.dispose()
                  break

               case 3:
                  ringObj = cm.getPlayer().getMarriageRing()
                  if (ringObj == null) {
                     Object itemid = getWeddingRingItemId(cm.getPlayer())

                     if (itemid != null) {
                        cm.sendOk("There you go, I've removed your old wedding ring.")
                        cm.gainItem((int) itemid, (short) -1)
                     } else if (hasEquippedWeddingRing(cm.getPlayer())) {
                        cm.sendOk("If you want your old wedding ring removed, please unequip it before talking to me.")
                     } else {
                        cm.sendOk("You're not married to require a divorce from it.")
                     }

                     cm.dispose()
                     return
                  }

                  cm.sendYesNo("So, you want to divorce from your partner? Be sure, this process #bcannot be rollbacked#k by any means, it's supposed to be an ultimatum from which your ring will be destroyed as consequence. That said, do you #rreally want to divorce#k?")
                  break
            }
         } else if (status == 2) {
            if (cm.getMeso() < divorceFee) {
               cm.sendOk("You don't have the required amount of #r" + divorceFee + " mesos#k for the divorce fee.")
               cm.dispose()
               return
            } else if (ringObj.isEquipped()) {
               cm.sendOk("Please unequip your ring before trying to divorce.")
               cm.dispose()
               return
            }

            cm.gainMeso(-divorceFee)
            RingActionHandler.breakMarriageRing(cm.getPlayer(), ringObj.itemId())
            cm.gainItem(ringObj.itemId(), (short) -1)

            cm.sendOk("You have divorced from your partner.")
            cm.dispose()
         }
      }
   }

   static def generateSelectionMenu(String[] array) {
      String menu = ""
      for (int i = 0; i < array.length; i++) {
         menu += "#L" + i + "#" + array[i] + "#l\r\n"
      }
      return menu
   }
}

NPC9201004 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201004(cm: cm))
   }
   return (NPC9201004) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }