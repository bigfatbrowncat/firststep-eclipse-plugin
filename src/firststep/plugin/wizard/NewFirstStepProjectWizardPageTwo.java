package firststep.plugin.wizard;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageOne;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageTwo;

import firststep.plugin.FirstStepNature;

public class NewFirstStepProjectWizardPageTwo extends NewJavaProjectWizardPageTwo {

	public NewFirstStepProjectWizardPageTwo(NewJavaProjectWizardPageOne mainPage) {
		super(mainPage);
	}
	
	
	
	public static void addFirstStepNature(IProject project, IProgressMonitor monitor) throws CoreException {
		if (monitor != null && monitor.isCanceled()) {
			throw new OperationCanceledException();
		}
		if (!project.hasNature(FirstStepNature.NATURE_ID)) {
			IProjectDescription description = project.getDescription();
			String[] prevNatures= description.getNatureIds();
			String[] newNatures = new String[prevNatures.length + 1];
			System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
			newNatures[prevNatures.length] = FirstStepNature.NATURE_ID;
			description.setNatureIds(newNatures);
			project.setDescription(description, monitor);
		} else {
			if (monitor != null) {
				monitor.worked(1);
			}
		}
	}
	
	@Override
	public void performFinish(IProgressMonitor monitor) throws CoreException, InterruptedException {
		super.performFinish(monitor);
		
		IProject project = getJavaProject().getProject();
		addFirstStepNature(project, monitor);
	}
	
	
}
