package npc

import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2032002 {
   NPCConversationManager cm
   int status = -1
   int selectedType = -1
   boolean gotAllDocs

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0) {
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }

         EventInstanceManager eim = cm.getPlayer().getEventInstance()

         if (status == 0) {
            if (!eim.isEventCleared()) {
               cm.sendSimple("...#b\r\n#L0#What am I supposed to do here?#l\r\n#L1#I brought items!#l\r\n#L2#I want to get out!#l")
            } else {
               cm.sendNext("You completed this ordeal, now receive your prize.")
            }
         } else if (status == 1) {
            if (!eim.isEventCleared()) {
               selectedType = selection
               if (selection == 0) {
                  cm.sendNext("To reveal the power of Zakum, you'll have to recreate its core. Hidden somewhere in this dungeon is a #b\"Fire Ore\"#k which is one of the necessary materials for that core. Find it, and bring it to me.\r\n\r\nOh, and could you do me a favour? There's also a number of #bPaper Documents#k lying under rocks around here. If you can get 30 of them, I can reward you for your efforts.")
                  cm.dispose()
               } else if (selection == 1) {
                  if (!cm.isEventLeader()) {
                     cm.sendNext("Please let your leader bring the materials to me to complete this ordeal.")
                     cm.dispose()
                     return
                  }

                  if (!cm.haveItem(4001018)) { //fire ore
                     cm.sendNext("Please bring the #bFire Ore#k with you.")
                     cm.dispose()
                  } else {
                     gotAllDocs = cm.haveItem(4001015, 30)
                     if (!gotAllDocs) { //documents
                        cm.sendYesNo("So, you brought the fire ore with you? In that case, I can give to you and to each member of your party a piece of it, that should be more than enough to make the core of Zakum. Make sure your whole party has room in their inventory before proceeding.")
                     } else {
                        cm.sendYesNo("So, you brought the fire ore and the documents with you? In that case, I can give to you and to each member of your party a piece of it, that should be more than enough to make the core of Zakum. As well, since you #rbrought the documents#k with you, I can also provide you a special item which will #bbring you to the mine's entrance at any time#k. Make sure your whole party has room in their inventory before proceeding.")
                     }
                  }
               } else if (selection == 2) {
                  cm.sendYesNo("Are you sure you want to exit? If you're the party leader, your party will also be removed from the mines.")
               }
            } else {
               if (eim.getProperty("gotDocuments") == 1) {
                  if (eim.gridCheck(cm.getPlayer()) == -1) {
                     if (cm.canHoldAll([2030007, 4031061], [5, 1])) {
                        cm.gainItem(2030007, (short) 5)
                        cm.gainItem(4031061, (short) 1)

                        eim.gridInsert(cm.getPlayer(), 1)
                     } else {
                        cm.sendOk("Make sure you have room in your inventory before proceeding.")
                     }
                  } else {
                     cm.sendOk("You have already received your share. You can now exit the mines through the portal over there.")
                  }
               } else {
                  if (eim.gridCheck(cm.getPlayer()) == -1) {
                     if (cm.canHold(4031061, 1)) {
                        cm.gainItem(4031061, (short) 1)

                        eim.gridInsert(cm.getPlayer(), 1)
                     } else {
                        cm.sendOk("Make sure you have room in your inventory before proceeding.")
                     }
                  } else {
                     cm.sendOk("You have already received your share. You can now exit the mines through the portal over there.")
                  }
               }

               cm.dispose()
            }

         } else if (status == 2) {
            if (selectedType == 1) {
               cm.gainItem(4001018, (short) -1)

               if (gotAllDocs) {
                  cm.gainItem(4001015, (short) -30)

                  eim.setProperty("gotDocuments", 1)
                  eim.giveEventPlayersExp(20000)
               } else {
                  eim.giveEventPlayersExp(12000)
               }

               eim.clearPQ()
               cm.dispose()
            } else if (selectedType == 2) {
               cm.warp(211042300)
               cm.dispose()
            }
         }
      }
   }
}

NPC2032002 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2032002(cm: cm))
   }
   return (NPC2032002) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }