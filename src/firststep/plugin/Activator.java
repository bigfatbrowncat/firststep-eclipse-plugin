package firststep.plugin;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import firststep.plugin.views.FirstStepPreviewView;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "firststep.plugin"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	private static class FirstStepClassLoader extends URLClassLoader {
		private String path;
		
		public FirstStepClassLoader(String path) throws MalformedURLException {
			super(new URL[] { new URL("file://" + path + "/firststep-java.jar") }, FirstStepClassLoader.class.getClassLoader());
			this.path = path;
		}
		
		void check() {
			try {
				Class.forName("firststep.Window", true, this);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("Can't load firststep library", e);
			}
		}
		
		@Override
		protected String findLibrary(String libname) {
			System.out.println("Native path: " + path);
			return path + "/" + libname + ".dll";
		}

	}
	
	private static FirstStepClassLoader firstStepClassLoader;
	public static FirstStepClassLoader getFirstStepClassLoader() {
		return firstStepClassLoader;
	}
	
	private void updateGraphicsView() {
		try {
		
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			if (window != null) {
				FirstStepPreviewView view = (FirstStepPreviewView)window.getActivePage().findView(FirstStepPreviewView.ID);
				if (view != null) {
					view.updateImage();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		// TODO Get the actual path
		try {
			firstStepClassLoader = new FirstStepClassLoader("C:\\mm\\msys\\home\\imizus\\Projects\\firststep-eclipse-plugin\\lib");
			firstStepClassLoader.check();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IResourceChangeListener listener = new IResourceChangeListener() {
			public void resourceChanged(IResourceChangeEvent event) {
				Display.getDefault().syncExec(new Runnable() {
					
					@Override
					public void run() {
						updateGraphicsView();
					}
				});
			}
		};
		workspace.addResourceChangeListener(listener, IResourceChangeEvent.POST_BUILD);
		
		
		if (PlatformUI.getWorkbench() != null && 
			PlatformUI.getWorkbench().getActiveWorkbenchWindow() != null) {

			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().addPartListener(new IPartListener() {
				
				@Override
				public void partOpened(IWorkbenchPart part) { 
					updateGraphicsView();
				}
				
				@Override
				public void partDeactivated(IWorkbenchPart part) { }
				
				@Override
				public void partClosed(IWorkbenchPart part) {
					//updateGraphicsView();
				}
				
				@Override
				public void partBroughtToTop(IWorkbenchPart part) { }
				
				@Override
				public void partActivated(IWorkbenchPart part) {
					if (part instanceof IEditorPart) {
						updateGraphicsView();
					}
				}
			});
		}
	}
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
