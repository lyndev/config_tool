/**
 * @date 2014/3/28 15:42
 * @author ChenLong
 */
package confdbtool;

import confdbtool.ui.frame.MainFrame;
import java.awt.EventQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * 策划配置工具main方法
 *
 * @author ChenLong
 */
public class ConfigDBTool
{
    public static void main(String[] args)
    {
        EventQueue.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    new MainFrame().setVisible(true);
                }
                catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex)
                {
                    Logger.getLogger(ConfigDBTool.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
}
