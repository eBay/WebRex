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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.ebayopensource.webrex.resource.ResourceFactory;
import com.ebayopensource.webrex.resource.ResourceRuntimeContext;
import com.ebayopensource.webrex.resource.api.IResource;
import com.ebayopensource.webrex.resource.spi.IExpressionEvaluator;

public class ResourceELEvaluator implements IExpressionEvaluator {
   protected void buildResourcePath(StringBuilder sb, ResourceExpression resourceExpression, Map<String, Object> arguments) {
      if (resourceExpression.m_type == ResourceExpressionType.NAMESPACE 
            || resourceExpression.m_type == ResourceExpressionType.TYPE 
            || resourceExpression.m_type == ResourceExpressionType.RES) {
         return;
      }

      buildResourcePath(sb, resourceExpression.getParent(), arguments);

      String key = resourceExpression.getKey();
      Object[] exprArguments = resourceExpression.getArguments();
      if (exprArguments != null) {
         arguments.put(key, exprArguments);
      }

      sb.append('/').append(key);
      
   }

   //ATTENTION sb may be changed after calling this function
   protected String convertELToPath(StringBuilder sb, String type, String namespace) {
      int indexUnderscore = sb.lastIndexOf("_");
      int indexPoint = sb.lastIndexOf(".");
      if (indexUnderscore > indexPoint) {
         sb.setCharAt(indexUnderscore, '.');
      }
      return sb.toString();
   }

   @Override
   public Object evaluate(ResourceExpression expr) {
      Object result = null;
      ResourceExpression namespaceExpr = getExpressionNamespace(expr);
      if(namespaceExpr != null) {
    	  ResourceExpression parent = namespaceExpr.getParent();
      
	      if(parent != null) {
	      String resType = parent.getKey();
	      String namespace = namespaceExpr.getKey();
	
		      if (namespace != null) {
		         StringBuilder sb = new StringBuilder();
		
		         Map<String, Object> arguments = new LinkedHashMap<String, Object>(3);
		         buildResourcePath(sb, expr, arguments);
		
		         String path = convertELToPath(sb, resType, namespace);
		         if (arguments.isEmpty()) {
		            result = ResourceFactory.createResource(resType, namespace, path);
		         } else {
		            result = ResourceFactory.createResource(resType, namespace, path, arguments);
		         }
		
		      }
	      }
      }
      
      return result;
   }

   @Override
   public Object evaluate(ResourceExpression expr, String property) {
      return evaluate(expr);
   }

   @Override
   public String evaluateAsString(ResourceExpression expr) {
      Object resource = evaluate(expr);
      if (resource != null) {
         return ((IResource) resource).getUrl(ResourceRuntimeContext.ctx().createResourceContext());
      }

      return "";
   }

   @Override
   public Set<Entry<String, Object>> evaluationAsEntrySet(ResourceExpression expr) {
      return Collections.emptySet();
   }

   @Override
   public Set<String> evaluationAsKeySet(ResourceExpression expr) {
      return Collections.emptySet();
   }

   protected ResourceExpression getExpressionNamespace(ResourceExpression expr) {
      while(expr != null) {
         if (expr.m_type == ResourceExpressionType.NAMESPACE) {
            return expr;
         }
         expr = expr.getParent();
      }
      return null;
   }

	   
//      if (expr instanceof ResBeanExpression || expr.getParent() instanceof ResBeanExpression) {
//         return false;
//      }
//
//      if (expr.getParent().getParent() instanceof ResBeanExpression) {
//         return true;
//      }
//
//      return false;

//   private boolean isTypeOrResNode(ResourceExpression expr) {
//	  ResourceExpressionType type = expr.getType();
//	  return type == ResourceExpressionType.RES || type == ResourceExpressionType.TYPE;
//      if (expr instanceof ResBeanExpression || expr.getParent() instanceof ResBeanExpression) {
//         return true;
//      }
//      return false;
//   }
}
