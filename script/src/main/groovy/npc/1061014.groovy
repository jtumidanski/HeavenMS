package npc

import client.MapleCharacter
import scripting.event.EventInstanceManager
import scripting.event.EventManager
import scripting.npc.NPCConversationManager
import server.expeditions.MapleExpedition
import server.expeditions.MapleExpeditionType
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1061014 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   MapleExpedition expedition
   List<Map.Entry<Integer, String>> expeditionMembers
   MapleCharacter player
   EventManager em
   MapleExpeditionType expeditionType = MapleExpeditionType.BALROG_NORMAL
   String expeditionName = "Balrog"
   String expeditionBoss = "Balrog"
   String expeditionMap = "Balrog's Tomb"

   String list = "What would you like to do?#b\r\n\r\n#L1#View current Expedition members#l\r\n#L2#Start the fight!#l\r\n#L3#Stop the expedition.#l"

   def start() {
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      player = cm.getPlayer()
      expedition = cm.getExpedition(expeditionType)
      em = cm.getEventManager("BalrogBattle")

      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0) {
            cm.dispose()
            return
         }

         if (status == 0) {
            if (player.getLevel() < expeditionType.getMinLevel() || player.getLevel() > expeditionType.getMaxLevel()) {
               cm.sendOk(I18nMessage.from("1061014_DO_NOT_MEET_CRITERIA").with(expeditionBoss))
               cm.dispose()
            } else if (expedition == null) { //Start an expedition
               cm.sendSimple(I18nMessage.from("1061014_HELLO").with(expeditionName, em.getProperty("party"), expeditionBoss))
               status = 1
            } else if (expedition.isLeader(player)) { //If you're the leader, manage the expedition
               if (expedition.isInProgress()) {
                  cm.sendOk(I18nMessage.from("1061014_EXPEDITION_ALREADY_IN_PROGRESS"))
                  cm.dispose()
               } else {
                  cm.sendSimple(list)
                  status = 2
               }
            } else if (expedition.isRegistering()) { //If the expedition is registering
               if (expedition.contains(player)) { //If you're in it but it hasn't started, be patient
                  cm.sendOk(I18nMessage.from("1061014_ALREADY_REGISTERED").with(expedition.getLeader().getName()))
                  cm.dispose()
               } else { //If you aren't in it, you're going to get added
                  cm.sendOk(expedition.addMember(cm.getPlayer()))
                  cm.dispose()
               }
            } else if (expedition.isInProgress()) { //Only if the expedition is in progress
               if (expedition.contains(player)) { //If you're registered, warp you in
                  EventInstanceManager eim = em.getInstance(expeditionName + player.getClient().getChannel())
                  if (eim.getIntProperty("canJoin") == 1) {
                     eim.registerPlayer(player)
                  } else {
                     cm.sendOk(I18nMessage.from("1061014_BATTLE_ALREADY_STARTED").with(expeditionBoss))
                  }

                  cm.dispose()
               } else { //If you're not in by now, tough luck
                  cm.sendOk(I18nMessage.from("1061014_ANOTHER_EXPEDITION_HAS_STARTED").with(expeditionBoss))
                  cm.dispose()
               }
            }
         } else if (status == 1) {
            if (selection == 1) {
               expedition = cm.getExpedition(expeditionType)
               if (expedition != null) {
                  cm.sendOk(I18nMessage.from("1061014_SOMEONE_ALREADY_THE_LEADER"))
                  cm.dispose()
                  return
               }

               int res = cm.createExpedition(expeditionType)
               if (res == 0) {
                  cm.sendOk(I18nMessage.from("1061014_EXPEDITION_CREATED").with(expeditionBoss))
               } else if (res > 0) {
                  cm.sendOk(I18nMessage.from("1061014_ATTEMPT_LIMIT"))
               } else {
                  cm.sendOk(I18nMessage.from("1061014_UNEXPECTED_ERROR_ON_START"))
               }

               cm.dispose()
            } else if (selection == 2) {
               cm.sendOk(I18nMessage.from("1061014_NOT_EVERYONE_IS_UP_TO_CHALLENGING").with(expeditionBoss))
               cm.dispose()
            } else {
               cm.sendSimple("Hi there. I am #b#nMu Young#n#k, the temple Keeper. This temple is currently under siege by the Balrog troops. We currently do not know who gave the orders. " +
                     "For a few weeks now, the #e#b Order of the Altair#n#k has been sending mercenaries, but they were eliminated every time." +
                     " So, traveler, would you like to try your luck at defeating this unspeakable horror?\r\n  #L1#What is the #eOrder of the Altair?")
               status = 10
            }
         } else if (status == 2) {
            if (selection == 1) {
               if (expedition == null) {
                  cm.sendOk(I18nMessage.from("1061014_EXPEDITION_COULD_NOT_BE_LOADED"))
                  cm.dispose()
                  return
               }
               expeditionMembers = expedition.getMemberList()
               int size = expeditionMembers.size()
               if (size == 1) {
                  cm.sendOk(I18nMessage.from("1061014_ONLY_MEMBER_OF_EXPEDITION"))
                  cm.dispose()
                  return
               }
               String text = "The following members make up your expedition (Click on them to expel them):\r\n"
               text += "\r\n\t\t1." + expedition.getLeader().getName()
               for (int i = 1; i < size; i++) {
                  text += "\r\n#b#L" + (i + 1) + "#" + (i + 1) + ". " + expeditionMembers.get(i).getValue() + "#l\n"
               }
               cm.sendSimple(text)
               status = 6
            } else if (selection == 2) {
               int min = expeditionType.getMinSize()
               int size = expedition.getMemberList().size()
               if (size < min) {
                  cm.sendOk(I18nMessage.from("1061014_MINIMUM_PLAYERS").with(min))
                  cm.dispose()
                  return
               }

               cm.sendOk(I18nMessage.from("1061014_SUCCESS").with(expeditionMap))
               status = 4
            } else if (selection == 3) {
               MessageBroadcaster.getInstance().sendMapServerNotice(player.getMap(), ServerNoticeType.LIGHT_BLUE, I18nMessage.from("EXPEDITION_ENDED_BY").with(expedition.getLeader().getName()))
               cm.endExpedition(expedition)
               cm.sendOk(I18nMessage.from("1061014_EXPEDITION_ENDED"))
               cm.dispose()
            }
         } else if (status == 4) {
            if (em == null) {
               cm.sendOk(I18nMessage.from("1061014_EVENT_COULD_NOT_BE_LOADED"))
               cm.dispose()
               return
            }

            em.setProperty("leader", player.getName())
            em.setProperty("channel", player.getClient().getChannel())
            if (!em.startInstance(expedition)) {
               cm.sendOk(I18nMessage.from("1061014_ANOTHER_EXPEDITION_HAS_STARTED").with(expeditionBoss))
               cm.dispose()
               return
            }

            cm.dispose()
         } else if (status == 6) {
            if (selection > 0) {
               Map.Entry<Integer, String> banned = expeditionMembers.get(selection - 1)
               expedition.ban(banned)
               cm.sendOk(I18nMessage.from("1061014_YOU_HAVE_BANNED").with(banned.getValue()))
               cm.dispose()
            } else {
               cm.sendSimple(list)
               status = 2
            }
         } else if (status == 10) {
            cm.sendOk(I18nMessage.from("1061014_ORDER_OF_ALTAIR"))
            cm.dispose()
         }
      }
   }
}

NPC1061014 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1061014(cm: cm))
   }
   return (NPC1061014) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }