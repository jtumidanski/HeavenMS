package tools;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import client.MapleCharacter;
import client.MapleClient;

public class I18nMessage implements UserMessage {
   private Locale locale;

   private String template;

   private String baseName = "MessageBundle";

   private List<Object> args;

   public static I18nMessage from(String template) {
      return new I18nMessage(template);
   }

   protected I18nMessage(String template) {
      this.template = template;
      this.args = new ArrayList<>();
   }

   @Override
   public I18nMessage to(MapleCharacter character) {
      locale = character.getLocale();
      return this;
   }

   @Override
   public I18nMessage to(MapleClient client) {
      locale = client.getLocale();
      return this;
   }

   public I18nMessage with(Object arg) {
      args.add(arg);
      return this;
   }

   public I18nMessage with(Object... args) {
      this.args.addAll(Arrays.asList(args));
      return this;
   }

   @Override
   public String evaluate() {
      try {
         if (args == null || args.size() == 0) {
            return getResourceBundle().getString(template);
         } else {
            return getI18nString(args.toArray());
         }
      } catch (MissingResourceException exception) {
         return template;
      }
   }

   protected ResourceBundle getResourceBundle() {
      return ResourceBundle.getBundle(baseName, locale);
   }

   protected String getI18nString(Object[] args) {
      String i18nMessage = getResourceBundle().getString(template);
      MessageFormat messageFormat = new MessageFormat(i18nMessage, locale);
      return messageFormat.format(args);
   }
}
