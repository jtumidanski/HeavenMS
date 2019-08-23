package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2010002 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int beauty = 0
   int price = 1000000
   int[] mface_v = [20000, 20001, 20003, 20004, 20005, 20006, 20007, 20008, 20012, 20014, 20022, 20028, 20031]
   int[] fface_v = [21000, 21001, 21002, 21003, 21004, 21005, 21006, 21007, 21008, 21012, 21014, 21023, 21026]
   int[] facenew = []

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def pushIfItemExists(int[] array, int itemid) {
      if ((itemid = cm.getCosmeticItem(itemid)) != -1 && !cm.isCosmeticEquipped(itemid)) {
         array << itemid
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 1) {  // disposing issue with stylishs found thanks to Vcoc
         cm.dispose()
      } else {
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 0) {
            cm.sendSimple("Well well well, welcome to the Orbis Plastic Surgery! Would you like to transform your face into something new? With a #b#t5152005##k, you can let us take care of the rest and have the face you've always wanted~!\r\n#L2#Plastic Surgery: #i5152005##t5152005##l")
         } else if (status == 1) {
            if (selection == 2) {
               facenew = []
               if (cm.getPlayer().getGender() == 0) {
                  for (int i = 0; i < mface_v.length; i++) {
                     pushIfItemExists(facenew, mface_v[i] + cm.getPlayer().getFace() % 1000 - (cm.getPlayer().getFace() % 100))
                  }
               }
               if (cm.getPlayer().getGender() == 1) {
                  for (int i = 0; i < fface_v.length; i++) {
                     pushIfItemExists(facenew, fface_v[i] + cm.getPlayer().getFace() % 1000 - (cm.getPlayer().getFace() % 100))
                  }
               }
               cm.sendStyle("I can totally transform your face into something new... how about giving us a try? For #b#t5152005##k, you can get the face of your liking...take your time in choosing the face of your preference.", facenew)
            }
         } else if (status == 2) {
            cm.dispose()
            if (cm.haveItem(5152005)) {
               cm.gainItem(5152005, (short) -1)
               cm.setFace(facenew[selection])
               cm.sendOk("Enjoy your new and improved face!")
            } else {
               cm.sendOk("Hmm ... it looks like you don't have the coupon specifically for this place. Sorry to say this, but without the coupon, there's no plastic surgery for you...")
            }
         }
      }
   }
}

NPC2010002 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2010002(cm: cm))
   }
   return (NPC2010002) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }