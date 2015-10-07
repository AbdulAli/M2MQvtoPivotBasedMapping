/*******************************************************************************
 * Copyright (c) 2007, 2011 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.evaluator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class GraphWalker {

	public static interface NodeProvider {
	    Object[] getLinkedNodes(Object node);
	}

	public static interface VertexProcessor {
	    boolean process(Object node);
	}
	
	public static final int MAX_WEIGHT = Integer.MAX_VALUE;
	
	
	public GraphWalker(NodeProvider nodeProvider) {
        myNodeProvider = nodeProvider;
    }

    public boolean walkBreadthFirst(Object root, VertexProcessor processor) {
        Set<Object> visited = new HashSet<Object>();
        List<Object> queue = new ArrayList<Object>();   
    
        visited.add(root);
        queue.add(root);
    
        while (!queue.isEmpty()) {
            Object u = queue.remove(0);
    
            Object[] succ = myNodeProvider.getLinkedNodes(u);
            for (int i = 0; i < succ.length; i++) {
                Object v = succ[i];
    
                if (!visited.contains(v)) {
                    queue.add(v);           
                    visited.add(v);
                }
            }
    
            boolean stop = processor.process(u);
            if (stop) {
                return true;
            }
        }
        
        return false;
    }
    
    public static class CycleException extends RuntimeException {
		private static final long serialVersionUID = -7749663546813761182L;
		
		public CycleException(Object from, Object to) {
            myFrom = from;
            myTo = to;
        }
        
        public Object getFrom()  { return myFrom; }
        public Object getTo() { return myTo; }
        
        @Override
		public String toString() {
        	return "Cycle from " + myFrom + " to " + myTo; //$NON-NLS-1$ //$NON-NLS-2$
        }
        
        private final Object  myFrom;
        private final Object  myTo;
    }
    
    public boolean walkDepthFirst(Object root, VertexProcessor processor) throws CycleException{
        class Traverser {
            Integer GREY  = new Integer(1);
            Integer BLACK = new Integer(2);
            
            boolean walkDepthFirst(Object u, Map<Object, Integer> visited, VertexProcessor processor) {
                visited.put(u, GREY);
                
                Object[] succ = myNodeProvider.getLinkedNodes(u);
                for (int i = 0; i < succ.length; i++) {
                    Object v = succ[i];
                    if (!visited.containsKey(v)) {
                        boolean stop = walkDepthFirst(v, visited, processor);
                        if (stop) {
                            return true;
                        }
                    }
                    else if (GREY == visited.get(v)){
                        throw new CycleException(u, v);
                    }
                }
                
                boolean stop = processor.process(u);
                visited.put(u, BLACK);
                return stop;
            }
        };
        
        Map<Object, Integer> visited = new HashMap<Object, Integer>();
        return new Traverser().walkDepthFirst(root, visited, processor);
    }
    
	public int getMinimumDistance(Object source, Object dest, int weight) {
		Map<ObjectPair, Integer> pathToWeight = new HashMap<ObjectPair, Integer>();
		getMinDistanceImpl(source, pathToWeight, weight);
		
		Integer d = pathToWeight.get(new ObjectPair(source, dest));
		return d == null ? MAX_WEIGHT : d.intValue();
	}
	
	private void getMinDistanceImpl(Object s, Map<ObjectPair, Integer> path2Weight, int weight) {
		Object[] nodes = myNodeProvider.getLinkedNodes(s);
		for (int k = 0; k < nodes.length; k++) {
			Object t = nodes[k];
			
			ObjectPair s2t = new ObjectPair(s, t);
			if (path2Weight.get(s2t) == null || ((Integer)path2Weight.get(s2t)).intValue() > weight) {
				path2Weight.put(s2t, new Integer(weight));
			}
			
			ObjectPair t2t = new ObjectPair(t, t);
			if (path2Weight.get(t2t) == null) {
				getMinDistanceImpl(t, path2Weight, weight);
			}
			
			Set<Map.Entry<ObjectPair, Integer>> paths = new HashSet<Map.Entry<ObjectPair, Integer>>(path2Weight.entrySet());
            for (Iterator<Entry<ObjectPair, Integer>> entryIt = paths.iterator(); entryIt.hasNext(); ) {
				Map.Entry<ObjectPair, Integer> entry = entryIt.next();

				ObjectPair t2i = (ObjectPair)entry.getKey();
				if(!t.equals(t2i.getFirst())) {
					continue;
				}

				Integer t2iW = (Integer)entry.getValue();
				Object i = t2i.getSecond();
				
				ObjectPair s2i = new ObjectPair(s, i);
				if (path2Weight.get(s2i) == null || ((Integer)path2Weight.get(s2i)).intValue() > weight + t2iW.intValue()) {
					path2Weight.put(s2i, new Integer(weight+t2iW.intValue()));
				}
			}
		}
		
		ObjectPair s2s = new ObjectPair(s, s);
		path2Weight.put(s2s, new Integer(0));
	}
	
	private static class ObjectPair {
		public ObjectPair(Object first, Object second) {
			myFirst = first;
			mySecond = second;
		}
		
		public Object getFirst() { 
			return myFirst;
		}
		
		public Object getSecond() {
			return mySecond;
		}
		
		@Override
		public boolean equals(Object o) {
			if (o instanceof ObjectPair == false) {
				return false;
			}
			
			ObjectPair pair = (ObjectPair)o;
			return myFirst.equals(pair.myFirst) && mySecond.equals(pair.mySecond); 
		}
		
		@Override
		public int hashCode() {
			return 17 + myFirst.hashCode()*37 + mySecond.hashCode(); 
		}
		
		private final Object myFirst;
		private final Object mySecond;
	}
    
    private final NodeProvider myNodeProvider;
	
}
