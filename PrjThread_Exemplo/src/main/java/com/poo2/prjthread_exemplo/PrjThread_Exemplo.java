package com.poo2.prjthread_exemplo;

import Classes.ClasseContA;
import Classes.ClasseContThreadB;

/**
 *
 * @author natsa
 */
public class PrjThread_Exemplo{

    public static void main(String[] args) {
        //new ClasseContA("Teste I", 100).contador();
        //new ClasseContA("Teste II", 100).contador();
        
       /*Thread A = new ClasseContThreadB("Teste I", 1000);
       Thread B = new ClasseContThreadB("Teste II", 1000); 
        
        A.start();
        B.start();*/
       
       Thread A = new Thread(new ClasseContThreadB ("Tes9te I", 1000));
       Thread B = new Thread(new ClasseContThreadB("Teste II", 1000)); 
        
       A.start();
       B.start();
       
        
    }
}
