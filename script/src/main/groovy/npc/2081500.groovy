package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2081500 {
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
            if (cm.getLevel() < 120 || Math.floor(cm.getJobId() / 100) != 5) {
               cm.sendOk("Please don't bother me right now, I am trying to concentrate.")
               cm.dispose()
            } else if (!cm.isQuestCompleted(6944)) {
               cm.sendOk("You have not yet passed my trials. I can not advance you until you do so.")
               cm.dispose()
            } else if (cm.getJobId() % 100 % 10 != 2) {
               cm.sendYesNo("You did a marvellous job passing my test. Are you ready to advance to your 4th job?")
            } else {
               cm.sendSimple("If I must, I can teach you the art of your class.\r\n#b#L0#Teach me the skills of my class.#l")
               //cm.dispose();
            }
         } else if (status == 1) {
            if (mode >= 1 && cm.getJobId() % 100 % 10 != 2) {
               if (cm.canHold(2280003, 1)) {
                  cm.changeJobById(cm.getJobId() + 1)
                  if (cm.getJobId() == 512) {
                     cm.teachSkill(5121001, (byte) 0, (byte) 10, -1)
                     cm.teachSkill(5121002, (byte) 0, (byte) 10, -1)
                     cm.teachSkill(5121007, (byte) 0, (byte) 10, -1)
                     cm.teachSkill(5121009, (byte) 0, (byte) 10, -1)
                  } else if (cm.getJobId() == 522) {
                     cm.teachSkill(5220001, (byte) 0, (byte) 10, -1)
                     cm.teachSkill(5220002, (byte) 0, (byte) 10, -1)
                     cm.teachSkill(5221004, (byte) 0, (byte) 10, -1)
                     cm.teachSkill(5220011, (byte) 0, (byte) 10, -1)
                  }
                  cm.gainItem(2280003, (short) 1)
               } else {
                  cm.sendOk("Please have one slot available on #bUSE#k inventory to receive a skill book.")
               }
            } else if (mode >= 1 && cm.getJobId() % 100 % 10 == 2) {
               if (cm.getJobId() == 512) {
                  if (cm.getPlayer().getSkillLevel(5121003) == 0) {
                     cm.teachSkill(5121003, (byte) 0, (byte) 10, -1)
                  }
                  if (cm.getPlayer().getSkillLevel(5121004) == 0) {
                     cm.teachSkill(5121004, (byte) 0, (byte) 10, -1)
                  }
                  if (cm.getPlayer().getSkillLevel(5121005) == 0) {
                     cm.teachSkill(5121005, (byte) 0, (byte) 10, -1)
                  }
                  if (cm.getPlayer().getSkillLevel(5121010) == 0) {
                     cm.teachSkill(5121010, (byte) 0, (byte) 10, -1)
                  }
               } else if (cm.getJobId() == 522) {
                  if (cm.getPlayer().getSkillLevel(5221006) == 0) {
                     cm.teachSkill(5221006, (byte) 0, (byte) 10, -1)
                  }
                  if (cm.getPlayer().getSkillLevel(5221007) == 0) {
                     cm.teachSkill(5221007, (byte) 0, (byte) 10, -1)
                  }
                  if (cm.getPlayer().getSkillLevel(5221008) == 0) {
                     cm.teachSkill(5221008, (byte) 0, (byte) 10, -1)
                  }
                  if (cm.getPlayer().getSkillLevel(5221009) == 0) {
                     cm.teachSkill(5221009, (byte) 0, (byte) 10, -1)
                  }
                  if (cm.getPlayer().getSkillLevel(5221003) == 0) {
                     cm.teachSkill(5221003, (byte) 0, (byte) 10, -1)
                  }
               }
               cm.sendOk("It is done. Leave me now.")
            }

            cm.dispose()
         }
      }
   }
}

NPC2081500 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2081500(cm: cm))
   }
   return (NPC2081500) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }