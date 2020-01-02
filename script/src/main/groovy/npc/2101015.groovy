package npc


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
         cm.sendOk("Hey, I did not see you on the field during the battle in the arena! What are you doing here?")
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
                  cm.sendOk("Your Battle Arena score: #b" + apqPoints + "#k points. You need to surpass #b100 points#k so that I can give you the #bPalm Tree Beach Chair#k. Talk to me again when you have enough points.")
                  cm.dispose()
               } else if (apqPoints + arena.getAriantRewardTier(cm.getPlayer()) >= 100) {
                  cm.sendOk("Your Battle Arena score: #b" + apqPoints + "#k points and you practically already have that score! Talk to my wife, #p2101016#to get them and then re-chat with me!")
                  cm.dispose()
               } else {
                  cm.sendNext("Wow, it looks like you got the #b100#k points ready to trade, let's trade?!")
               }
            } else if (selection == 1) {
               cm.sendOk("The main objective of the Battle Arena is to allow the player to accumulate points so that they can be traded honorably for the highest prize: the #bPalm Tree Beach Chair#k. Collect points during the battles and talk to me when it's time to get the prize. In each battle, the player is given the opportunity to score points based on the amount of jewelry that the player has at the end. But be careful! If your points distance from other players #ris too high#k, this will have been all for nothing and you will earn mere #r1 point#k only.")
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