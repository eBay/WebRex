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

package com.ebayopensource.webrex.resource.impl;

import com.ebayopensource.webrex.resource.ResourceRuntimeContext;
import com.ebayopensource.webrex.resource.api.IDeferRenderable;
import com.ebayopensource.webrex.resource.spi.IDeferProcessor;
import com.ebayopensource.webrex.util.Markers;

public class ResourceDeferProcessor implements IDeferProcessor {
   private static final IMarkerHandler DEFAULT_HANDLE = new ResourceMarkerHandler();

   @Override
   public IMarkerHandler getMarkerHandker() {
      return DEFAULT_HANDLE;
   }
   
   private MarkerParser parser;
   
   public ResourceDeferProcessor(boolean ignoreWhitespaces) {
	   parser = ignoreWhitespaces ? MarkerParserV4.INSTANCE : MarkerParserV3.INSTANCE;
   }
   
   public ResourceDeferProcessor() {
	   this(true);
   }

   /**
    * - adjust resource model based on profile model if have;<br/>
    * - Parse template like: <br/>
    * text${marker1}text${marker2}text...
    */
   @Override
   public void process(StringBuilder content) {
	   parser.parse(content, getMarkerHandker());
   }
   
   public static interface MarkerParser {
	   public void parse(StringBuilder content, IMarkerHandler handler);
   }

   public static enum MarkerParserV3 implements MarkerParser {
      INSTANCE;

      private static final int BUFFER_SIZE = 256;

      public void parse(StringBuilder content, IMarkerHandler handler) {
         char[] buffer = new char[BUFFER_SIZE];

         boolean dollar = false;
         boolean bracket = false;
         int len = content.length();

         int pos = 0; //Position of Content
         int markedIndex = 0; //Position of "$" in char buffer
         int markedDollar = 0; //Position of masker in char buffer
         int startPos = 0; //Start position of Content, the characters behind this was copied to target StringBuilder
         int bufSize = BUFFER_SIZE; //Current Buffer Size
         char ch;

         StringBuilder target = new StringBuilder((int) (len * 1.2));

         while (pos < len) {
            bufSize = BUFFER_SIZE > len - pos ? len - pos : BUFFER_SIZE;
            content.getChars(pos, pos + bufSize, buffer, 0);

            for (int i = 0; i < bufSize; i++) {
               ch = buffer[i];
               switch (ch) {
               case '$':
                  if (bracket) {
                     bracket = false;
                  }
                  markedIndex = i;
                  markedDollar = pos + i;

                  dollar = true;
                  break;
               case '{':
                  if (!bracket && dollar) {
                     bracket = true;
                     dollar = false;

                     markedIndex = i + 1;
                  }
                  break;
               case '}':
                  if (bracket) {
                     // process marker
                     int markerLength = i - markedIndex;

                     if (markerLength > 0) {
                        //${MARK...}                       
                        String replaced = handler.translateMarker(content.substring(pos + markedIndex, pos + i));
                        if (replaced != null) {
                           if (markedDollar > startPos) { //Append the content behind this mark
                              target.append(content, startPos, markedDollar);
                           }

                           int replacedLength = replaced.length();
                           if (replacedLength > 0) {
                              target.append(replaced);
                           }

                           startPos = pos + i + 1;
                        }
                     }

                     bracket = false;
                  } else {
                     dollar = false;
                  }
                  break;
               default:
                  if (!bracket) {
                     dollar = false;
                  }
                  break;
               }
            }

            pos += bufSize;

            if (dollar || bracket) {
               markedIndex = -(bufSize - markedIndex);
            } else {
               markedIndex = 0;
            }
         }
         if (startPos < len) {
            target.append(content, startPos, len);
         }

         content.setLength(0);
         content.append(target);
      }
   }

   public static enum MarkerParserV4 implements MarkerParser {
	      INSTANCE;

	      private static final int BUFFER_SIZE = 256;

	      public void parse(StringBuilder content, IMarkerHandler handler) {
	         char[] buffer = new char[BUFFER_SIZE];

	         boolean dollar = false;
	         boolean bracket = false;
	         int len = content.length();

	         int pos = 0; //Position of Content
	         int markedIndex = 0; //Position of "$" in char buffer
	         int markedDollar = 0; //Position of masker in char buffer
	         int startPos = 0; //Start position of Content, the characters behind this was copied to target StringBuilder
	         int bufSize = BUFFER_SIZE; //Current Buffer Size
	         char ch;
	         
	         boolean triming = false;

	         StringBuilder target = new StringBuilder((int) (len * 1.2));

	         while (pos < len) {
	            bufSize = BUFFER_SIZE > len - pos ? len - pos : BUFFER_SIZE;
	            content.getChars(pos, pos + bufSize, buffer, 0);

	            for (int i = 0; i < bufSize; i++) {
	               ch = buffer[i];
	               switch (ch) {
	               case '$':
	                  if (bracket) {
	                     bracket = false;
	                  }
	                  triming = false;
	                  markedIndex = i;
	                  markedDollar = pos + i;

	                  dollar = true;
	                  break;
	               case '{':
	                  if (!bracket && dollar) {
	                     bracket = true;
	                     dollar = false;

	                     markedIndex = i + 1;
	                  }
	                  break;
	               case '}':
	                  if (bracket) {
	                     // process marker
	                     int markerLength = i - markedIndex;

	                     if (markerLength > 0) {
	                        //${MARK...}                       
	                        String replaced = handler.translateMarker(content.substring(pos + markedIndex, pos + i));
	                        if (replaced != null) {
	                           if (markedDollar > startPos) { //Append the content behind this mark
	                              target.append(content, startPos, markedDollar);
	                           }

	                           int replacedLength = replaced.length();
	                           if (replacedLength > 0) {
	                              target.append(replaced);
	                           }

	                           startPos = pos + i + 1;
	                        }
	                     }

	                     bracket = false;
	                  } else {
	                     dollar = false;
	                  }
	                  break;
	               case '>':
	            	   if (!triming) {
	            		   triming = true;
	            	   }
	            	   markedIndex = i;
            		   bracket = false;
            		   dollar = false;
	            	   break;
	               case ' ':
	               case '\t':
	               case '\r':
	               case '\n':
	            	   if (triming) {
	            		   break;
	            	   }
	               case '<':
	            	   if (triming) {
	            		  int spaceLength = i - markedIndex;
	                       if (spaceLength > 1) {
	                          //>    <
                              if (markedIndex + pos >= startPos) { //Append the content behind this mark
                                 target.append(content, startPos, pos + markedIndex+1);
                              }
	                          startPos = pos + i;
	                       }
	                       triming = false;
	                       break;
	            	   }
	               default:
	            	  triming = false;
	                  if (!bracket) {
	                     dollar = false;
	                  }
	                  break;
	               }
	            }

	            pos += bufSize;

	            if (dollar || bracket  || triming) {
	               markedIndex = -(bufSize - markedIndex);
	            } else {
	               markedIndex = 0;
	            }
	         }
	         if (startPos < len) {
	            target.append(content, startPos, len);
	         }

	         content.setLength(0);
	         content.append(target);
	      }
	   }
   public static class ResourceMarkerHandler implements IMarkerHandler {
      @Override
      public boolean handle(StringBuilder sb, String marker) {
         String result = translateMarker(marker);

         if (result != null) {
            sb.append(result);

            return true;
         } else {
            return false;
         }
      }

      public String translateMarker(String marker) {
         String[] sections = Markers.forDefer().parse(marker);

         if (sections != null && sections.length == 1) {
            String id = sections[0];
            ResourceRuntimeContext ctx = ResourceRuntimeContext.ctx();
            IDeferRenderable renderable = ctx.getDeferRenderable(id);

            if (renderable != null) {
               return renderable.deferRender();
            } else {
               return null;
            }
         } else {
            return null;
         }
      }
   }
}
