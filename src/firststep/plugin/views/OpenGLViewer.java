package firststep.plugin.views;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import firststep.Framebuffer;
import firststep.SWTFramebuffer;

public class OpenGLViewer extends Viewer {
	
	private static SWTFramebuffer mainFramebuffer;

	private volatile ClassLoader classLoader;
	private volatile Object renderableInstance;
	private volatile long startTimeMillis;

	private RenderErrorView errorView;
	private StackLayout stackLayout;

	private Composite parent;

	public SWTFramebuffer getMainFramebuffer() {
		return mainFramebuffer;
	}
	
	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public OpenGLViewer(Composite parent, int style) {
		this.parent = parent;
		stackLayout = new StackLayout();
		parent.setLayout(stackLayout);

		errorView = new RenderErrorView(parent, style);
		mainFramebuffer = new SWTFramebuffer(parent, style | SWT.NO_BACKGROUND);
		mainFramebuffer.setHandler(new SWTFramebuffer.Handler() {
			
			@Override
			public void handleException(Throwable t) {
				setMessageException(t);
			}
			
			@Override
			public void draw(Framebuffer framebuffer) throws Exception {
				Class<?> framebufferClass = classLoader.loadClass("firststep.Framebuffer");

				if (renderableInstance != null) {
					
					Class<?> renderableClass = renderableInstance.getClass();
					
					Method renderMethod = renderableClass.getMethod("render", framebufferClass);

					// Beginning the drawing process outside of a Renderable
					framebuffer.beginDrawing();
					renderMethod.invoke(renderableInstance, framebuffer);
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
					}
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
	
	public void setNewRenderableInstance(ClassLoader classLoader, Object renderableInstance) {
		this.classLoader = classLoader;
		this.renderableInstance = renderableInstance;
		this.startTimeMillis = System.currentTimeMillis();
		
		setMessageException(null);
		refresh();
	}
	
	@Override
	public Control getControl() {
		return mainFramebuffer.getControl();
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
		mainFramebuffer.redraw();
	}

	public void setMessageException(Throwable exception) {
		if (exception == null) {
			stackLayout.topControl = mainFramebuffer.getControl();
		} else {
			stackLayout.topControl = errorView;
			errorView.setException(exception);
		}
		parent.layout();
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
