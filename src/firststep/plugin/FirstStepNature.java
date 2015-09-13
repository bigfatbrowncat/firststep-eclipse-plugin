package firststep.plugin;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import firststep.plugin.container.FirstStepContainer;
import firststep.plugin.container.FirstStepContainerInitializer;

public class FirstStepNature implements IProjectNature {

	public static final String NATURE_ID = "firststep.plugin.FirstStepNature";

	private void addFirstStepLibrary(IProject project) {
		FirstStepContainerInitializer initializer = (FirstStepContainerInitializer) JavaCore.getClasspathContainerInitializer(FirstStepContainer.ID);
		IPath path = FirstStepContainer.ID_PATH;
		
		IJavaProject ijp = JavaCore.create(project);
		try {
			initializer.initialize(path, ijp);
			IClasspathEntry a_container = JavaCore.newContainerEntry(path);
			IClasspathEntry[] old_containers = ijp.getRawClasspath();
			IClasspathEntry[] all_containers = new IClasspathEntry[old_containers.length + 1];
			for (int i = 0; i < old_containers.length; i++) {
				all_containers[i] = old_containers[i];
			}
			all_containers[old_containers.length] = a_container;
			ijp.setRawClasspath(all_containers, true, null);
		} catch (CoreException e) {
			// e.printStackTrace();
		}
		
	}
	
	@Override
	public void configure() throws CoreException {
		addFirstStepLibrary(getProject());
	}

	@Override
	public void deconfigure() throws CoreException {
		// TODO Auto-generated method stub

	}

	private IProject project;
	
	@Override
	public IProject getProject() {
		return project;
	}

	@Override
	public void setProject(IProject project) {
		this.project = project;

	}

}
