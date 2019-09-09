package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9270037 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int beauty = 0
   int[] mhair_r = [30110, 30180, 30260, 30290, 30300, 30350, 30470, 30720, 30840]
   int[] fhair_r = [31110, 31200, 31250, 31280, 31600, 31640, 31670, 31810, 34020]
   int[] hairnew = []
   int[] haircolor = []


   def start() {
      cm.sendSimple("Hi, I'm the assistant here. Dont worry, I'm plenty good enough for this. If you have #b#t5150032##k or #b#t5151027##k by any chance, then allow me to take care of the rest?\r\n#L1#Haircut: #i5150032##t5150032##l\r\n#L2#Dye your hair: #i5151027##t5151027##l")
   }

   def pushIfItemExists(int[] array, int itemid) {
      if ((itemid = cm.getCosmeticItem(itemid)) != -1 && !cm.isCosmeticEquipped(itemid)) {
         array << itemid
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 1) {
         cm.dispose()
      } else {
         status++
         if (selection == 1) {
            beauty = 1
            hairnew = []

            if (cm.getPlayer().getGender() == 0) {
               for (int i = 0; i < mhair_r.length; i++) {
                  pushIfItemExists(hairnew, mhair_r[i] + (cm.getPlayer().getHair() % 10).intValue())
               }
            } else {
               for (int i = 0; i < fhair_r.length; i++) {
                  pushIfItemExists(hairnew, fhair_r[i] + (cm.getPlayer().getHair() % 10).intValue())
               }
            }

            cm.sendYesNo("If you use the REG coupon your hair will change RANDOMLY with a chance to obtain a new experimental style that I came up with. Are you going to use #b#t5150032##k and really change your hairstyle?")
         } else if (selection == 2) {
            beauty = 2
            haircolor = []
            int current = (cm.getPlayer().getHair() / 10).intValue() * 10
            for (int i = 0; i < 8; i++) {
               pushIfItemExists(haircolor, current + i)
            }
            cm.sendYesNo("If you use the REG coupon your hair will change RANDOMLY. Do you still want to use #b#t5151027##k and change it up?")
         } else if (status == 2) {
            if (beauty == 1) {
               if (cm.haveItem(5150032)) {
                  cm.gainItem(5150032, (short) -1)
                  cm.setHair(hairnew[Math.floor(Math.random() * hairnew.length).intValue()])
                  cm.sendOk("Enjoy your new and improved hairstyle!")
               } else {
                  cm.sendOk("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't give you a haircut without it. I'm sorry...")
               }
            }
            if (beauty == 2) {
               if (cm.haveItem(5151027)) {
                  cm.gainItem(5151027, (short) -1)
                  cm.setHair(haircolor[Math.floor(Math.random() * haircolor.length).intValue()])
                  cm.sendOk("Enjoy your new and improved haircolor!")
               } else {
                  cm.sendOk("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't dye your hair without it. I'm sorry...")
               }
            }
            cm.dispose()
         }
      }
   }
}

NPC9270037 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9270037(cm: cm))
   }
   return (NPC9270037) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }