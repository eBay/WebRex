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

package com.ebayopensource.webrex.resource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

public class DiagnosisModel {
   private Stack<IDiagnosisNode> m_current = new Stack<DiagnosisModel.IDiagnosisNode>();

   private Map<DiagnosisType, List<IDiagnosisNode>> m_map = new LinkedHashMap<DiagnosisModel.DiagnosisType, List<IDiagnosisNode>>();

   private void addNode(DiagnosisType type, IDiagnosisNode node) {
      List<IDiagnosisNode> nodes = m_map.get(type);
      if (nodes == null) {
         nodes = new ArrayList<DiagnosisModel.IDiagnosisNode>();
         m_map.put(type, nodes);
      }
      nodes.add(node);
   }

   public void evaluateEL(String elPath, MODE mode) {
      if (mode == MODE.START) {
         EL el = new EL(elPath, DiagnosisType.EL);
         m_current.push(el);
      } else if (mode == MODE.END) {
         IDiagnosisNode node = m_current.pop();
         if (node.getStatus() == null) {
            ((EL) node).setStatus(DiagnosisStatus.ERR);
         }
         addNode(DiagnosisType.EL, node);
      } else {
         throw new UnsupportedOperationException("Unsupported mode:" + mode);
      }
   }

   public void evaluateELAsString(String elPath, MODE mode) {
      if (mode == MODE.START) {
         EL el = new EL(elPath, DiagnosisType.ELString);
         m_current.push(el);
      } else if (mode == MODE.END) {
         IDiagnosisNode node = m_current.pop();
         addNode(DiagnosisType.ELString, node);
      } else {
         throw new UnsupportedOperationException("Unsupported mode:" + mode);
      }
   }

   @SuppressWarnings("unchecked")
   public <N extends IDiagnosisNode> N peek() {
      return (N) m_current.peek();
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder(2048);
      sb.append('{');
      boolean firstNode = true;
      for (Entry<DiagnosisType, List<IDiagnosisNode>> entry : m_map.entrySet()) {
         DiagnosisType type = entry.getKey();
         List<IDiagnosisNode> nodes = entry.getValue();
         if (!firstNode) {
             sb.append(',');
         }
         sb.append(quote(type.name())).append(":[");
         boolean first = true;
         for (IDiagnosisNode node : nodes) {
            if (!first) {
               sb.append(',');
            }
            sb.append(quote(node.toString()));
            first = false;
         }
         sb.append(']');
         firstNode = false;
      }
      sb.append('}');
      return sb.toString();
   }

   private static String quote(Object name) {
      if (name instanceof Enum<?>) {
         return "\"" + ((Enum<?>) name).name() + "\"";
      } else {
         return "\"" + name + "\"";
      }
   }

   public enum DiagnosisStatus {
      OK, UNRESOLVING, ERR
   }

   public enum DiagnosisType {
      ELString, EL, Resource, Tag
   }

   public static class EL extends Node implements IDiagnosisNode {

      private String m_elPath;

      public EL(String elPath, DiagnosisType type) {
         super(type);
         m_elPath = elPath;
      }

      public String getELPath() {
         return m_elPath;
      }

      @Override
      public String toString() {
         StringBuilder sb = new StringBuilder(128);
         sb.append('{');
         sb.append(quote("el")).append(':').append(quote(m_elPath));
         sb.append(',').append(quote("status")).append(':').append(quote(getStatus()));
         if (getException() != null) {
            sb.append(',').append(quote("err")).append(':').append(quote(getException()));
         }
         sb.append('}');
         return sb.toString();
      }
   }

   public static abstract class Node implements IDiagnosisNode {

      private DiagnosisStatus m_status;

      private DiagnosisType m_type;

      private Exception m_exception;

      public Node(DiagnosisType type) {
         m_type = type;
      }

      @Override
      public Exception getException() {
         return m_exception;
      }

      @Override
      public DiagnosisStatus getStatus() {
         return m_status;
      }

      @Override
      public DiagnosisType getType() {
         return m_type;
      }

      public boolean isError() {
         return m_status == DiagnosisStatus.ERR;
      }

      public void setException(Exception e) {
         m_exception = e;
         m_status = DiagnosisStatus.ERR;
      }

      public void setStatus(DiagnosisStatus status) {
         m_status = status;
      }

      @Override
      public String toString() {
         return "Node [m_status=" + m_status + ", m_type=" + m_type + "]";
      }
   }

   public interface IDiagnosisNode {
      public Exception getException();

      public DiagnosisStatus getStatus();

      public DiagnosisType getType();
   }

   public enum MODE {
      START, END
   }

    //Warning, Errors
   
   //Env

   //EL usage

   //Tag usage

   //Slot usage
   // System slot
   // Aggregation

   //Resource usage
   // Image
   // CSS
   // CSS-Image
   // Less
   // Dust
   // De-dup resources

   //start type
   //...log...
   //end type, result
}
