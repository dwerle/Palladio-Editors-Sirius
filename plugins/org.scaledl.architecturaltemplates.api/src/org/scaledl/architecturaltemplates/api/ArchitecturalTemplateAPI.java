package org.scaledl.architecturaltemplates.api;

import org.eclipse.emf.ecore.EObject;
import org.modelversioning.emfprofile.Profile;
import org.modelversioning.emfprofile.Stereotype;
import org.palladiosimulator.commons.emfutils.EMFLoadHelper;
import org.palladiosimulator.mdsdprofiles.api.ProfileAPI;
import org.palladiosimulator.mdsdprofiles.api.StereotypeAPI;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.system.System;
import org.scaledl.architecturaltemplates.type.AT;
import org.scaledl.architecturaltemplates.type.Role;

/**
 * An API class providing methods to interact with Architectural Templates
 * 
 * @author Max Schettler
 *
 */
public final class ArchitecturalTemplateAPI {

	/**
	 * The key for the tagged value with which a {@link Stereotype}s`
	 * {@link Role} can be loaded.
	 */
	private static final String ROLE_URI = "roleURI";

	/**
	 * The name-suffix that identifies a {@link Role} or its corresponding
	 * {@link Stereotype} as an system-role.
	 */
	private static final String SYSTEM_ROLE_NAME_SUFFIX = "System";

	/**
	 * Hidden constructor.
	 */
	private ArchitecturalTemplateAPI() {
	}

	/**
	 * Gets the {@link Role} associated with the given {@link Stereotype}.
	 * @param stereotype the {@link Stereotype}
	 * @return the {@link Role}
	 * @throws RuntimeException if the given stereotype does not conform the role-convention.
	 */
	public static Role getRole(final Stereotype stereotype) {
		if (!isRole(stereotype)) {
			throw new RuntimeException("Stereotype \"" + stereotype
					+ "\" is no role");
		}
		java.lang.System.out.println(stereotype.getTaggedValue(ROLE_URI).getDefaultValueLiteral());
		final EObject roleURIEObject = EMFLoadHelper
				.loadAndResolveEObject(stereotype.getTaggedValue(ROLE_URI)
						.getDefaultValueLiteral());
		if (!(roleURIEObject instanceof Role)) {
			throw new RuntimeException("RoleURI Stereotype \"" + stereotype
					+ "\" does not refer to a role");
		}
		return (Role) roleURIEObject;
	}

	/**
	 * Gets the {@link AT} associated with the given {@link Profile}.
	 * @param profile the {@link Profile}
	 * @return the {@link AT}
	 * @throws RuntimeException if the given architectural template does not conform the Architectural-Template-convention.
	 */
	public static AT getArchitecturalTemplate(final Profile profile) {
		if (!isArchitecturalTemplate(profile)) {
			throw new RuntimeException("Profile \"" + profile
					+ "\" is no Architectural Template");
		}
		return getRole(profile.getStereotypes().get(0)).getAT();
	}

	/**
	 * Tests whether a {@link Stereotype} conforms the role-convention (a tagged-value for the {@link #ROLE_URI} exists).
	 * {@see #isRole}
	 */
	public static boolean isRole(final Stereotype stereotype) {
		return stereotype.getTaggedValue(ROLE_URI) != null;
	}
	
	/**
	 * Tests whether a {@link Stereotype} is a system-role.
	 * {@see #isSystemRole}
	 */
	public static boolean isSystemRole(final Stereotype stereotype) {
		return isRole (stereotype) && stereotype.getName().endsWith(SYSTEM_ROLE_NAME_SUFFIX);
	}

	/**
	 * Tests whether a {@link Profile} is an Architecural-Template.
	 * {@see #isArchitecturalTemplate}
	 */
	public static boolean isArchitecturalTemplate(final Profile profile) {

		int count = 0;

		for (Stereotype s : profile.getStereotypes())
		{
			if (!isRole(s)) return false;
			if (isSystemRole(s)) count++;
		}
		
		return count == 1;
	}

	/**
	 * Gets the {@link Stereotype} that represents the system-role for the given {@link Profile}.
	 * @param profile the ArchitecturalTemplate-{@link Profile}
	 * @return the SystemRole-{@link Stereotype}
	 * @throws RuntimeException if the given profile is no Architectural Template
	 */
	public static Stereotype getSystemRoleStereotype(final Profile profile) {
		if (!isArchitecturalTemplate(profile)) {
			throw new RuntimeException("Profile \"" + profile
					+ "\" is no Architectural Template");
		}
		
		for (Stereotype s : profile.getStereotypes())
		{
			if (isSystemRole(s)) return s;
		}
		
		return null;
	}

	/**
	 * Applies the given {@link AT} to the {@link System}.
	 * @param system the {@link System}
	 * @param architecturalTemplate the {@link AT}
	 * @throws RuntimeException if the Architectural Template does not define any roles.
	 * @see #applyArchitecturalTemplate(System, Profile)
	 */
	public static void applyArchitecturalTemplate(final System system,
			final AT architecturalTemplate) {
		if (architecturalTemplate.getRoles().size() == 0) {
			throw new RuntimeException("Architectural Template \""
					+ architecturalTemplate + "\" does not contain any roles");
		}

		applyArchitecturalTemplate(system, architecturalTemplate.getRoles()
				.get(0).getStereotype().getProfile());
	}

	/**
	 * Applies the given Architectural-Template-{@link Profile} to the {@link System}.
	 * @param system the {@link System}
	 * @param profile the {@link Profile}
	 * @throws RuntimeException if the profile does not define an Architectural Template.
	 */
	public static void applyArchitecturalTemplate(final System system,
			final Profile profile) {
		if (!isArchitecturalTemplate(profile)) {
			throw new RuntimeException("Profile \"" + profile
					+ "\" is no Architectural Template");
		}

		final Stereotype systemRoleStereotype = getSystemRoleStereotype(profile);

		ProfileAPI.applyProfile(system.eResource(), profile);
		StereotypeAPI.applyStereotype(system, systemRoleStereotype);
	}

	/**
	 * Unapplies the given {@link AT} from the {@link System}.
	 * @param system the {@link System}
	 * @param architecturalTemplate the {@link AT}
	 * @throws RuntimeException if the Architectural Template does not define any roles.
	 * @see #unapplyArchitecturalTemplate(System, Profile)
	 */
	public static void unapplyArchitecturalTemplate(final System system,
			final AT architecturalTemplate) {
		if (architecturalTemplate.getRoles().size() == 0) {
			throw new RuntimeException("Architectural Template \""
					+ architecturalTemplate + "\" does not contain any roles");
		}

		unapplyArchitecturalTemplate(system, architecturalTemplate.getRoles()
				.get(0).getStereotype().getProfile());
	}

	/**
	 * Unapplies the given Architectural-Template-{@link Profile} from the {@link System}.
	 * @param system the {@link System}
	 * @param profile the {@link Profile}
	 * @throws RuntimeException if the profile does not define an Architectural Template.
	 */
	public static void unapplyArchitecturalTemplate(System system,
			Profile profile) {
		if (!isArchitecturalTemplate(profile)) {
			throw new RuntimeException("Profile \"" + profile
					+ "\" is no Architectural Template");
		}
		ProfileAPI.unapplyProfile(system.eResource(), profile);
	}

	/**
	 * Applies the given {@link Role} to the {@link AssemblyContext}.
	 * @param assemblyContext the {@link AssemblyContext}
	 * @param role the {@link Role}
	 */
	public static void applyRole(final AssemblyContext assemblyContext,
			final Role role) {
		StereotypeAPI.applyStereotype(assemblyContext, role.getStereotype());
	}

	/**
	 * Applies the given Role-{@link Stereotype} to the {@link AssemblyContext}.
	 * @param assemblyContext the {@link AssemblyContext}
	 * @param stereotype the {@link Stereotype}
	 * @throws RuntimeException if the given stereotype does not conform the role-convention.
	 */
	public static void applyRole(final AssemblyContext assemblyContext,
			final Stereotype stereotype) {
		if (!isRole(stereotype)) {
			throw new RuntimeException("Stereotype \"" + stereotype
					+ "\" is no role");
		}
		StereotypeAPI.applyStereotype(assemblyContext, stereotype);
	}

	/**
	 * Unapplies the given {@link Role} from the {@link AssemblyContext}.
	 * @param assemblyContext the {@link AssemblyContext}
	 * @param role the {@link Role}
	 */
	public static void unapplyRole(final AssemblyContext assemblyContext,
			final Role role) {
		StereotypeAPI.unapplyStereotype(assemblyContext, role.getStereotype());
	}

	/**
	 * Unapplies the given Role-{@link Stereotype} from the {@link AssemblyContext}.
	 * @param assemblyContext the {@link AssemblyContext}
	 * @param stereotype the {@link Stereotype}
	 * @throws RuntimeException if the given stereotype does not conform the role-convention.
	 */
	public static void unapplyRole(final AssemblyContext assemblyContext,
			final Stereotype stereotype) {
		if (!isRole(stereotype)) {
			throw new RuntimeException("Stereotype \"" + stereotype
					+ "\" is no role");
		}
		StereotypeAPI.unapplyStereotype(assemblyContext, stereotype);
	}

}
