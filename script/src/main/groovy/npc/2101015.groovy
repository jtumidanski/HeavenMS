package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager
import server.partyquest.AriantColiseum

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2101015 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   AriantColiseum arena

   def start() {
      arena = cm.getPlayer().getAriantColiseum()
      if (arena == null) {
         cm.sendOk(I18nMessage.from("2101015_WHAT_ARE_YOU_DOING_HERE"))
         cm.dispose()
         return
      }

      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0 && status == 0) {
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 0) {
            String[] options = ["I would like to check my battle points! / I would like to exchange (1) Palm Tree Beach Chair", "I would like to know more about the points of the Battle Arena."]
            String menuStr = generateSelectionMenu(options)
            cm.sendSimple("Hello, what I can do for you?\r\n\r\n" + menuStr)
         } else if (status == 1) {
            if (selection == 0) {
               int apqPoints = cm.getPlayer().getAriantPoints()
               if (apqPoints < 100) {
                  cm.sendOk(I18nMessage.from("2101015_YOUR_SCORE").with(apqPoints))
                  cm.dispose()
               } else if (apqPoints + arena.getAriantRewardTier(cm.getPlayer()) >= 100) {
                  cm.sendOk(I18nMessage.from("2101015_TALK_TO_MY_WIFE").with(apqPoints))
                  cm.dispose()
               } else {
                  cm.sendNext(I18nMessage.from("2101015_LET_US_TRADE"))
               }
            } else if (selection == 1) {
               cm.sendOk(I18nMessage.from("2101015_MAIN_OBJECTIVE"))
               cm.dispose()
            }
         } else if (status == 2) {
            cm.getPlayer().gainAriantPoints(-100)
            cm.gainItem(3010018, (short) 1)
            cm.dispose()
         }
      }
   }

   static def generateSelectionMenu(String[] array) {     // nice tool for generating a string for the sendSimple functionality
      String menu = ""
      for (int i = 0; i < array.length; i++) {
         menu += "#L" + i + "##b" + array[i] + "#l#k\r\n"
      }
      return menu
   }
}

NPC2101015 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2101015(cm: cm))
   }
   return (NPC2101015) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }