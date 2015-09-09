package first_step_plugin;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.ISavedState;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.DisplayAccess;
import org.eclipse.ui.internal.UISynchronizer;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import firststepplugin.views.FirstStepPreviewView;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "first-step-plugin"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	private void updateGraphicsView() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			FirstStepPreviewView view = (FirstStepPreviewView)window.getActivePage().findView(FirstStepPreviewView.ID);
			if (view != null) {
				view.updateImage();
			}
		}
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	
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
		workspace.addResourceChangeListener(listener,
				IResourceChangeEvent.POST_BUILD/*
												 * | IResourceChangeEvent.
												 * POST_CHANGE
												 */);
		
		 Workbench.getInstance().getActiveWorkbenchWindow().getPartService().addPartListener(new IPartListener() {
			
			@Override
			public void partOpened(IWorkbenchPart part) { }
			
			@Override
			public void partDeactivated(IWorkbenchPart part) { }
			
			@Override
			public void partClosed(IWorkbenchPart part) {
				updateGraphicsView();
			}
			
			@Override
			public void partBroughtToTop(IWorkbenchPart part) { }
			
			@Override
			public void partActivated(IWorkbenchPart part) {
				updateGraphicsView();
			}
		});
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
