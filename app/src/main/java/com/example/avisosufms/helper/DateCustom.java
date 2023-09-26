package com.example.avisosufms.helper;

import java.text.SimpleDateFormat;

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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        String horaAtual = simpleDateFormat.format(hora);
        return horaAtual;
    }
}
