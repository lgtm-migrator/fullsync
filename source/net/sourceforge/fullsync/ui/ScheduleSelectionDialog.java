package net.sourceforge.fullsync.ui;

import java.io.PrintWriter;
import java.io.StringWriter;

import net.sourceforge.fullsync.schedule.Schedule;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;


/**
* This code was generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* *************************************
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED
* for this machine, so Jigloo or this code cannot be used legally
* for any corporate or commercial purpose.
* *************************************
*/
public class ScheduleSelectionDialog extends org.eclipse.swt.widgets.Dialog 
{
    class NullScheduleOptions extends ScheduleOptions
    {
        public NullScheduleOptions( Composite parent, int style )
        {
            super(parent, style);
        }
        public String getSchedulingName()
        {
            return "none";
        }
        public boolean canHandleSchedule( Schedule sched )
        {
            return false;
        }
        public Schedule getSchedule()
        {
            return null;
        }
        public void setSchedule( Schedule sched )
        {
        }
    }
    
	private Group groupOptions;
	private Combo cbType;
	private Composite compositeTop;
	private Button buttonCancel;
	private Button buttonOk;
	private org.eclipse.swt.widgets.Shell dialogShell;
	private Label labelScheduleType;

	private Schedule schedule;

	public ScheduleSelectionDialog( Shell parent, int style ) 
	{
		super(parent, style);
		
	}

	public void open() 
	{
		try {
		    dialogShell = new Shell( getParent(), SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM | SWT.TOOL );
		    dialogShell.setText( "Edit Scheduling" );
			GridLayout thisLayout = new GridLayout();
			thisLayout.numColumns = 2;
			dialogShell.setLayout(thisLayout);
			{
                compositeTop = new Composite(dialogShell, SWT.NONE);
                GridLayout compositeTopLayout = new GridLayout();
                GridData compositeTopLData = new GridData();
                compositeTopLData.heightHint = 21;
                compositeTopLData.horizontalSpan = 2;
                compositeTopLData.horizontalAlignment = GridData.FILL;
                compositeTop.setLayoutData(compositeTopLData);
                compositeTopLayout.numColumns = 2;
                compositeTopLayout.marginHeight = 0;
                compositeTopLayout.horizontalSpacing = 15;
                compositeTop.setLayout(compositeTopLayout);
                {
                    labelScheduleType = new Label(compositeTop, SWT.NONE);
                    labelScheduleType.setText("Scheduling Type:");
                    GridData labelScheduleTypeLData = new GridData();
                    labelScheduleTypeLData.heightHint = 13;
                    labelScheduleType.setLayoutData(labelScheduleTypeLData);
                }
                {
                    cbType = new Combo(compositeTop, SWT.DROP_DOWN | SWT.READ_ONLY);
                    GridData cbTypeLData = new GridData();
                    cbTypeLData.heightHint = 21;
                    cbType.setLayoutData(cbTypeLData);
                    cbType.addListener(SWT.Modify, new Listener() {
                        public void handleEvent(Event arg0) {
                            ((StackLayout) groupOptions.getLayout()).topControl = groupOptions
                                .getChildren()[cbType.getSelectionIndex()];
                            groupOptions.layout();
                        }
                    });
                }
            }
            {
                groupOptions = new Group(dialogShell, SWT.NONE);
                StackLayout groupOptionsLayout = new StackLayout();
                GridData groupOptionsLData = new GridData();
                groupOptionsLData.grabExcessVerticalSpace = true;
                groupOptionsLData.horizontalAlignment = GridData.FILL;
                groupOptionsLData.verticalAlignment = GridData.FILL;
                groupOptionsLData.horizontalSpan = 2;
                groupOptions.setLayoutData(groupOptionsLData);
                groupOptions.setLayout(groupOptionsLayout);
                groupOptions.setText("Options");
            }
            {
                buttonOk = new Button(dialogShell, SWT.PUSH | SWT.CENTER);
                buttonOk.setText("Ok");
                buttonOk.addSelectionListener(new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent evt) 
                    {
                        try {
                            schedule = ((ScheduleOptions)((StackLayout)groupOptions.getLayout()).topControl).getSchedule();
                            dialogShell.dispose();
                        } catch( Exception ex ) {
                            MessageBox mb = new MessageBox( dialogShell, SWT.ICON_ERROR );
                            mb.setText( "An error occured" );
                            
                            StringWriter writer = new StringWriter();
                            ex.printStackTrace( new PrintWriter( writer ) );
                            mb.setMessage( "The following error occured: "+writer.getBuffer().toString() );
                            mb.open();
                        }
                    }
                });

                GridData buttonOkLData = new GridData();
                buttonOkLData.horizontalAlignment = GridData.END;
                buttonOkLData.heightHint = 23;
                buttonOkLData.grabExcessHorizontalSpace = true;
                buttonOk.setLayoutData(buttonOkLData);
            }
            {
                buttonCancel = new Button(dialogShell, SWT.PUSH | SWT.CENTER);
                buttonCancel.setText("Cancel");
                GridData buttonCancelLData = new GridData();
                buttonCancelLData.heightHint = 23;
                buttonCancel.setLayoutData(buttonCancelLData);
                buttonCancel.addSelectionListener(new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent evt) {
                        dialogShell.dispose();
                    }
                });
            }
            addScheduleOptions( new NullScheduleOptions( groupOptions, SWT.NULL ) );
            cbType.select( 0 );
    		addScheduleOptions( new IntervalScheduleOptions( groupOptions, SWT.NULL ) );
    		addScheduleOptions( new CrontabScheduleOptions( groupOptions, SWT.NULL ) );

    		Display display = dialogShell.getDisplay();
			dialogShell.setSize(346, 280);
            
			Rectangle rect = getParent().getBounds();
			dialogShell.setLocation( 
			        rect.x + (rect.width /2) - dialogShell.getSize().x/2,
			        rect.y + (rect.height/2) - dialogShell.getSize().y/2);
			dialogShell.layout();
    		dialogShell.open();
			while( !dialogShell.isDisposed() ) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addScheduleOptions( ScheduleOptions options )
	{
	    cbType.add( options.getSchedulingName() );
	    if( options.canHandleSchedule( schedule ) )
        {
            cbType.setText( options.getSchedulingName() );
            options.setSchedule( schedule );
        }
	}

	public void setSchedule( Schedule schedule )
	{
	    this.schedule = schedule;
	}
	
	public Schedule getSchedule()
	{
	    return this.schedule;
	}
}
