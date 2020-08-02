package npc
import tools.I18nMessage

import client.inventory.Item
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201014 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   boolean marriageRoom
   int marriageAction = 0
   List<Item> marriageGifts

   def start() {
      marriageRoom = cm.getPlayer().getMarriageInstance() != null
      if (!marriageRoom) {
         marriageGifts = cm.getUnclaimedMarriageGifts()
         marriageAction = (marriageGifts.size() != 0 ? 2 : ((cm.haveItem(4031423) || cm.haveItem(4031424)) ? 1 : 0))
      }

      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == 1) {
         status++
      } else {
         cm.dispose()
         return
      }
      if (marriageRoom) {
         if (status == 0) {
            String talk = "Hi there, welcome to the wedding's Gift Registry. From which spouse's wish list would you like to take a look?"
            String[] options = ["Groom", "Bride"]

            cm.sendSimple(talk + "\r\n\r\n#b" + generateSelectionMenu(options) + "#k")
         } else {
            cm.sendMarriageWishList(selection == 0)
            cm.dispose()
         }
      } else {
         if (marriageAction == 2) {     // unclaimed gifts
            if (status == 0) {
               String talk = "Hi there, it seems you have unclaimed gifts from your wedding. Claim them here on the wedding's Gift Registry reserve."
               cm.sendNext(talk)
            } else {
               cm.sendMarriageGifts(marriageGifts)
               cm.dispose()
            }
         } else if (marriageAction == 1) {     // onyx prizes
            if (status == 0) {
               String msg = "Hello I exchange Onyx Chest for Bride and Groom and the Onyx Chest for prizes!#b"
               String[] choice1 = ["I have an Onyx Chest for Bride and Groom", "I have an Onyx Chest"]
               for (int i = 0; i < choice1.length; i++) {
                  msg += "\r\n#L" + i + "#" + choice1[i] + "#l"
               }
               cm.sendSimple(msg)
            } else if (status == 1) {
               if (selection == 0) {
                  if (cm.haveItem(4031424)) {
                     if (cm.getPlayer().isMarried()) {
                        if (cm.getInventory(2).getNextFreeSlot() >= 0) {
                           int rand = Math.floor(Math.random() * 4).intValue()
                           if (rand == 0) {
                              cm.gainItem(2022179, (short) 10)
                           } else if (rand == 1) {
                              cm.gainItem(2022282, (short) 10)
                           } else if (rand == 2) {
                              cm.gainItem(2210005, (short) 5)
                           } else if (rand == 3) {
                              cm.gainItem(2210003, (short) 5)
                           }
                           cm.gainItem(4031424, (short) -1)
                           cm.dispose()
                        } else {
                           cm.sendOk(I18nMessage.from("9201014_NEED_USE_SLOT_FREE"))
                           cm.dispose()
                        }
                     } else {
                        cm.sendOk(I18nMessage.from("9201014_MUST_BE_MARRIED_TO_CLAIM"))
                        cm.dispose()
                     }
                  } else {
                     cm.sendOk(I18nMessage.from("9201014_NO_ONYX_CHEST_TO_GIVE"))
                     cm.dispose()
                  }
               } else if (selection == 1) {
                  if (cm.haveItem(4031423)) {
                     cm.sendSimple(I18nMessage.from("9201014_CHOOSE_PRIZE"))
                  } else {
                     cm.sendOk(I18nMessage.from("9201014_NO_ONYX_CHEST"))
                     cm.dispose()
                  }
               }
            } else if (status == 2) {
               if (cm.getInventory(2).getNextFreeSlot() >= 0) {
                  if (selection == 0) {
                     cm.gainItem(2022011, (short) 10)
                  } else if (selection == 1) {
                     cm.gainItem(2000005, (short) 50)
                  } else if (selection == 2) {
                     cm.gainItem(2022273, (short) 10)
                  } else if (selection == 3) {
                     cm.gainItem(2022179, (short) 3)
                  }
                  cm.gainItem(4031423, (short) -1)
                  cm.dispose()
               } else {
                  cm.sendOk(I18nMessage.from("9201014_NEED_USE_SLOT_FREE"))
                  cm.dispose()
               }
            }
         } else {
            cm.sendOk(I18nMessage.from("9201014_HELLO"))
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

NPC9201014 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201014(cm: cm))
   }
   return (NPC9201014) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }