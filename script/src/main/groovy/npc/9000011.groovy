package npc
import tools.I18nMessage

import client.MapleQuestStatus
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9000011 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int[] quantities = [10, 8, 6, 5, 4, 3, 2, 1, 1, 1]
   int[] prize1 = [1442047, 2000000, 2000001, 2000002, 2000003, 2000004, 2000005, 2430036, 2430037, 2430038, 2430039, 2430040]
   //1 day
   int[] prize2 = [1442047, 4080100, 4080001, 4080002, 4080003, 4080004, 4080005, 4080006, 4080007, 4080008, 4080009, 4080010, 4080011]
   int[] prize3 = [1442047, 1442048, 2022070]
   int[] prize4 = [1442048, 2430082, 2430072] //7 day
   int[] prize5 = [1442048, 2430091, 2430092, 2430093, 2430101, 2430102] //10 day
   int[] prize6 = [1442048, 1442050, 2430073, 2430074, 2430075, 2430076, 2430077] //15 day
   int[] prize7 = [1442050, 3010183, 3010182, 3010053, 2430080] //20 day
   int[] prize8 = [1442050, 3010178, 3010177, 3010075, 1442049, 2430053, 2430054, 2430055, 2430056, 2430103, 2430136]
   //30 day
   int[] prize9 = [1442049, 3010123, 3010175, 3010170, 3010172, 3010173, 2430201, 2430228, 2430229] //60 day
   int[] prize10 = [1442049, 3010172, 3010171, 3010169, 3010168, 3010161, 2430117, 2430118, 2430119, 2430120, 2430137]
   //1 year

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (status >= 0 && mode == 0) {
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 0) {
            cm.sendNext(I18nMessage.from("9000011_CAN_I_HANG_OUT").with(cm.getNpc()))
         } else if (status == 1) {
            cm.sendSimple(I18nMessage.from("9000011_WHAT_KIND_OF_EVENT"))
         } else if (status == 2) {
            if (selection == 0) {
               cm.sendNext(I18nMessage.from("9000011_ALL_THIS_MONTH"))
               cm.dispose()
            } else if (selection == 1) {
               cm.sendSimple(I18nMessage.from("9000011_MANY_GAMES"))
            } else if (selection == 2) {
               MapleQuestStatus questStatus = cm.getQuestRecord(100295)
               if (questStatus.getCustomData() == null) {
                  questStatus.setCustomData("0")
               }
               int dat = (questStatus.getCustomData()).toInteger()
               if (dat + 3600000 >= cm.getCurrentTime()) {
                  cm.sendNext(I18nMessage.from("9000011_YOU_HAVE_ENTERED_THE_EVENT_ALREADY"))
               } else if (!cm.canHold(4031019)) {
                  cm.sendNext(I18nMessage.from("9000011_SAVE_INVENTORY_SPACE"))
               } else if (cm.getClient().getChannelServer().getEvent() > -1 && !cm.haveItem(4031019)) {
                  cm.getPlayer().saveLocation("EVENT")
                  cm.getPlayer().setChalkboard(null)
                  questStatus.setCustomData("" + cm.getCurrentTime())

                  int eventMapId = cm.getClient().getChannelServer().getEvent().getMapId()
                  if (eventMapId == 109080000 || eventMapId == 109080010) {
                     cm.warp(eventMapId, 0)
                  } else {
                     cm.warp(eventMapId, "join00")
                  }
               } else {
                  cm.sendNext(I18nMessage.from("9000011_EVENT_NOT_BEEN_STARTED"))
               }
               cm.dispose()
            } else if (selection == 3) {
               String selStr = "Which Certificate of straight Win do you wish to exchange?"
               for (int i = 0; i < quantities.length; i++) {
                  selStr += "\r\n#b#L" + i + "##t" + (4031332 + i) + "# Exchange(" + quantities[i] + ")#l"
               }
               cm.sendSimple(selStr)
               status = 9
            }
         } else if (status == 3) {
            if (selection == 0) {
               cm.sendNext(I18nMessage.from("9000011_OLA_OLA_INFO"))
               cm.dispose()
            } else if (selection == 1) {
               cm.sendNext(I18nMessage.from("9000011_FITNESS_INFO"))
               cm.dispose()
            } else if (selection == 2) {
               cm.sendNext(I18nMessage.from("9000011_SNOWBALL_INFO"))
               cm.dispose()
            } else if (selection == 3) {
               cm.sendNext(I18nMessage.from("9000011_COCONUT_INFO"))
               cm.dispose()
            } else if (selection == 4) {
               cm.sendNext(I18nMessage.from("9000011_OX_QUIZ_INFO"))
               cm.dispose()
            } else if (selection == 5) {
               cm.sendNext(I18nMessage.from("9000011_TREASURE_INFO"))
               cm.dispose()
            }
         } else if (status == 10) {
            if (selection < 0 || selection > quantities.length) {
               return
            }
            int ite = 4031332 + selection
            int quantity = quantities[selection]
            int[] pri
            switch (selection) {
               case 0:
                  pri = prize1
                  break
               case 1:
                  pri = prize2
                  break
               case 2:
                  pri = prize3
                  break
               case 3:
                  pri = prize4
                  break
               case 4:
                  pri = prize5
                  break
               case 5:
                  pri = prize6
                  break
               case 6:
                  pri = prize7
                  break
               case 7:
                  pri = prize8
                  break
               case 8:
                  pri = prize9
                  break
               case 9:
                  pri = prize10
                  break
               default:
                  cm.dispose()
                  return
            }
            int rand = Math.floor(Math.random() * pri.length).intValue()
            if (!cm.haveItem(ite, quantity)) {
               cm.sendOk(I18nMessage.from("9000011_YOU_NEED").with(quantity, ite))
            } else if (cm.getInventory(1).getNextFreeSlot() <= -1 || cm.getInventory(2).getNextFreeSlot() <= -1 || cm.getInventory(3).getNextFreeSlot() <= -1 || cm.getInventory(4).getNextFreeSlot() <= -1) {
               cm.sendOk(I18nMessage.from("9000011_NEED_SPACE_FOR_ITEM"))
            } else {
               cm.gainItem(pri[rand], (short) 1)
               cm.gainItem(ite, (short) -quantity)
               cm.gainMeso(100000 * selection)
            }
            cm.dispose()
         }
      }
   }
}

NPC9000011 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9000011(cm: cm))
   }
   return (NPC9000011) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }