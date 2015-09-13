package firststep.plugin.views;

import java.lang.reflect.Method;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import firststep.SWTFramebuffer;

public class OpenGLViewer extends Viewer {
	
	private static SWTFramebuffer mainFramebuffer;

	private RenderErrorView errorView;
	private StackLayout stackLayout;

	private Composite parent;

	public SWTFramebuffer getMainFramebuffer() {
		return mainFramebuffer;
	}

	public OpenGLViewer(Composite parent, int style) {
		this.parent = parent;
		stackLayout = new StackLayout();
		parent.setLayout(stackLayout);

		errorView = new RenderErrorView(parent, style);
		mainFramebuffer = new SWTFramebuffer(parent, style | SWT.NO_BACKGROUND);
		mainFramebuffer.setExceptionHandler(new SWTFramebuffer.ExceptionHandler() {
			
			@Override
			public void handle(Throwable t) {
				setMessageException(t);
			}
		});
	}
	
	public void setRenderMethod(Object loadedRenderableInstance, Method renderMethod) {
		mainFramebuffer.setRenderMethod(loadedRenderableInstance, renderMethod);
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
