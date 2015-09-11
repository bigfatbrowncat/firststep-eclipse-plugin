package firststep.plugin.container;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IAccessRule;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.osgi.framework.Bundle;

import firststep.plugin.Logger;

public class FirstStepContainer implements IClasspathContainer {
    public static final Path ID = new Path("firststep.plugin.container.FIRSTSTEP_CONTAINER");
    private static final String TITLE = "FirstStep Library";

    private IPath containerPath;
    private URL jarURL, libBaseURL;

    public FirstStepContainer(IPath path, IJavaProject project) {
        containerPath = path;

        Bundle bundle = Platform.getBundle(firststep.plugin.Activator.PLUGIN_ID);
    	try {
			libBaseURL = FileLocator.resolve(bundle.getEntry("/lib"));
			jarURL = FileLocator.resolve(bundle.getEntry("/lib/firststep-java.jar"));
			
		} catch (IOException e) {
			Logger.log(Logger.ERROR, "Can't find the FirstStep library file in the plugin folder. Cause: " + e.getMessage());
			throw new RuntimeException("Can't find the FirstStep library file in the plugin folder", e);
		}
    }

    public IClasspathEntry[] getClasspathEntries() {
        ArrayList<IClasspathEntry> entryList = new ArrayList<IClasspathEntry>();
        
		IPath location = new Path(jarURL.getPath());
		IPath srcPath = null;
		
		// Attribute "native library path" for libfirststep
		IClasspathAttribute nativeLibPath = JavaCore.newClasspathAttribute(JavaRuntime.CLASSPATH_ATTR_LIBRARY_PATH_ENTRY, libBaseURL.getPath());
		
		// Classpath entry for firststep-java.jar
		IClasspathEntry cpe = JavaCore.newLibraryEntry(
				location,
				srcPath,
				new Path("/"),
				new IAccessRule[] { },
				new IClasspathAttribute[] { nativeLibPath },
				false);
		
        entryList.add(cpe);
        
        // convert the list to an array and return it
        IClasspathEntry[] entryArray = new IClasspathEntry[entryList.size()];

        return (IClasspathEntry[]) entryList.toArray(entryArray);
    }

    public String getDescription() {
    	return TITLE;
    }

    public int getKind() {
        return IClasspathContainer.K_APPLICATION;
    }

    public IPath getPath() {
        return containerPath;
    }
}
