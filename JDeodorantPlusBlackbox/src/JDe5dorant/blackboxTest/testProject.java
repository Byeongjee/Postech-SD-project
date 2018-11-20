package JDe5dorant.blackboxTest;

import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellCloses;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.LibraryLocation;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.*;
 
public class testProject {
	/**
	 *  Creating (kind of) Mock Project will be exploited during Black-Box Test
	 *  reference :: https://jaxenter.com/introduction-functional-testing-swtbot-123449.html 
	 */
    public static void buildProject() throws CoreException {
    	IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    	
    	// Creating Project
    	IProject project = root.getProject("testProject");
    	
    	project.create(null); 
    	project.open(null);
    	
    	IProjectDescription description = project.getDescription();
    	description.setNatureIds(new String[] { JavaCore.NATURE_ID });
    	project.setDescription(description, null);
    	
    	IJavaProject javaProject = JavaCore.create(project); 
    	
    	// Creating Folder
    	IFolder binFolder = project.getFolder("bin");
    	binFolder.create(false, true, null);
    	javaProject.setOutputLocation(binFolder.getFullPath(), null);
    	
    	IFolder sourceFolder = project.getFolder("src");
    	sourceFolder.create(false, true, null);
    	
    	// Creating Class-Path
    	List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();
    	IVMInstall vmInstall = JavaRuntime.getDefaultVMInstall();
    	LibraryLocation[] locations = JavaRuntime.getLibraryLocations(vmInstall);
    	for (LibraryLocation element : locations) {
    	 entries.add(JavaCore.newLibraryEntry(element.getSystemLibraryPath(), null, null));
    	}
    	javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[entries.size()]), null);
    	
    	// Creating Source Folder (Package)
    	IPackageFragmentRoot fragRoot = javaProject.getPackageFragmentRoot(sourceFolder);
    	IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
    	IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
    	System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
    	newEntries[oldEntries.length] = JavaCore.newSourceEntry(fragRoot.getPath());
    	javaProject.setRawClasspath(newEntries, null);
    	
    	// Creating Package 
    	IPackageFragment _package = 
    			javaProject.getPackageFragmentRoot(sourceFolder).createPackageFragment("SpeculativeGenerality", false, null);
    	StringBuffer source;
    	
    	//// �׽�Ʈ Ŭ������
    	// Generate Java Class : NoChildInterface
    	source = new StringBuffer();
    	source.append("package " + _package.getElementName() + ";\n");
    	String strNoChildInterface = "public interface NoChildInterface {\r\n" + 
    			"	int NoChildInterface_Method(int input);\r\n" + 
    			"}";
    	source.append(strNoChildInterface);
		ICompilationUnit classNoChildInterface = _package.createCompilationUnit("NoChildInterface.java", source.toString(), false, null);
		
		// Generate Java Class : OneChildInterface
		source = new StringBuffer();
    	source.append("package " + _package.getElementName() + ";\n");
    	String strOneChildInterface = "\r\n" + 
    			"public interface OneChildInterface {\r\n" + 
    			"	int OneChildInterface_Method(int input);\r\n" + 
    			"	\r\n" + 
    			"	abstract int OneChildInterface_AbstractMethod();\r\n" + 
    			"}";
    	source.append(strOneChildInterface);
		ICompilationUnit classOneChildInterface = _package.createCompilationUnit("NoChildInterface.java", source.toString(), false, null);
		
		// Generate Java Class : OneChild_Interface
		source = new StringBuffer();
		source.append("package " + _package.getElementName() + ";\n");
		String strOneChild_Interface = "public class OC_Abs extends OneChildAbstract {\r\n" + 
				"	int var = 5;\r\n" + 
				"\r\n" + 
				"	public OC_Abs(int y) {\r\n" + 
				"		super(y);\r\n" + 
				"	}\r\n" + 
				"\r\n" + 
				"	@Override\r\n" + 
				"	public int OneChildAstract_Method(int a) {\r\n" + 
				"		return a;\r\n" + 
				"	}\r\n" + 
				"\r\n" + 
				"	int OneChildAbstract_Method() {\r\n" + 
				"		return var;\r\n" + 
				"	}\r\n" + 
				"}";
		source.append(strOneChild_Interface);
		ICompilationUnit classOneChild_Interface = _package.createCompilationUnit("NoChildInterface.java", source.toString(), false, null);
				
		
		// Generate Java Class : OneChildAbstract
		source = new StringBuffer();
    	source.append("package " + _package.getElementName() + ";\n");
    	String strOneChildAbstract = "public abstract class OneChildAbstract {\r\n" + 
    			"	int var = 10;\r\n" + 
    			"\r\n" + 
    			"	public OneChildAbstract(int x) {\r\n" + 
    			"		this.var = x;\r\n" + 
    			"	}\r\n" + 
    			"\r\n" + 
    			"	public abstract int OneChildAstract_Method(int a);\r\n" + 
    			"}";
    	source.append(strOneChildAbstract);
    	ICompilationUnit classOneChildAbstract = _package.createCompilationUnit("OneChildAbstract.java", source.toString(), false, null);
		
    	// Generate Java Class : OneChild_Abs
		source = new StringBuffer();
		source.append("package " + _package.getElementName() + ";\n");
		String strOneChild_Abstract = "public class OC_Abs extends OneChildAbstract {\r\n" + 
				"	int var = 5;\r\n" + 
				"\r\n" + 
				"	public OC_Abs(int y) {\r\n" + 
				"		super(y);\r\n" + 
				"	}\r\n" + 
				"\r\n" + 
				"	@Override\r\n" + 
				"	public int OneChildAstract_Method(int a) {\r\n" + 
				"		return a;\r\n" + 
				"	}\r\n" + 
				"\r\n" + 
				"	int OneChildAbstract_Method() {\r\n" + 
				"		return var;\r\n" + 
				"	}\r\n" + 
				"}";
		source.append(strOneChild_Abstract);
		ICompilationUnit classOneChild_Abstract = _package.createCompilationUnit("OneChild_Abs.java", source.toString(), false, null);
		
    	// Generate Java Class : TwoChildAbstract
		source = new StringBuffer();
		source.append("package " + _package.getElementName() + ";\n");
		String strTwoChildAbstract = "public abstract class TwoChildAbstract {\r\n" + 
				"	int var = 10;\r\n" + 
				"	\r\n" + 
				"	public TwoChildAbstract(int arg) {\r\n" + 
				"		var = arg;\r\n" + 
				"	}\r\n" + 
				"	\r\n" + 
				"	int OneChildAstract_Method(int a)\r\n" + 
				"	{\r\n" + 
				"		return a + var*10;\r\n" + 
				"	}\r\n" + 
				"}";
		source.append(strTwoChildAbstract);
		ICompilationUnit classTwoChildAbstract = _package.createCompilationUnit("TwoChildAbstract.java", source.toString(), false, null);
		
		// Generate Java Class : TC_MoreParameterConstructor
		source = new StringBuffer();
		source.append("package " + _package.getElementName() + ";\n");
		String strTwoChildMoreParameterConstructor = "public class TC_MoreParameterConstructor extends TwoChildAbstract {\r\n" + 
				"	int optional;\r\n" + 
				"	\r\n" + 
				"	public TC_MoreParameterConstructor(int arg, int opt) {\r\n" + 
				"		super(arg);\r\n" + 
				"		optional = opt;\r\n" + 
				"	}\r\n" + 
				"}";
		source.append(strTwoChildMoreParameterConstructor);
		ICompilationUnit classTwoChildMoreParameterConstructor = _package.createCompilationUnit("TwoChild_LongMethod.java", source.toString(), false, null);

		// Generate Java Class : TwoChild_UnnecessaryParameter
		source = new StringBuffer();
		source.append("package " + _package.getElementName() + ";\n");
		String strTwoChildUnnecessaryParameter = "public class TC_UnnecessaryParameter extends TwoChildAbstract {\r\n" + 
				"	public TC_UnnecessaryParameter(int arg) {\r\n" + 
				"		super(arg); // Comments\r\n" + 
				"	}\r\n" + 
				"	\r\n" + 
				"	int var = 5;\r\n" + 
				"	int UncessaryParameter(int a, int b, int c) {\r\n" + 
				"		return var;\r\n" + 
				"	}\r\n" + 
				"}";
		source.append(strTwoChildUnnecessaryParameter);
		ICompilationUnit classTwoChildUnnecessaryParameter = _package.createCompilationUnit("TwoChild_UnnecessaryParameter.java", source.toString(), false, null);
		
    }
    
    public static void deleteProject() throws CoreException {
    	IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    	IProject project = root.getProject("testProject");
    	
    	project.delete(true, null);
    }
}