package firststep;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.opengl.WrappedGLCanvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import firststep.Framebuffer;
import firststep.internal.GL3W;

public class SWTFramebuffer extends Framebuffer {

	public interface ExceptionHandler {
		void handle(Throwable t);
	}
	
	private static boolean gl3wInitialized = false;

	private WrappedGLCanvas glCanvas;
	private Object loadedRenderableInstance;
	private Method renderMethod;
	private ExceptionHandler exceptionHandler;
	
	public void setExceptionHandler(ExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

	private static WrappedGLCanvas createCanvas(Composite parent, int style) {
		GLData data = new GLData();
		data.doubleBuffer = true;
		
		WrappedGLCanvas glCanvas = new WrappedGLCanvas(parent, style | SWT.NO_BACKGROUND, data);
		glCanvas.setCurrent();

		if (!gl3wInitialized) {
			GL3W.init();
			gl3wInitialized = true;
		}
		//mainFramebuffer = new MainFramebuffer(glCanvas.getSize().x, glCanvas.getSize().y);
		System.out.println("OpenGL version: " + GL3W.getGLVersionMajor() + "." + GL3W.getGLSLVersionMinor());
		
		return glCanvas;
	}
	
	private SWTFramebuffer(WrappedGLCanvas canvas) {
		super(100, 100);
		this.glCanvas = canvas;
	}
	
	public SWTFramebuffer(Composite parent, int style) {
		this(createCanvas(parent, style));
		
		glCanvas.addControlListener(new ControlListener() {
			
			@Override
			public void controlResized(ControlEvent e) {
				resize(glCanvas.getSize().x, glCanvas.getSize().y);
			}
			
			@Override public void controlMoved(ControlEvent e) { }
		});
		
		glCanvas.addPaintListener(new PaintListener() {
			
			@Override
			public void paintControl(PaintEvent e) {
				glCanvas.setCurrent();
				
				try {
					clearDrawingStack();
					if (renderMethod != null && loadedRenderableInstance != null) {
						
						// Beginning the drawing process outside of a Renderable
						beginDrawing();
						renderMethod.invoke(loadedRenderableInstance, SWTFramebuffer.this);
						// Ending the drawing process outside of a Renderable
						endDrawing();
						
						checkStackClear();
						//setMessageException(null);
					} else {
						GL3W.glClearColor(0.4f, 0.2f, 0.2f, 1.0f);
						GL3W.glClear(GL3W.GL_COLOR_BUFFER_BIT | GL3W.GL_STENCIL_BUFFER_BIT | GL3W.GL_DEPTH_BUFFER_BIT);
					}
				} catch (IllegalAccessException | IllegalArgumentException e1) {
					//e1.printStackTrace();
					if (exceptionHandler != null) exceptionHandler.handle(e1);
					//setMessageException(e1);
				} catch (InvocationTargetException e1) {
					//e1.printStackTrace();
					if (e1.getCause() instanceof Error) {
						if (exceptionHandler != null) exceptionHandler.handle(new RuntimeException("Can't execute the file. That probably means compilation error. Check Problems list", e1));
					} else {
						if (exceptionHandler != null) exceptionHandler.handle(e1.getCause());
					}
				}
				catch (Exception e2) {
					//e2.printStackTrace();
					//setMessageException(e2);
					if (exceptionHandler != null) exceptionHandler.handle(e2);
				}
				
				glCanvas.swapBuffers();
			}
		});
	}

	public Control getControl() {
		return glCanvas;
	}
	
	/*@Override
	public void clearDrawingStack() {
		super.clearDrawingStack();
	}
	
	@Override
	public void checkStackClear() {
		super.checkStackClear();
	}*/
	
	public void setRenderMethod(Object loadedRenderableInstance, Method renderMethod) {
		this.renderMethod = renderMethod;
		this.loadedRenderableInstance = loadedRenderableInstance;
		//refresh();
	}

	public void redraw() {
		glCanvas.redraw();
	}
	
	public void dispose() {
		if (!glCanvas.isDisposed()) glCanvas.dispose();
	}
	
	public boolean isDisposed() {
		return glCanvas.isDisposed();
	}
}
