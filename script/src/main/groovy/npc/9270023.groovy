package npc

import scripting.ScriptUtils
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9270023 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int beauty = 0
   int[] maleFace = [20002, 20005, 20006, 20013, 20017, 20021, 20024]
   int[] femaleFace = [21002, 21003, 21014, 21016, 21017, 21021, 21027]
   int[] faceNew = []

   def start() {
      cm.sendSimple("If you use this regular coupon, your face may transform into a random new look...do you still want to do it using #b#t5152037##k, I will do it anyways for you. But don't forget, it will be random!\r\n#L2#OK! (Uses #i5152037# #t5152037#)#l")
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 1) {
         cm.dispose()
      } else {
         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 1) {
            if (!cm.haveItem(5152037)) {
               cm.sendOk("Hmm ... it looks like you don't have the coupon specifically for this place. Sorry to say this, but without the coupon, there's no plastic surgery for you...")
               cm.dispose()
               return
            }

            faceNew = []
            if (cm.getPlayer().getGender() == 0) {
               for (int i = 0; i < maleFace.length; i++) {
                  faceNew = ScriptUtils.pushItemIfTrue(faceNew, maleFace[i] + cm.getPlayer().getFace() % 1000 - (cm.getPlayer().getFace() % 100), { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
               }
            }
            if (cm.getPlayer().getGender() == 1) {
               for (int i = 0; i < femaleFace.length; i++) {
                  faceNew = ScriptUtils.pushItemIfTrue(faceNew, femaleFace[i] + cm.getPlayer().getFace() % 1000 - (cm.getPlayer().getFace() % 100), { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
               }
            }
            cm.sendYesNo("If you use the regular coupon, your face may transform into a random new look...do you still want to do it using #b#t5152037##k?")
         } else if (status == 2) {
            cm.gainItem(5152037, (short) -1)
            cm.setFace(faceNew[Math.floor(Math.random() * faceNew.length).intValue()])
            cm.sendOk("Enjoy your new and improved face!")

            cm.dispose()
         }
      }
   }
}

NPC9270023 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9270023(cm: cm))
   }
   return (NPC9270023) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }