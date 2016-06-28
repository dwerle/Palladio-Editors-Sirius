package org.palladiosimulator.editors.usage.externaljavaactions;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.editors.commons.externaljavaactions.OpenStoExDialog;
import org.palladiosimulator.pcm.usagemodel.OpenWorkload;

import de.uka.ipd.sdq.stoex.RandomVariable;

public class OpenWorkloadInterArrivalTimeAction extends OpenStoExDialog {

	public OpenWorkloadInterArrivalTimeAction() {

	}

	@Override
	public boolean canExecute(Collection<? extends EObject> arg0) {
		return true;
	}

	@Override
	public RandomVariable getRandomVariable(EObject element) {
		OpenWorkload ow = (OpenWorkload) element;
		return ow.getInterArrivalTime_OpenWorkload();
	}

}