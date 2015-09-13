package firststep.plugin.container;

import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPage;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPageExtension;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import firststep.plugin.Messages;

public class FirstStepContainerPage extends WizardPage implements IClasspathContainerPage, IClasspathContainerPageExtension {

    private IJavaProject project;

    public FirstStepContainerPage() {
        super(Messages.PageName, Messages.PageTitle, null);
        setDescription(Messages.PageDesc);
        setPageComplete(true);
    }
    
    public void initialize(IJavaProject project, IClasspathEntry[] currentEntries) {
        this.project = project;
    }

    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
        composite.setFont(parent.getFont());
        
        createDirGroup(composite);
        setControl(composite);
    }
    
    private void createDirGroup(Composite parent) {
        Composite mainGroup = new Composite(parent, SWT.NONE);
        GridLayout layout= new GridLayout();
        layout.numColumns = 1;
        mainGroup.setLayout(layout);
        mainGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL| GridData.VERTICAL_ALIGN_FILL));

        Label label = new Label(mainGroup, SWT.NONE);
        label.setText(Messages.Label);

        setControl(mainGroup);
    }


    @Override
    public boolean finish() {
        return true;        
    }

    private IClasspathEntry selection;
    
	@Override
	public IClasspathEntry getSelection() {
		if (selection == null) {
			selection = JavaCore.newContainerEntry(FirstStepContainer.ID_PATH); 
		}
		return selection;
	}

	@Override
	public void setSelection(IClasspathEntry containerEntry) {
		this.selection = containerEntry;
	}

}
