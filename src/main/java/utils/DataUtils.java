package utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DataUtils {
    public static String getDataComDiferencaDias(Integer qtdDias){
        Calendar calendar = Calendar.getInstance(); //retorna instancia do calendar representando a data atual
        calendar.add(Calendar.DAY_OF_MONTH, qtdDias );
        return getDataFormatada(calendar.getTime());

    }

    //formata data em String
    public static String getDataFormatada(Date data){
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        return format.format(data);
    }
}
