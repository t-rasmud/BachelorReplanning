/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import worldmodel.MapAgent;
import worldmodel.MapBox;
import worldmodel.World;
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JTextField;
import java.beans.*; //property change stuff
import java.awt.*;
import java.awt.event.*;
import javax.swing.JFrame;
import worldmodel.Goal;
import worldmodel.Wall;
/**
 *
 * @author jakopchronos
 */
public class AddItemsPanel extends JPanel{
    public AddItemsPanel(final World world){
        this.setPreferredSize(new Dimension(1000, 100));
        this.setMaximumSize( this.getPreferredSize() );
        this.setBackground(Color.blue);
        
        MapAgent agent = new MapAgent(1,0);
        this.add(agent);
        agent.addMouseListener(new MouseAdapter()
                      {
                        @Override
                       public void mouseClicked(MouseEvent me)
                         {
                            world.removeMovableObject();
                            world.setMoveAbleObject(world.createNewAgent());
                         }
                      });
        
        MapBox box = new MapBox("Q",0,0);
        this.add(box);
        final JFrame parent = (JFrame)this.getParent();
        box.addMouseListener(new MouseAdapter()
                      {
                        @Override
                       public void mouseClicked(MouseEvent me)
                         {
                            world.removeMovableObject();
                            String s = (String)JOptionPane.showInputDialog(
                                parent,
                                "Select letter for box\n",
                                "Customized Dialog",
                                JOptionPane.PLAIN_MESSAGE,
                                null,
                                null,
                                "q");
                            world.setMoveAbleObject(new MapBox(s,0,0));
                         }
                      });
        Goal goal = new Goal("Q",0,0);
        this.add(goal);
        goal.addMouseListener(new MouseAdapter()
                      {
                        @Override
                       public void mouseClicked(MouseEvent me)
                         {
                            world.removeMovableObject();
                            String s = (String)JOptionPane.showInputDialog(
                                parent,
                                "Select letter for box\n",
                                "Customized Dialog",
                                JOptionPane.PLAIN_MESSAGE,
                                null,
                                null,
                                "q");
                            world.setMoveAbleObject(new Goal(s,0,0));
                         }
                      });
        Wall wall = new Wall(0);
        this.add(wall);
        wall.addMouseListener(new MouseAdapter()
                      {
                        @Override
                       public void mouseClicked(MouseEvent me)
                         {
                            world.removeMovableObject();
                            world.setMoveAbleObject(new Wall(0));
                         }
                      });
    }
    
    private class CustomDialog extends JDialog
                   implements ActionListener,
                              PropertyChangeListener {
    private String typedText = null;
    private JTextField textField;

    private String magicWord;
    private JOptionPane optionPane;

    private String btnString1 = "Enter";
    private String btnString2 = "Cancel";

    /**
     * Returns null if the typed string was invalid;
     * otherwise, returns the string as the user entered it.
     */
    public String getValidatedText() {
        return typedText;
    }

    /** Creates the reusable dialog. */
    public CustomDialog(Frame aFrame, String aWord) {
        super(aFrame, true);

        magicWord = aWord.toUpperCase();
        setTitle("Quiz");

        textField = new JTextField(10);

        //Create an array of the text and components to be displayed.
        String msgString1 = "What was Dr. SEUSS's real last name?";
        String msgString2 = "(The answer is \"" + magicWord
                              + "\".)";
        Object[] array = {msgString1, msgString2, textField};

        //Create an array specifying the number of dialog buttons
        //and their text.
        Object[] options = {btnString1, btnString2};

        //Create the JOptionPane.
        optionPane = new JOptionPane(array,
                                    JOptionPane.QUESTION_MESSAGE,
                                    JOptionPane.YES_NO_OPTION,
                                    null,
                                    options,
                                    options[0]);

        //Make this dialog display it.
        setContentPane(optionPane);

        //Handle window closing correctly.
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent we) {
                /*
                 * Instead of directly closing the window,
                 * we're going to change the JOptionPane's
                 * value property.
                 */
                    optionPane.setValue(new Integer(
                                        JOptionPane.CLOSED_OPTION));
            }
        });

        //Ensure the text field always gets the first focus.
        addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent ce) {
                textField.requestFocusInWindow();
            }
        });

        //Register an event handler that puts the text into the option pane.
        textField.addActionListener(this);

        //Register an event handler that reacts to option pane state changes.
        optionPane.addPropertyChangeListener(this);
    }

    /** This method handles events for the text field. */
    public void actionPerformed(ActionEvent e) {
        optionPane.setValue(btnString1);
    }

    /** This method reacts to state changes in the option pane. */
    public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();

        if (isVisible()
         && (e.getSource() == optionPane)
         && (JOptionPane.VALUE_PROPERTY.equals(prop) ||
             JOptionPane.INPUT_VALUE_PROPERTY.equals(prop))) {
            Object value = optionPane.getValue();

            if (value == JOptionPane.UNINITIALIZED_VALUE) {
                //ignore reset
                return;
            }

            //Reset the JOptionPane's value.
            //If you don't do this, then if the user
            //presses the same button next time, no
            //property change event will be fired.
            optionPane.setValue(
                    JOptionPane.UNINITIALIZED_VALUE);

            if (btnString1.equals(value)) {
                    typedText = textField.getText();
                String ucText = typedText.toUpperCase();
                if (magicWord.equals(ucText)) {
                    //we're done; clear and dismiss the dialog
                    clearAndHide();
                } else {
                    //text was invalid
                    textField.selectAll();
                    JOptionPane.showMessageDialog(
                                    CustomDialog.this,
                                    "Sorry, \"" + typedText + "\" "
                                    + "isn't a valid response.\n"
                                    + "Please enter "
                                    + magicWord + ".",
                                    "Try again",
                                    JOptionPane.ERROR_MESSAGE);
                    typedText = null;
                    textField.requestFocusInWindow();
                }
            } else { //user closed dialog or clicked cancel
                typedText = null;
                clearAndHide();
            }
        }
    }

    /** This method clears the dialog and hides it. */
    public void clearAndHide() {
        textField.setText(null);
        setVisible(false);
    }
}
}
