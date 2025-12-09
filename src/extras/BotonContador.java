/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package basededatos;


import javax.swing.JButton;

/**
 *
 * @author noobe
 */
public class BotonContador extends JButton{
   //Propiedades
    int pulsaciones;
    
    //Constructor
    public BotonContador(){
        pulsaciones=0;
    }
    
   //Metodos
    //Asigna una cantidad de pulsaciones
    public void setPulsaciones(int p){
        pulsaciones=p;
    }
    
    //devuelve las pulsaciones del boton
    public int getPulsaciones(){
        return pulsaciones;
    }
    
    //incrementa en uno las pulsaciones
    public void incrementa(){
        pulsaciones++;
    }
    
    //decrementa en uno las pulsaciones
    public void decrementa(){
        pulsaciones--;
    }
    
    //pone las pulsacionesa a cero
    public void reiniciar(){
        pulsaciones=0;
    }
    
    //aumenta las pulsaciones en una cantidad c
    public void aumenta(int c){
        pulsaciones=pulsaciones+c;
    }
    
    //disminuye las pulsaciones en una cantidad c
    public void disminuye(int c){
        pulsaciones=pulsaciones-c;
    }
}
