/*
    Copyright [2015-2016] eBay Software Foundation

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.ebayopensource.webrex.util;

import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;

import org.w3c.dom.Node;

/**
 * Utilities to analyze the metadata of png format images. 
 */
public class PngImageUtils {
   private static final String NONE_ALPHA = "none";

   private static final int IMAGE_INDEX_ZERO = 0;

   private static final String PNG_FORMAT = "png";

   private static final String CHANNEL_SEPARATOR = " ";

   private static final String VALUE_ATTR = "value";

   private static final String BITS_PER_SAMPLE_NODE = "BitsPerSample";

   private static final String ALPHA_NODE = "Alpha";

   /**
    * Determines if the png image is alpha transparent or not.<br>
    * We will assume the given image is a PNG format file, 
    * that means if it's not a png file, we always returns false.
    * 
    * @param input	the binary data of the .png file
    * @return		<tt>true</tt> if the file is a .png and has alpha transparency.
    */
   public static boolean isAlphaTransparentPng(Object input) {
      if (input == null) {
         return false;
      }
      IIOMetadata metadata = retrievePngMetadata(input);
      if (metadata == null) {
         return false;
      }
      return isAlphaTransparentPng(metadata);
   }

   /**
    * Looks into the meta-data of png see if it's alpha-transparent
    * 
    * @param meta
    * @return
    */
   private static boolean isAlphaTransparentPng(IIOMetadata meta) {
      String[] names = meta.getMetadataFormatNames();
      // Actually we know only one DOM tree contains the "Alpha" node
      for (String name : names) {
         Node node = meta.getAsTree(name);
         Node alpha = getNodeByName(node, ALPHA_NODE);
         if (isAlphaTransprent(alpha)) {
            return true;
         }
      }
      return false;
   }

   /**
    * Returns the bit depth of alpha transparency channel of the given image file
    * 
    * @param input	the binary data of the .png file
    * @return 0 if the file does not exist or is not a png image
    */
   public static int getTransparencyChannelBitDepth(Object input) {
      try {
         if (input == null) {
            return 0;
         }
         IIOMetadata metadata = retrievePngMetadata(input);
         if (metadata == null) {
            return 0;
         }
         boolean isAlphaTransparent = isAlphaTransparentPng(metadata);
         if (!isAlphaTransparent) {
            return 0;
         }
         return getTransparencyChannelBitDepth(metadata);
      } catch (Exception e) {
         //Should not happen, but to be on the safe side, we catch all the exception here.
         //TODO: log
         return 0;
      }
   }

   /**
    * Returns the bit depth of the transparency channel of the image 
    * represented by the given meta-data
    * 
    * @param meta	the metadata of the image
    * @return		the bit depth
    */
   private static int getTransparencyChannelBitDepth(IIOMetadata meta) {
      String[] names = meta.getMetadataFormatNames();
      // Actually we know only one DOM tree contains the "Alpha" node
      for (String name : names) {
         Node node = meta.getAsTree(name);
         Node bitsPerSample = getNodeByName(node, BITS_PER_SAMPLE_NODE);
         int bitDepth = getTransparencyChannelBitDepth(bitsPerSample);
         if (bitDepth != 0) {
            return bitDepth;
         }
      }
      return 0;
   }

   /**
    * Analyzes the bit depth of transparency channel.<br>
    * The following literal is extracted from <Portable Network Graphics (PNG) Specification (Second Edition)> <br>
    * Sample depth: number of bits used to represent a sample value. In an indexed-colour PNG image, samples are stored
    * in the palette and thus the sample depth is always 8 by definition of the palette. In other types of PNG image it
    * is the same as the bit depth.<br>
    * 
    * 
    * @param bitsPerSample
    * @return
    */
   private static int getTransparencyChannelBitDepth(Node bitsPerSample) {
      if (bitsPerSample == null) {
         return 0;
      }
      Node valueAttr = bitsPerSample.getAttributes().getNamedItem(VALUE_ATTR);
      if (valueAttr == null) {
         return 0;
      }
      if (valueAttr.getNodeValue() == null) {
         return 0;
      }
      // Sample:
      // RGBAlpha:8 8 8 8
      // GrayAlpha:8 8

      String value = valueAttr.getNodeValue();
      if (value.lastIndexOf(CHANNEL_SEPARATOR) < 0) {
         return 0;
      }
      try {
         value = value.substring(value.lastIndexOf(CHANNEL_SEPARATOR) + 1);
         return Integer.parseInt(value);
      }
      // In case of ArrayOutOfBoundException and NotANumber
      catch (Exception e) {
         return 0;// should not happen
      }
   }

   /**
    * Retrieves the meta-data of the image, if it's not a png file, return <code>null</code>.
    * 
    * @param filename
    * @return
    */
   private static IIOMetadata retrievePngMetadata(Object input) {
      try {
         ImageInputStream iis = ImageIO.createImageInputStream(input);
         Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
         while (readers.hasNext()) {
            ImageReader reader = readers.next();
            String formatName = reader.getFormatName();
            if (formatName != null && formatName.equalsIgnoreCase(PNG_FORMAT)) {
               reader.setInput(iis);
               return reader.getImageMetadata(IMAGE_INDEX_ZERO);
            }
         }
      } catch (IOException e) {
         return null;
      }
      return null;
   }

   /**
    * Looks into the alpha node, see if it's alpha transparent
    * 
    * @param alphaNode
    * @return
    */
   private static boolean isAlphaTransprent(Node alphaNode) {
      if (alphaNode == null) {
         return false;
      }
      Node valueAttr = alphaNode.getAttributes().getNamedItem(VALUE_ATTR);
      if (valueAttr == null) {
         return false;
      }
      if (valueAttr.getNodeValue() == null) {
         return false;
      }
      if (valueAttr.getNodeValue().equals(NONE_ALPHA)) {
         return false;
      }
      return true;
   }

   /**
    * Searches the DOM tree to return the node represented by the given name, if there are more than one node with the
    * given name, we always return the first node.
    * 
    * @param name
    * @return
    */
   private static Node getNodeByName(Node node, String name) {
      if (name == null || node == null) {
         return null;
      }
      if (node.getNodeName().equals(name)) {
         return node;
      }

      Node theDesiredNode = null;

      // Search the children
      Node child = node.getFirstChild();
      while (child != null) {
         theDesiredNode = getNodeByName(child, name);
         if (theDesiredNode != null) {
            return theDesiredNode;
         }
         child = child.getNextSibling();
      }
      return null;
   }
}
