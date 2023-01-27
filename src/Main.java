import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import mslinks.ShellLink;

public class Main
{
  public static void main(String[] args)
      throws FileNotFoundException, IOException, ClassNotFoundException,
      InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException,
      InterruptedException, LineUnavailableException, UnsupportedAudioFileException
  {
    Properties prop = new Properties();
    prop.load(new FileInputStream("prop.properties"));
    File srcDir = new File(prop.getProperty("src"));
    File destDir = new File(prop.getProperty("dest"));

    for (File srcFile : srcDir.listFiles())
    {
      String destFilePath = destDir.getAbsolutePath() + '/' + srcFile.getName();
      try
      {
        LogUtil.out("Move " + srcFile.getAbsolutePath() + " to " + destFilePath);
        Files.move(srcFile.toPath(), Paths.get(destFilePath),
                   StandardCopyOption.REPLACE_EXISTING);
      }
      catch (DirectoryNotEmptyException e)
      {
        LogUtil.out("Delete " + destFilePath);
        FileUtils.deleteDirectory(new File(destFilePath));
        LogUtil.out("Move " + srcFile.getAbsolutePath() + " to " + destFilePath);
        Files.move(srcFile.toPath(), Paths.get(destFilePath),
                   StandardCopyOption.REPLACE_EXISTING);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }

    for (File destSubDir : destDir.listFiles())
    {
      if (destSubDir.isDirectory() && !destSubDir.getName().equalsIgnoreCase("startup"))
      {
        for (File copyFile : FileUtils.listFiles(destSubDir, TrueFileFilter.INSTANCE,
                                                 TrueFileFilter.INSTANCE))
        {
          LogUtil
              .out("Copy " + copyFile.getAbsolutePath() + " to " + destDir.getAbsolutePath());
          FileUtils.copyFile(copyFile,
                             new File(destDir.getAbsolutePath() + '/' + copyFile.getName()));
        }
        LogUtil.out("Delete " + destSubDir.getName());
        FileUtils.deleteDirectory(destSubDir);
      }
    }

    Set<File> delSet = new HashSet<File>();
    for (File destFile : destDir.listFiles())
    {
      if (destFile.isFile() &&
          FileNameUtils.getExtension(destFile.getName()).equalsIgnoreCase("lnk"))
      {
        try
        {
          File targetFile = new File(new ShellLink(destFile).resolveTarget());
          if (!targetFile.exists() && !targetFile.getAbsolutePath().contains("%windir%"))
          {
            delSet.add(destFile);
          }
        }
        catch (Exception e)
        {
          // e.printStackTrace();
        }
      }
    }

    if (!delSet.isEmpty())
    {
      String msg = "";
      for (File delFile : delSet)
      {
        msg += delFile.getName() + '\n';
      }
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      if (JOptionPane.showConfirmDialog(null, msg, "Delete?",
                                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
      {
        for (File delFile : delSet)
        {
          LogUtil.out("Delete " + delFile.getAbsolutePath());
          Files.delete(delFile.toPath());
        }
      }
    }
    WavUtil.flush();
  }
}
