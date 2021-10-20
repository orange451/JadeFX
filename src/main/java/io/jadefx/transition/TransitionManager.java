package io.jadefx.transition;

import java.util.ArrayList;
import java.util.List;

import io.jadefx.stage.Context;

public class TransitionManager {
	private static List<Transition> activeTransitions = new ArrayList<Transition>();
	
	static void add(Transition transition) {
		activeTransitions.add(transition);
	}
	
	static boolean remove(Transition transition) {
		return activeTransitions.remove(transition);
	}
	
	public static void tick() {
		if ( activeTransitions.size() <= 0 )
			return;
		
		Context.getCurrent().flush();
		
		for (int i = 0; i < activeTransitions.size(); i++) {
			Transition t = activeTransitions.get(i);
			t.tick(t.getProgress());
			
			if (t.isFinished()) {
				t.completedCallback();
				activeTransitions.remove(i);
				i--;
			}
		}
	}
}
