package jm.task.core.jdbc.util;

import jm.task.core.jdbc.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import static java.lang.System.setProperty;
import static org.postgresql.PGProperty.PASSWORD;
import static org.postgresql.shaded.com.ongres.scram.common.ScramAttributes.USERNAME;

public class Util {
    // реализуйте настройку соеденения с БД
    private static Connection connection = null;
    private static Util instance = null;

    private Util() {
        try {
            if (connection == null || connection.isClosed()) {
                Properties props = getProperties();
                connection = DriverManager
                        .getConnection(props.getProperty("db.url"), props.getProperty("db.username"), props.getProperty("db.password"));
                //poroperty - по ключу возвращ значение/ получение из prop файла
            }
        }catch (SQLException | IOException e){
            e.printStackTrace();
        }
    }
    public static Util getInstance(){ // для обеспечения Singleton-паттерна, чтобы гарантировать, что только 1 экз класса Util будет создан.
        if (null == instance){
            instance = new Util();
        }
        return instance;
    }
        public Connection getConnection(){
        return connection;
        }

        private static Properties getProperties() throws IOException{ //  загружает свойства из файла конфигурации DB, для получения информации о подключении к db
            Properties properties = new Properties();
            try (InputStream in = Files.newInputStream(Paths.get(Util.class.getResource("/database.properties").toURI()))){
                properties.load(in);
                return properties;
            }catch (IOException | URISyntaxException e){
                throw new IOException("Конфиг базы данных не найден", e);
            }
        }

        public static SessionFactory sessionFactory;

        public static Session getSession(){
            if (sessionFactory == null){
                try {
                    Configuration configuration = new Configuration().addAnnotatedClass(User.class);
                     sessionFactory = configuration.buildSessionFactory();

                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println("Оибка создания SessionFactory");
                }
            }
            return sessionFactory.getCurrentSession();  //
        }
}
