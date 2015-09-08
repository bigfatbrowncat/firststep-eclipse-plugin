package firststepplugin.views;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import firststep.Color;
import firststep.MainFramebuffer;
import firststep.Window;
import firststep.internal.GL3W;

public class OpenGLViewer extends Viewer {

	private MainFramebuffer mainFramebuffer;
	private org.eclipse.swt.opengl.GLCanvas glCanvas;
	
	private Object loadedRenderableInstance;
	private Method renderMethod;
	
	public void setRenderMethod(Object loadedRenderableInstance, Method renderMethod) {
		this.renderMethod = renderMethod;
		this.loadedRenderableInstance = loadedRenderableInstance;
		refresh();
	}
	
	//static Long nanoVGContext = null;
	
	/**
	 * As soon as we need at least one window to support NanoVG context,
	 * this function (which should be a static initializer in an ideal world)
	 * is called by the {@link Window} class constructor
	 */
	/*static void ensureNanoVGContextCreated() {
		if (nanoVGContext == null) {
			nanoVGContext = NVG.create(firststep.internal.NVG.NVG_ANTIALIAS | firststep.internal.NVG.NVG_STENCIL_STROKES | firststep.internal.NVG.NVG_DEBUG);
			if (nanoVGContext == 0) {
				//GLFW.terminate();
				throw new RuntimeException("NanoVG can't create a context for the window");
			}
			//getLogger().log(Level.INFO, "NanoVG context is created");
		}
	}*/
	
	public MainFramebuffer getMainFramebuffer() {
		return mainFramebuffer;
	}
	
	public OpenGLViewer(Composite parent, int style) {
		parent.setLayout(new FillLayout());

		GLData data = new GLData();
		data.doubleBuffer = true;
		glCanvas = new GLCanvas(parent, style | /*SWT.DOUBLE_BUFFERED | */SWT.NO_BACKGROUND, data);
		glCanvas.setCurrent();
		GL3W.init();
		System.out.println("OpenGL version: " + GL3W.getGLVersionMajor() + "." + GL3W.getGLSLVersionMinor());
		mainFramebuffer = new MainFramebuffer(glCanvas.getSize().x, glCanvas.getSize().y);
		
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
				MainFramebuffer.ensureNanoVGContextCreated();
/*				GL3W.glClearColor(0.9f, 0.5f, 0, 1.0f);
				GL3W.glViewport(0, 0, glCanvas.getSize().x, glCanvas.getSize().y	);
				GL3W.glClear(GL3W.GL_COLOR_BUFFER_BIT | GL3W.GL_STENCIL_BUFFER_BIT | GL3W.GL_DEPTH_BUFFER_BIT);*/
				
				/*mainFramebuffer.setBackground(Color.fromRGBA(0.2f, 0.2f, 0.2f, 1.f));
				mainFramebuffer.beginDrawing();
				mainFramebuffer.beginPath();
				mainFramebuffer.moveTo(20, 20);
				mainFramebuffer.lineTo(50, 50);
				mainFramebuffer.strokeColor(Color.fromRGBA(0.2f, 0.0f, 1.0f, 1.0f));
				mainFramebuffer.strokeWidth(5);
				mainFramebuffer.stroke();
				mainFramebuffer.endDrawing();*/
				
				try {
					if (renderMethod != null && loadedRenderableInstance != null) {
						renderMethod.invoke(loadedRenderableInstance, mainFramebuffer);
					} else {
						GL3W.glClearColor(0.4f, 0.2f, 0.2f, 1.0f);
						GL3W.glClear(GL3W.GL_COLOR_BUFFER_BIT | GL3W.GL_STENCIL_BUFFER_BIT | GL3W.GL_DEPTH_BUFFER_BIT);
					}
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
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

	@Override
	public void setInput(Object input) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSelection(ISelection selection, boolean reveal) {
		// TODO Auto-generated method stub

	}

}
