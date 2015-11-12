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

package com.ebayopensource.webrex.resource.tag;

import com.ebayopensource.webrex.resource.api.IResource;

public interface ITag {
   /**
    * Build a resource instance for this tag. The component could be an instance of resource, 
    * for example, ICssResource, IJsResource etc.
    * 
    * Return null if there is no resource built, that leads to render() method will be skipped.
    */
   public IResource build();

   /**
    * Be called when a tag ends.
    */
   public void end();

   /**
    * Get the tag env.
    */
   public ITagEnv getEnv();

   /**
    * Return a data model of the tag instance.
    * 
    * @return data model of the tag instance.
    */
   public ITagModel getModel();

   /**
    * Get the current tag state; please refer {@link State}
    */
   public State getState();

   /**
    * Render the component 
    * @param component the rendering component
    * @return tag rendering result
    */
   public String render(IResource resource);

   /**
    * Inject an environment instance for this tag to live on.
    * 
    * @param env
    */
   public void setEnv(ITagEnv env);

   /**
    * Set tag state
    * @param newState new state to be assgined
    */
   public void setState(State newState);

   /**
    * Be called when a tag starts.
    */
   public void start();

   /**
    * Tag state definition and acceptable next states 
    */
   public static enum State {
      CREATED(0, 1),

      STARTED(1, 2),

      BUILT(2, 3, 9),

      RENDERED(3, 9),

      ENDED(9, 1);

      private int m_id;

      private int[] m_nextStateIds;

      private State(int id, int... nextStates) {
         m_id = id;
         m_nextStateIds = nextStates;
      }

      public boolean canTransit(State nextState) {
         for (int id : m_nextStateIds) {
            if (id == nextState.getId()) {
               return true;
            }
         }

         return false;
      }

      public int getId() {
         return m_id;
      }

      public boolean isBuilt() {
         return this == BUILT;
      }

      public boolean isCreated() {
         return this == CREATED;
      }

      public boolean isEnded() {
         return this == ENDED;
      }

      public boolean isRendered() {
         return this == RENDERED;
      }

      public boolean isStarted() {
         return this == STARTED;
      }
   }
}
