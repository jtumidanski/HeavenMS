package server.maps;

import java.awt.Point;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MapleFootholdTree {
   private static int maxDepth = 8;
   private MapleFootholdTree nw = null;
   private MapleFootholdTree ne = null;
   private MapleFootholdTree sw = null;
   private MapleFootholdTree se = null;
   private List<MapleFoothold> footholds = new LinkedList<>();
   private Point p1;
   private Point p2;
   private Point center;
   private int depth = 0;
   private int maxDropX;
   private int minDropX;

   public MapleFootholdTree(Point p1, Point p2) {
      this.p1 = p1;
      this.p2 = p2;
      center = new Point((p2.x - p1.x) / 2, (p2.y - p1.y) / 2);
   }

   public MapleFootholdTree(Point p1, Point p2, int depth) {
      this.p1 = p1;
      this.p2 = p2;
      this.depth = depth;
      center = new Point((p2.x - p1.x) / 2, (p2.y - p1.y) / 2);
   }

   public void insert(MapleFoothold f) {
      if (depth == 0) {
         if (f.firstPoint().x > maxDropX) {
            maxDropX = f.firstPoint().x;
         }
         if (f.firstPoint().x < minDropX) {
            minDropX = f.firstPoint().x;
         }
         if (f.secondPoint().x > maxDropX) {
            maxDropX = f.secondPoint().x;
         }
         if (f.secondPoint().x < minDropX) {
            minDropX = f.secondPoint().x;
         }
      }
      if (depth == maxDepth ||
            (f.firstPoint().x >= p1.x && f.secondPoint().x <= p2.x &&
                  f.firstPoint().y >= p1.y && f.secondPoint().y <= p2.y)) {
         footholds.add(f);
      } else {
         if (nw == null) {
            nw = new MapleFootholdTree(p1, center, depth + 1);
            ne = new MapleFootholdTree(new Point(center.x, p1.y), new Point(p2.x, center.y), depth + 1);
            sw = new MapleFootholdTree(new Point(p1.x, center.y), new Point(center.x, p2.y), depth + 1);
            se = new MapleFootholdTree(center, p2, depth + 1);
         }
         if (f.secondPoint().x <= center.x && f.secondPoint().y <= center.y) {
            nw.insert(f);
         } else if (f.firstPoint().x > center.x && f.secondPoint().y <= center.y) {
            ne.insert(f);
         } else if (f.secondPoint().x <= center.x && f.firstPoint().y > center.y) {
            sw.insert(f);
         } else {
            se.insert(f);
         }
      }
   }

   private List<MapleFoothold> getRelevants(Point p) {
      return getRelevants(p, new LinkedList<>());
   }

   private List<MapleFoothold> getRelevants(Point p, List<MapleFoothold> list) {
      list.addAll(footholds);
      if (nw != null) {
         if (p.x <= center.x && p.y <= center.y) {
            nw.getRelevants(p, list);
         } else if (p.x > center.x && p.y <= center.y) {
            ne.getRelevants(p, list);
         } else if (p.x <= center.x && p.y > center.y) {
            sw.getRelevants(p, list);
         } else {
            se.getRelevants(p, list);
         }
      }
      return list;
   }

   private MapleFoothold findWallR(Point p1, Point p2) {
      MapleFoothold ret;
      for (MapleFoothold f : footholds) {
         if (f.isWall() && f.firstPoint().x >= p1.x && f.firstPoint().x <= p2.x &&
               f.firstPoint().y >= p1.y && f.secondPoint().y <= p1.y) {
            return f;
         }
      }
      if (nw != null) {
         if (p1.x <= center.x && p1.y <= center.y) {
            ret = nw.findWallR(p1, p2);
            if (ret != null) {
               return ret;
            }
         }
         if ((p1.x > center.x || p2.x > center.x) && p1.y <= center.y) {
            ret = ne.findWallR(p1, p2);
            if (ret != null) {
               return ret;
            }
         }
         if (p1.x <= center.x && p1.y > center.y) {
            ret = sw.findWallR(p1, p2);
            if (ret != null) {
               return ret;
            }
         }
         if ((p1.x > center.x || p2.x > center.x) && p1.y > center.y) {
            ret = se.findWallR(p1, p2);
            return ret;
         }
      }
      return null;
   }

   public MapleFoothold findWall(Point p1, Point p2) {
      if (p1.y != p2.y) {
         throw new IllegalArgumentException();
      }
      return findWallR(p1, p2);
   }

   public MapleFoothold findBelow(Point p) {
      List<MapleFoothold> relevants = getRelevants(p);
      List<MapleFoothold> xMatches = new LinkedList<>();
      for (MapleFoothold fh : relevants) {
         if (fh.firstPoint().x <= p.x && fh.secondPoint().x >= p.x) {
            xMatches.add(fh);
         }
      }
      Collections.sort(xMatches);
      for (MapleFoothold fh : xMatches) {
         if (!fh.isWall()) {
            if (fh.firstPoint().y != fh.secondPoint().y) {
               int calcY;
               double s1 = Math.abs(fh.secondPoint().y - fh.firstPoint().y);
               double s2 = Math.abs(fh.secondPoint().x - fh.firstPoint().x);
               double s4 = Math.abs(p.x - fh.firstPoint().x);
               double alpha = Math.atan(s2 / s1);
               double beta = Math.atan(s1 / s2);
               double s5 = Math.cos(alpha) * (s4 / Math.cos(beta));
               if (fh.secondPoint().y < fh.firstPoint().y) {
                  calcY = fh.firstPoint().y - (int) s5;
               } else {
                  calcY = fh.firstPoint().y + (int) s5;
               }
               if (calcY >= p.y) {
                  return fh;
               }
            } else {
               if (fh.firstPoint().y >= p.y) {
                  return fh;
               }
            }
         }
      }
      return null;
   }

   public int getX1() {
      return p1.x;
   }

   public int getX2() {
      return p2.x;
   }

   public int getY1() {
      return p1.y;
   }

   public int getY2() {
      return p2.y;
   }

   public int getMaxDropX() {
      return maxDropX;
   }

   public int getMinDropX() {
      return minDropX;
   }
}
