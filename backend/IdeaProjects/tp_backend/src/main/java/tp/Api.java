package tp;

import org.jboss.resteasy.plugins.interceptors.CorsFilter;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;
import java.util.LinkedHashSet;

import org.jboss.resteasy.plugins.interceptors.CorsFilter;


@ApplicationPath("/api")
public class Api extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> set = new HashSet<>();
        set.add( DBConnector.class );

        return set;
    }
}
