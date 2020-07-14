package GamePackage.GameObjects;

import utilities.AttributeString;
import utilities.Vector2D;

import java.awt.*;


public class AttributeStringObject<T> extends StringObject {

    private AttributeString<T> attributeString;

    public AttributeStringObject(Vector2D p, Vector2D v, String attribute, T value, String suffix, int a){
        super(p,v,a);
        makeAttributeString(attribute,value,suffix);
    }

    public AttributeStringObject(Vector2D p, Vector2D v, String attribute, T value, int a){
        this(p,v,attribute,value,"",a);
    }

    public AttributeStringObject(Vector2D p, Vector2D v, String attribute, T value, String suffix, int a, Font f){
        this(p,v,attribute,value,suffix,a);
        this.setTheFont(f);
    }

    public AttributeStringObject(Vector2D p, Vector2D v, String attribute, T value, int a, Font f){
        this(p,v,attribute,value,"",a,f);
    }

    /*
    public AttributeStringObject(Vector2D p, Vector2D v, String attribute, T value){
        super(p,v);
        makeAttributeString(attribute,value,"");
    }*/


    public AttributeStringObject<T> revive(T value){
        super.revive();
        return (this.setValue(value));
    }

    public AttributeStringObject<T> setAttribute(String attributeName){ attributeString.rename(attributeName); updateText(); return this;}

    public AttributeStringObject<T> setValue(T value){
        attributeString.showValue(value);
        updateText();
        return this;
    }

    public AttributeStringObject<T> setSuffix(String suffix){ attributeString.changeSuffix(suffix); updateText(); return this;}

    public String getAttributeName(){ return attributeString.getAttributeName(); }

    public T getValue(){ return attributeString.getValue();}

    public String getSuffix() { return  attributeString.getSuffix(); }

    private void updateText(){ setText(attributeString.toString()); }

    public AttributeStringObject<T> kill(){ super.kill(); return this; }

    public void update(){
        updateText();
        super.update();
    }

    private void makeAttributeString(String att, T val, String suf){
        attributeString = new AttributeString<>(att,val, suf);
        setText(attributeString.toString());
    }
}
