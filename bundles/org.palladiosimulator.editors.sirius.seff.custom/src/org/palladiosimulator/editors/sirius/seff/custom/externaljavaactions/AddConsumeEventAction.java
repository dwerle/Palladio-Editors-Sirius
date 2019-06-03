package org.palladiosimulator.editors.sirius.seff.custom.externaljavaactions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.sirius.tools.api.ui.IExternalJavaAction;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.palladiosimulator.editors.commons.dialogs.selection.PalladioSelectEObjectDialog;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.EventGroup;
import org.palladiosimulator.pcm.repository.EventType;
import org.palladiosimulator.pcm.repository.ProvidedRole;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.SinkRole;
import org.palladiosimulator.pcm.seff.ServiceEffectSpecification;
import org.palladiosimulator.simulizar.indirection.actions.ConsumeEventAction;

public class AddConsumeEventAction implements IExternalJavaAction {
	
	public static final Shell SHELL = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

	@Override
	public void execute(Collection<? extends EObject> selections, Map<String, Object> parameters) {
		ConsumeEventAction emitEventAction = (ConsumeEventAction) parameters.get("instance");
		EventType eventType = getEventType(emitEventAction);
		emitEventAction.setEventType(eventType);

	}

	private EventType getEventType(ConsumeEventAction consumeEventAction) {

		Collection<Object> filter = new ArrayList<Object>();

		filter.add(Repository.class);
		filter.add(EventGroup.class);
		filter.add(EventType.class);

		Collection<EReference> additionalChildReferences = new ArrayList<EReference>();

		PalladioSelectEObjectDialog dialog = new PalladioSelectEObjectDialog(SHELL, filter, additionalChildReferences,
				consumeEventAction.eResource().getResourceSet());

		dialog.setProvidedService(EventType.class);

		//Only show EventTypes from EventGroups from SourceRoles of the parent BasicComponent
		for (Object o : dialog.getTreeViewer().getExpandedElements()) {
			if (!(o instanceof EventGroup))
				continue;
			ServiceEffectSpecification seff = SEFFUtil.getEnclosingSEFF(consumeEventAction
					.getResourceDemandingBehaviour_AbstractAction());
			BasicComponent parent = seff.getBasicComponent_ServiceEffectSpecification();
			
			boolean found = false;
			for (ProvidedRole r : parent.getProvidedRoles_InterfaceProvidingEntity()) {
				if (!(r instanceof SinkRole))
					continue;
				SinkRole sinkRole = (SinkRole) r;
				if (sinkRole.getEventGroup__SinkRole().equals(o)) {
					found = true;
					consumeEventAction.setSinkRole(sinkRole);
				}
			}
			if (!found)
				dialog.getTreeViewer().remove(o);
		}
		
		dialog.open();
		return (EventType) dialog.getResult();
	}


	@Override
	public boolean canExecute(Collection<? extends EObject> selections) {
		// TODO Auto-generated method stub
		return true;
	}

}
