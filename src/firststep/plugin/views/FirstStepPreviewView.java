package firststep.plugin.views;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class FirstStepPreviewView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "firststep.plugin.views.FirstStepPreviewView";

	private OpenGLViewer viewer;
	private Action action1;
	private Action action2;
	private Action doubleClickAction;

	/*
	 * The content provider class is responsible for
	 * providing objects to the view. It can wrap
	 * existing objects in adapters or simply return
	 * objects as-is. These objects may be sensitive
	 * to the current input of the view, or ignore
	 * it and always show the same content 
	 * (like Task List, for example).
	 */
	 
	/*class ViewContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}
		public void dispose() {
		}
		public Object[] getElements(Object parent) {
			return new String[] { "One", "Two", "Three" };
		}
	}
	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}
		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}
		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().
					getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}
	class NameSorter extends ViewerSorter {
	}*/

	/**
	 * The constructor.
	 */
	public FirstStepPreviewView() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new OpenGLViewer(parent, 0);
		
		//viewer.setContentProvider(new ViewContentProvider());
		//viewer.setLabelProvider(new ViewLabelProvider());
		//viewer.setSorter(new NameSorter());
		viewer.setInput(getViewSite());

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "first-step-plugin.viewer");
		getSite().setSelectionProvider(viewer);
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				FirstStepPreviewView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(action1);
		manager.add(new Separator());
		manager.add(action2);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1);
		manager.add(action2);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
		manager.add(action2);
	}

	private void makeActions() {
		action1 = new Action() {
			public void run() {
				
				
				//String content = IOUtils.toString(file.getContents(), file.getCharset());
			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
		action2 = new Action() {
			public void run() {
				showMessage("Action 2 executed");
			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
		action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				showMessage("Double-click detected on "+obj.toString());
			}
		};
	}

	private void hookDoubleClickAction() {
		/*viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});*/
	}
	private void showMessage(String message) {
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			"FirstStep Graphics",
			message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

   	public static List getSourceContainerEntries(IJavaProject jp) throws JavaModelException {
   		List containers = new ArrayList(10);
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
				if(entry.getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
					containers.add(entry);
				}
			}
   		}
		
		return containers;
   	}

   	private static IClasspathEntry getSourceContainerEntry(IContainer container) throws JavaModelException {
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
	
	/**
	 *  Return the output container where this file will either be copied or compiled into.
	 */
	public static IContainer getOutputContainer(IProject prj, IFile res) throws JavaModelException {
    	IContainer container = res.getParent();
    	IClasspathEntry sourceContainer = getSourceContainerEntry(container);
    	if(sourceContainer == null) {
    		return null;
    	}
    	
		IPath outputPath = sourceContainer.getOutputLocation();
		if(outputPath == null) {
			IJavaProject jp = JavaCore.create(res.getProject());
			outputPath = jp.getOutputLocation();
		}
		return (IContainer)prj.getParent().findMember(outputPath);
	}
	
	public static final String DOT_CLASS_EXTENSION = ".class"; //$NON-NLS-1$

	private static IClassFile getClassFile(IContainer outputContainer, IType type) {
		IClassFile cfile = type.getClassFile();
		if(cfile != null) {
			return cfile;
		}
		
		// Else, this type comes from a source type. 
		IPath newFile = new Path(type.getTypeQualifiedName() + DOT_CLASS_EXTENSION);
		IPackageFragment packageFragment = type.getPackageFragment();
		IPath newFilePackage = new Path(packageFragment.getElementName().replace('.', IPath.SEPARATOR));
		IFile file = outputContainer.getFile(newFilePackage.append(newFile));
		return getClassFile(file);
	}

	public static IClassFile getClassFile(IFile file) {
		IJavaElement element = JavaCore.create(file);
		IClassFile result = null;
		if((element != null) && (element.getElementType() == IJavaElement.CLASS_FILE)) {
			result = (IClassFile)element;
		}
		return result;
	}
	
	public static Map<IType, IClassFile> getClassFilesForAllTypesInFile(IProject prj, IFile javaFile) throws JavaModelException {
		HashMap<IType, IClassFile>res = new HashMap<>();
		
		ICompilationUnit cu = (ICompilationUnit)JavaCore.create(javaFile);
		if((cu == null) || (!cu.exists())) {
			return res;
		}
		
		IType[] types = cu.getAllTypes();
		//Set files = new HashSet(types.length);
		for(int i=0; i<types.length; i++) {
			IType type = types[i];
			IClassFile cfile = getClassFile(getOutputContainer(prj, javaFile), type);
			if(cfile != null) {
				res.put(type, cfile);
			}
		}
		
		return res;
	}
	
/*	public class CCLoader extends ClassLoader {
		//private HashMap<Class<?>> classes = new HashSet<>();
		private Class<?> theClass;
		private String className;
		private byte[] classdata = null;

		public CCLoader(ClassLoader classLoader) {
			super(classLoader);
		}

		public void setClassContent(String name, IClassFile classFile) throws JavaModelException {
			className = name;
			byte[] data = classFile.getBytes();
			classdata = new byte[data.length];
			System.arraycopy(data, 0, classdata, 0, data.length);
		}

		public Class<?> loadClass(String name) throws ClassNotFoundException {
			return findClass(name);
		}

		@Override
		public Class<?> findClass(String name) throws ClassNotFoundException {
			Class<?> result = null;
			try {
				if (name.replace('/', '.').equals(className)) {
					if (theClass == null) {
						theClass = defineClass(name, this.classdata, 0, this.classdata.length, null);
					}
					result = theClass;
				} else {
					result = super.findClass(name);
				}
				return result;
			} catch (SecurityException se) {
				
			} catch (Exception e) {
				System.out.println(e.toString());
				return null;
			}
			return result;
		}

	}*/

	public void updateImage() {
		try {
			
			IWorkbenchPart workbenchPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
			URI workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile().toURI();

			IEditorPart editor = (IEditorPart) workbenchPart.getSite().getPage().getActiveEditor();
			if (editor == null) throw new FileNotFoundException("No java editor open");
			
			IFile file = (IFile) editor.getEditorInput().getAdapter(IFile.class);
			if (file == null) throw new FileNotFoundException("The file being opened isn't a Java file");

			if (!file.getProject().hasNature(JavaCore.NATURE_ID) ||
				!file.getProject().isNatureEnabled(JavaCore.NATURE_ID)) {
			
				new IOException("The project being opened isn't a Java project");
			}
			
	    	IJavaProject javaProject = JavaCore.create(file.getProject());
			//ICompilationUnit javaFile = (ICompilationUnit) JavaCore.create(file);
			
	    	
	    	ArrayList<IType> renderableTypes = new ArrayList<>();
			Map<IType, IClassFile> classFiles = getClassFilesForAllTypesInFile(file.getProject(), file);
			for (IType t : classFiles.keySet()) {
				// Checking if superinterfaces list contains "firststep.Renderable"
				for (String sits : t.getSuperInterfaceNames()) {
					String[][] qname = t.resolveType(sits);
					if (qname != null && qname.length == 1 && 
							qname[0][0].equals("firststep.contracts") && 
							qname[0][1].equals("Renderable")) {
						
						renderableTypes.add(t);
						break;
					}
				}
			}
			
			if (renderableTypes.size() == 0) throw new IOException("There is no firststep.contracts.Renderable implementations in the opened file");

			// Constructing the target .class files folder
			// 1. Getting the raw path to the project
			IPath rawProjectPath = javaProject.getProject().getRawLocation();
			if (rawProjectPath == null) {
				rawProjectPath = javaProject.getProject().getLocation();
			}
			// 2. Getting the output path related to the workspace root
			IPath outputPath = javaProject.getOutputLocation();
			// 3. Cutting away the project folder from it
			outputPath = outputPath.removeFirstSegments(1);
			// 4. Concatenating
			IPath targetPath = rawProjectPath.append(outputPath).addTrailingSeparator();
			
			URL classURL = targetPath.toFile().toURI().toURL();
			ArrayList<URL> urls = new ArrayList<URL>();
			urls.add(classURL);
			List<IClasspathEntry> l = getJarContainerEntries(javaProject);
			for (IClasspathEntry e : l) {
				urls.add(e.getPath().toFile().toURI().toURL());
			}
			
			for (IType tp : renderableTypes) {

				try (URLClassLoader ucl = new URLClassLoader(urls.toArray(new URL[] {}), getClass().getClassLoader())) {
					
					Class<?> loadedRenderableClass = ucl.loadClass(tp.getFullyQualifiedName());
					Class<?> framebufferClass = ucl.loadClass("firststep.Framebuffer");
						
					Constructor<?> loadedRenderableConstructor = loadedRenderableClass.getConstructor();
					Object loadedRenderableInstance = loadedRenderableConstructor.newInstance();
					Method testMethod = loadedRenderableClass.getMethod("render", framebufferClass);

					viewer.setRenderMethod(loadedRenderableInstance, testMethod);
					viewer.setMessageException(null);
					viewer.refresh();
				}
			}
		} catch (IOException | CoreException | ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException e) {
			viewer.setRenderMethod(null, null);
			viewer.setMessageException(e);
		} catch (InvocationTargetException e) {
			viewer.setRenderMethod(null, null);
			viewer.setMessageException(e.getCause());
		}
	}
	
	@Override
	public void dispose() {
		viewer.dispose();
		super.dispose();
	}
}