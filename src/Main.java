import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

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
      LogUtil.out(srcFile.getName());
    }

  }
}
