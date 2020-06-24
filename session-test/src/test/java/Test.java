import com.googlecode.mp4parser.FileDataSourceImpl;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.tracks.h265.H265TrackImpl;
import com.googlecode.mp4parser.authoring.tracks.h265.H265TrackImplOld;

import java.io.IOException;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2020/4/23 10:26
 * @Version: v1.0
 */
public class Test {

    public static void main(String[] args) {
        try {
            String path = "/Users/zzy/Downloads/";
            String originFile = path + "20200420235112.mp4";
            String newFile = path + "111.mp4";
            H265TrackImpl track = new H265TrackImpl(new FileDataSourceImpl(originFile));
            Movie movie = new Movie();
//                            movie.addTrack(track);
//                            Container mp4file = new DefaultMp4Builder().build(movie);
//                            FileChannel fc = new FileOutputStream(new File(newFile)).getChannel();
//                            mp4file.writeContainer(fc);
//                            fc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
