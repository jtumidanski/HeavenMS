package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9209001 {
   NPCConversationManager cm
   int status = -1
   int sel = -1
   int sel2 = -1

   def start() {
      cm.sendOk(I18nMessage.from("9209001_HELLO"))
      cm.dispose()

      //cm.sendSimple(I18nMessage.from("9209001_HELLO_OPENS_TODAY"))
   }

   def action(Byte mode, Byte type, Integer selection) {
      status++
      if (status == 6 && mode == 1) {
         sel2 = -1
         status = 0
      }
      if (mode != 1) {
         if (mode == 0 && type == 0) {
            status -= 2
         } else {
            cm.dispose()
            return
         }
      }
      if (status == 0) {
         if (sel == -1) {
            sel = selection
         }
         if (selection == 0) {
            cm.sendNext(I18nMessage.from("9209001_WILL_SEND_YOU"))
         } else {
            cm.sendSimple(I18nMessage.from("9209001_WHAT_WOULD_YOU_LIKE_TO_KNOW"))
         }
      } else if (status == 1) {
         if (sel == 0) {
            cm.getPlayer().saveLocation("EVENT")
            cm.warp(680100000 + (Math.random() * 3).intValue())
            cm.dispose()
         } else if (selection == 0) {
            cm.sendNext(I18nMessage.from("9209001_ONLY_ON_SUNDAYS"))
            status -= 2
         } else if (selection == 1) {
            cm.sendSimple(I18nMessage.from("9209001_RARE_GOODS"))
         } else {
            cm.sendNext(I18nMessage.from("9209001_KEEP_US_IN_YOUR_THOUGHTS"))
            cm.dispose()
         }
      } else if (status == 2) {
         if (sel2 == -1) {
            sel2 = selection
         }
         if (sel2 == 0) {
            cm.sendNext(I18nMessage.from("9209001_CAN_FIND_MANY_ITEMS"))
         } else {
            cm.sendNext(I18nMessage.from("9209001_GROWS_TO_BE_A_CHICKEN"))
         }
      } else if (status == 3) {
         if (sel2 == 0) {
            cm.sendNextPrev(I18nMessage.from("9209001_CAN_BE_RESOLD"))
         } else {
            cm.sendNextPrev(I18nMessage.from("9209001_DEPOSIT_MONEY"))
         }
      } else if (status == 4) {
         if (sel2 == 0) {
            cm.sendNextPrev(I18nMessage.from("9209001_PRICES_FLUCTUATE"))
         } else {
            cm.sendNextPrev(I18nMessage.from("9209001_WILL_REWARD_YOU"))
         }
      } else if (status == 5) {
         if (sel2 == 0) {
            cm.sendNextPrev(I18nMessage.from("9209001_TEST_YOUR_BUSINESS_WIT"))
         } else {
            cm.sendNextPrev(I18nMessage.from("9209001_CHECK_ITS_GROWTH"))
         }
      }
   }
}

NPC9209001 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9209001(cm: cm))
   }
   return (NPC9209001) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }