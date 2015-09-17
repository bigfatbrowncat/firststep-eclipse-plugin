package firststep.plugin.views;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import firststep.SWTFramebuffer;
import firststep.SWTFramebufferHandler;
import firststep.plugin.Activator;

public class OpenGLViewer extends Viewer {
	
	private static SWTFramebuffer mainFramebuffer;

	private volatile Object renderableInstance;
	private volatile long startTimeMillis;

	private RenderErrorView errorView;
	private StackLayout stackLayout;

	private Composite parent;

	public SWTFramebuffer getMainFramebuffer() {
		return mainFramebuffer;
	}

	ClassLoader classLoader;

	public OpenGLViewer(Composite parent, int style) throws ReflectiveOperationException {
		this.parent = parent;
		stackLayout = new StackLayout();
		parent.setLayout(stackLayout);

		classLoader = Activator.getFirstStepClassLoader();
		
		errorView = new RenderErrorView(parent, style);

		mainFramebuffer = new SWTFramebuffer(classLoader, parent, SWT.NO_BACKGROUND);

		mainFramebuffer.setHandler(new SWTFramebufferHandler() {
			
			@Override
			public void handleException(Throwable t) {
				setMessageException(classLoader, t);
			}
			
			@Override
			public void draw(SWTFramebuffer framebuffer) throws Exception {
				Class<?> framebufferClass = classLoader.loadClass("firststep.Framebuffer");

				if (renderableInstance != null) {
					
					Class<?> renderableClass = renderableInstance.getClass();
					
					Method renderMethod = renderableClass.getMethod("render", framebufferClass);

					// Beginning the drawing process outside of a Renderable
					framebuffer.beginDrawing();
					renderMethod.invoke(renderableInstance, framebuffer.getPrimaryFramebuffer());
					// Ending the drawing process outside of a Renderable
					framebuffer.endDrawing();
					
					//setMessageException(null);
				}
			}
			
			@Override
			public void frame() {
				try {
					if (classLoader != null && renderableInstance != null) {
						Class<?> renderableInterface = classLoader.loadClass("firststep.contracts.Renderable");
						Class<?> animatableInterface = classLoader.loadClass("firststep.contracts.Animatable");
						Class<?> renderableClass = renderableInstance.getClass();
						
						if (animatableInterface.isAssignableFrom(renderableClass)) {
							
							Method getDurationMethod = renderableClass.getMethod("getDuration");
							Method setCurrentTime = renderableClass.getMethod("setCurrentTime", float.class);
							
							Float timeDuration = (Float) getDurationMethod.invoke(renderableInstance);
							long curTimeMillis = System.currentTimeMillis();
							float curTimeFloat = (float)(curTimeMillis - startTimeMillis) / 1000;
							if (timeDuration != null) {
								while (curTimeFloat > timeDuration) curTimeFloat -= timeDuration;
								startTimeMillis = (long)(curTimeMillis - 1000 * curTimeFloat);
							}
							setCurrentTime.invoke(renderableInstance, curTimeFloat);
						}
					} /*else {
						System.out.println("Empty");
					}*/
				} catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				}
				
				/*Display.getDefault().syncExec(new Runnable() {
					
					@Override
					public void run() {
						if (!mainFramebuffer.isDisposed()) mainFramebuffer.redraw();
					}
				});*/
				
			}
			
		});
	}
	
	private void setNewRenderableInstance(ClassLoader classLoader, Object renderableInstance) {
		this.classLoader = classLoader;
		this.renderableInstance = renderableInstance;
		this.startTimeMillis = System.currentTimeMillis();
		
		setMessageException(classLoader, null);
		refresh();
	}
	
	@Override
	public Control getControl() {
		if (mainFramebuffer != null && !mainFramebuffer.isDisposed()) {
			return mainFramebuffer.getControl();
		} else {
			return errorView;
		}
	}

	@Override
	public Object getInput() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISelection getSelection() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void refresh() {
		if (mainFramebuffer != null && !mainFramebuffer.isDisposed()) mainFramebuffer.redraw();
	}

	public void setMessageException(ClassLoader classLoader, Throwable exception) {
		if (exception != null) 	this.renderableInstance = null;
		
		if (exception == null) {
/*			if (mainFramebuffer != null) {
				if (!mainFramebuffer.isDisposed()) mainFramebuffer.dispose();
			}
	*/		
			stackLayout.topControl = mainFramebuffer.getControl();

		} else {
			stackLayout.topControl = errorView;
			errorView.setException(exception);
		}
		parent.layout();
	}
	
	private boolean findSuperInterfaceRecursively(ClassLoader classLoader, Class<?> clz, Class<?> iface) throws JavaModelException, ClassNotFoundException {
		if (clz == null) return false;
		if (clz.getInterfaces() != null) {
			for (Class<?> intf : clz.getInterfaces()) {
				if (intf.equals(iface)) return true;
				if (findSuperInterfaceRecursively(classLoader, intf, iface)) return true;
			}
		}
		if (findSuperInterfaceRecursively(classLoader, clz.getSuperclass(), iface)) return true;
		return false;
	}
	private boolean findSuperInterfaceRecursively(ClassLoader classLoader, IType t, String pkg, String name) throws JavaModelException, ClassNotFoundException {
		Class<?> clz = classLoader.loadClass(t.getFullyQualifiedName());
		Class<?> iface = classLoader.loadClass(pkg + "." + name);
		return findSuperInterfaceRecursively(classLoader, clz, iface);
	}
	private void appendBySuperInterface(ClassLoader classLoader, ArrayList<IType> res, Map<IType, IClassFile> classFiles, String pkg, String name) throws JavaModelException, ClassNotFoundException {
    	ArrayList<IType> filteredTypes = res;
		for (IType t : classFiles.keySet()) {
			if (findSuperInterfaceRecursively(classLoader, t, pkg, name)) filteredTypes.add(t);
		}
	}
	public boolean drawRenderable(ClassLoader ucl, Map<IType, IClassFile> classFiles) throws JavaModelException, IOException, ReflectiveOperationException {
		ArrayList<IType> renderableTypes = new ArrayList<>();
    	appendBySuperInterface(ucl, renderableTypes, classFiles, "firststep.contracts", "Renderable");
    	appendBySuperInterface(ucl, renderableTypes, classFiles, "firststep.contracts", "Animatable");
		
		if (renderableTypes.size() == 0) return false;

		
		IType tp = renderableTypes.get(0);	// TODO Support multiple types
		Object loadedRenderableInstance = null;
		if (mainFramebuffer != null && mainFramebuffer.getPrimaryFramebuffer() != null)
		{
			Class<?> loadedRenderableClass = ucl.loadClass(tp.getFullyQualifiedName());
				
			Constructor<?> loadedRenderableConstructor = loadedRenderableClass.getConstructor();
			loadedRenderableInstance = loadedRenderableConstructor.newInstance();

		}
		setNewRenderableInstance(ucl, loadedRenderableInstance);
		return true;
	}

	
	@Override
	public void setInput(Object input) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSelection(ISelection selection, boolean reveal) {
		// TODO Auto-generated method stub

	}

	public void dispose() {
		if (!mainFramebuffer.isDisposed()) mainFramebuffer.dispose();
		if (!errorView.isDisposed()) errorView.dispose();
	}
}
