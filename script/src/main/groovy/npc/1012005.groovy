package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		Cloy
	Map(s): 		Victoria Road : Henesys Park (100000200)
	Description: 		Pet Master
*/


class NPC1012005 {
   NPCConversationManager cm
   int status = -2
   int sel = -1

   def start() {
      status = -2
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

         if (status == -1) {
            cm.sendNext(I18nMessage.from("1012005_BY_CHANCE"))
         } else if (status == 0) {
            cm.sendSimple(I18nMessage.from("1012005_WHAT_DO_YOU_WANT_TO_KNOW"))
         } else if (status == 1) {
            sel = selection
            if (selection == 0) {
               status = 3
               cm.sendNext(I18nMessage.from("1012005_KNOW_MORE_ABOUT_PETS"))
            } else if (selection == 1) {
               status = 6
               cm.sendNext(I18nMessage.from("1012005_COMMANDS"))
            } else if (selection == 2) {
               status = 11
               cm.sendNext(I18nMessage.from("1012005_DYING"))
            } else if (selection == 3) {
               cm.sendNext(I18nMessage.from("1012005_KITTY_COMMANDS"))
            } else if (selection == 4) {
               cm.sendNext(I18nMessage.from("1012005_BROWN_PUPPY_COMMANDS"))
            } else if (selection == 5) {
               cm.sendNext(I18nMessage.from("1012005_BUNNY_COMMANDS"))
            } else if (selection == 6) {
               cm.sendNext(I18nMessage.from("1012005_KARGO_COMMANDS"))
            } else if (selection == 7) {
               cm.sendNext(I18nMessage.from("1012005_RUDOLPH_COMMANDS"))
            } else if (selection == 8) {
               cm.sendNext(I18nMessage.from("1012005_BLACK_PIG_COMMANDS"))
            } else if (selection == 9) {
               cm.sendNext(I18nMessage.from("1012005_PANDA_COMMANDS"))
            } else if (selection == 10) {
               cm.sendNext(I18nMessage.from("1012005_HUSKY_COMMANDS"))
            } else if (selection == 11) {
               cm.sendNext(I18nMessage.from("1012005_DINO_COMMANDS"))
            } else if (selection == 12) {
               cm.sendNext(I18nMessage.from("1012005_MONKEY_COMMANDS"))
            } else if (selection == 13) {
               cm.sendNext(I18nMessage.from("1012005_TURKEY_COMMANDS"))
            } else if (selection == 14) {
               cm.sendNext(I18nMessage.from("1012005_WHITE_TIGER_COMMANDS"))
            } else if (selection == 15) {
               cm.sendNext(I18nMessage.from("1012005_PENGUIN"))
            } else if (selection == 16) {
               cm.sendNext(I18nMessage.from("1012005_GOLDEN_PIG"))
            } else if (selection == 17) {
               cm.sendNext(I18nMessage.from("1012005_ROBOT"))
            } else if (selection == 18) {
               cm.sendNext(I18nMessage.from("1012005_MINI_YETI"))
            } else if (selection == 19) {
               cm.sendNext(I18nMessage.from("1012005_JR_BALROG"))
            } else if (selection == 20) {
               cm.sendNext(I18nMessage.from("1012005_BABY_DRAGON"))
            } else if (selection == 21) {
               cm.sendNext(I18nMessage.from("1012005_COLORED_DRAGON"))
            } else if (selection == 22) {
               cm.sendNext(I18nMessage.from("1012005_BLACK_DRAGON"))
            } else if (selection == 23) {
               cm.sendNext(I18nMessage.from("1012005_JR_REAPER"))
            } else if (selection == 24) {
               cm.sendNext(I18nMessage.from("1012005_PORCUPINE"))
            } else if (selection == 25) {
               cm.sendNext(I18nMessage.from("1012005_SNOWMAN"))
            } else if (selection == 26) {
               cm.sendNext(I18nMessage.from("1012005_SKUNK"))
            } else if (selection == 27) {
               status = 14
               cm.sendNext(I18nMessage.from("1012005_TRANSFER_POINTS"))
            }
            if (selection > 2 && selection < 27) {
               cm.dispose()
            }
         } else if (status == 2) {
            if (sel == 0) {
               cm.sendNextPrev(I18nMessage.from("1012005_CANT_GIVE_TOO_MUCH_LIFE"))
            } else if (sel == 1) {
               cm.sendNextPrev(I18nMessage.from("1012005_TRY_HARD_RAISING_IT"))
            } else if (sel == 2) {
               cm.sendNextPrev(I18nMessage.from("1012005_AFTER_SOME_TIME"))
            } else if (sel == 27) {
               cm.sendYesNo(I18nMessage.from("1012005_DO_YOU_REALLY_WANT_TO_BUY"))
            }
         } else if (status == 3) {
            if (sel == 0) {
               cm.sendNextPrev(I18nMessage.from("1012005_OH_YEA_THEY_REACT"))
            } else if (sel == 1) {
               cm.sendNextPrev(I18nMessage.from("1012005_THEY_HAVE_HUNGER"))
            } else if (sel == 2) {
               cm.sendNextPrev(I18nMessage.from("1012005_SAD_TO_SEE_THEM_STOP"))
            } else if (sel == 27) {
               if (cm.getMeso() < 250000 || !cm.canHold(4160011)) {
                  cm.sendOk(I18nMessage.from("1012005_NEED_EMPTY_SLOT_AND_ENOUGH_MESOS"))
               } else {
                  cm.gainMeso(-250000)
                  cm.gainItem(4160011, (short) 1)
               }
               cm.dispose()
            }
         } else if (status == 4) {
            if (sel != 1) {
               cm.dispose()
            }
            cm.sendNextPrev(I18nMessage.from("1012005_NEED_PET_FOOD"))
         } else if (status == 5) {
            cm.sendNextPrev(I18nMessage.from("1012005_FEED_ON_REGULAR_BASIS"))
         } else {
            cm.dispose()
         }
      }
   }
}

NPC1012005 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1012005(cm: cm))
   }
   return (NPC1012005) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }