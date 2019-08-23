package npc

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
            String talk = "Hi there, welcome to the wedding's Gift Registry. From which spouse's wishlist would you like to take a look?"
            String[] options = ["Groom", "Bride"]

            cm.sendSimple(talk + "\r\n\r\n#b" + generateSelectionMenu(options) + "#k")
         } else {
            cm.sendMarriageWishlist(selection == 0)
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
                     if (cm.getPlayer().isMarried()) {   // thanks MedicOP for solving an issue here
                        if (cm.getInventory(2).getNextFreeSlot() >= 0) {
                           int rand = Math.floor(Math.random() * bgPrizes.length).intValue()
                           cm.gainItem(bgPrizes[rand][0], (short) bgPrizes[rand][1])

                           cm.gainItem(4031424, (short) -1)
                           cm.dispose()
                        } else {
                           cm.sendOk("You don't have a free USE slot right now.")
                           cm.dispose()
                        }
                     } else {
                        cm.sendOk("You must be married to claim the prize for this box.")
                        cm.dispose()
                     }
                  } else {
                     cm.sendOk("You don't have an Onyx Chest for Bride and Groom.")
                     cm.dispose()
                  }
               } else if (selection == 1) {
                  if (cm.haveItem(4031423)) {
                     if (cm.getInventory(2).getNextFreeSlot() >= 0) {
                        int rand = Math.floor(Math.random() * cmPrizes.length).intValue()
                        cm.gainItem(cmPrizes[rand][0], (short) cmPrizes[rand][1])

                        cm.gainItem(4031423, (short) -1)
                        cm.dispose()
                     } else {
                        cm.sendOk("You don't have a free USE slot right now.")
                        cm.dispose()
                     }
                  } else {
                     cm.sendOk("You don't have an Onyx Chest.")
                     cm.dispose()
                  }
               }
            }
         } else {
            cm.sendOk("Hi there, welcome to Amoria's Wedding Gift Registry reserve. We redistribute and tender gifts for both wedding spouses and lucky ceremonial attenders.")
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