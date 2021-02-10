package kankanimulecodeanalysis.views;

import kankanimulecodeanalysis.Activator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.*;
import org.json.JSONArray;
import org.osgi.framework.Bundle;

import groovy.util.GroovyScriptEngine;
import org.eclipse.jface.viewers.*;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.osgi.service.resolver.State;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.*;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.SWT;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;

import javax.inject.Inject;



/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class CodeAnalysisView extends ViewPart  implements PropertyChangeListener{

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "kankanimulecodeanalysis.views.CodeAnalysisView";

	@Inject IWorkbench workbench;
	
	List<String> projectfiles;
	Composite _parent;
	String projectName="";
	String fileName="";
	TableViewer viewer;
	private Action clearTable;

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.BORDER_SOLID | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		
		makeActions(parent);
		contributeToActionBars();
		_parent = parent;
		TableViewer tv =createView(parent);
		createTable(parent);
	}
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(clearTable);
		 
	}
	private void contributeToActionBars() {
		IActionBars bars=getViewSite().getActionBars();
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(clearTable);
	}
	
	private void makeActions(Composite parent) {
		clearTable=new Action() {
			@Override
			public void run() {
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						if (!viewer.getTable().isDisposed()) {
							// TODO Auto-generated method stub
							viewer.getTable().removeAll();
							_parent = parent;
							createTable(parent);
							viewer.refresh();
						}
					}
				});
			}
		};
		clearTable.setText("Clear Table");
		clearTable.setToolTipText("Clear Table Content");
		clearTable.setImageDescriptor(Activator.getImageDescriptor("icons/sample.png"));
	}
	private void findAllProjectFiles(IContainer container) throws CoreException {
        IResource[] members = container.members();        
        for (IResource member : members) {
        	//folders reading
            if (member instanceof IContainer) {
                IContainer c = (IContainer) member;
            	findAllProjectFiles(c);
            } else if (member instanceof IFile) {
            	//files reading
    		projectfiles.add(member.getName());
        	
            }
        }
    }

	public TableViewer createView(Composite parent)
	{

		
		//table creation
		Table t = viewer.getTable();
		GridLayout layout = new GridLayout();
		t.setLayoutData(layout);
	    t.setLinesVisible(true); 
	    t.setHeaderVisible(true);
	     
	    TableViewerColumn tc1 = new TableViewerColumn(viewer, SWT.LEFT);
	    TableViewerColumn tc2 = new TableViewerColumn(viewer, SWT.LEFT);
	    TableViewerColumn tc3 = new TableViewerColumn(viewer, SWT.LEFT);
	    TableViewerColumn tc4 = new TableViewerColumn(viewer, SWT.LEFT);
	    TableViewerColumn tc5 = new TableViewerColumn(viewer, SWT.LEFT);
	    
	    
	    tc1.getColumn().setText("Error");
	    tc2.getColumn().setText("File Name");
	    tc3.getColumn().setText("LN");
	    tc4.getColumn().setText("Project");
	    tc5.getColumn().setText("Priority");
	    
	    tc1.getColumn().setWidth(400);
        tc2.getColumn().setWidth(150);
        tc3.getColumn().setWidth(35);
        tc4.getColumn().setWidth (400);
        tc5.getColumn().setWidth(50);
        
        final TableColumn column = tc5.getColumn();
	    viewer.getTable().setSortColumn(column);
        
        t.setHeaderVisible(true);
        
	    return viewer;
	    
	}
	
	public void createTable(Composite parent)
	{
		
		
		Table t = viewer.getTable();
		
	    //get workspace 
	    IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = workspace.getRoot();
        System.out.println("workspace location : " + root.getLocation().toString());
        IProject[] projects = root.getProjects();
        //iterate the projects and fetch all the files
        for (IProject project : projects) {
            projectName = root.getLocation().toString() + project.getFullPath();
			System.out.println("Project Name : " + projectName);
			

			@SuppressWarnings("deprecation")
			State state = Platform.getPlatformAdmin().getState();
			BundleDescription [] bundles = state.getBundles();
			System.out.println("printing bundles");
			Bundle bundle=null;
			for(int i=0; i< bundles.length; i++)
			{
				if(bundles[i].getName().toString().contains(Activator.PLUGIN_ID))
				{
					bundle = Platform.getBundle(bundles[i].getName().toString());
					break;
				}
			}
			
			Enumeration<URL> groovyEntries = bundle.findEntries("/src/main/resources", "*.groovy", true);
			URL currentURL = null;
			try
			{
				currentURL = groovyEntries.nextElement();
			} catch (Exception e) {
				// TODO: handle exception
				currentURL = null;
				System.out.println(e.toString());
			}

			
			if(currentURL == null)
			{
				//log error here
			}
			
			
			//run all groovy files and all methods starts with validate
			while( currentURL != null)
			{
				
				try
				{
					
					System.out.println("current URL: " + currentURL);
					//run validate* methods from groovy file
					URL jreURL = Platform.find(bundle, new Path(currentURL.getFile()));   
					runGroovy(projectName, jreURL, t);
					try { 
			    		currentURL = groovyEntries.nextElement();	                	 
					} catch (Exception e) {
						// TODO: handle exception
						currentURL = null;	 
						break;
					}
				}catch (Exception e) {
					
					break;	                	
				}
			}

        }
        System.out.println("Successful");
	}

	private void runGroovy( String projectName, URL jreURL, Table t) 
	//run all the groovy methods found in the given URL/file
	{
		
		try
		{
			Class scriptClass = new GroovyScriptEngine(".")
					.loadScriptByName(jreURL.toString());
			Object scriptInstance = scriptClass.newInstance();
			java.lang.reflect.Method[] methods = scriptClass.getMethods();
			
			for(int i=0; i<methods.length;i++)
			{
				if(methods[i].getName().contains("validate"))
				{
					
	    			Object str = scriptClass.getDeclaredMethod(methods[i].getName(), new Class[] { Object.class}).invoke(
	    					scriptInstance, new Object[]  {  projectName });
	    			if(str != null && !str.toString().isEmpty())
	    			{
	    				JSONArray errors = new JSONArray(str.toString());
	    				for (int count=0; count<errors.length();count++)
	    				{
	    					TableItem item1 = new TableItem(t, SWT.NONE);
        					item1.setText(new String[] { errors.getJSONObject(count).get("errorDetails").toString(), errors.getJSONObject(count).get("fileName").toString(), errors.getJSONObject(count).get("lineNumber").toString(), projectName, errors.getJSONObject(count).get("priority").toString()});
	    				}
	        		}
				}
				
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		viewer.refresh();
	}}
