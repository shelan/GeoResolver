import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.google.i18n.phonenumbers.geocoding.PhoneNumberOfflineGeocoder;

import java.sql.*;
import java.util.Locale;

public class Test {

    public static void main(String[] args) throws NumberParseException, SQLException, ClassNotFoundException {
        Phonenumber.PhoneNumber number = PhoneNumberUtil.getInstance().parseAndKeepRawInput("+448008080000", "");
        String desc = PhoneNumberOfflineGeocoder.getInstance().getDescriptionForNumber(
                number, new Locale("en", "US"));
        System.out.println(desc);
        String region = PhoneNumberUtil.getInstance().getRegionCodeForNumber(number);
        System.out.println(region);
        System.out.println("test".split(":")[0]);
        Class.forName("org.h2.Driver");
        Connection conn = DriverManager.
                getConnection("jdbc:h2:./test");
        // conn.close();

        String user = "";
        String password = "";
        //Connection conn = DriverManager.getConnection(url, user, password);
        PreparedStatement ps2 = conn.prepareStatement("Show tables;");
        ResultSet rs = ps2.executeQuery();
        while (rs.next()){
            rs.getRow();
        }
    }

}
