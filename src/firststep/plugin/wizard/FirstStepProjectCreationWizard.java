/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package firststep.plugin.wizard;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

import firststep.plugin.Messages;

/**
 * This example shows how to implement an own project wizard that uses the
 * JavaCapabilityConfigurationPage to allow the user to configure the Java build path.
 */
public class FirstStepProjectCreationWizard extends Wizard implements IExecutableExtension, INewWizard {

	private NewFirstStepProjectWizardPageOne fMainPage;
	private NewFirstStepProjectWizardPageTwo fJavaPage;
	//private IWizardPage fExtraPage;

	private IConfigurationElement fConfigElement;

	public FirstStepProjectCreationWizard() {
		setWindowTitle("New FirstStep Project");
	}
	
	@Override
	public void setInitializationData(IConfigurationElement cfig, String propertyName, Object data) {
		//  The config element will be used in <code>finishPage</code> to set the result perspective.
		fConfigElement= cfig;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	@Override
	public void addPages() {
		super.addPages();

		fMainPage = new NewFirstStepProjectWizardPageOne();
		fMainPage.setProjectName("FirstStepProject");
		fMainPage.setTitle(Messages.NewProjectTitle);
		fMainPage.setDescription(Messages.NewProjectDesc);

		// the main page
		addPage(fMainPage);

		// the Java build path configuration page
		fJavaPage = new NewFirstStepProjectWizardPageTwo(fMainPage);

		addPage(fJavaPage);

	}


	@Override
	public boolean performFinish() {
		WorkspaceModifyOperation op= new WorkspaceModifyOperation() {
			@Override
			protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException {
				fJavaPage.performFinish(monitor);
				// use the result from the extra page
				
			}
		};
		try {
			getContainer().run(false, true, op);

			IJavaProject newElement= fJavaPage.getJavaProject();

			IWorkingSet[] workingSets= fMainPage.getWorkingSets();
			if (workingSets.length > 0) {
				PlatformUI.getWorkbench().getWorkingSetManager().addToWorkingSets(newElement, workingSets);
			}
			BasicNewProjectResourceWizard.updatePerspective(fConfigElement);
			BasicNewResourceWizard.selectAndReveal(newElement.getResource(), PlatformUI.getWorkbench().getActiveWorkbenchWindow());


		} catch (InvocationTargetException e) {
			return false; // TODO: should open error dialog and log
		} catch  (InterruptedException e) {
			return false; // canceled
		}
		return true;
	}

	@Override
	public boolean performCancel() {
		fJavaPage.performCancel();
		return true;
	}



}