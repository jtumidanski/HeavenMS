package npc

import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager
import tools.I18nMessage

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
               cm.sendSimple(I18nMessage.from("2032002_WHAT_AM_I_SUPPOSED_TO_DO"))
            } else {
               cm.sendNext(I18nMessage.from("2032002_RECEIVE_YOUR_PRIZE"))
            }
         } else if (status == 1) {
            if (!eim.isEventCleared()) {
               selectedType = selection
               if (selection == 0) {
                  cm.sendNext(I18nMessage.from("2032002_RECREATE_ITS_CORE"))
                  cm.dispose()
               } else if (selection == 1) {
                  if (!cm.isEventLeader()) {
                     cm.sendNext(I18nMessage.from("2032002_HAVE_LEADER_BRING_MATERIALS"))
                     cm.dispose()
                     return
                  }

                  if (!cm.haveItem(4001018)) { //fire ore
                     cm.sendNext(I18nMessage.from("2032002_BRING_THE_FIRE_ORE"))
                     cm.dispose()
                  } else {
                     gotAllDocs = cm.haveItem(4001015, 30)
                     if (!gotAllDocs) { //documents
                        cm.sendYesNo(I18nMessage.from("2032002_EACH_MEMBER_GETS_PIECE"))
                     } else {
                        cm.sendYesNo(I18nMessage.from("2032002_EACH_MEMBER_GETS_PIECE_2"))
                     }
                  }
               } else if (selection == 2) {
                  cm.sendYesNo(I18nMessage.from("2032002_ARE_YOU_SURE_YOU_WANT_TO_EXIT"))
               }
            } else {
               if (eim.getProperty("gotDocuments") == 1) {
                  if (eim.gridCheck(cm.getPlayer()) == -1) {
                     if (cm.canHoldAll([2030007, 4031061], [5, 1])) {
                        cm.gainItem(2030007, (short) 5)
                        cm.gainItem(4031061, (short) 1)

                        eim.gridInsert(cm.getPlayer(), 1)
                     } else {
                        cm.sendOk(I18nMessage.from("2032002_MAKE_INVENTORY_ROOM"))
                     }
                  } else {
                     cm.sendOk(I18nMessage.from("2032002_ALREADY_RECEIVED_SHARE"))
                  }
               } else {
                  if (eim.gridCheck(cm.getPlayer()) == -1) {
                     if (cm.canHold(4031061, 1)) {
                        cm.gainItem(4031061, (short) 1)

                        eim.gridInsert(cm.getPlayer(), 1)
                     } else {
                        cm.sendOk(I18nMessage.from("2032002_MAKE_INVENTORY_ROOM"))
                     }
                  } else {
                     cm.sendOk(I18nMessage.from("2032002_ALREADY_RECEIVED_SHARE"))
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