package provider.wz;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import constants.game.GameConstants;
import provider.MapleData;
import provider.MapleDataEntity;

public class XMLDomMapleData implements MapleData {
   private Node node;
   private File imageDataDir;

   public XMLDomMapleData(FileInputStream fis, File imageDataDir) {
      try {
         DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
         Document document = documentBuilder.parse(fis);
         this.node = document.getFirstChild();
      } catch (ParserConfigurationException | IOException | SAXException e) {
         throw new RuntimeException(e);
      }
      this.imageDataDir = imageDataDir;
   }

   private XMLDomMapleData(Node node) {
      this.node = node;
   }

   @Override
   public synchronized MapleData getChildByPath(String path) {  // the whole XML reading system seems susceptible to give nulls on strenuous read scenarios
      String[] segments = path.split("/");
      if (segments[0].equals("..")) {
         return ((MapleData) getParent()).getChildByPath(path.substring(path.indexOf("/") + 1));
      }

      Node myNode;
      myNode = node;
      for (String s : segments) {
         NodeList childNodes = myNode.getChildNodes();
         boolean foundChild = false;
         for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE && childNode.getAttributes().getNamedItem("name").getNodeValue().equals(s)) {
               myNode = childNode;
               foundChild = true;
               break;
            }
         }
         if (!foundChild) {
            return null;
         }
      }

      XMLDomMapleData ret = new XMLDomMapleData(myNode);
      ret.imageDataDir = new File(imageDataDir, getName() + "/" + path).getParentFile();
      return ret;
   }

   @Override
   public synchronized List<MapleData> getChildren() {
      List<MapleData> ret = new ArrayList<>();

      NodeList childNodes = node.getChildNodes();
      for (int i = 0; i < childNodes.getLength(); i++) {
         Node childNode = childNodes.item(i);
         if (childNode.getNodeType() == Node.ELEMENT_NODE) {
            XMLDomMapleData child = new XMLDomMapleData(childNode);
            child.imageDataDir = new File(imageDataDir, getName());
            ret.add(child);
         }
      }

      return ret;
   }

   @Override
   public synchronized Object getData() {
      NamedNodeMap attributes = node.getAttributes();
      MapleDataType type = getType();
      switch (type) {
         case DOUBLE:
         case FLOAT:
         case INT:
         case SHORT: {
            String value = attributes.getNamedItem("value").getNodeValue();
            Number number = GameConstants.parseNumber(value);

            return switch (type) {
               case DOUBLE -> number.doubleValue();
               case FLOAT -> number.floatValue();
               case INT -> number.intValue();
               case SHORT -> number.shortValue();
               default -> null;
            };
         }
         case STRING:
         case UOL: {
            return attributes.getNamedItem("value").getNodeValue();
         }
         case VECTOR: {
            String x = attributes.getNamedItem("x").getNodeValue();
            String y = attributes.getNamedItem("y").getNodeValue();
            return new Point(Integer.parseInt(x), Integer.parseInt(y));
         }
         case CANVAS: {
            String width = attributes.getNamedItem("width").getNodeValue();
            String height = attributes.getNamedItem("height").getNodeValue();
            return new FileStoredPngMapleCanvas(Integer.parseInt(width), Integer.parseInt(height), new File(
                  imageDataDir, getName() + ".png"));
         }
         default:
            return null;
      }
   }

   @Override
   public synchronized MapleDataType getType() {
      String nodeName = node.getNodeName();

      return switch (nodeName) {
         case "imgdir" -> MapleDataType.PROPERTY;
         case "canvas" -> MapleDataType.CANVAS;
         case "convex" -> MapleDataType.CONVEX;
         case "sound" -> MapleDataType.SOUND;
         case "uol" -> MapleDataType.UOL;
         case "double" -> MapleDataType.DOUBLE;
         case "float" -> MapleDataType.FLOAT;
         case "int" -> MapleDataType.INT;
         case "short" -> MapleDataType.SHORT;
         case "string" -> MapleDataType.STRING;
         case "vector" -> MapleDataType.VECTOR;
         case "null" -> MapleDataType.IMG_0x00;
         default -> null;
      };
   }

   @Override
   public synchronized MapleDataEntity getParent() {
      Node parentNode;
      parentNode = node.getParentNode();
      if (parentNode.getNodeType() == Node.DOCUMENT_NODE) {
         return null;
      }
      XMLDomMapleData parentData = new XMLDomMapleData(parentNode);
      parentData.imageDataDir = imageDataDir.getParentFile();
      return parentData;
   }

   @Override
   public synchronized String getName() {
      return node.getAttributes().getNamedItem("name").getNodeValue();
   }

   @Override
   public synchronized Iterator<MapleData> iterator() {
      return getChildren().iterator();
   }
}
