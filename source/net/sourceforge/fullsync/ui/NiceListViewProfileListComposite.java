package net.sourceforge.fullsync.ui;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;

import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.ProfileListChangeListener;
import net.sourceforge.fullsync.ProfileManager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class NiceListViewProfileListComposite extends ProfileListComposite implements ProfileListChangeListener
{
    class ContentComposite extends Composite
    {
        private Profile profile;
        
        private Label labelSource;
        private Label labelDestination;
        private Label labelLastUpdate;
        private Label labelNextUpdate;
        private ToolBar toolbar;
        
        public ContentComposite( Composite parent, int style )
        {
            super( parent, style );
            initGui();
        }
        public void initGui()
        {
			GridLayout layout = new GridLayout( 2, false );
			layout.marginHeight = 1;
			layout.marginWidth = 1;
			layout.verticalSpacing = 2;
			layout.horizontalSpacing = 20;
			this.setLayout( layout );
			
			labelSource = new Label( this, SWT.NULL );
			labelSource.setLayoutData( new GridData( GridData.FILL, GridData.CENTER, false, false ) );
			labelSource.setSize( 300, 16 );
			
			toolbar = new ToolBar(this, SWT.FLAT);
			GridData d = new GridData( GridData.END, GridData.CENTER, true, false );
			d.verticalSpan = 4;
			toolbar.setLayoutData( d );
	
			ToolItem t = new ToolItem( toolbar, SWT.PUSH );
			t.setImage( imageRun );
			t.addSelectionListener( new SelectionAdapter() {
			    public void widgetSelected( SelectionEvent e )
	            {
	                handler.runProfile( profile );
	            }
			});
	
			t = new ToolItem( toolbar, SWT.PUSH );
			t.setImage( imageEdit );
			t.addSelectionListener( new SelectionAdapter() {
			    public void widgetSelected( SelectionEvent e )
	            {
	                handler.editProfile( profile );
	            }
			});
	
			t = new ToolItem( toolbar, SWT.PUSH );
			t.setImage( imageDelete );
			t.addSelectionListener( new SelectionAdapter() {
			    public void widgetSelected( SelectionEvent e )
	            {
	                handler.deleteProfile( profile );
	            }
			});
			
			labelDestination = new Label( this, SWT.NULL );
			labelDestination.setLayoutData( new GridData( GridData.FILL, GridData.CENTER, false, false ) );
			labelDestination.setSize( 200, 16 );
			labelLastUpdate = new Label( this, SWT.NULL );
			labelLastUpdate.setLayoutData( new GridData( GridData.FILL, GridData.CENTER, false, false ) );
			labelLastUpdate.setSize( 300, 16 );
			labelNextUpdate = new Label( this, SWT.NULL );
			labelNextUpdate.setLayoutData( new GridData( GridData.FILL, GridData.CENTER, false, false ) );
			labelNextUpdate.setSize( 200, 16 );
        }
        public void updateComponent()
        {
            long now = new Date().getTime();
			labelSource.setText( "Source: "+profile.getSource() );
			labelDestination.setText( "Destination: "+profile.getDestination() );
			labelLastUpdate.setText( "Last Update: "+profile.getLastUpdate() );
			labelNextUpdate.setText( "Next Update: "+profile.getNextUpdate() );
			layout();			
        }
        public void setProfile( Profile profile )
        {
            this.profile = profile;
            updateComponent();
        }
        public Profile getProfile()
        {
            return profile;
        }
    }
    
    private ScrolledComposite scrollPane;
    private NiceListView profileList;
    private HashMap profilesToItems;
    
    private ProfileManager profileManager;
    private ProfileListControlHandler handler;
    
    private Image imageRun;
    private Image imageEdit;
    private Image imageDelete;

    public NiceListViewProfileListComposite( Composite parent, int style )
    {
        super( parent, style );
        loadImages();
        initGui();
    }
    
    private void initGui()
    {
        scrollPane = new ScrolledComposite( this, SWT.BORDER | SWT.V_SCROLL );
		profileList = new NiceListView(scrollPane, SWT.NULL);
		scrollPane.setExpandHorizontal( true );
		scrollPane.setExpandVertical( false );
		scrollPane.setBackground( getDisplay().getSystemColor( SWT.COLOR_WHITE ) );
		scrollPane.setAlwaysShowScrollBars( true );
		scrollPane.setContent( profileList );
		scrollPane.getVerticalBar().setIncrement(20);
		profileList.pack();
		this.setLayout( new FillLayout() );
		this.layout();
    }
    private void loadImages()
    {
        imageRun = new Image( getDisplay(), "images/Profile_Run.gif" );
        imageEdit = new Image( getDisplay(), "images/Profile_Edit.gif" );
        imageDelete = new Image( getDisplay(), "images/Profile_Delete.gif" );
    }
    
    private void populateProfileList()
    {
        if( getProfileManager() != null )
	    {
            profilesToItems = new HashMap();
            profileList.clear();
            
	        Enumeration e = getProfileManager().getProfiles();
	        while( e.hasMoreElements() )
	        {
	            Profile p = (Profile)e.nextElement();
	            
	            NiceListViewItem item = new NiceListViewItem( profileList, SWT.NULL );
				ContentComposite content = new ContentComposite( item, SWT.NULL );
				content.setProfile( p );
	            item.setImage( new Image( getDisplay(), "images/Profile_Default.gif" ) );
				item.setText( p.getName() );
				item.setStatusText( p.getDescription() );
				item.setContent( content );
				
				profilesToItems.put( p, item );
	        }
			profileList.pack();
	    }
    }
    
    public Profile getSelectedProfile()
    {
        ContentComposite content = (ContentComposite)profileList.getSelectedContent();
        if( content != null )
             return content.getProfile();
        else return null;
    }
    public void setProfileManager( ProfileManager profileManager )
    {
	    if( this.profileManager != null )
	    {
	        profileManager.removeProfilesChangeListener( this );
	        
	    }
        this.profileManager = profileManager;
        if( this.profileManager != null )
        {
            profileManager.addProfilesChangeListener( this );
        }
        populateProfileList();
    }
    public ProfileManager getProfileManager()
    {
        return profileManager;
    }
    public ProfileListControlHandler getHandler()
    {
        return handler;
    }
    public void setHandler( ProfileListControlHandler handler )
    {
        this.handler = handler;
    }
    public void profileListChanged()
    {
        getDisplay().syncExec( new Runnable() {
            public void run()
            {
                populateProfileList();
            }
        } );
    }
    public void profileChanged( final Profile p )
    {
        getDisplay().syncExec( new Runnable() {
            public void run()
            {
                Object composite = profilesToItems.get( p );
                if( composite == null )
                {
                    populateProfileList();
                } else{
                    NiceListViewItem item = (NiceListViewItem) composite;
                    ContentComposite content = (ContentComposite) item.getContent();
                    item.setText( content.getProfile().getName() );
                    item.setStatusText( content.getProfile().getDescription() );
                    content.updateComponent();
                }
            }
        } );
    }
}
