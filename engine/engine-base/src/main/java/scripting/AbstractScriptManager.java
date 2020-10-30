package scripting;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import client.MapleClient;
import com.ms.logs.LogType;
import com.ms.logs.LoggerOriginator;
import com.ms.logs.LoggerUtil;

public abstract class AbstractScriptManager {
   private final ScriptEngineFactory sef;

   protected AbstractScriptManager() {
      sef = new ScriptEngineManager().getEngineByName("groovy").getFactory();
   }

   protected ScriptEngine getScriptEngine(String path) {
      ScriptEngine engine = sef.getScriptEngine();
      engine = evalPrerequisites(engine, getPrerequisites());
      return eval(engine, path);
   }

   protected String[] getPrerequisites() {
      return new String[0];
   }

   protected ScriptEngine evalPrerequisites(ScriptEngine engine, String... paths) {
      ScriptEngine primedEngine = engine;
      for (String path : paths) {
         primedEngine = eval(primedEngine, path);
         if (primedEngine == null) {
            return null;
         }
      }
      return primedEngine;
   }

   protected ScriptEngine eval(ScriptEngine engine, String path) {
      path = "script/src/main/groovy/" + path;
      File scriptFile = null;
      if (new File(path + ".groovy").exists()) {
         scriptFile = new File(path + ".groovy");
      }
      if (scriptFile == null) {
         return null;
      }

      try (FileReader fr = new FileReader(scriptFile)) {
         engine.eval(fr);
      } catch (final ScriptException | IOException t) {
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.INVOCABLE, t, path);
         return null;
      }
      return engine;
   }

   protected ScriptEngine getScriptEngine(String path, MapleClient c) {
      String cachePath = "script/src/main/groovy/" + path;
      ScriptEngine engine = c.getScriptEngine(cachePath);

      if (engine == null) {
         engine = getScriptEngine(path);
         c.setScriptEngine(path, engine);
      }

      return engine;
   }

   protected void resetContext(String path, MapleClient c) {
      c.removeScriptEngine("script/src/main/groovy/" + path + ".groovy");
   }
}
