package npc
import tools.I18nMessage


import scripting.event.EventInstanceManager
import scripting.event.EventManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9040000 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   EventManager em

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def findLobby(guild) {
      for (Iterator<EventInstanceManager> iterator = em.getInstances().iterator(); iterator.hasNext();) {
         EventInstanceManager lobby = iterator.next()

         if (lobby.getIntProperty("guild") == guild) {
            if (lobby.getIntProperty("canJoin") == 1) {
               return lobby
            } else {
               return null
            }
         }
      }

      return null
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
            em = cm.getEventManager("GuildQuest")
            if (em == null) {
               cm.sendOk(I18nMessage.from("9040000_ENCOUNTERED_ERROR"))

               cm.dispose()
               return
            }

            cm.sendSimple(I18nMessage.from("9040000_GUILD_QUEST_INFO").with(em.getProperty("party")))

         } else if (status == 1) {
            sel = selection
            if (selection == 0) {
               if (!cm.isGuildLeader()) {
                  cm.sendOk(I18nMessage.from("9040000_MASTER_OR_JR_MASTER_MUST_REGISTER"))

                  cm.dispose()
               } else {
                  if (em.isQueueFull()) {
                     cm.sendOk(I18nMessage.from("9040000_QUEUE_FULL"))

                     cm.dispose()
                  } else {
                     int qsize = em.getQueueSize()
                     cm.sendYesNo(((qsize > 0) ? "There is currently #r" + qsize + "#k guilds queued on. " : "") + "Do you wish for your guild to join this queue?")
                  }
               }
            } else if (selection == 1) {
               if (cm.getPlayer().getGuildId() > 0) {
                  EventInstanceManager eim = findLobby(cm.getPlayer().getGuildId())
                  if (eim == null) {
                     cm.sendOk(I18nMessage.from("9040000_NOT_CURRENTLY"))

                  } else {
                     if (cm.isLeader()) {
                        em.getEligibleParty(cm.getParty().orElseThrow())
                        eim.registerParty(cm.getPlayer())
                     } else {
                        eim.registerPlayer(cm.getPlayer())
                     }
                  }
               } else {
                  cm.sendOk(I18nMessage.from("9040000_NEED_TO_BE_IN_GUILD"))

               }

               cm.dispose()
            } else {
               String reqStr = ""
               reqStr += "\r\n\r\n    Team requirements:\r\n\r\n"
               reqStr += "     - 1 team member #rbelow or equal level 30#k.\r\n"
               reqStr += "     - 1 team member who is a #rThief with Dark Sight#k skill and #rmaxed Haste#k.\r\n"
               reqStr += "     - 1 team member who is a Magician with #rmaxed Teleport#k.\r\n"
               reqStr += "     - 1 team member who is a #rlong ranged attacker#k like Bowman, Assassin, or Gunslinger.\r\n"
               reqStr += "     - 1 team member with #rgood jumping skills#k like Assassin with maxed Flash Jump or Gunslinger with Wings.\r\n"

               cm.sendOk("#e#b<Guild Quest: Sharenian Ruins>#k#n\r\n Team up with your guild members in an auspicious attempt to recover the Rubian from the skeleton's grasp, with teamwork overcoming many puzzles and challenges awaiting inside the Sharenian tombs. Great rewards can be obtained upon the instance completion, and Guild Points can be racked up for your Guild." + reqStr)
               cm.dispose()
            }
         } else if (status == 2) {
            if (sel == 0) {
               byte entry = em.addGuildToQueue(cm.getPlayer().getGuildId(), cm.getPlayer().getId())
               if (entry > 0) {
                  cm.sendOk(I18nMessage.from("9040000_REGISTERED_SUCCESSFULLY"))

               } else if (entry == ((byte) 0)) {
                  cm.sendOk(I18nMessage.from("9040000_QUEUE_FULL"))

               } else {
                  cm.sendOk(I18nMessage.from("9040000_ALREADY_QUEUED"))

               }
            }

            cm.dispose()
         }
      }
   }
}

NPC9040000 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9040000(cm: cm))
   }
   return (NPC9040000) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }