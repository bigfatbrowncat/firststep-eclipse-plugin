package firststep.plugin.wizard;

import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageOne;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import firststep.plugin.Messages;

public class NewFirstStepProjectWizardPageOne extends NewJavaProjectWizardPageOne {
	
	private Label advancedSeparatorLabel;
	private Button advancedButton;
	private Composite advancedComposite;
	private Composite advancedButtonComposite;
	private Label dontCareLabel;
	private Composite advancedMessageComposite;
	private StackLayout advancedMessageCompositeLayout;
	
	public NewFirstStepProjectWizardPageOne() {
	}
	
	private GridLayout initGridLayout(GridLayout layout, boolean marginW, boolean marginH) {
		layout.marginWidth= 0;
		layout.marginHeight= 0;

		if (marginW) {
			layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		}
		
		if (marginH) {
			layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		} 

		return layout;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);

		final Composite composite= new Composite(parent, SWT.NULL);
		composite.setFont(parent.getFont());
		composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		
		
		Composite basicComposite = new Composite(composite, SWT.NONE);
		basicComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		basicComposite.setLayout(new GridLayout(1, false));
		
		advancedButtonComposite = new Composite(composite, SWT.NONE);
		advancedButtonComposite.setLayout(initGridLayout(new GridLayout(2, false), true, false));
		advancedButtonComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		advancedMessageComposite = new Composite(advancedButtonComposite, SWT.NONE);
		advancedMessageComposite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		advancedMessageCompositeLayout = new StackLayout();
		advancedMessageComposite.setLayout(advancedMessageCompositeLayout);
		
		dontCareLabel = new Label(advancedMessageComposite, SWT.NONE);
		dontCareLabel.setText("If you don't care about any advanced options, just fill in the project name and click \"Finish\"");
		
		advancedSeparatorLabel = new Label(advancedMessageComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
		
		advancedButton = new Button(advancedButtonComposite, SWT.NONE);
		advancedButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		advancedButton.setText("Advanced >>");
		
		advancedComposite = new Composite(composite, SWT.NONE);
		advancedComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		advancedComposite.setLayout(new GridLayout(1, false));

		
		

		// create UI elements
		Control nameControl= createNameControl(basicComposite);
		nameControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Control locationControl= createLocationControl(advancedComposite);
		locationControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Control jreControl= createJRESelectionControl(advancedComposite);
		jreControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Control layoutControl= createProjectLayoutControl(advancedComposite);
		layoutControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Control workingSetControl= createWorkingSetControl(advancedComposite);
		workingSetControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Control infoControl= createInfoControl(advancedComposite);
		infoControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		setControl(composite);
		composite.setLayout(new GridLayout(1, false));

		toggleAdvancedPanel();
		
		advancedButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				toggleAdvancedPanel();
			}
			
			@Override public void widgetDefaultSelected(SelectionEvent e) { }
		});
	}
	
	
	protected void toggleAdvancedPanel() {
		if (advancedComposite.isVisible()) {
			advancedButton.setText(Messages.AdvancedButton);
			advancedComposite.setVisible(false);
			advancedMessageCompositeLayout.topControl = dontCareLabel;
		} else {
			advancedButton.setText(Messages.SimpleButton);
			advancedComposite.setVisible(true);
			advancedMessageCompositeLayout.topControl = advancedSeparatorLabel;
		}
		advancedButtonComposite.layout();
	}
}
