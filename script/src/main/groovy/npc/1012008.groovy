package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		Casey the Game Master
	Map(s): 		Henesys Game Park
	Description: 	
*/


class NPC1012008 {
   NPCConversationManager cm
   int status = -1
   int sel = -1
   int current
   int[] omok = [4080000, 4080001, 4080002, 4080003, 4080004, 4080005]
   int[] omok1piece = [4030000, 4030000, 4030000, 4030010, 4030011, 4030011]
   int[] omok2piece = [4030001, 4030010, 4030011, 4030001, 4030010, 4030001]
   int omokAmount = 99

   def start() {
      current = 0
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1 && current > 0) {
         cm.dispose()
         return
      } else {
         if (mode == 1) {
            status++
         } else {
            status--
         }
      }

      if (status == 0) {
         cm.sendSimple(I18nMessage.from("1012008_NEED_A_BREATHER"))

      } else if (status == 1) {
         if (selection == 0) {
            cm.sendSimple(I18nMessage.from("1012008_YOU_WANT_TO_MAKE"))
         } else if (selection == 1) {
            cm.sendSimple(I18nMessage.from("1012008_WANT_TO_LEARN_MORE"))
         }

      } else if (status == 2) {
         if (selection == 2) {
            current = 1
            cm.sendNext(I18nMessage.from("1012008_OMOK_RULES"))
         } else if (selection == 3) {
            current = 2
            cm.sendNext(I18nMessage.from("1012008_MATCH_CARD_RULES"))

         } else if (selection == 4) {
            current = 3
            cm.sendNext(I18nMessage.from("1012008_OMOK_NEEDS"))

         } else if (selection == 5) {
            current = 4
            if (cm.haveItem(4030012, 15)) {
               cm.gainItem(4030012, (short) -15)
               cm.gainItem(4080100, (short) 1)
            } else {
               cm.sendNext(I18nMessage.from("1012008_MATCH_CARD_NEEDS"))
               cm.dispose()
            }
         }


      } else if (status == 3) {
         if (current == 1) {
            cm.sendNextPrev(I18nMessage.from("1012008_OMOK_COST"))
         } else if (current == 2) {
            cm.sendNextPrev(I18nMessage.from("1012008_MATCH_CARD_COST"))

         } else if (current == 3) {
            def choices = ""
            for (def i = 0; i < omok.length; i++) {
               choices += "\r\n#L" + i + "##b#t" + omok[i] + "##k#l"
            }
            cm.sendSimple(I18nMessage.from("1012008_WHICH_SET").with(choices))
         }

      } else if (status == 4) {
         if (current == 1 || current == 2) {
            cm.sendNextPrev(I18nMessage.from("1012008_HOW_TO_START")) //Oh yeah, because people WALK in Omok Rooms.
         } else if (current == 3) {
            if (cm.haveItem(omok1piece[selection], 99) && cm.haveItem(omok2piece[selection], 99) && cm.haveItem(4030009, 1)) {
               cm.gainItem(omok1piece[selection], (short) -omokAmount)
               cm.gainItem(omok2piece[selection], (short) -omokAmount)
               cm.gainItem(4030009, (short) -1)
               cm.gainItem(omok[selection], (short) 1)
               cm.dispose()
            } else {
               cm.sendNext(I18nMessage.from("1012008_OMOK_MATERIALS").with(omok[selection], omokAmount, omok1piece[selection], omokAmount, omok2piece[selection], 4030009))
               cm.dispose()
            }
         }

      } else if (status == 5) {
         if (current == 1) {
            cm.sendNextPrev(I18nMessage.from("1012008_OMOK_START"))
         } else if (current == 2) {
            cm.sendNextPrev(I18nMessage.from("1012008_MATCH_CARD_START"))
         }

      } else if (status == 6) {
         if (current == 1) {
            cm.sendNextPrev(I18nMessage.from("1012008_REDO_OR_TIE"))
         } else if (current == 2) {
            cm.sendNextPrev(I18nMessage.from("1012008_MOVE_ON_TIME"))
         }

      } else if (status == 7) {
         if (current == 1) {
            cm.sendPrev(I18nMessage.from("1012008_NEXT_GAME"))
         } else if (current == 2) {
            cm.sendNextPrev(I18nMessage.from("1012008_LONGER_STREAK"))
         }
      } else if (status == 8) {
         if (current == 2) {
            cm.sendPrev(I18nMessage.from("1012008_NEXT_GAME"))
         }
      }
   }
}

NPC1012008 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1012008(cm: cm))
   }
   return (NPC1012008) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }