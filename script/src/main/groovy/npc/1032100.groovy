package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1032100 {
   NPCConversationManager cm
   int status = 0
   int selected = -1
   String item

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (status == 1 && mode == 0) {
            cm.dispose()
            return
         } else if (status == 2 && mode == 0) {
            cm.sendNext(I18nMessage.from("1032100_GET_THE_MATERIALS_READY").with(item))
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 0) {
            if (cm.getLevel() >= 40) {
               cm.sendNext(I18nMessage.from("1032100_HELLO"))
            } else {
               cm.sendOk(I18nMessage.from("1032100_HELLO_MORE"))
               cm.dispose()
            }
         } else if (status == 1) {
            cm.sendSimple(I18nMessage.from("1032100_WHAT_DO_YOU_WANT_TO_MAKE"))
         } else if (status == 2) {
            selected = selection
            if (selection == 0) {
               item = "Moon Rock"
               cm.sendYesNo(I18nMessage.from("1032100_MOON_ROCK"))
            } else if (selection == 1) {
               item = "Star Rock"
               cm.sendYesNo(I18nMessage.from("1032100_STAR_ROCK"))
            } else if (selection == 2) {
               item = "Black Feather"
               cm.sendYesNo(I18nMessage.from("1032100_BLACK_FEATHER"))
            }
         } else if (status == 3) {
            if (selected == 0) {
               if (cm.haveItem(4011000) && cm.haveItem(4011001) && cm.haveItem(4011002) && cm.haveItem(4011003) && cm.haveItem(4011004) && cm.haveItem(4011005) && cm.haveItem(4011006) && cm.getMeso() >= 10000) {
                  cm.gainMeso(-10000)
                  for (int i = 4011000; i < 4011007; i++) {
                     cm.gainItem(i, (short) -1)
                  }
                  cm.gainItem(4011007, (short) 1)
                  cm.sendNext(I18nMessage.from("1032100_SUCCESS").with(item))
               } else {
                  cm.sendNext(I18nMessage.from("1032100_NOT_ENOUGH_MESOS"))
               }
            } else if (selected == 1) {
               if (cm.haveItem(4021000) && cm.haveItem(4021001) && cm.haveItem(4021002) && cm.haveItem(4021003) && cm.haveItem(4021004) && cm.haveItem(4021005) && cm.haveItem(4021006) && cm.haveItem(4021007) && cm.haveItem(4021008) && cm.getMeso() >= 15000) {
                  cm.gainMeso(-15000)
                  for (int j = 4021000; j < 4021009; j++) {
                     cm.gainItem(j, (short) -1)
                  }
                  cm.gainItem(4021009, (short) 1)
                  cm.sendNext(I18nMessage.from("1032100_SUCCESS").with(item))
               } else {
                  cm.sendNext(I18nMessage.from("1032100_NOT_ENOUGH_MESOS"))
               }
            } else if (selected == 2) {
               if (cm.haveItem(4001006) && cm.haveItem(4011007) && cm.haveItem(4021008) && cm.getMeso() >= 30000) {
                  cm.gainMeso(-30000)
                  for (int k = 4001006; k < 4021009; k += 10001) {
                     cm.gainItem(k, (short) -1)
                  }
                  cm.gainItem(4031042, (short) 1)
                  cm.sendNext(I18nMessage.from("1032100_SUCCESS").with(item))
               } else {
                  cm.sendNext(I18nMessage.from("1032100_NOT_ENOUGH_MESOS"))
               }
            }
            cm.dispose()
         }
      }
   }
}

NPC1032100 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1032100(cm: cm))
   }
   return (NPC1032100) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }