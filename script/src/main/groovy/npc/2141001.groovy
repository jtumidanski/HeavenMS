package npc

import client.MapleCharacter
import scripting.event.EventInstanceManager
import scripting.event.EventManager
import scripting.npc.NPCConversationManager
import server.expeditions.MapleExpedition
import server.expeditions.MapleExpeditionType
import tools.MessageBroadcaster
import tools.ServerNoticeType

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2141001 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   MapleExpedition expedition
   List<Map.Entry<Integer, String>> expeditionMembers
   MapleCharacter player
   EventManager em
   MapleExpeditionType expeditionType = MapleExpeditionType.PINK_BEAN
   String expeditionName = "Twilight of the Gods"
   String expeditionBoss = "Pink Bean"
   String expeditionMap = "Twilight of Gods"

   String list = "What would you like to do?#b\r\n\r\n#L1#View current Expedition members#l\r\n#L2#Start the fight!#l\r\n#L3#Stop the expedition.#l"

   def start() {
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {

      player = cm.getPlayer()
      expedition = cm.getExpedition(expeditionType)
      em = cm.getEventManager("PinkBeanBattle")

      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0) {
            cm.dispose()
            return
         }

         if (status == 0) {
            if (player.getLevel() < expeditionType.getMinLevel() || player.getLevel() > expeditionType.getMaxLevel()) {
               cm.sendOk("You do not meet the criteria to battle " + expeditionBoss + "!")
               cm.dispose()
            } else if (expedition == null) { //Start an expedition
               cm.sendSimple("#e#b<Expedition: " + expeditionName + ">\r\n#k#n" + em.getProperty("party") + "\r\n\r\nWould you like to assemble a team to take on #r" + expeditionBoss + "#k?\r\n#b#L1#Lets get this going!#l\r\n#L2#No, I think I'll wait a bit...#l")
               status = 1
            } else if (expedition.isLeader(player)) { //If you're the leader, manage the expedition
               if (expedition.isInProgress()) {
                  cm.sendOk("Your expedition is already in progress, for those who remain battling lets pray for those brave souls.")
                  cm.dispose()
               } else {
                  cm.sendSimple(list)
                  status = 2
               }
            } else if (expedition.isRegistering()) { //If the expedition is registering
               if (expedition.contains(player)) { //If you're in it but it hasn't started, be patient
                  cm.sendOk("You have already registered for the expedition. Please wait for #r" + expedition.getLeader().getName() + "#k to begin it.")
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
                     cm.sendOk("Your expedition already started the battle against " + expeditionBoss + ". Lets pray for those brave souls.")
                  }

                  cm.dispose()
               } else { //If you're not in by now, tough luck
                  cm.sendOk("Another expedition has taken the initiative to challenge " + expeditionBoss + ", lets pray for those brave souls.")
                  cm.dispose()
               }
            }
         } else if (status == 1) {
            if (selection == 1) {
               expedition = cm.getExpedition(expeditionType)
               if (expedition != null) {
                  cm.sendOk("Someone already taken the initiative to be the leader of the expedition. Try joining them!")
                  cm.dispose()
                  return
               }

               int res = cm.createExpedition(expeditionType)
               if (res == 0) {
                  cm.sendOk("The #r" + expeditionBoss + " Expedition#k has been created.\r\n\r\nTalk to me again to view the current team, or start the fight!")
               } else if (res > 0) {
                  cm.sendOk("Sorry, you've already reached the quota of attempts for this expedition! Try again another day...")
               } else {
                  cm.sendOk("An unexpected error has occurred when starting the expedition, please try again later.")
               }

               cm.dispose()
            } else if (selection == 2) {
               cm.sendOk("Sure, not everyone's up to challenging " + expeditionBoss + ".")
               cm.dispose()
            }
         } else if (status == 2) {
            if (selection == 1) {
               if (expedition == null) {
                  cm.sendOk("The expedition could not be loaded.")
                  cm.dispose()
                  return
               }
               expeditionMembers = expedition.getMemberList()
               int size = expeditionMembers.size()
               if (size == 1) {
                  cm.sendOk("You are the only member of the expedition.")
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
                  cm.sendOk("You need at least " + min + " players registered in your expedition.")
                  cm.dispose()
                  return
               }

               cm.sendOk("The expedition will begin and you will now be escorted to the #b" + expeditionMap + "#k.")
               status = 4
            } else if (selection == 3) {
               MessageBroadcaster.getInstance().sendMapServerNotice(player.getMap(), ServerNoticeType.LIGHT_BLUE, expedition.getLeader().getName() + " has ended the expedition.")
               cm.endExpedition(expedition)
               cm.sendOk("The expedition has now ended. Sometimes the best strategy is to run away.")
               cm.dispose()
            }
         } else if (status == 4) {
            if (em == null) {
               cm.sendOk("The event could not be initialized, please report this on the forum.")
               cm.dispose()
               return
            }
            em.setProperty("leader", player.getName())
            em.setProperty("channel", player.getClient().getChannel())
            if (!em.startInstance(expedition)) {
               cm.sendOk("Another expedition has taken the initiative to challenge " + expeditionBoss + ", lets pray for those brave souls.")
               cm.dispose()
               return
            }
            cm.dispose()
         } else if (status == 6) {
            if (selection > 0) {
               Map.Entry<Integer, String> banned = expeditionMembers.get(selection - 1)
               expedition.ban(banned)
               cm.sendOk("You have banned " + banned.getValue() + " from the expedition.")
               cm.dispose()
            } else {
               cm.sendSimple(list)
               status = 2
            }
         }
      }
   }
}

NPC2141001 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2141001(cm: cm))
   }
   return (NPC2141001) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }