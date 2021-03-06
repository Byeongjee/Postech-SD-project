package showCodeSmellInformation_Tab;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.*;
import org.junit.runner.RunWith;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IWorkingCopy;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;
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

import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellCloses;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.CoreException;

@RunWith(SWTBotJunit4ClassRunner.class)
public class tester {
	private static SWTWorkbenchBot bot;
	private static boolean flagProjectOn = false;
	
	private static void openPackageExplorer() {
		bot.menu("Window").menu("Show View").menu("Other...").click();
		SWTBotShell dialog = bot.shell("Show View");
		dialog.activate();
		bot.tree().getTreeItem("Java").expand().getNode("Package Explorer").doubleClick();
	}
	
	public static void openSpeculativeGeneralityTab() {
		bot.menu("JDe5dorant").menu("Speculative Generality").click();
		bot.viewByTitle("Speculative Generality");
	}
	
	@BeforeClass
	public static void initBot() throws CoreException {
		bot = new SWTWorkbenchBot();
		//bot.viewByTitle("Welcome").close();
		openPackageExplorer();
		openSpeculativeGeneralityTab();
	}

	@AfterClass
	public static void afterClass() throws CoreException {
		bot.resetWorkbench();
	}

	private void selectSGTarget() {
		SWTBotView packageExplorer = bot.viewByTitle("Package Explorer");
		packageExplorer.show();
		packageExplorer.bot().tree().getTreeItem("testProject").expand().getNode("src").expand().getNode("SpeculativeGenerality").click();
	}
	
	private void turnOnProject(int arg) throws CoreException {
		if(!flagProjectOn) {
			testSGProject.buildProject(arg);
			flagProjectOn = true;
		} else {
			testSGProject.deleteProject();
			testSGProject.buildProject(arg);
		}
	}

	@Test
	public void testSGInformation() throws CoreException {
		SWTBotView detectionApplier;
		
		turnOnProject(0);
		selectSGTarget();
		
		detectionApplier = bot.viewByTitle("Speculative Generality");
		detectionApplier.show();
		detectionApplier.getToolbarButtons().get(0).click();
    	assertTrue(detectionApplier.bot().tree().getTreeItem("SpeculativeGenerality.NoChildInterface").isEnabled());
    	
    	assertEquals(detectionApplier.bot().tree().getTreeItem("SpeculativeGenerality.NoChildInterface").cell(2), "Interface Class");    	
    	assertEquals(detectionApplier.bot().tree().getTreeItem("SpeculativeGenerality.NoChildInterface").cell(3), "Merge Class");
    	
    	testSGProject.deleteProject();
	}
	

	public static void openMessageChainTab() {
		bot.menu("JDe5dorant").menu("Message Chain").click();
		bot.viewByTitle("Message Chain");
		assertTrue(bot.viewByTitle("Message Chain").isActive());
	}
	
	@Test
	public void testMCInformation() throws CoreException {
		testMCProject.buildProject();
		
		bot.menu("JDe5dorant").menu("Message Chain").click();
		bot.viewByTitle("Message Chain");
		
		SWTBotView packageExplorer = bot.viewByTitle("Package Explorer");
		packageExplorer.show();
		packageExplorer.bot().tree().getTreeItem("testProject").click();

		SWTBotView detectionApplier = bot.viewByTitle("Message Chain");
		detectionApplier.show();
		detectionApplier.getToolbarButtons().get(0).click();
		
		detectionApplier.bot().tree().getTreeItem("").select();
		detectionApplier.bot().tree().getTreeItem("").expand();

		assertEquals("387", detectionApplier.bot().tree().getTreeItem("").getNode(1).cell(0));
		assertEquals("test_message->method2->method3->method4", detectionApplier.bot().tree().getTreeItem("").getNode(1).cell(1));
		
		testMCProject.deleteProject();
	}
	
	
	@Test
	public void testLPLInformation() throws CoreException {
		testLPLProject.buildLPLProject();
		
		bot.menu("JDe5dorant").menu("Long Parameter List").click();
		bot.viewByTitle("Long Parameter List");
		
		SWTBotView packageExplorer = bot.viewByTitle("Package Explorer");
		packageExplorer.show();
		packageExplorer.bot().tree().getTreeItem("testLPLProject").click();

		SWTBotView detectionApplier = bot.viewByTitle("Long Parameter List");
		detectionApplier.show();
		detectionApplier.getToolbarButtons().get(0).click();
		
		assertEquals(detectionApplier.bot().tree().getTreeItem("Long Parameter List").cell(4), "4");
		
		bot.resetActivePerspective();
    	SWTBotView view = bot.viewByTitle("Project Explorer");
    	view.bot().tree().getTreeItem("testLPLProject").contextMenu("Delete").click();
    	SWTBotShell deleteShell = bot.shell("Delete Resources");
    	deleteShell.activate();
    	bot.checkBox("Delete project contents on disk (cannot be undone)").click();
    	bot.button("OK").click();
	}
	
	
}
