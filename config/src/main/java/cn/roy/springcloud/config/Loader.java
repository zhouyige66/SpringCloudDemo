package cn.roy.springcloud.config;

import org.apache.commons.codec.Charsets;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @Description: 配置文件加载器
 * @Author: Roy Z
 * @Date: 2019-05-08 10:57
 * @Version: v1.0
 */
public class Loader implements PropertySourceLoader {

    public Loader() {
    }

    @Override
    public String[] getFileExtensions() {
        return new String[]{"properties", "xml"};
    }

    public List<PropertySource<?>> load(String name, Resource resource) throws IOException {
        Map<String, ?> properties = this.loadProperties(resource);
        return properties.isEmpty() ? Collections.emptyList()
                : Collections.singletonList(new OriginTrackedMapPropertySource(name, properties));
    }

    private Map<String, ?> loadProperties(Resource resource) throws IOException {
        Properties props = new Properties();
        fillProperties(props, resource);
        return (Map)props;
    }

    private void fillProperties(Properties props, Resource resource) throws IOException {
        InputStream is = resource.getInputStream();
        try {
            String filename = resource.getFilename();
            if (filename != null && filename.endsWith(".xml")) {
                props.loadFromXML(is);
            } else {
                props.load(new InputStreamReader(is, Charsets.UTF_8));
            }
        } finally {
            is.close();
        }
    }
}
