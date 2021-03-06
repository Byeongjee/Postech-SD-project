package gr.uom.java.jdeodorant.refactoring.views;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.wizard.Wizard;

/**
 * Preview Wizard Page to open preview dialog
 * @author Sukyung Oh
 *
 */
public class MCPreviewWizard extends Wizard {
   private IJavaProject javaProject;
   private ICompilationUnit workingCompilationUnit;
   private MCRefactorPreviewPage previewPage;
   
   private String origin;
   private String refactor;
   
   private boolean flagCancel;
   
   public MCPreviewWizard(ICompilationUnit workingCopy, String origin, String refactor) {
		super();
		setNeedsProgressMonitor(true);
		this.workingCompilationUnit = workingCopy;
		
		this.origin = origin;
		this.refactor = refactor;
   }

   @Override
   public String getWindowTitle() {
      return "Refactoring";
   }
   
   @Override
   public void addPages() {
	  previewPage = new MCRefactorPreviewPage(this.origin, this.refactor);
      addPage(previewPage);
   }
   
   @Override
   public boolean performFinish() {
      try {
			workingCompilationUnit.reconcile(ICompilationUnit.NO_AST, false, null, null);
			workingCompilationUnit.commitWorkingCopy(false, null);
			workingCompilationUnit.discardWorkingCopy();
			flagCancel = false;
      } catch (JavaModelException e) {
			e.printStackTrace();
	}
      return true;
   }
   
   @Override
	public boolean performCancel() {
	  this.flagCancel = true;
      return true;
  }

   public boolean getFlagCancel() {
	return flagCancel;
}
   
}