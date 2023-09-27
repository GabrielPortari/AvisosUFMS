package com.example.avisosufms.helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;


public class DateCustom {
    /*Método criado para retornar a data atual no formado dd/MM/yyyy*/
    public static String getDataAtual(){
        long data = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dataAtual = simpleDateFormat.format(data);
        return dataAtual;
    }
    public static String getHoraAtual(){
        long hora = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        String horaAtual = simpleDateFormat.format(hora);
        return horaAtual;
    }
}
