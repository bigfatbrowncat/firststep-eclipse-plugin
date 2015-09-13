package firststep.plugin.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class RenderErrorView extends Composite {
	
	private Throwable exception;
	private Label lblErrorText;
	private Text stacktraceText;
	private Button btnAdvanced;
	
	public RenderErrorView(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(3, false));
		
		Label lblNewLabel = new Label(this, SWT.RIGHT);
		GridData gd_lblNewLabel = new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1);
		gd_lblNewLabel.verticalIndent = 4;
		gd_lblNewLabel.widthHint = 27;
		lblNewLabel.setLayoutData(gd_lblNewLabel);
		lblNewLabel.setImage(ResourceManager.getPluginImage("org.eclipse.ui", "/icons/full/progress/errorstate.png"));
		
		Label lblErrorTitle = new Label(this, SWT.NONE);
		lblErrorTitle.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		GridData gd_lblErrorTitle = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_lblErrorTitle.verticalIndent = 6;
		gd_lblErrorTitle.widthHint = 15;
		gd_lblErrorTitle.heightHint = 21;
		lblErrorTitle.setLayoutData(gd_lblErrorTitle);
		lblErrorTitle.setText("Rendering is impossible because of a problem:");
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		
		lblErrorText = new Label(this, SWT.WRAP);
		lblErrorText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		lblErrorText.setText("< empty >");
		
		btnAdvanced = new Button(this, SWT.NONE);
		btnAdvanced.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!stacktraceText.getVisible()) {
					stacktraceText.setVisible(true);
					btnAdvanced.setVisible(false);
				}
			}
		});
		btnAdvanced.setText("Advanced >>");
		new Label(this, SWT.NONE);
		
		stacktraceText = new Text(this, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		GridData gd_stacktraceText = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd_stacktraceText.verticalIndent = 4;
		stacktraceText.setLayoutData(gd_stacktraceText);
		stacktraceText.setVisible(false);
	}
	
	public void setException(Throwable exception) {
		this.exception = exception;
		lblErrorText.setText(exception.getLocalizedMessage());
		StringBuilder sb = new StringBuilder();
		sb.append("Exception class ");
		sb.append(exception.getClass().getCanonicalName());
		sb.append("\n");
		for (int i = 0; i < exception.getStackTrace().length; i++) {
			sb.append("  at ");
			sb.append(exception.getStackTrace()[i].toString());
			sb.append('\n');
		}
		stacktraceText.setText(sb.toString());
		layout();
	}
}
