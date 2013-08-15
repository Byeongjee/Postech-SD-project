package gr.uom.java.jdeodorant.refactoring.views;

import java.util.ArrayList;
import java.util.List;

import gr.uom.java.ast.visualization.ICustomInformationControlCreator;
import gr.uom.java.ast.visualization.IInformationProvider;
import gr.uom.java.ast.visualization.FeatureEnviedMethodInformationControlCreator;
import gr.uom.java.ast.visualization.InformationControlManager;
import gr.uom.java.ast.visualization.SearchInputAction;
import gr.uom.java.ast.visualization.ZoomInputAction;
import gr.uom.java.ast.visualization.PackageMapDiagramInformationProvider;
import gr.uom.java.ast.visualization.PackageMapDiagram;
import gr.uom.java.ast.visualization.ZoomAction;
import gr.uom.java.distance.CandidateRefactoring;
import gr.uom.java.jdeodorant.refactoring.Activator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.ScalableLayeredPane;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;

public class CodeSmellPackageExplorer extends ViewPart {
	public static final String ID = "gr.uom.java.jdeodorant.views.CodeSmellPackageExplorer";
	private FigureCanvas figureCanvas; 
	private ScalableLayeredPane root = null;
	private boolean ctrlPressed= false;
	public static IProgressMonitor monitor;
	public static double SCALE_FACTOR=1;

	@Override
	public void createPartControl(Composite parent) {

		parent.setLayout(new FillLayout());

		figureCanvas = new FigureCanvas(parent, SWT.DOUBLE_BUFFERED);
		figureCanvas.setBackground(ColorConstants.white);

		CandidateRefactoring[] candidates = CodeSmellVisualizationDataSingleton.getCandidates();
		PackageMapDiagram diagram = null;
		
		if(candidates != null){
			diagram = new PackageMapDiagram(candidates, monitor);
			
			root = diagram.getRoot();

		}



		//figureCanvas.setViewport(new FreeformViewport());
		figureCanvas.addKeyListener( new KeyListener() {

			public void keyPressed(KeyEvent e) {
				if(e.keyCode == SWT.CTRL){
					ctrlPressed = true;
				}
			}

			public void keyReleased(KeyEvent e) {
				if(e.keyCode== SWT.CTRL)
					ctrlPressed = false;

			}
		});
		MouseWheelListener listener = new MouseWheelListener() {
			private double scale;
			private static final double ZOOM_INCRENENT = 0.1;
			private static final double ZOOM_DECREMENT = 0.1;

			private void zoom(int count, Point point) {
				if (count > 0) {
					scale += ZOOM_INCRENENT;

				} else {
					scale -= ZOOM_DECREMENT;
				}

				if (scale <= 0) {
					scale = 0;
				}
				Viewport viewport = (Viewport) root.getParent();

				if(scale>1){
					viewport.setHorizontalLocation((int) (point.x*(scale -1)+ scale*viewport.getLocation().x));
					viewport.setVerticalLocation((int) (point.y*(scale-1)+scale*viewport.getLocation().y));
				}
				
				SCALE_FACTOR=scale;
				root.setScale(scale);
			}

			public void mouseScrolled(MouseEvent e) {

				if(ctrlPressed == true){
					scale = root.getScale();
					Point point = new Point(e.x,e.y);
					int count = e.count;
					zoom(count, point);

				}

			}
		};

		figureCanvas.addMouseWheelListener(listener);
		figureCanvas.setContents(root);
		if(diagram != null)
			hookTooltips(diagram);


		
		
		
		ImageDescriptor imageDescriptor = Activator.getImageDescriptor("/icons/" + "magnifier.png");
		IActionBars bars = getViewSite().getActionBars();
		IToolBarManager manager = bars.getToolBarManager();
		
		if(PackageMapDiagram.projectName != null){
			
				
		LabelControlContribution infoLabel = new LabelControlContribution("Label", "Feature Envy Analysis of system: ", null);
		LabelControlContribution projectNameLabel = new LabelControlContribution("Label", PackageMapDiagram.projectName+"  ", new Font(null, "Consolas", 10, SWT.BOLD));
		
		manager.add(infoLabel);
		manager.add(projectNameLabel);
		
		manager.add(new Separator());
		}
		
		Action act=new Action("Zoom",SWT.DROP_DOWN){};
		act.setImageDescriptor(imageDescriptor);
		act.setMenuCreator(new MyMenuCreator());
		manager.add(act);
		
		
		SearchInputAction searchAction = new SearchInputAction();
		searchAction.setText("Search");
		manager.add(searchAction);
		
		
		
		

	}
	class MyMenuCreator implements IMenuCreator{

		private IAction action;
		private Menu menu;

		public void selectionChanged(IAction action, ISelection selection)
		{
			if (action != this.action)
			{
				action.setMenuCreator(this);
				this.action = action;
			}
		} 

		public Menu getMenu(Control ctrl){
			Menu menu = new Menu(ctrl);
			addActionToMenu(menu, newZoomAction(0.5));
			addActionToMenu(menu, newZoomAction(1));
			addActionToMenu(menu, newZoomAction(2));
			//	addActionToMenu(menu, newZoomAction(0));

			ZoomInputAction inputZoomAction = new ZoomInputAction(root);
			inputZoomAction.setText("Other...");

			addActionToMenu(menu, inputZoomAction);
			return menu;

		}

		public void dispose() {
			if (menu != null)
			{
				menu.dispose();
			}
		}

		public Menu getMenu(Menu parent) {
			return null;
		}

		private void addActionToMenu(Menu menu, IAction action)
		{
			ActionContributionItem item= new ActionContributionItem(action);
			item.fill(menu, -1);
		}
	}

	public ZoomAction newZoomAction(double scale){
		ZoomAction zoomAction = new ZoomAction(root, scale);
		if(scale != 0){
			double percent = scale*100;
			zoomAction.setText((int) percent +"%");
			zoomAction.setImageDescriptor(Activator.getImageDescriptor("/icons/" + "magnifier.png"));
		}else
			zoomAction.setText("Scale to Fit");
		return zoomAction;
	}
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
	
	public class LabelControlContribution extends ControlContribution  
	{  
		private String name;
		private Font font;
	  
	    protected LabelControlContribution(String id, String name, Font font)  
	    { 
	        super(id);  
	        this.name= name;
	        this.font= font;
	    }  
	  
	    @Override  
	    protected Control createControl(Composite parent)  
	    {  
	        //Label b = new Label(parent, SWT.LEFT);  
	       // b.setText("project name");  
	       // return b;  
	    	Label label= new Label(parent, SWT.CENTER);
	    	    	
	    	
	    	//label.setText("Feature Envy Analysis of system: \b" + PackageMapDiagram.projectName+"  ");
	    	label.setText(name);
	    	if(font != null)
	    	label.setFont(font);
	    	return label;
	    }  
	    
	     
	}


	private void hookTooltips(PackageMapDiagram diagram) {
		// Create an information provider for our table viewer
		IInformationProvider informationProvider = new PackageMapDiagramInformationProvider(diagram);

		// Our table viewer contains elements of type String, Person and URL.
		// Strings are handled by default. For Person and URL we need custom control creators.
		List<ICustomInformationControlCreator> informationControlCreators = new ArrayList<ICustomInformationControlCreator>();
		informationControlCreators.add(new FeatureEnviedMethodInformationControlCreator());

		Control control =figureCanvas;


		final InformationControlManager informationControlManager = new InformationControlManager(informationProvider, informationControlCreators, false);
		informationControlManager.install(control);

		// MouseListener to show the information when the user hovers a table item
		control.addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseHover(MouseEvent event) {
				informationControlManager.showInformation();
			}
		});

		// DisposeListener to uninstall the information control manager

		DisposeListener listener = new DisposeListener(){

			public void widgetDisposed(DisposeEvent e) {
				informationControlManager.dispose();

			}

		};
		control.addDisposeListener(listener);
		// Install tooltips
		//Tooltips.install(diagram.getControl(), informationProvider, informationControlCreators, false);
	}
	public static void setMonitor(IProgressMonitor monitor) {
		CodeSmellPackageExplorer.monitor = monitor;
	}
	
	

}