import java.io.File;
import java.io.FileInputStream;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

public class Main
{
  public static void main(String[] args) throws Exception
  {
    Properties prop = new Properties();
    prop.load(new FileInputStream("prop.properties"));
    File srcDir = new File(prop.getProperty("src"));
    File destDir = new File(prop.getProperty("dest"));

    for (File srcFile : srcDir.listFiles())
    {
      String destFilePath = destDir.getAbsolutePath() + "\\" + srcFile.getName();
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
    WavUtil.flush();
  }
}
