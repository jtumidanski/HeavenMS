package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201050 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int minLevel = 10

   def start() {
      status = -1
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
         if (status == 0 && mode == 1) {
            if (cm.isQuestCompleted(4911)) {
               cm.sendNext(I18nMessage.from("9201050_GOOD_JOB"))
               cm.dispose()
            } else if (cm.isQuestCompleted(4900) || cm.isQuestStarted(4900)) {
               cm.sendNext(I18nMessage.from("9201050_PAY_ATTENTION"))
               cm.dispose()
            } else {
               String selStr = "What up! Name's Icebyrd Slimm, mayor of New Leaf City! Happy to see you accepted my invite. So, what can I do for you?#b"
               String[] info = ["What is this place?", "Who is Professor Foxwit?", "What's a Foxwit Door?", "Where are the MesoGears?", "What is the Krakian Jungle?", "What's a Gear Portal?", "What do the street signs mean?", "What's the deal with Jack Masque?", "Lita Lawless looks like a tough cookie, what's her story?", "When will new boroughs open up in the city?", "I want to take the quiz!"]
               for (int i = 0; i < info.length; i++) {
                  selStr += "\r\n#L" + i + "# " + info[i] + "#l"
               }
               cm.sendSimple(selStr)
            }
         } else if (status == 1) {
            switch (selection) {
               case 0:
                  cm.sendNext(I18nMessage.from("9201050_ALWAYS_DREAMED"))
                  status -= 2
                  break
               case 1:
                  cm.sendNext(I18nMessage.from("9201050_PRETTY_SPRY"))
                  status -= 2
                  break
               case 2:
                  cm.sendNext(I18nMessage.from("9201050_PRESSING_UP"))
                  status -= 2
                  break
               case 3:
                  cm.sendNext(I18nMessage.from("9201050_MONSTER_INFESTED"))
                  status -= 2
                  break
               case 4:
                  cm.sendNext(I18nMessage.from("9201050_OUTSKIRTS"))
                  status -= 2
                  break
               case 5:
                  cm.sendNext(I18nMessage.from("9201050_ANCIENT_TECH"))
                  status -= 2
                  break
               case 6:
                  cm.sendNext(I18nMessage.from("9201050_WE_ARE_ALWAYS_BUILDING"))
                  status -= 2
                  break
               case 7:
                  cm.sendNext(I18nMessage.from("9201050_TOO_COOL_FOR_SCHOOL"))
                  status -= 2
                  break
               case 8:
                  cm.sendNext(I18nMessage.from("9201050_REKINDLED_FRIENDSHIP"))
                  status -= 2
                  break
               case 9:
                  cm.sendNext(I18nMessage.from("9201050_HARD_AT_WORK"))
                  status -= 2
                  break
               case 10:
                  if (cm.getLevel() >= minLevel) {
                     cm.sendNext(I18nMessage.from("9201050_NO_PROBLEM"))
                     cm.startQuest(4900)
                  } else {
                     cm.sendNext(I18nMessage.from("9201050_EXPLORE_A_BIT_MORE"))
                  }

                  cm.dispose()
                  break
            }
         }
      }
   }
}

NPC9201050 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201050(cm: cm))
   }
   return (NPC9201050) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }