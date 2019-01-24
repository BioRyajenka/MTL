import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * Created by Jackson on 17.12.2016.
 */
public final class Util {
    private Util() {

    }

    public static List<String> getLines(String sourcePath) throws IOException {
        File file = new File(sourcePath);
        return Files.readAllLines(file.toPath());
    }

//    public static String ntimes(String s, int n) {
//        StringBuilder resb = new StringBuilder();
//        for (int i = 0; i < n; i++) {
//            resb.append(s);
//        }
//        return resb.toString();
//    }
}
