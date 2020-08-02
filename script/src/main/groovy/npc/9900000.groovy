package npc
import tools.I18nMessage

import scripting.ScriptUtils
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9900000 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int beauty = 0
   int[] hairColor = []
   int[] skin = [0, 1, 2, 3, 4, 5, 9, 10]
   int[] femaleHair = [31000, 31010, 31020, 31030, 31040, 31050, 31060, 31070, 31080, 31090, 31100, 31110, 31120, 31130, 31140, 31150, 31160, 31170, 31180, 31190, 31200, 31210, 31220, 31230, 31240, 31250, 31260, 31270, 31280, 31290, 31300, 31310, 31320, 31330, 31340, 31350, 31400, 31410, 31420, 31440, 31450, 31460, 31470, 31480, 31490, 31510, 31520, 31530, 31540, 31550, 31560, 31570, 31580, 31590, 31600, 31610, 31620, 31630, 31640, 31650, 31670, 31660, 31680, 31690, 31700, 31710, 31720, 31730, 31740, 31750, 31760, 31770, 31780, 31790, 31800, 31810, 31820, 31830, 31840, 31850, 31860, 31870, 31880, 31890, 31910, 31920, 31930, 31940, 31950, 34010, 34020, 34030, 34050, 34110]
   int[] hair = [30000, 30010, 30020, 30030, 30040, 30050, 30060, 30070, 30080, 30090, 30110, 30120, 30130, 30140, 30150, 30160, 30170, 30180, 30190, 30200, 30210, 30220, 30230, 30240, 30250, 30260, 30270, 30280, 30290, 30300, 30310, 30320, 30330, 30340, 30350, 30360, 30370, 30400, 30410, 30420, 30440, 30450, 30460, 30470, 30480, 30490, 30510, 30520, 30530, 30540, 30550, 30560, 30570, 30580, 30590, 30600, 30610, 30620, 30630, 30640, 30650, 30660, 30670, 30680, 30690, 30700, 30710, 30720, 30730, 30740, 30750, 30760, 30770, 30780, 30790, 30800, 30810, 30820, 30830, 30840, 30860, 30870, 30880, 30890, 30900, 30910, 30920, 30930, 30940, 30950, 30990, 33000, 33040, 33100]
   int[] hairNew = []
   int[] face = [20000, 20001, 20002, 20003, 20004, 20005, 20006, 20007, 20008, 20009, 20010, 20011, 20012, 20013, 20014, 20015, 20016, 20017, 20018, 20019, 20020, 20021, 20022, 20023, 20024, 20025, 20026, 20027, 20028, 20029, 20031, 20032]
   int[] femaleFace = [21000, 21001, 21002, 21003, 21004, 21005, 21006, 21007, 21008, 21009, 21010, 21011, 21012, 21013, 21014, 21016, 21017, 21018, 21019, 21020, 21021, 21022, 21023, 21024, 21025, 21026, 21027, 21029, 21030]
   int[] faceNew = []
   int[] colors = []
   int price = 100000

   def start() {
      if (cm.getPlayer().gmLevel() < 1) {
         cm.sendOk(I18nMessage.from("9900000_WASSUP"))
         cm.dispose()
         return
      }

      if (cm.getPlayer().isMale()) {
         cm.sendSimple(I18nMessage.from("9900000_CHANGE_YOUR_LOOK").with(price))
      } else {
         cm.sendSimple(I18nMessage.from("9900000_CHANGE_YOUR_LOOK_2").with(price))
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1 || cm.getPlayer().gmLevel() < 1) {
         cm.dispose()
         return
      }
      if (status == 1) {
         beauty = selection + 1
         if (cm.getMeso() > price) {
            if (selection == 0) {
               cm.sendStyle("Pick one?", skin)
            } else if (selection == 1 || selection == 5) {
               (selection == 1 ? hair : femaleHair).each { int key, int value ->
                  hairNew = ScriptUtils.pushItemIfTrue(hairNew, value, { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
               }
               cm.sendStyle("Pick one?", hairNew)
            } else if (selection == 2) {
               int baseHair = (cm.getPlayer().getHair() / 10).intValue() * 10
               for (int k = 0; k < 8; k++) {
                  hairColor = ScriptUtils.pushItemIfTrue(hairColor, baseHair + k, { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
               }
               cm.sendStyle("Pick one?", hairColor)
            } else if (selection == 3 || selection == 6) {
               (selection == 3 ? face : femaleFace).each { int key, int value ->
                  faceNew = ScriptUtils.pushItemIfTrue(faceNew, value, { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })

               }

               cm.sendStyle("Pick one?", faceNew)
            } else if (selection == 4) {
               int baseFace = (cm.getPlayer().getFace() / 1000).intValue() * 1000 + (cm.getPlayer().getFace() % 100).intValue()
               for (int i = 0; i < 9; i++) {
                  colors = ScriptUtils.pushItemIfTrue(colors, baseFace + (i * 100), { itemId -> cm.cosmeticExistsAndIsntEquipped(itemId) })
               }
               cm.sendStyle("Pick one?", colors)
            }
         } else {
            cm.sendNext(I18nMessage.from("9900000_NOT_ENOUGH_MESOS").with(price))
            cm.dispose()
         }

      } else if (status == 2) {
         if (beauty == 1) {
            cm.setSkin(skin[selection])
            cm.gainMeso(-price)
         }
         if (beauty == 2 || beauty == 6) {
            cm.setHair(hairNew[selection])
            cm.gainMeso(-price)
         }
         if (beauty == 3) {
            cm.setHair(hairColor[selection])
            cm.gainMeso(-price)
         }
         if (beauty == 4 || beauty == 7) {
            cm.setFace(faceNew[selection])
            cm.gainMeso(-price)
         }
         if (beauty == 5) {
            cm.setFace(colors[selection])
            cm.gainMeso(-price)
         }
         cm.dispose()
      }
   }
}

NPC9900000 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9900000(cm: cm))
   }
   return (NPC9900000) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }