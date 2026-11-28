import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FolderOutput {
    private static final String folder = "output/" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());

    public static String path(String namaFile) {
        new File(folder).mkdirs();
        return folder + "/" + namaFile;
    }
}