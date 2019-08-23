package npc

import client.MapleCharacter
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201000 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int state

   int item
   Object mats
   Object matQty
   int cost

   String[] options

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
            options = ["I want to make a ring.", "I want to discard the ring box I have."]
            cm.sendSimple("I'm #p9201000#, the #bengagement ring maker#k. How can I help you?\r\n\r\n#b" + generateSelectionMenu(options))
         } else if (status == 1) {
            if (selection == 0) {
               if (!cm.isQuestCompleted(100400)) {
                  if (!cm.isQuestStarted(100400)) {
                     state = 0
                     cm.sendNext("So you want to make a engagement ring, huh? Very well, I can provide one after you receive #rblessings#k from your #b#p9201003##k.")
                  } else {
                     cm.sendOk("Take the blessings from your #b#p9201003##k before trying to craft an engagement ring. They must be waiting for you at home, beyond #rHenesys hunting grounds#k.")
                     cm.dispose()
                  }
               } else {
                  if (hasEngagementBox(cm.getPlayer())) {
                     cm.sendOk("Sorry, you already have an engagement box. I cannot provide you more than one box per time.")
                     cm.dispose()
                     return
                  }
                  if (cm.getPlayer().getGender() != 0) {
                     cm.sendOk("Sorry, but the ring box is currently available only for males.")
                     cm.dispose()
                     return
                  }

                  state = 1
                  options = ["Moonstone", "Star Gem", "Golden Heart", "Silver Swan"]
                  String selStr = "So, what kind of engagement ring you want me to craft?\r\n\r\n#b" + generateSelectionMenu(options)
                  cm.sendSimple(selStr)
               }
            } else {
               if (hasEngagementBox(cm.getPlayer())) {
                  for (int i = 2240000; i <= 2240003; i++) {
                     cm.removeAll(i)
                  }

                  cm.sendOk("Your ring box has been discarded.")
               } else {
                  cm.sendOk("You have no ring box to discard.")
               }

               cm.dispose()
            }
         } else if (status == 2) {
            if (state == 0) {
               cm.sendOk("Where do they live, you ask? My, it goes way back... you see, I'm a friend of theirs, and I was the one who crafted and personally delivered their engagement ring. They live beyond #rHenesys Hunting Grounds#k, I'm sure you know where it is.")
               cm.startQuest(100400)
               cm.dispose()
            } else {
               int[] itemSet = [2240000, 2240001, 2240002, 2240003]
               List matSet = [[4011007, 4021007], [4021009, 4021007], [4011006, 4021007], [4011004, 4021007]]
               List matQtySet = [[1, 1], [1, 1], [1, 1], [1, 1]]
               int[] costSet = [30000, 20000, 10000, 5000]

               item = itemSet[selection]
               mats = matSet[selection]
               matQty = matQtySet[selection]
               cost = costSet[selection]

               String prompt = "Then I'm going to craft you a #b#t" + item + "##k, is that right?"
               prompt += " In that case, I'm going to need specific items from you in order to make it. Make sure you have room in your inventory, though!#b"

               if (mats instanceof ArrayList && matQty instanceof ArrayList) {
                  for (int i = 0; i < mats.size(); i++) {
                     prompt += "\r\n#i" + mats[i] + "# " + matQty[i] + " #t" + mats[i] + "#"
                  }
               } else {
                  prompt += "\r\n#i" + mats + "# " + matQty + " #t" + mats + "#"
               }

               if (cost > 0) {
                  prompt += "\r\n#i4031138# " + cost + " meso"
               }

               cm.sendYesNo(prompt)
            }
         } else if (status == 3) {
            boolean complete = true
            int recvItem = item, recvQty = 1, qty = 1

            if (!cm.canHold(recvItem, recvQty)) {
               cm.sendOk("Check your inventory for a free slot first.")
               cm.dispose()
               return
            } else if (cm.getMeso() < cost * qty) {
               cm.sendOk("I'm sorry but there's a fee for my services. Please bring me the right amount of mesos here before trying to forge a ring.")
               cm.dispose()
               return
            } else {
               if (mats instanceof ArrayList && matQty instanceof ArrayList) {
                  for (int i = 0; complete && i < mats.size(); i++) {
                     if (!cm.haveItem(mats[i] as Integer, (matQty[i] as Integer) * qty)) {
                        complete = false
                     }
                  }
               } else if (!cm.haveItem(mats as Integer, (matQty as Integer) * qty)) {
                  complete = false
               }
            }

            if (!complete) {
               cm.sendOk("Hm, it seems you're lacking some ingredients for the engagement ring. Please provide them first, will you?")
            } else {
               if (mats instanceof ArrayList && matQty instanceof ArrayList) {
                  for (int i = 0; i < mats.size(); i++) {
                     cm.gainItem(mats[i] as Integer, (short) ((-matQty[i] as Integer) * qty))
                  }
               } else {
                  cm.gainItem(mats as Integer, (short) ((-matQty as Integer) * qty))
               }

               if (cost > 0) {
                  cm.gainMeso(-cost * qty)
               }

               cm.gainItem(recvItem, (short) recvQty)
               cm.sendOk("All done, the engagement ring came out just right. I wish you a happy engagement.")
            }
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

   static def hasEngagementBox(MapleCharacter player) {
      for (int i = 2240000; i <= 2240003; i++) {
         if (player.haveItem(i)) {
            return true
         }
      }

      return false
   }
}

NPC9201000 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201000(cm: cm))
   }
   return (NPC9201000) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }