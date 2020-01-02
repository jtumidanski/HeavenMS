package scripting;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import client.MapleClient;
import tools.FilePrinter;

public abstract class AbstractScriptManager {

   private ScriptEngineFactory sef;

   protected AbstractScriptManager() {
      sef = new ScriptEngineManager().getEngineByName("groovy").getFactory();
   }

   protected ScriptEngine getScriptEngine(String path) {
      path = "script/src/main/groovy/" + path;
      File scriptFile = null;
      if (new File(path + ".groovy").exists()) {
         scriptFile = new File(path + ".groovy");
      }
      if (scriptFile == null) {
         return null;
      }

      ScriptEngine engine = sef.getScriptEngine();
      try (FileReader fr = new FileReader(scriptFile)) {
         engine.eval(fr);
      } catch (final ScriptException | IOException t) {
         FilePrinter.printError(FilePrinter.INVOCABLE + path.substring(12), t, path);
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
