package provider;

import java.awt.Point;
import java.awt.image.BufferedImage;

import provider.wz.MapleDataType;

public class MapleDataTool {
   public static String getString(MapleData data) {
      return ((String) data.getData());
   }

   public static String getString(MapleData data, String def) {
      if (data == null || data.getData() == null) {
         return def;
      } else {
         return ((String) data.getData());
      }
   }

   public static String getString(String path, MapleData data) {
      return getString(data.getChildByPath(path));
   }

   public static String getString(String path, MapleData data, String def) {
      return getString(data.getChildByPath(path), def);
   }

   public static double getDouble(MapleData data) {
      return (Double) data.getData();
   }

   public static float getFloat(MapleData data) {
      return (Float) data.getData();
   }

   public static int getInt(MapleData data) {
      if (data == null || data.getData() == null) {
         return 0;// DEF?
      }
      return (Integer) data.getData();
   }

   public static int getInt(String path, MapleData data) {
      return getInt(data.getChildByPath(path));
   }

   public static int getIntConvert(MapleData data) {
      if (data.getType() == MapleDataType.STRING) {
         return Integer.parseInt(getString(data));
      } else {
         return getInt(data);
      }
   }

   public static int getIntConvert(MapleData data, int def) {
      if (data == null) {
         return def;
      }
      if (data.getType() == MapleDataType.STRING) {
         String dd = getString(data);
         if (dd.endsWith("%")) {
            dd = dd.substring(0, dd.length() - 1);
         }
         try {
            return Integer.parseInt(dd);
         } catch (NumberFormatException nfe) {
            return def;
         }
      } else {
         return getInt(data, def);
      }
   }

   public static int getIntConvert(String path, MapleData data) {
      MapleData d = data.getChildByPath(path);
      if (d.getType() == MapleDataType.STRING) {
         return Integer.parseInt(getString(d));
      } else {
         return getInt(d);
      }
   }

   public static int getInt(MapleData data, int def) {
      if (data == null || data.getData() == null) {
         return def;
      } else if (data.getType() == MapleDataType.STRING) {
         return Integer.parseInt(getString(data));
      } else {
         Object numData = data.getData();
         if (numData instanceof Integer) {
            return (Integer) numData;
         } else {
            return (Short) numData;
         }
      }
   }

   public static int getInt(String path, MapleData data, int def) {
      return getInt(data.getChildByPath(path), def);
   }

   public static int getIntConvert(String path, MapleData data, int def) {
      MapleData d = data.getChildByPath(path);
      if (d == null) {
         return def;
      }
      if (d.getType() == MapleDataType.STRING) {
         try {
            return Integer.parseInt(getString(d));
         } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            return def;
         }
      } else {
         return getInt(d, def);
      }
   }

   public static BufferedImage getImage(MapleData data) {
      return ((MapleCanvas) data.getData()).getImage();
   }

   public static Point getPoint(MapleData data) {
      return ((Point) data.getData());
   }

   public static Point getPoint(String path, MapleData data) {
      return getPoint(data.getChildByPath(path));
   }

   public static Point getPoint(String path, MapleData data, Point def) {
      final MapleData pointData = data.getChildByPath(path);
      if (pointData == null) {
         return def;
      }
      return getPoint(pointData);
   }

   public static String getFullDataPath(MapleData data) {
      StringBuilder path = new StringBuilder();
      MapleDataEntity myData = data;
      while (myData != null) {
         path.insert(0, myData.getName() + "/");
         myData = myData.getParent();
      }
      return path.substring(0, path.length() - 1);
   }
}
