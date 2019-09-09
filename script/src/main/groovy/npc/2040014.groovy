package npc

import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2040014 {
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

   int[] items = [4080100, 4080006, 4080007, 4080008, 4080009, 4080010, 4080011]
   List matSet = [[4030012], [4030009, 4030013, 4030014], [4030009, 4030013, 4030016], [4030009, 4030014, 4030016], [4030009, 4030015, 4030013], [4030009, 4030015, 4030014], [4030009, 4030015, 4030016]]
   List matQtySet = [[99], [1, 99, 99], [1, 99, 99], [1, 99, 99], [1, 99, 99], [1, 99, 99], [1, 99, 99]]
   int[] costSet = [10000, 25000, 25000, 25000, 25000, 25000, 25000]

   def start() {
      cm.getPlayer().setCS(true)
      String selStr = "Hey there! My name is #p2040014#, and I am a specialist in mini-games. What kind of mini-game you want me to make? #b"
      String[] options = ["#i4080100# #t4080100#", "#i4080006# #t4080006#", "#i4080007# #t4080007#", "#i4080008# #t4080008#", "#i4080009# #t4080009#", "#i4080010# #t4080010#", "#i4080011# #t4080011#"]
      for (int i = 0; i < options.length; i++) {
         selStr += "\r\n#L" + i + "# " + options[i] + "#l"
      }
      cm.sendSimple(selStr)
   }

   def action(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         cm.dispose()
         return
      }
      if (status == 0) {
         selectedItem = selection

         item = items[selectedItem]
         mats = matSet[selectedItem]
         matQty = matQtySet[selectedItem]
         cost = costSet[selectedItem]
         qty = 1

         String prompt = "So we are going for "
         if (qty == 1) {
            prompt += "a #t" + item + "#"
         } else {
            prompt += qty + " #t" + item + "#"
         }
         prompt += ", right? In that case, I'm going to need specific items from you in order to make it. Make sure you have room in your inventory, though!#b"
         if (mats instanceof ArrayList && matQty instanceof ArrayList) {
            for (int i = 0; i < mats.size(); i++) {
               prompt += "\r\n#i" + mats[i] + "# " + ((matQty[i] as Integer) * qty) + " #t" + mats[i] + "#"
            }
         } else {
            prompt += "\r\n#i" + mats + "# " + ((matQty as Integer) * qty) + " #t" + mats + "#"
         }
         if (cost > 0) {
            prompt += "\r\n#i4031138# " + (cost * qty) + " meso"
         }
         cm.sendYesNo(prompt)
      } else if (status == 1) {
         boolean complete = true

         if (cm.getMeso() < (cost * qty)) {
            cm.sendOk("See, I need to specify my wages to support my career, that cannot be bypassed. I will gladly help you once you've got the money.")
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
            cm.sendOk("You are lacking some items for the set you want to make. Please provide them so that we can assemble the game set.")
         } else {
            if (cm.canHold(item, qty)) {
               if (mats instanceof ArrayList && matQty instanceof ArrayList) {
                  for (int i = 0; i < mats.size(); i++) {
                     cm.gainItem(mats[i] as Integer, (short) -((matQty[i] as Integer) * qty))
                  }
               } else {
                  cm.gainItem(mats as Integer, (short) -((matQty as Integer) * qty))
               }
               cm.gainMeso(-(cost * qty))

               cm.gainItem(item, (short) qty)
               cm.sendOk("There is your game set. Have fun!")
            } else {
               cm.sendOk("I can't make a set for you if there's no room in your ETC inventory for it. Please free a space first and then talk to me.")
            }
         }

         cm.dispose()
      }
   }
}

NPC2040014 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2040014(cm: cm))
   }
   return (NPC2040014) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }