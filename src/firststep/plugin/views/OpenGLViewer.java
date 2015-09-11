package firststep.plugin.views;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.opengl.WrappedGLCanvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import firststep.MainFramebuffer;
import firststep.internal.GL3W;

public class OpenGLViewer extends Viewer {
	
	private static MainFramebuffer mainFramebuffer;

	private WrappedGLCanvas glCanvas;
	private RenderErrorView errorView;
	private StackLayout stackLayout;
	
	private Object loadedRenderableInstance;
	private Method renderMethod;
	
	public void setRenderMethod(Object loadedRenderableInstance, Method renderMethod) {
		this.renderMethod = renderMethod;
		this.loadedRenderableInstance = loadedRenderableInstance;
		refresh();
	}
	
	public MainFramebuffer getMainFramebuffer() {
		return mainFramebuffer;
	}
	
	private Composite parent;
	
	private static boolean gl3wInitialized;

	public OpenGLViewer(Composite parent, int style) {
		this.parent = parent;
		stackLayout = new StackLayout();
		parent.setLayout(stackLayout);

		GLData data = new GLData();
		data.doubleBuffer = true;
		errorView = new RenderErrorView(parent, style);
		
		glCanvas = new WrappedGLCanvas(parent, style | SWT.NO_BACKGROUND, data);
		glCanvas.setCurrent();
		if (!gl3wInitialized) {
			GL3W.init();
			gl3wInitialized = true;
		}
		mainFramebuffer = new MainFramebuffer(glCanvas.getSize().x, glCanvas.getSize().y);
		System.out.println("OpenGL version: " + GL3W.getGLVersionMajor() + "." + GL3W.getGLSLVersionMinor());
		
//		glCanvas.setVisible(true);
		
		glCanvas.addControlListener(new ControlListener() {
			
			@Override
			public void controlResized(ControlEvent e) {
				mainFramebuffer.resize(glCanvas.getSize().x, glCanvas.getSize().y);
			}
			
			@Override
			public void controlMoved(ControlEvent e) { }
		});
		
		glCanvas.addPaintListener(new PaintListener() {
			
			@Override
			public void paintControl(PaintEvent e) {
				glCanvas.setCurrent();
				
				try {
					mainFramebuffer.clearDrawingStack();
					if (renderMethod != null && loadedRenderableInstance != null) {
						
						// Beginning the drawing process outside of a Renderable
						mainFramebuffer.beginDrawing();
						renderMethod.invoke(loadedRenderableInstance, mainFramebuffer);
						// Ending the drawing process outside of a Renderable
						mainFramebuffer.endDrawing();
						
						mainFramebuffer.checkStackClear();
						setMessageException(null);
					} else {
						GL3W.glClearColor(0.4f, 0.2f, 0.2f, 1.0f);
						GL3W.glClear(GL3W.GL_COLOR_BUFFER_BIT | GL3W.GL_STENCIL_BUFFER_BIT | GL3W.GL_DEPTH_BUFFER_BIT);
					}
				} catch (IllegalAccessException | IllegalArgumentException e1) {
					setMessageException(e1);
				} catch (InvocationTargetException e1) {
					setMessageException(e1.getCause());
				}
				catch (Exception e2) {
					setMessageException(e2);
				}
				
				glCanvas.swapBuffers();
			}
		});
	}
	
	@Override
	public Control getControl() {
		return glCanvas;
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
		glCanvas.redraw();
	}

	public void setMessageException(Throwable exception) {
		if (exception == null) {
			stackLayout.topControl = glCanvas;
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
		if (!glCanvas.isDisposed()) glCanvas.dispose();
		if (!errorView.isDisposed()) errorView.dispose();
	}
}
