package com.planet_ink.grinder;
import java.awt.*;
import java.util.*;
import javax.swing.JTextPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JEditorPane;
import javax.swing.JList;
import javax.swing.JButton;

public class GrinderMOBs extends Dialog {

    private Frame myParent=null;
    private MapGrinder.MOB mob;
    private static boolean upAlready=false;
    int[] stockCodes;
    int[] wornCodes;
    
    
	public GrinderMOBs(Frame parent, 
	                   String title, 
	                   String text, 
	                   boolean modal)
	{
		super(parent, modal);
        myParent=parent;
		// This code is automatically generated by Visual Cafe when you add
		// components to the visual environment. It instantiates and initializes
		// the components. To modify the code, only use code syntax that matches
		// what Visual Cafe can generate, or Visual Cafe may be unable to back
		// parse your Java file into its visual environment.
        
		//{{INIT_CONTROLS
		setLayout(null);
		setBackground(new java.awt.Color(255,255,128));
		setForeground(java.awt.Color.blue);
		setFont(new Font("Dialog", Font.BOLD, 12));
		setSize(541,495);
		setVisible(false);
		okButton.setLabel("OK");
		add(okButton);
		okButton.setBounds(12,468,66,24);
		CancelButton.setLabel("Cancel");
		add(CancelButton);
		CancelButton.setBounds(468,468,66,24);
		JLabel1.setRequestFocusEnabled(false);
		JLabel1.setSelectedTextColor(java.awt.Color.blue);
		JLabel1.setCaretColor(java.awt.Color.blue);
		JLabel1.setOpaque(false);
		JLabel1.setDisabledTextColor(java.awt.Color.blue);
		JLabel1.setEditable(false);
		JLabel1.setEnabled(false);
		add(JLabel1);
		JLabel1.setBackground(java.awt.Color.yellow);
		JLabel1.setBounds(12,12,516,24);
		JLabel2.setText("Class:");
		add(JLabel2);
		JLabel2.setForeground(java.awt.Color.black);
		JLabel2.setFont(new Font("Dialog", Font.BOLD, 12));
		JLabel2.setBounds(12,36,36,24);
		JLabel3.setText("Class:");
		add(JLabel3);
		JLabel3.setForeground(java.awt.Color.black);
		JLabel3.setBounds(60,36,144,24);
		JLabel4.setText("Experience Level:");
		add(JLabel4);
		JLabel4.setForeground(java.awt.Color.black);
		JLabel4.setFont(new Font("Dialog", Font.BOLD, 12));
		JLabel4.setBounds(216,36,108,24);
		add(level);
		level.setBounds(324,36,24,24);
		JLabel5.setText("Ability Code:");
		add(JLabel5);
		JLabel5.setForeground(java.awt.Color.black);
		JLabel5.setFont(new Font("Dialog", Font.BOLD, 12));
		JLabel5.setBounds(216,60,108,24);
		add(ability);
		ability.setBounds(324,60,24,24);
		JLabel6.setText("Avg. Hit Points:");
		JLabel6.setEnabled(false);
		add(JLabel6);
		JLabel6.setForeground(java.awt.Color.black);
		JLabel6.setFont(new Font("Dialog", Font.BOLD, 12));
		JLabel6.setBounds(360,36,108,24);
		hp.setEnabled(false);
		add(hp);
		hp.setBounds(456,36,36,24);
		JLabel7.setText("Rejuv/Ticks:");
		add(JLabel7);
		JLabel7.setForeground(java.awt.Color.black);
		JLabel7.setFont(new Font("Dialog", Font.BOLD, 12));
		JLabel7.setBounds(12,60,72,24);
		add(rejuv);
		rejuv.setBounds(84,60,120,24);
		JLabel8.setText("Alignment:");
		JLabel8.setEnabled(false);
		add(JLabel8);
		JLabel8.setForeground(java.awt.Color.black);
		JLabel8.setFont(new Font("Dialog", Font.BOLD, 12));
		JLabel8.setBounds(12,168,60,24);
		alignment.setEnabled(false);
		add(alignment);
		alignment.setBounds(84,168,72,25);
		JLabel9.setText("Money:");
		JLabel9.setEnabled(false);
		add(JLabel9);
		JLabel9.setForeground(java.awt.Color.black);
		JLabel9.setFont(new Font("Dialog", Font.BOLD, 12));
		JLabel9.setBounds(12,240,60,24);
		money.setEnabled(false);
		add(money);
		money.setBounds(84,240,72,24);
		JLabel10.setText("Gender:");
		JLabel10.setEnabled(false);
		add(JLabel10);
		JLabel10.setForeground(java.awt.Color.black);
		JLabel10.setFont(new Font("Dialog", Font.BOLD, 12));
		JLabel10.setBounds(12,192,60,24);
		gender.setEnabled(false);
		add(gender);
		gender.setBounds(84,192,72,25);
		JLabel11.setText("Name:");
		JLabel11.setEnabled(false);
		add(JLabel11);
		JLabel11.setForeground(java.awt.Color.black);
		JLabel11.setFont(new Font("Dialog", Font.BOLD, 12));
		JLabel11.setBounds(12,96,60,24);
		name.setEnabled(false);
		add(name);
		name.setBounds(84,96,108,24);
		JLabel12.setText("Display:");
		JLabel12.setEnabled(false);
		add(JLabel12);
		JLabel12.setForeground(java.awt.Color.black);
		JLabel12.setFont(new Font("Dialog", Font.BOLD, 12));
		JLabel12.setBounds(12,120,60,24);
		displayText.setEnabled(false);
		add(displayText);
		displayText.setBounds(84,120,264,24);
		JLabel13.setText("Description:");
		JLabel13.setEnabled(false);
		add(JLabel13);
		JLabel13.setForeground(java.awt.Color.black);
		JLabel13.setFont(new Font("Dialog", Font.BOLD, 12));
		JLabel13.setBounds(12,144,72,24);
		description.setEnabled(false);
		add(description);
		description.setBounds(84,144,264,24);
		attack.setEnabled(false);
		add(attack);
		attack.setBounds(228,216,24,24);
		JLabel14.setText("Armor:");
		JLabel14.setEnabled(false);
		add(JLabel14);
		JLabel14.setForeground(java.awt.Color.black);
		JLabel14.setFont(new Font("Dialog", Font.BOLD, 12));
		JLabel14.setBounds(180,168,48,24);
		JLabel15.setText("Attack:");
		JLabel15.setEnabled(false);
		add(JLabel15);
		JLabel15.setForeground(java.awt.Color.black);
		JLabel15.setFont(new Font("Dialog", Font.BOLD, 12));
		JLabel15.setBounds(180,216,48,24);
		speed.setEnabled(false);
		add(speed);
		speed.setBounds(228,192,24,24);
		damage.setEnabled(false);
		add(damage);
		damage.setBounds(228,240,24,24);
		JLabel16.setText("Damage:");
		JLabel16.setEnabled(false);
		add(JLabel16);
		JLabel16.setForeground(java.awt.Color.black);
		JLabel16.setFont(new Font("Dialog", Font.BOLD, 12));
		JLabel16.setBounds(168,240,60,24);
		armor.setEnabled(false);
		add(armor);
		armor.setBounds(228,168,24,24);
		JLabel17.setText("Speed:");
		JLabel17.setEnabled(false);
		add(JLabel17);
		JLabel17.setForeground(java.awt.Color.black);
		JLabel17.setFont(new Font("Dialog", Font.BOLD, 12));
		JLabel17.setBounds(180,192,48,24);
		weight.setEnabled(false);
		add(weight);
		weight.setBounds(84,216,72,24);
		JLabel18.setText("Weight:");
		JLabel18.setEnabled(false);
		add(JLabel18);
		JLabel18.setForeground(java.awt.Color.black);
		JLabel18.setFont(new Font("Dialog", Font.BOLD, 12));
		JLabel18.setBounds(12,216,60,24);
		behaviors.setMultipleMode(true);
		behaviors.setEnabled(false);
		add(behaviors);
		behaviors.setBounds(360,120,168,132);
		JLabel19.setText("Behaviors (select):");
		JLabel19.setEnabled(false);
		add(JLabel19);
		JLabel19.setForeground(java.awt.Color.black);
		JLabel19.setFont(new Font("Dialog", Font.BOLD, 12));
		JLabel19.setBounds(360,96,156,24);
		abilities.setMultipleMode(true);
		abilities.setEnabled(false);
		add(abilities);
		abilities.setBounds(180,288,168,144);
		JLabel20.setText("Abilities (select):");
		JLabel20.setEnabled(false);
		add(JLabel20);
		JLabel20.setForeground(java.awt.Color.black);
		JLabel20.setFont(new Font("Dialog", Font.BOLD, 12));
		JLabel20.setBounds(180,264,168,24);
		store.setEnabled(false);
		add(store);
		store.setBounds(360,288,168,144);
		JLabel21.setText("Personal Inventory:");
		JLabel21.setEnabled(false);
		add(JLabel21);
		JLabel21.setForeground(java.awt.Color.black);
		JLabel21.setFont(new Font("Dialog", Font.BOLD, 12));
		JLabel21.setBounds(12,264,168,24);
		worn.setEnabled(false);
		add(worn);
		worn.setBounds(60,432,108,25);
		JLabel22.setText("Stock:");
		JLabel22.setEnabled(false);
		add(JLabel22);
		JLabel22.setForeground(java.awt.Color.black);
		JLabel22.setFont(new Font("Dialog", Font.BOLD, 12));
		JLabel22.setBounds(360,432,48,24);
		JLabel23.setText("Worn:");
		JLabel23.setEnabled(false);
		add(JLabel23);
		JLabel23.setForeground(java.awt.Color.black);
		JLabel23.setFont(new Font("Dialog", Font.BOLD, 12));
		JLabel23.setBounds(12,432,48,24);
		inventory.setEnabled(false);
		add(inventory);
		inventory.setBounds(12,288,156,144);
		JLabel24.setText("Store Stock:");
		JLabel24.setEnabled(false);
		add(JLabel24);
		JLabel24.setForeground(java.awt.Color.black);
		JLabel24.setFont(new Font("Dialog", Font.BOLD, 12));
		JLabel24.setBounds(360,264,84,24);
		stock.setEnabled(false);
		add(stock);
		stock.setBounds(408,432,36,24);
		whatISell.setEnabled(false);
		add(whatISell);
		whatISell.setBounds(444,264,84,25);
		fillMe.setLabel("GenFill");
		fillMe.setEnabled(false);
		add(fillMe);
		fillMe.setBounds(360,60,60,24);
		sneaking.setLabel("Sneaking");
		sneaking.setEnabled(false);
		add(sneaking);
		sneaking.setForeground(java.awt.Color.black);
		sneaking.setBounds(264,168,84,24);
		invisible.setLabel("Invisible");
		invisible.setEnabled(false);
		add(invisible);
		invisible.setForeground(java.awt.Color.black);
		invisible.setBounds(264,192,84,24);
		flying.setLabel("Flying");
		flying.setEnabled(false);
		add(flying);
		flying.setForeground(java.awt.Color.black);
		flying.setBounds(264,216,84,24);
		darkvision.setLabel("Darkvision");
		darkvision.setEnabled(false);
		add(darkvision);
		darkvision.setForeground(java.awt.Color.black);
		darkvision.setBounds(264,240,84,24);
		//$$ titledBorder1.move(48,420);
		setTitle("Yo baby yo baby yo!");
		setResizable(false);
		//}}
		
		JLabel1.setText(text);
		JLabel1.setForeground(java.awt.Color.blue);
		JLabel1.setFont(new Font("Dialog",Font.BOLD,12));
		setTitle(title);
        
		//{{REGISTER_LISTENERS
		SymWindow aSymWindow = new SymWindow();
		this.addWindowListener(aSymWindow);
		SymAction lSymAction = new SymAction();
		okButton.addActionListener(lSymAction);
		CancelButton.addActionListener(lSymAction);
		SymKey lSymKey=new SymKey();
		okButton.addKeyListener(lSymKey);
		CancelButton.addKeyListener(lSymKey);
		SymKey aSymKey = new SymKey();
		//SymText lSymText = new SymText();
		//SymPropertyChange lSymPropertyChange = new SymPropertyChange();
		SymItem lSymItem = new SymItem();
		inventory.addItemListener(lSymItem);
		store.addItemListener(lSymItem);
		SymFocus aSymFocus = new SymFocus();
		stock.addFocusListener(aSymFocus);
		worn.addItemListener(lSymItem);
		level.addFocusListener(aSymFocus);
		ability.addFocusListener(aSymFocus);
		worn.addKeyListener(aSymKey);
		stock.addKeyListener(aSymKey);
		fillMe.addActionListener(lSymAction);
		//}}

		alignment.add("Good");
		alignment.add("Neutral");
		alignment.add("Evil");
		try {
			alignment.select(0);
		}
		catch (IllegalArgumentException e) { }
		whatISell.add("Anything");
		whatISell.add("General");
		whatISell.add("Armor");
		whatISell.add("M. Items");
		whatISell.add("Weapons");
		whatISell.add("Pets");
		whatISell.add("Leather");
		whatISell.add("Only Stock");
		whatISell.add("Training");
		whatISell.add("Casting");
		
		worn.add("Not Carried");
		worn.add("Inventory");
		worn.add("Head");
		worn.add("Neck");
		worn.add("Torso");
		worn.add("Arms");
		worn.add("Left Wrist");
		worn.add("Right Wrist");
		worn.add("Left Finger");
		worn.add("Right Finger");
		worn.add("Feet");
		worn.add("Held");
		worn.add("Wielded");
		worn.add("Hands");
		worn.add("Floating Nearby");
		worn.add("Waist");
		worn.add("Legs");
		try {
			worn.select(0);
		}
		catch (IllegalArgumentException e) { }
		gender.add("Male");
		gender.add("Female");
		try {
			gender.select(0);
		}
		catch (IllegalArgumentException e) { }
	}
    
	public void addNotify()
	{
		// Record the size of the window prior to calling parents addNotify.
                Dimension d = getSize();

		super.addNotify();

		// Only do this once.
		if (fComponentsAdjusted)
			return;

		// Adjust components according to the insets
		Insets insets = getInsets();
		setSize(insets.left + insets.right + d.width, insets.top + insets.bottom + d.height);
		Component components[] = getComponents();
		for (int i = 0; i < components.length; i++)
		{
			Point p = components[i].getLocation();
			p.translate(insets.left, insets.top);
			components[i].setLocation(p);
		}

		// Used for addNotify check.
		fComponentsAdjusted = true;
	}

    public void populateDropDowns()
    {
        JLabel3.setText(mob.classID);
        level.setText(""+mob.level);
        ability.setText(""+mob.ability);
        rejuv.setText(""+mob.rejuv);
        JLabel12.setText("Misc Text:");
        if(mob.classID.startsWith("Gen")&&(mob.classID.length()>4)&&(Character.isUpperCase(mob.classID.charAt(3))))
        {
            MapGrinder.GenGen g=new MapGrinder.GenGen();
            GenGrinder.setPropertiesStr(mob,g,mob.miscText());
            
            sneaking.setEnabled(true);
            invisible.setEnabled(true);
            flying.setEnabled(true);
            darkvision.setEnabled(true);
            sneaking.setState((g.disposition&GenGrinder.IS_SNEAKING)==GenGrinder.IS_SNEAKING);
            flying.setState((g.disposition&GenGrinder.IS_FLYING)==GenGrinder.IS_FLYING);
            invisible.setState((g.disposition&GenGrinder.IS_INVISIBLE)==GenGrinder.IS_INVISIBLE);
            darkvision.setState((g.sensesMask&GenGrinder.CAN_SEE_DARK)==GenGrinder.CAN_SEE_DARK);
            
            
            
            g.level=mob.level;
            g.ability=mob.ability;
            g.rejuv=mob.rejuv;
            JLabel12.setText("Display:");
            fillMe.setEnabled(true);
            for(int c=0;c<this.getComponentCount();c++)
            {
                Component C=this.getComponent(c);
                C.setEnabled(true);
            }
            JLabel6.setEnabled(false);
            hp.setEnabled(false);
	        int l=GenGrinder.s_int(level.getText());
	        int a=GenGrinder.s_int(ability.getText());
	        a = a / 2;
	        hp.setText(""+((l*10)+(a*l)));
            name.setText(g.name);
            displayText.setText(g.displayText);
            description.setText(g.description);
            alignment.select(0);
            if(g.alignment<650)
            {
                alignment.select(1);
                if(g.alignment<350)
                    alignment.select(2);
            }
            gender.select(0);
            if(g.gender=='F')
                gender.select(1);
            money.setText(""+g.money);
            weight.setText(""+g.weight);
            armor.setText(""+g.armor);
            speed.setText(""+g.speed);
            attack.setText(""+g.attack);
            damage.setText(""+g.damage);
            Vector V=TheGrinder.getBehaviorTypes();
            for(int v=0;v<V.size();v++)
            {
                behaviors.add((String)V.elementAt(v));
                if(g.behaviors!=null)
                for(int e=0;e<g.behaviors.size();e++)
                    if(((String)g.behaviors.elementAt(e)).equalsIgnoreCase((String)V.elementAt(v)))
                        behaviors.select(v);
            }
            V=TheGrinder.getAbilityTypes();
            for(int v=0;v<V.size();v++)
            {
                abilities.add((String)V.elementAt(v));
                if(g.abilities!=null)
                for(int e=0;e<g.abilities.size();e++)
                    if(((String)g.abilities.elementAt(e)).equalsIgnoreCase((String)V.elementAt(v)))
                        abilities.select(v);
            }
            V=TheGrinder.getItemTypes();
            wornCodes=new int[V.size()+1];
            for(int v=0;v<V.size();v++)
            {
                inventory.add((String)V.elementAt(v));
                if(g.items!=null)
                for(int e=0;e<g.items.size();e++)
                    if((((String)g.items.elementAt(e)).equalsIgnoreCase((String)V.elementAt(v)))
                    &&((g.wornCodes!=null)&&(g.wornCodes.size()>e)))
                    {
                        int wc=((Integer)g.wornCodes.elementAt(e)).intValue();
                        for(int y=0;y<30;y++)
                        {
                            int bit=1<<y;
                            if(((wc&bit)==bit)&&(y<(worn.getItemCount()-2)))
                                wornCodes[v]=y+2;
                        }
                        if(wornCodes[v]==0) 
                            wornCodes[v]=1;
                    }
            }
            
            if(mob.classID.equals("GenShopkeeper"))
            {
                whatISell.select(g.whatISell);
                V=TheGrinder.getAbilityTypes();
                Vector V1=TheGrinder.getMOBTypes();
                Vector V2=TheGrinder.getItemTypes();
                
                stockCodes=new int[V.size()+V1.size()+V2.size()+1];
                for(int v=0;v<V.size();v++)
                {
                    store.add((String)V.elementAt(v));
                    if(g.inventory!=null)
                    for(int e=0;e<g.inventory.size();e++)
                        if((((String)g.inventory.elementAt(e)).equalsIgnoreCase((String)V.elementAt(v)))
                        &&((g.numItems!=null)&&(g.numItems.size()>e)))
                            stockCodes[v]=((Integer)g.numItems.elementAt(e)).intValue();
                }
                for(int v=0;v<V1.size();v++)
                {
                    store.add((String)V1.elementAt(v));
                    if(g.inventory!=null)
                    for(int e=0;e<g.inventory.size();e++)
                        if((((String)g.inventory.elementAt(e)).equalsIgnoreCase((String)V1.elementAt(v)))
                        &&((g.numItems!=null)&&(g.numItems.size()>e)))
                            stockCodes[V.size()+v]=((Integer)g.numItems.elementAt(e)).intValue();
                }
                for(int v=0;v<V2.size();v++)
                {
                    store.add((String)V2.elementAt(v));
                    if(g.inventory!=null)
                    for(int e=0;e<g.inventory.size();e++)
                        if((((String)g.inventory.elementAt(e)).equalsIgnoreCase((String)V2.elementAt(v)))
                        &&((g.numItems!=null)&&(g.numItems.size()>e)))
                            stockCodes[V.size()+V1.size()+v]=((Integer)g.numItems.elementAt(e)).intValue();
                }
            }
            else
            {
               JLabel24.setEnabled(false);
               whatISell.setEnabled(false);
               store.setEnabled(false);
               JLabel22.setEnabled(false);
               stock.setEnabled(false);
            }
        }
        else
        {
            JLabel12.setEnabled(true);
            displayText.setEnabled(true);
            displayText.setText(mob.miscText());
        }
    }

    public void dePopulateDropDowns()
    {
        if(mob==null) return;
        
        mob.level=GenGrinder.s_int(level.getText());
        mob.ability=GenGrinder.s_int(ability.getText());
        mob.rejuv=GenGrinder.s_int(rejuv.getText());
        if(mob.rejuv<=0)
            mob.rejuv=Integer.MAX_VALUE;
        if(mob.classID.startsWith("Gen")&&(mob.classID.length()>4)&&(Character.isUpperCase(mob.classID.charAt(3))))
        {
            MapGrinder.GenGen g=new MapGrinder.GenGen();
            GenGrinder.setPropertiesStr(mob,g,mob.miscText());
            
            g.disposition=g.disposition&((int)GenGrinder.ALLMASK-GenGrinder.IS_SNEAKING);
            g.disposition=g.disposition&((int)GenGrinder.ALLMASK-GenGrinder.IS_FLYING);
            g.disposition=g.disposition&((int)GenGrinder.ALLMASK-GenGrinder.IS_INVISIBLE);
            if(sneaking.getState()) g.disposition=g.disposition|GenGrinder.IS_SNEAKING;
            if(flying.getState()) g.disposition=g.disposition|GenGrinder.IS_FLYING;
            if(invisible.getState()) g.disposition=g.disposition|GenGrinder.IS_INVISIBLE;
            g.sensesMask=g.sensesMask&((int)GenGrinder.ALLMASK-GenGrinder.CAN_SEE_DARK);
            if(darkvision.getState()) g.sensesMask=g.sensesMask|GenGrinder.CAN_SEE_DARK;
            
            g.level=mob.level;
            g.ability=mob.ability;
            g.rejuv=mob.rejuv;
            g.name=name.getText();
            g.displayText=displayText.getText();
            g.description=description.getText();
            if(alignment.getSelectedIndex()==0)
                g.alignment=1000;
            else
            if(alignment.getSelectedIndex()==2)
                g.alignment=0;
            else
                g.alignment=500;
            if(gender.getSelectedIndex()==0)
                g.gender=(char)'M';
            else
                g.gender=(char)'F';
            g.money=GenGrinder.s_int(money.getText());
            g.weight=GenGrinder.s_int(weight.getText());
            g.armor=GenGrinder.s_int(armor.getText());
            try
            {
                g.speed=Double.parseDouble(speed.getText());
            }
            catch (Exception e)
            {
                g.speed=GenGrinder.s_int(speed.getText());
                if(g.speed==0) g.speed=1;
            }
            g.attack=GenGrinder.s_int(attack.getText());
            g.damage=GenGrinder.s_int(damage.getText());
            g.behaviors=new Vector();
            for(int v=0;v<behaviors.getItemCount();v++)
                if(behaviors.isIndexSelected(v))
                    g.behaviors.addElement(behaviors.getItem(v));
            g.abilities=new Vector();
            for(int v=0;v<abilities.getItemCount();v++)
                if(abilities.isIndexSelected(v))
                    g.abilities.addElement(abilities.getItem(v));
            g.items=new Vector();
            g.wornCodes=new Vector();
            for(int v=0;v<wornCodes.length;v++)
            {
                int x=wornCodes[v];
                if(x>0)
                {
                    g.items.addElement(inventory.getItem(v));
                    if(x==1)
                        g.wornCodes.addElement(new Integer(0));
                    else
                    {
                        x--;
                        x--;
                        int bit=1<<x;
                        g.wornCodes.addElement(new Integer(bit));
                    }
                }
            }
            
            if(mob.classID.equals("GenShopkeeper"))
            {
                g.whatISell=whatISell.getSelectedIndex();
                g.inventory=new Vector();
                g.numItems=new Vector();
                for(int v=0;v<stockCodes.length;v++)
                {
                    int x=stockCodes[v];
                    if(x>0)
                    {
                        g.inventory.addElement(store.getItem(v));
                        g.numItems.addElement(new Integer(x));
                    }
                }
            }
            mob.setMiscText(GenGrinder.getPropertiesStr(mob,g));
        }
        else
            mob.setMiscText(displayText.getText());
    }

    public static MapGrinder.MOB doMe(Frame parent, 
                                      MapGrinder.MOB oldMOB,
	                                  String title)
	{
	    if(GrinderMOBs.upAlready) return null;
	    GrinderMOBs.upAlready=true;
	    String text="Modify MOB "+oldMOB.classID+".";
	    GrinderMOBs GOC=new GrinderMOBs(parent,title,text,true);
	    GOC.mob=oldMOB.cloneof();
	    GOC.populateDropDowns();
	    GOC.setVisible(true);
	    GOC.dePopulateDropDowns();
	    GrinderMOBs.upAlready=false;
	    return GOC.mob;
	}


	public void setVisible(boolean b)
	{
	    if (b)
	    {
    		Rectangle bounds = getParent().getBounds();
    		Rectangle abounds = getBounds();

    		setLocation(bounds.x + (bounds.width - abounds.width)/ 2,
    			 bounds.y + 50+(bounds.height - abounds.height)/2);
	    }

		super.setVisible(b);
	}

	//{{DECLARE_CONTROLS
	java.awt.Button okButton = new java.awt.Button();
	java.awt.Button CancelButton = new java.awt.Button();
	javax.swing.JTextPane JLabel1 = new javax.swing.JTextPane();
	javax.swing.JLabel JLabel2 = new javax.swing.JLabel();
	javax.swing.JLabel JLabel3 = new javax.swing.JLabel();
	javax.swing.JLabel JLabel4 = new javax.swing.JLabel();
	javax.swing.JTextField level = new javax.swing.JTextField();
	javax.swing.JLabel JLabel5 = new javax.swing.JLabel();
	javax.swing.JTextField ability = new javax.swing.JTextField();
	javax.swing.JLabel JLabel6 = new javax.swing.JLabel();
	javax.swing.JTextField hp = new javax.swing.JTextField();
	javax.swing.JLabel JLabel7 = new javax.swing.JLabel();
	javax.swing.JTextField rejuv = new javax.swing.JTextField();
	javax.swing.JLabel JLabel8 = new javax.swing.JLabel();
	java.awt.Choice alignment = new java.awt.Choice();
	javax.swing.JLabel JLabel9 = new javax.swing.JLabel();
	javax.swing.JTextField money = new javax.swing.JTextField();
	javax.swing.JLabel JLabel10 = new javax.swing.JLabel();
	java.awt.Choice gender = new java.awt.Choice();
	javax.swing.JLabel JLabel11 = new javax.swing.JLabel();
	javax.swing.JTextField name = new javax.swing.JTextField();
	javax.swing.JLabel JLabel12 = new javax.swing.JLabel();
	javax.swing.JTextField displayText = new javax.swing.JTextField();
	javax.swing.JLabel JLabel13 = new javax.swing.JLabel();
	javax.swing.JTextField description = new javax.swing.JTextField();
	javax.swing.JTextField attack = new javax.swing.JTextField();
	javax.swing.JLabel JLabel14 = new javax.swing.JLabel();
	javax.swing.JLabel JLabel15 = new javax.swing.JLabel();
	javax.swing.JTextField speed = new javax.swing.JTextField();
	javax.swing.JTextField damage = new javax.swing.JTextField();
	javax.swing.JLabel JLabel16 = new javax.swing.JLabel();
	javax.swing.JTextField armor = new javax.swing.JTextField();
	javax.swing.JLabel JLabel17 = new javax.swing.JLabel();
	javax.swing.JTextField weight = new javax.swing.JTextField();
	javax.swing.JLabel JLabel18 = new javax.swing.JLabel();
	java.awt.List behaviors = new java.awt.List(4);
	javax.swing.JLabel JLabel19 = new javax.swing.JLabel();
	java.awt.List abilities = new java.awt.List(4);
	javax.swing.JLabel JLabel20 = new javax.swing.JLabel();
	java.awt.List store = new java.awt.List(4);
	javax.swing.JLabel JLabel21 = new javax.swing.JLabel();
	java.awt.Choice worn = new java.awt.Choice();
	javax.swing.JLabel JLabel22 = new javax.swing.JLabel();
	javax.swing.JLabel JLabel23 = new javax.swing.JLabel();
	java.awt.List inventory = new java.awt.List(4);
	javax.swing.JLabel JLabel24 = new javax.swing.JLabel();
	javax.swing.JTextField stock = new javax.swing.JTextField();
	java.awt.Choice whatISell = new java.awt.Choice();
	java.awt.Button fillMe = new java.awt.Button();
	java.awt.Checkbox sneaking = new java.awt.Checkbox();
	java.awt.Checkbox invisible = new java.awt.Checkbox();
	java.awt.Checkbox flying = new java.awt.Checkbox();
	java.awt.Checkbox darkvision = new java.awt.Checkbox();
	//}}
    
    // Used for addNotify check.
	boolean fComponentsAdjusted = false;

	class SymAction implements java.awt.event.ActionListener
	{
		public void actionPerformed(java.awt.event.ActionEvent event)
		{
			Object object = event.getSource();
			if (object == okButton)
				okButton_ActionPerformed(event);
			else if (object == CancelButton)
				CancelButton_ActionPerformed(event);
			else if (object == fillMe)
				fillMe_ActionPerformed(event);
			
		}
	}

	void okButton_ActionPerformed(java.awt.event.ActionEvent event)
	{
		try {
			this.dispose();
		} catch (Exception e) {
		}
	}


	class SymWindow extends java.awt.event.WindowAdapter
	{
		public void windowOpened(java.awt.event.WindowEvent event)
		{
			Object object = event.getSource();
			if (object == GrinderMOBs.this)
				GrinderMOBs_WindowOpened(event);
		}

		public void windowClosing(java.awt.event.WindowEvent event)
		{
			Object object = event.getSource();
			if (object == GrinderMOBs.this)
				GrinderMOBs_WindowClosing(event);
		}
	}

	void GrinderMOBs_WindowClosing(java.awt.event.WindowEvent event)
	{
		// to do: code goes here.
	}


	class SymKey extends java.awt.event.KeyAdapter
	{
		public void keyReleased(java.awt.event.KeyEvent event)
		{
			Object object = event.getSource();
			if (object == stock)
				stock_keyReleased(event);
		}

		public void keyTyped(java.awt.event.KeyEvent event)
		{
			//Object object = event.getSource();
			
		}

		public void keyPressed(java.awt.event.KeyEvent event)
		{
			Object object = event.getSource();
			if ((object == okButton)&&((event.getKeyCode()==13)||(event.getKeyCode()==10)))
                okButton_ActionPerformed(null);
			if ((object == CancelButton)&&((event.getKeyCode()==13)||(event.getKeyCode()==10)))
                CancelButton_ActionPerformed(null);
			else if (object == worn)
				worn_KeyPressed(event);
			else if (object == stock)
				stock_keyPressed(event);
				
		}
	}
	void GrinderMOBs_WindowOpened(java.awt.event.WindowEvent event)
	{
	}

	void CancelButton_ActionPerformed(java.awt.event.ActionEvent event)
	{
	    mob=null;
		try {
			this.dispose();
		} catch (Exception e) {
		}

			 
	}

	class SymText implements java.awt.event.TextListener
	{
		public void textValueChanged(java.awt.event.TextEvent event)
		{
		}
	}

	class SymPropertyChange implements java.beans.PropertyChangeListener
	{
		public void propertyChange(java.beans.PropertyChangeEvent event)
		{
		}
	}

	class SymItem implements java.awt.event.ItemListener
	{
		public void itemStateChanged(java.awt.event.ItemEvent event)
		{
			Object object = event.getSource();
			if (object == inventory)
				inventory_ItemStateChanged(event);
			else if (object == store)
				store_ItemStateChanged(event);
			else if (object == worn)
				worn_ItemStateChanged(event);
		}
	}

	void inventory_ItemStateChanged(java.awt.event.ItemEvent event)
	{
	    if(inventory.getSelectedIndex()>=0)
    	    worn.select(wornCodes[inventory.getSelectedIndex()]);
	}

	void store_ItemStateChanged(java.awt.event.ItemEvent event)
	{
	    if(stockCodes[store.getSelectedIndex()]<=0)
	        stock.setText("N/A");
	    else
    	    stock.setText(""+stockCodes[store.getSelectedIndex()]);
	}

	class SymFocus extends java.awt.event.FocusAdapter
	{
		public void focusLost(java.awt.event.FocusEvent event)
		{
			Object object = event.getSource();
			if (object == stock)
				stock_focusLost(event);
			else if (object == level)
				level_focusLost(event);
			else if (object == ability)
				ability_focusLost(event);
		}
	}

	void stock_focusLost(java.awt.event.FocusEvent event)
	{
	}

	void worn_ItemStateChanged(java.awt.event.ItemEvent event)
	{
	    int x=inventory.getSelectedIndex();
	    if(x>=0)
	        wornCodes[x]=worn.getSelectedIndex();
	}

	void level_focusLost(java.awt.event.FocusEvent event)
	{
        if(mob.classID.startsWith("Gen")&&(mob.classID.length()>4)&&(Character.isUpperCase(mob.classID.charAt(3))))
        {
	        int l=GenGrinder.s_int(level.getText());
	        int a=GenGrinder.s_int(ability.getText());
	        a = a / 2;
	        hp.setText(""+((l*10)+(a*l)));
	    }
	}

	void ability_focusLost(java.awt.event.FocusEvent event)
	{
        if(mob.classID.startsWith("Gen")&&(mob.classID.length()>4)&&(Character.isUpperCase(mob.classID.charAt(3))))
        {
	        int l=GenGrinder.s_int(level.getText());
	        int a=GenGrinder.s_int(ability.getText());
	        a = a / 2;
	        hp.setText(""+((l*10)+(a*l)));
	    }
	}

	void worn_KeyPressed(java.awt.event.KeyEvent event)
	{
		// to do: code goes here.
			 
	}

	void stock_keyPressed(java.awt.event.KeyEvent event)
	{
	    int x=store.getSelectedIndex();
	    if(x>=0)
	        stockCodes[x]=GenGrinder.s_int(stock.getText());
	}

	void stock_keyReleased(java.awt.event.KeyEvent event)
	{
	    int x=store.getSelectedIndex();
	    if(x>=0)
	        stockCodes[x]=GenGrinder.s_int(stock.getText());
	}

	void fillMe_ActionPerformed(java.awt.event.ActionEvent event)
	{
	    int levell=GenGrinder.s_int(level.getText());
	    if(GrinderYesNo.askMe(myParent,"Confirm some gen-help","Repopulate Attack, Damage, and Armor for a level "+levell+" mob?"))
	    {
	        MapGrinder.MOB bob=new MapGrinder.MOB();
	        bob.level=levell;
	        TheGrinder.reFillMe(bob,mob.classID,true);
            MapGrinder.GenGen g=new MapGrinder.GenGen();
            GenGrinder.setPropertiesStr(bob,g,bob.miscText());
            ability.setText("11");
            weight.setText("150");
            attack.setText(""+g.attack);
            damage.setText(""+g.damage);
            armor.setText(""+g.armor);
            money.setText(""+g.money);
	        int l=GenGrinder.s_int(level.getText());
	        int a=GenGrinder.s_int(ability.getText());
	        a = a / 2;
	        hp.setText(""+((l*10)+(a*l)));
	    }
	}
}
