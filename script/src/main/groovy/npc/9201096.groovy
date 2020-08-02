package npc
import tools.I18nMessage

import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201096 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int selectedType = -1
   int selectedItem = -1
   int item
   Object mats
   Object matQty
   int cost
   int qty
   boolean equip
   boolean last_use

   def start() {
      cm.getPlayer().setCS(true)
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == 1) {
         status++
      } else {
         cm.sendOk(I18nMessage.from("9201096_VERY_WELL"))
         cm.dispose()
         return
      }

      if (status == 0) {
         String selStr = "Hey, are you aware about the expeditions running right now at the Crimsonwood Keep? So, there is a great opportunity for one to improve themselves, one can rack up experience and loot pretty fast there."
         cm.sendNext(selStr)
      } else if (status == 1) {
         String selStr = "Said so, methinks making use of some strong utility potions can potentially create some differential on the front, and by this I mean to start crafting #b#t2022284##k's to help on the efforts. So, getting right down to business, I'm currently pursuing #rplenty#k of those items: #r#t4032010##k, #r#t4032011##k, #r#t4032012##k, and some funds to support the cause. Would you want to get some of these boosters?"
         cm.sendYesNo(selStr)
      } else if (status == 2) {
         //selectedItem = selection;
         selectedItem = 0

         int[] itemSet = [2022284, 7777777]
         List matSet = [[4032010, 4032011, 4032012]]
         List matQtySet = [[60, 60, 45]]
         int[] costSet = [75000, 7777777]
         item = itemSet[selectedItem]
         mats = matSet[selectedItem]
         matQty = matQtySet[selectedItem]
         cost = costSet[selectedItem]

         String prompt = "Ok, I'll be crafting some #t" + item + "#. In that case, how many of those do you want me to make?"
         cm.sendGetNumber(prompt, 1, 1, 100)
      } else if (status == 3) {
         qty = (selection > 0) ? selection : (selection < 0 ? -selection : 1)
         last_use = false

         String prompt = "So, you want me to make "
         if (qty == 1) {
            prompt += "a #t" + item + "#?"
         } else {
            prompt += qty + " #t" + item + "#?"
         }

         prompt += " In that case, I'm going to need specific items from you in order to make it. And make sure you have room in your inventory!#b"

         if (mats instanceof ArrayList && matQty instanceof ArrayList) {
            for (int i = 0; i < mats.size(); i++) {
               prompt += "\r\n#i" + mats[i] + "# " + ((matQty[i] as Integer) * qty) + " #t" + mats[i] + "#"
            }
         } else {
            prompt += "\r\n#i"
            prompt += mats
            prompt += "# "
            prompt += matQty * qty
            prompt += " #t"
            prompt += mats + "#"
         }

         if (cost > 0) {
            prompt += "\r\n#i4031138# " + cost * qty + " meso"
         }
         cm.sendYesNo(prompt)
      } else if (status == 4) {
         boolean complete = true

         if (cm.getMeso() < cost * qty) {
            cm.sendOk(I18nMessage.from("9201096_NEED_FUNDS"))
         } else if (!cm.canHold(item, qty)) {
            cm.sendOk(I18nMessage.from("9201096_NEED_SPARE_SLOT"))
         } else {
            if (mats instanceof ArrayList && matQty instanceof ArrayList) {
               for (int i = 0; complete && i < mats.size(); i++) {
                  if (matQty[i] * qty == 1) {
                     complete = cm.haveItem(mats[i] as Integer)
                  } else {
                     complete = cm.haveItem(mats[i] as Integer, (matQty[i] as Integer) * qty)
                  }
               }
            } else {
               complete = cm.haveItem(mats as Integer, (matQty as Integer) * qty)
            }

            if (!complete) {
               cm.sendOk(I18nMessage.from("9201096_NOT_ENOUGH_RESOURCES"))
            } else {
               if (mats instanceof ArrayList && matQty instanceof ArrayList) {
                  for (int i = 0; i < mats.size(); i++) {
                     cm.gainItem(mats[i] as Integer, (short) ((-matQty[i] as Integer) * qty))
                  }
               } else {
                  cm.gainItem(mats as Integer, (short) ((-matQty as Integer) * qty))
               }
               cm.gainMeso(-cost * qty)
               cm.gainItem(item, (short) qty)
               cm.sendOk(I18nMessage.from("9201096_THERE_IT_IS"))
            }
         }
         cm.dispose()
      }
   }
}

NPC9201096 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201096(cm: cm))
   }
   return (NPC9201096) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }