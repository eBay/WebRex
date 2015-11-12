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

package com.ebayopensource.webrex.resource.expression;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.ebayopensource.webrex.resource.DiagnosisModel.DiagnosisStatus;
import com.ebayopensource.webrex.resource.DiagnosisModel.EL;
import com.ebayopensource.webrex.resource.DiagnosisModel.MODE;
import com.ebayopensource.webrex.resource.ResourceRuntimeContext;
import com.ebayopensource.webrex.resource.spi.IExpressionEvaluator;
import com.ebayopensource.webrex.resource.spi.IResourceRegistry;
import com.ebayopensource.webrex.util.JavaHighResTimer;

@SuppressWarnings("rawtypes")
public class ResourceExpression implements Map {
   public static final ResourceELEvaluator DEFAULT_EVALUATOR = new ResourceELEvaluator();

   private Object[] m_arguments;

   private String m_key;

   private ResourceExpression m_parent;
   
   ResourceExpressionType m_type;
   
   public ResourceExpression(String key, ResourceExpression parent) {
	   this(key, parent, ResourceExpressionType.NODE);
   }
   

   public ResourceExpression(String key, ResourceExpression parent, ResourceExpressionType type) {
      m_key = key;
      m_parent = parent;
      m_type = type;
   }

   public ResourceExpression(String key, ResourceExpression parent, ResourceExpressionType type, Object... args) {
      this(key, parent, type);
      m_arguments = args;
   }

   /**
    * Used to support EL with method call and parameters
    * @param key
    * @param arguments
    * @return
    */
   public Object call(Object key, Object[] arguments) {
      return new ResourceExpression((String) key, this, ResourceExpressionType.NODE, arguments);
   }

   @Override
   public void clear() {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean containsKey(Object key) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean containsValue(Object value) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Set entrySet() {
      IExpressionEvaluator evaluator = getEvaluator();
      Set<Entry<String, Object>> result = evaluator.evaluationAsEntrySet(this);

      return result;
   }

   public Object evaluate() {
      ResourceRuntimeContext ctx = ResourceRuntimeContext.ctx();
      boolean showDiag = ctx.isShowDiag();
      String elPath = null;
      try {
         if (showDiag) {
            elPath = getELPath();
            ctx.getShowDiagModel().evaluateEL(elPath, MODE.START);
         }

         IExpressionEvaluator evaluator = getEvaluator();
         Object result = evaluator.evaluate(this);

         if (showDiag) {
            EL el = ctx.getShowDiagModel().peek();
            if (!el.isError()) {
               el.setStatus(result != null ? DiagnosisStatus.OK : DiagnosisStatus.UNRESOLVING);
            }
         }

         return result;
      } finally {
         if (showDiag) {
            ctx.getShowDiagModel().evaluateEL(elPath, MODE.END);
         }
      }
   }

   public String evaluateAsString() {
      ResourceRuntimeContext ctx = ResourceRuntimeContext.ctx();
      boolean showDiag = ctx.isShowDiag();
      String elPath = null;
      try {
         if (showDiag) {
            elPath = getELPath();
            ctx.getShowDiagModel().evaluateELAsString(elPath, MODE.START);
         }

         IExpressionEvaluator evaluator = getEvaluator();
         String result = evaluator.evaluateAsString(this);

         if (showDiag) {
            EL el = ctx.getShowDiagModel().peek();
            if (!el.isError()) {
               el.setStatus(result != null && !result.isEmpty() ? DiagnosisStatus.OK : DiagnosisStatus.UNRESOLVING);
            }
         }

         return result;
      } finally {
         if (showDiag) {
            ctx.getShowDiagModel().evaluateELAsString(elPath, MODE.END);
         }
      }
   }

   private String getELPath() {
      StringBuilder sb = new StringBuilder();
      sb.append(m_key);

      ResourceExpression parent = m_parent;
      while (parent != null) {
         sb.insert(0, parent.m_key + ".");
         parent = parent.getParent();
      }

      return sb.toString();
   }

   @Override
   public Object get(Object expr) {
      String key = expr.toString();
      //support property
      if (key.charAt(0) == '$') {
         IExpressionEvaluator evaluator = getEvaluator();
         return evaluator.evaluate(this, key);
      } else {
         return new ResourceExpression(key, this, ResourceExpressionType.NODE);
      }
   }

   public Object[] getArguments() {
      return m_arguments;
   }

   protected String getELType(ResourceExpression expr) {
      while(expr != null) {
         if (expr.m_parent instanceof ResBeanExpression) {
            return expr.getKey();
         }
         expr = expr.m_parent;
      }
      return null;
   }

   protected IExpressionEvaluator getEvaluator() {
      IResourceRegistry registry = ResourceRuntimeContext.ctx().getConfig().getRegistry();
      String elType = getELType(this);
      IExpressionEvaluator evaluator = registry.getExpressionEvaluator(elType);

      if (evaluator == null) {
         evaluator = DEFAULT_EVALUATOR;
      }
      return evaluator;
   }

   public String getKey() {
      return m_key;
   }

   public ResourceExpression getParent() {
      return m_parent;
   }

   @Override
   public boolean isEmpty() {
      return evaluate() == null;
   }

   @Override
   public Set keySet() {
      IExpressionEvaluator evaluator = getEvaluator();
      Set<String> result = evaluator.evaluationAsKeySet(this);

      return result;
   }

   @Override
   public Object put(Object key, Object value) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(Map m) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Object remove(Object key) {
      throw new UnsupportedOperationException();
   }

   //Allow to customize argument passing
   public void setArguments(Object[] arguments) {
      m_arguments = arguments;
   }

   @Override
   public int size() {
      throw new UnsupportedOperationException();
   }

   @Override
   public String toString() {
      long[] startingTime = JavaHighResTimer.begin();
      String result = evaluateAsString();

      ResourceRuntimeContext ctx = ResourceRuntimeContext.ctx();
      if (ctx.isStatisticsEnabled()) {
         ctx.getStatistics().addItem(getClass(), JavaHighResTimer.endVal(startingTime));
      }
      return result;
   }

   @Override
   public Collection values() {
      throw new UnsupportedOperationException();
   }

public ResourceExpressionType getType() {
	return m_type;
}

public void setType(ResourceExpressionType type) {
	m_type = type;
}

}
