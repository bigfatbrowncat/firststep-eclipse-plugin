package tools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

public class JavaTools {
	public static List<IClasspathEntry> getSourceContainerEntries(IJavaProject jp) throws JavaModelException {
		List<IClasspathEntry> containers = new ArrayList<>(10);
		IProject project = jp.getProject();
		if(project.isAccessible() && jp.exists()) {
			IClasspathEntry entries[] = jp.getResolvedClasspath(true);
			for(int i=0; i<entries.length; i++) {
				IClasspathEntry entry = entries[i];
				if(entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
					containers.add(entry);
				}
			}
		}
		
		return containers;
	}
	
	public static List<IClasspathEntry> getJarContainerEntries(IJavaProject jp) throws JavaModelException {
		List<IClasspathEntry> containers = new ArrayList<IClasspathEntry>(10);
		IProject project = jp.getProject();
		if(project.isAccessible() && jp.exists()) {
			IClasspathEntry entries[] = jp.getResolvedClasspath(true);
			for(int i=0; i<entries.length; i++) {
				IClasspathEntry entry = entries[i];
				if (entry.getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
					containers.add(entry);
				}
			}
		}
		
		return containers;
	}
	
	public static IClasspathEntry getSourceContainerEntry(IContainer container) throws JavaModelException {
		IJavaProject jp = JavaCore.create(container.getProject());
		List entries = getSourceContainerEntries(jp);
		Iterator iterator = entries.iterator();
		while(iterator.hasNext()) {
			IClasspathEntry entry = (IClasspathEntry)iterator.next();
			if(entry.getPath().isPrefixOf(container.getFullPath())){
				return entry;
			}
		}
		return null;
	}
}

