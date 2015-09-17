package firststep;

import java.lang.reflect.Constructor;
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
import org.eclipse.swt.widgets.Display;

//import firststep.Framebuffer;
//import firststep.internal.GL3W;

public class SWTFramebuffer {

	private static final int FPS = 30;
	/**
	 * private Framebuffer framebuffer;
	 */
	private Object primaryFramebuffer;

	private ClassLoader classLoader;
	
	public Object getPrimaryFramebuffer() {
		return primaryFramebuffer;
	}

	private class RefreshingThread extends Thread {
		volatile boolean killSelf = false;
		
		@Override
		public void run() {
			while (!killSelf) {
				try {
					if (handler != null) handler.frame();
					Display.getDefault().syncExec(new Runnable() {
						
						@Override
						public void run() {
							if (!glCanvas.isDisposed() && glCanvas.isVisible()) {
								glCanvas.redraw();
							}
						}
					});
					
					Thread.sleep(1000 / FPS);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private static boolean gl3wInitialized = false;

	private WrappedGLCanvas glCanvas;
	private volatile SWTFramebufferHandler handler;
	private RefreshingThread refreshingThread = new RefreshingThread();
	
	public void setHandler(SWTFramebufferHandler handler) {
		this.handler = handler;
	}

	private static WrappedGLCanvas createCanvas(ClassLoader classLoader, Composite parent, int style) throws ReflectiveOperationException {
		GLData data = new GLData();
		data.doubleBuffer = true;
		
		WrappedGLCanvas glCanvas = new WrappedGLCanvas(parent, style | SWT.NO_BACKGROUND, data);
		glCanvas.setCurrent();

		if (!gl3wInitialized) {
			Class<?> gl3wClass = Class.forName("firststep.internal.GL3W", true, classLoader);
			Method gl3wClassInitMethod = gl3wClass.getMethod("init");
			gl3wClassInitMethod.invoke(null);
			/* instead of
			GL3W.init(); */
			gl3wInitialized = true;
		}
		//System.out.println("OpenGL version: " + GL3W.getGLVersionMajor() + "." + GL3W.getGLSLVersionMinor());
		
		return glCanvas;
	}
	
	private SWTFramebuffer(ClassLoader classLoader, WrappedGLCanvas canvas) throws ReflectiveOperationException {
		this.classLoader = classLoader;
		
		//if (primaryFramebuffer == null) {
			Class<?> framebufferClass = Class.forName("firststep.Framebuffer", true, classLoader);
			Constructor<?> framebufferClassConstructor = framebufferClass.getDeclaredConstructor(int.class, int.class);
			framebufferClassConstructor.setAccessible(true);
			primaryFramebuffer = framebufferClassConstructor.newInstance(100, 100);
		//}
		/* instead of */
		//this.framebuffer = new Framebuffer(100, 100);
		
		this.glCanvas = canvas;
		refreshingThread.start();
	}
	
	public SWTFramebuffer(ClassLoader classLoader, Composite parent, int style) throws IllegalArgumentException, SecurityException, ReflectiveOperationException {
		this(classLoader, createCanvas(classLoader, parent, style));
		
		glCanvas.addControlListener(new ControlListener() {
			
			@Override
			public void controlResized(ControlEvent e) {
				try {
					Class<?> framebufferClass = Class.forName("firststep.Framebuffer", true, classLoader);
					Method framebufferClassResizeMethod;
					framebufferClassResizeMethod = framebufferClass.getMethod("resize", int.class, int.class);
					framebufferClassResizeMethod.invoke(primaryFramebuffer, glCanvas.getSize().x, glCanvas.getSize().y);
					/* instead of */
					//framebuffer.resize(glCanvas.getSize().x, glCanvas.getSize().y);
				} catch (ReflectiveOperationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			@Override public void controlMoved(ControlEvent e) { }
		});
		
		glCanvas.addPaintListener(new PaintListener() {
			
			@Override
			public void paintControl(PaintEvent e) {
				Class<?> framebufferClass;
				Method framebufferClassClearDrawingStackMethod = null;
				Method framebufferClassCheckStackClearMethod = null;
				try {
					framebufferClass = Class.forName("firststep.Framebuffer", true, classLoader);
					framebufferClassClearDrawingStackMethod = framebufferClass.getDeclaredMethod("clearDrawingStack");
					framebufferClassClearDrawingStackMethod.setAccessible(true);
					framebufferClassCheckStackClearMethod = framebufferClass.getDeclaredMethod("checkStackClear");
					framebufferClassCheckStackClearMethod.setAccessible(true);
				} catch (ReflectiveOperationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				glCanvas.setCurrent();
				
				try {
					framebufferClassClearDrawingStackMethod.invoke(primaryFramebuffer);
					//framebuffer.clearDrawingStack();

					if (handler != null) handler.draw(SWTFramebuffer.this);
					framebufferClassCheckStackClearMethod.invoke(primaryFramebuffer);
					//framebuffer.checkStackClear();

					/*else {
						GL3W.glClearColor(0.4f, 0.2f, 0.2f, 1.0f);
						GL3W.glClear(GL3W.GL_COLOR_BUFFER_BIT | GL3W.GL_STENCIL_BUFFER_BIT | GL3W.GL_DEPTH_BUFFER_BIT);
					}*/
				} catch (IllegalAccessException | IllegalArgumentException e1) {
					//e1.printStackTrace();
					if (handler != null) handler.handleException(e1);
					//setMessageException(e1);
				} catch (InvocationTargetException e1) {
					//e1.printStackTrace();
					if (e1.equals(Error.class)) {
						if (handler != null) handler.handleException(new RuntimeException("Can't execute the file. That probably means compilation error. Check Problems list", e1));
					} else {
						if (handler != null) handler.handleException(e1.getCause());
					}
				}
				catch (Exception e2) {
					//e2.printStackTrace();
					//setMessageException(e2);
					if (handler != null) handler.handleException(e2);
				}
				
				glCanvas.swapBuffers();
			}
		});
	}

	public Control getControl() {
		return glCanvas;
	}
	
	public void beginDrawing() {
		try {
			Class<?> framebufferClass = Class.forName("firststep.Framebuffer", true, classLoader);
			Method framebufferClassBeginDrawingMethod = framebufferClass.getMethod("beginDrawing");
			framebufferClassBeginDrawingMethod.invoke(primaryFramebuffer);
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
	}
	
	public void endDrawing() {
		try {
			Class<?> framebufferClass = Class.forName("firststep.Framebuffer", true, classLoader);
			Method framebufferClassEndDrawingMethod = framebufferClass.getMethod("endDrawing");
			framebufferClassEndDrawingMethod.invoke(primaryFramebuffer);
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
	}

	/*@Override
	public void clearDrawingStack() {
		super.clearDrawingStack();
	}
	
	@Override
	public void checkStackClear() {
		super.checkStackClear();
	}*/
	
	/*public void setRenderMethod(Object loadedRenderableInstance, Method renderMethod) {
		this.renderMethod = renderMethod;
		this.loadedRenderableInstance = loadedRenderableInstance;
		//refresh();
	}*/

	public void redraw() {
		glCanvas.redraw();
	}
	
	public void dispose() {
		if (!glCanvas.isDisposed()) glCanvas.dispose();
		if (refreshingThread != null) refreshingThread.killSelf = true;
	}
	
	public boolean isDisposed() {
		return glCanvas.isDisposed();
	}
}
