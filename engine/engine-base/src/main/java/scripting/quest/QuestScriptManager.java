package scripting.quest;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashMap;
import java.util.Map;
import javax.script.Invocable;
import javax.script.ScriptEngine;

import client.MapleClient;
import com.ms.logs.LogType;
import com.ms.logs.LoggerOriginator;
import com.ms.logs.LoggerUtil;
import constants.game.GameConstants;
import scripting.AbstractScriptManager;

public class QuestScriptManager extends AbstractScriptManager {

   private static QuestScriptManager instance = new QuestScriptManager();
   private Map<MapleClient, QuestActionManager> qms = new HashMap<>();
   private Map<MapleClient, ScriptEngine> scripts = new HashMap<>();

   public static QuestScriptManager getInstance() {
      return instance;
   }

   private ScriptEngine getQuestScriptEngine(MapleClient c, short questId) {
      ScriptEngine iv = getScriptEngine("quest/" + questId, c);
      if (iv == null && GameConstants.isMedalQuest(questId)) {
         iv = getScriptEngine("quest/medalQuest", c);   // start generic medal quest
      }

      return iv;
   }

   public void start(MapleClient c, short questId, int npc, boolean hasScriptRequirement) {
      try {
         QuestActionManager qm = new QuestActionManager(c, questId, npc, true);
         if (qms.containsKey(c)) {
            return;
         }
         if (c.canClickNPC()) {
            qms.put(c, qm);
            if (!hasScriptRequirement) {
               qm.dispose();
               return;
            }

            ScriptEngine iv = getQuestScriptEngine(c, questId);
            if (iv == null) {
               LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.QUEST_UNCODED, "START Quest " + questId + " is not coded.");
               qm.dispose();
               return;
            }

            iv.put("qm", qm);
            scripts.put(c, iv);
            c.setClickedNPC();
            ((Invocable) iv).invokeFunction("start", (byte) 1, (byte) 0, 0);
         }
      } catch (final UndeclaredThrowableException ute) {
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.QUEST, ute, String.format("QuestId [%d]", questId));
         dispose(c);
      } catch (final Throwable t) {
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.QUEST, t, String.format("QuestId [%d]", getQM(c).getQuestId()));
         dispose(c);
      }
   }

   public void start(MapleClient c, byte mode, byte type, int selection) {
      ScriptEngine iv = scripts.get(c);
      if (iv != null) {
         try {
            c.setClickedNPC();
            ((Invocable) iv).invokeFunction("start", mode, type, selection);
         } catch (final Throwable ute) {
            LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.QUEST, ute, String.format("QuestId [%d]", getQM(c).getQuestId()));
            dispose(c);
         }
      }
   }

   public void end(MapleClient c, short questId, int npc, boolean hasScriptRequirement) {
      if (!c.getPlayer().getMap().containsNPC(npc)) {
         dispose(c);
         return;
      }
      try {
         QuestActionManager qm = new QuestActionManager(c, questId, npc, false);
         if (qms.containsKey(c)) {
            return;
         }
         if (c.canClickNPC()) {
            qms.put(c, qm);
            if (!hasScriptRequirement) {
               qm.dispose();
               return;
            }

            ScriptEngine iv = getQuestScriptEngine(c, questId);
            if (iv == null) {
               LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.QUEST_UNCODED, "END Quest" + questId + " is not coded.");
               qm.dispose();
               return;
            }

            iv.put("qm", qm);
            scripts.put(c, iv);
            c.setClickedNPC();
            ((Invocable) iv).invokeFunction("end", (byte) 1, (byte) 0, 0);
         }
      } catch (final UndeclaredThrowableException ute) {
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.QUEST, ute, String.format("QuestId [%d]", questId));
         dispose(c);
      } catch (final Throwable t) {
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.QUEST, t, String.format("QuestId [%d]", getQM(c).getQuestId()));
         dispose(c);
      }
   }

   public void end(MapleClient c, byte mode, byte type, int selection) {
      ScriptEngine iv = scripts.get(c);
      if (iv != null) {
         try {
            c.setClickedNPC();
            ((Invocable) iv).invokeFunction("end", mode, type, selection);
         } catch (final Throwable ute) {
            LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.QUEST, ute, String.format("QuestId [%d]", getQM(c).getQuestId()));
            dispose(c);
         }
      }
   }

   public void dispose(QuestActionManager qm, MapleClient c) {
      qms.remove(c);
      scripts.remove(c);
      c.getPlayer().setNpcCoolDown(System.currentTimeMillis());
      resetContext("quest/" + qm.getQuestId(), c);
      c.getPlayer().flushDelayedUpdateQuests();
   }

   public void raiseOpen(MapleClient c, short questId, int npc) {
      try {
         QuestActionManager qm = new QuestActionManager(c, questId, npc, true);
         if (qms.containsKey(c)) {
            return;
         }
         if (c.canClickNPC()) {
            qms.put(c, qm);

            ScriptEngine iv = getQuestScriptEngine(c, questId);
            if (iv == null) {
               qm.dispose();
               return;
            }

            iv.put("qm", qm);
            scripts.put(c, iv);
            c.setClickedNPC();
            ((Invocable) iv).invokeFunction("raiseOpen");
         }
      } catch (final UndeclaredThrowableException ute) {
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.QUEST, ute, String.format("QuestId [%d]", questId));
         dispose(c);
      } catch (final Throwable t) {
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.QUEST, t, String.format("QuestId [%d]", getQM(c).getQuestId()));
         dispose(c);
      }
   }

   public void dispose(MapleClient c) {
      QuestActionManager qm = qms.get(c);
      if (qm != null) {
         dispose(qm, c);
      }
   }

   public QuestActionManager getQM(MapleClient c) {
      return qms.get(c);
   }

   public void reloadQuestScripts() {
      scripts.clear();
      qms.clear();
   }
}
