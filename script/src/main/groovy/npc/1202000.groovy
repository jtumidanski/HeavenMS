package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1202000 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (cm.getPlayer().getMapId() == 140090000) {
            if (!cm.containsAreaInfo((short) 21019, "helper=clear")) {
               if (status == 0) {
                  cm.sendNext("You've finally awoken...!", (byte) 8)
               } else if (status == 1) {
                  cm.sendNextPrev("And you are...?", (byte) 2)
               } else if (status == 2) {
                  cm.sendNextPrev("The hero who fought against the Black Magician... I've been waiting for you to wake up!", (byte) 8)
               } else if (status == 3) {
                  cm.sendNextPrev("Who... Who are you? And what are you talking about?", (byte) 2)
               } else if (status == 4) {
                  cm.sendNextPrev("And who am I...? I can't remember anything... Ouch, my head hurts!", (byte) 2)
               } else if (status == 5) {
                  cm.showIntro("Effect/Direction1.img/aranTutorial/face")
                  cm.showIntro("Effect/Direction1.img/aranTutorial/ClickLilin")
                  cm.updateAreaInfo((short) 21019, "helper=clear")
                  cm.dispose()
               }
            } else {
               if (status == 0) {
                  cm.sendNextPrev("Are you alright?", (byte) 8)
               } else if (status == 1) {
                  cm.sendNextPrev("I can't remember anything. Where am I? And who are you...?", (byte) 2)
               } else if (status == 2) {
                  cm.sendNextPrev("Stay calm. There is no need to panic. You can't remember anything because the curse of the Black Magician erased your memory. I'll tell you everything you need to know...step by step.", (byte) 8)
               } else if (status == 3) {
                  cm.sendNextPrev("You're a hero who fought the Black Magician and saved Maple World hundreds of years ago. But at the very last moment, the curse of the Black Mage put you to sleep for a long, long time. That's when you lost all of your memories.", (byte) 8)
               } else if (status == 4) {
                  cm.sendNextPrev("This island is called Rien, and it's where the Black Magician trapped you. Despite its name, this island is always covered in ice and snow because of the Black Magician's curse. You were found deep inside the Ice Cave.", (byte) 8)
               } else if (status == 5) {
                  cm.sendNextPrev("My name is Lilin and I belong to the clan of Rien. The Rien Clan has been waiting for a hero to return for a long time now, and we finally found you. You've finally returned!", (byte) 8)
               } else if (status == 6) {
                  cm.sendNextPrev("I've said too much. It's okay if you don't really understand everything I just told you. You'll get it eventually. For now, #byou should head to town#k. I'll stay by your side and help you until you get there.", (byte) 8)
               } else if (status == 7) {
                  cm.spawnGuide()
                  cm.warp(140090100, 0)
                  cm.dispose()
               }
            }
         } else {
            if (status == 0) {
               cm.sendSimple("Is there anything you're still curious about? If so, I'll try to explain it better. #b#l\r\n#L0#Who am I? #l #l\r\n#L1#Where am I? #l #l\r\n#L2#Who are you?#l#l\r\n#L3#Tell me what I have to do.#l #l\r\n#L4#Tell me about my Inventory.#l #l\r\n#L5#How do I advance my skills?#l #l\r\n#L6#I want to know how to equip items.#l #l\r\n#L7#How do I use quick slots? #l #l\r\n#L8#How can I open breakable containers?#l #l\r\n#L9#I want to sit in a chair but I forgot how.#l#k")
            } else if (status == 1) {
               if (selection == 0) {
                  cm.sendNext("You are one of the heroes that saved Maple World from the Black Magician hundreds of years ago. You've lost your memory due to the curse of the Black Mage.")
                  cm.dispose()
               } else if (selection == 1) {
                  cm.sendNext("This island is called Rien, and this is where the Black Magician's curse put you to sleep. It's a small island covered in ice and snow, and the majority of the residents are Penguins.")
                  cm.dispose()
               } else if (selection == 2) {
                  cm.sendNext("I'm Lilin, a clan member of Rien, and I've been waiting for your return as the prophecy foretold. I'll be your guide for now.")
                  cm.dispose()
               } else if (selection == 3) {
                  cm.sendNext("Let's not waste any more time and just get to town. I'll give you the details when we get there.")
                  cm.dispose()
               } else if (selection == 4) {
                  cm.guideHint(14)
                  cm.dispose()
               } else if (selection == 5) {
                  cm.guideHint(15)
                  cm.dispose()
               } else if (selection == 6) {
                  cm.guideHint(16)
                  cm.dispose()
               } else if (selection == 7) {
                  cm.guideHint(17)
                  cm.dispose()
               } else if (selection == 8) {
                  cm.guideHint(18)
                  cm.dispose()
               } else if (selection == 9) {
                  cm.guideHint(19)
                  cm.dispose()
               }
            }
         }
      }
   }
}

NPC1202000 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1202000(cm: cm))
   }
   return (NPC1202000) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }